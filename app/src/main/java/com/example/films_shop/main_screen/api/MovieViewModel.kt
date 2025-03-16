import android.util.Log
import androidx.compose.runtime.mutableStateOf
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import androidx.paging.Pager
import androidx.paging.PagingConfig
import androidx.paging.PagingData
import androidx.paging.PagingSource
import androidx.paging.PagingState
import androidx.paging.cachedIn
import com.example.films_shop.main_screen.api.Genre
import kotlinx.coroutines.flow.Flow
import com.example.films_shop.main_screen.api.Movie
import com.example.films_shop.main_screen.api.MovieApiService
import com.example.films_shop.main_screen.api.Persons
import com.example.films_shop.main_screen.api.Poster
import com.example.films_shop.main_screen.api.Rating
import com.example.films_shop.main_screen.api.RetrofitInstance
import com.example.films_shop.main_screen.api.apiKey
import com.example.films_shop.main_screen.business_logic.FavoriteMovie
import com.google.firebase.firestore.FirebaseFirestore
import retrofit2.HttpException
import java.io.IOException

class MoviePagingSource(
    private val apiService: MovieApiService,
    private val apiKey: String
) : PagingSource<Int, Movie>() {

    override suspend fun load(params: LoadParams<Int>): LoadResult<Int, Movie> {
        val page = params.key ?: 1
        return try {
            val response = apiService.getTop250Movies(apiKey, page = page, limit = 10)

            val updatedMovies = response.docs.map { movie ->
                movie.copy(
                    poster = movie.poster?.copy(url = movie.poster.url ?: "https://raw.githubusercontent.com/RustamKZ/recfi_ap/refs/heads/master/poster.jpg")
                        ?: Poster("https://raw.githubusercontent.com/RustamKZ/recfi_ap/refs/heads/master/poster.jpg"),
                    persons = movie.persons?.filter { it.profession == "режиссеры" } ?: emptyList()
                )
            }
            val filteredMovies = updatedMovies.filter { it.name != null }

            LoadResult.Page(
                data = filteredMovies,
                prevKey = if (page == 1) null else page - 1,
                nextKey = if (response.docs.isNotEmpty()) page + 1 else null
            )
        } catch (e: IOException) {
            LoadResult.Error(e)
        } catch (e: HttpException) {
            LoadResult.Error(e)
        }
    }

    override fun getRefreshKey(state: PagingState<Int, Movie>): Int? {
        return state.anchorPosition?.let { state.closestPageToPosition(it)?.prevKey?.plus(1) }
    }
}


class MovieViewModel : ViewModel() {
    val favoriteMoviesState = mutableStateOf<List<Movie>>(emptyList())

    fun loadFavoriteMovies(db: FirebaseFirestore, uid: String) {
        db.collection("users").document(uid).collection("favorites_movies")
            .addSnapshotListener { snapshot, error ->
                if (error != null) {
                    Log.e("MyLog", "Ошибка загрузки избранных фильмов", error)
                    return@addSnapshotListener
                }
                snapshot?.let {
                    val movies = it.documents.mapNotNull { doc -> doc.toObject(FavoriteMovie::class.java) }
                        .map { favorite ->
                            Movie(
                                id = favorite.key,
                                name = favorite.name,
                                year = favorite.year,
                                poster = favorite.posterUrl?.let { Poster(it) },
                                genres = favorite.genres?.map { Genre(it) },
                                rating = Rating(favorite.rating ?: 0.0),
                                persons = favorite.persons?.map { Persons(it) },
                                description = favorite.description,
                                isFavorite = true
                            )
                        }
                    favoriteMoviesState.value = movies
                    Log.d("MyLog", "Избранное обновлено, всего: ${movies.size}")
                }
            }
    }

    val moviePagingFlow: Flow<PagingData<Movie>> = Pager(
        config = PagingConfig(pageSize = 10, prefetchDistance = 2),
        pagingSourceFactory = { MoviePagingSource(RetrofitInstance.api, apiKey) }
    ).flow.cachedIn(viewModelScope)  // Кэшируем данные в viewModelScope
}
