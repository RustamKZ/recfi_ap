import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import androidx.paging.Pager
import androidx.paging.PagingConfig
import androidx.paging.PagingData
import androidx.paging.PagingSource
import androidx.paging.PagingState
import androidx.paging.cachedIn
import kotlinx.coroutines.flow.Flow
import com.example.films_shop.main_screen.api.Movie
import com.example.films_shop.main_screen.api.MovieApiService
import com.example.films_shop.main_screen.api.Poster
import com.example.films_shop.main_screen.api.RetrofitInstance
import com.example.films_shop.main_screen.api.apiKey
import retrofit2.HttpException
import java.io.IOException

class MoviePagingSource(
    private val apiService: MovieApiService,
    private val apiKey: String
) : PagingSource<Int, Movie>() {

    override suspend fun load(params: LoadParams<Int>): LoadResult<Int, Movie> {
        val page = params.key ?: 1
        return try {
            val response = apiService.getPopularMovies(apiKey, page = page, limit = 10)

            val updatedMovies = response.docs.map { movie ->
                movie.copy(
                    poster = movie.poster?.copy(url = movie.poster.url ?: "https://raw.githubusercontent.com/suai-os-2024/os-task3-RustamKZ/refs/heads/master/poster.jpg?token=GHSAT0AAAAAAC66GJ66PDYUKTAJBY73HZH2Z6AWKMQ")
                        ?: Poster("https://raw.githubusercontent.com/suai-os-2024/os-task3-RustamKZ/refs/heads/master/poster.jpg?token=GHSAT0AAAAAAC66GJ66PDYUKTAJBY73HZH2Z6AWKMQ")
                )
            }


            LoadResult.Page(
                data = updatedMovies,
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
    val moviePagingFlow: Flow<PagingData<Movie>> = Pager(
        config = PagingConfig(pageSize = 10, prefetchDistance = 2),
        pagingSourceFactory = { MoviePagingSource(RetrofitInstance.api, apiKey) }
    ).flow.cachedIn(viewModelScope)  // Кэшируем данные в viewModelScope
}
