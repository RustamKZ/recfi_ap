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
import com.example.films_shop.main_screen.api.Backdrop
import com.example.films_shop.main_screen.api.ExternalId
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
import com.example.films_shop.main_screen.business_logic.BookmarkMovie
import com.example.films_shop.main_screen.business_logic.RatedMovie
import com.google.firebase.firestore.FirebaseFirestore
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.flatMapLatest
import retrofit2.HttpException
import java.io.IOException

enum class ContentType(val apiValue: String) {
    MOVIES("movie"),
    TV_SERIES("tv-series"),
    CARTOONS("cartoon")
}

class MoviePagingSource(
    private val apiService: MovieApiService,
    private val apiKey: String,
    private val contentType: ContentType,
) : PagingSource<Int, Movie>() {

    override suspend fun load(params: LoadParams<Int>): LoadResult<Int, Movie> {
        val page = params.key ?: 1
        return try {
            val response = when (contentType) {
                ContentType.MOVIES -> apiService.getTop250Movies(apiKey, page = page, limit = 10)
                ContentType.TV_SERIES -> apiService.getTop250TvSeries(
                    apiKey,
                    page = page,
                    limit = 10
                )

                ContentType.CARTOONS -> apiService.getTop250Cartoons(
                    apiKey,
                    page = page,
                    limit = 10
                )
            }
            // ЛОГ: выводим все поля каждого фильма
//            response.docs.forEach { movie ->
//                Log.d("MovieDebug", "Загружен фильм: ${movie.name}, tmdbId: ${movie.externalId}")
//            }
            val updatedMovies = response.docs.map { movie ->
                movie.copy(
                    poster = movie.poster?.copy(
                        url = movie.poster.url
                            ?: "https://raw.githubusercontent.com/RustamKZ/recfi_ap/refs/heads/master/poster.jpg"
                    )
                        ?: Poster("https://raw.githubusercontent.com/RustamKZ/recfi_ap/refs/heads/master/poster.jpg"),
                    backdrop = when {
                        // Проверяем наличие backdrop и что его url не null
                        movie.backdrop != null && movie.backdrop.url != null ->
                            movie.backdrop.copy(url = movie.backdrop.url)
                        // Если есть постер и его url не null, создаем Backdrop
                        movie.poster != null && movie.poster.url != null ->
                            Backdrop(movie.poster.url)
                        // Иначе используем дефолтную ссылку
                        else ->
                            Backdrop("https://raw.githubusercontent.com/RustamKZ/recfi_ap/refs/heads/master/poster.jpg")
                    },
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
    val favoriteTvSeriesState = mutableStateOf<List<Movie>>(emptyList())
    val favoriteCartoonsState = mutableStateOf<List<Movie>>(emptyList())

    val bookmarkMoviesState = mutableStateOf<List<Movie>>(emptyList())
    val bookmarkTvSeriesState = mutableStateOf<List<Movie>>(emptyList())
    val bookmarkCartoonsState = mutableStateOf<List<Movie>>(emptyList())

    val ratedMoviesState = mutableStateOf<List<Movie>>(emptyList())
    val ratedTvSeriesState = mutableStateOf<List<Movie>>(emptyList())
    val ratedCartoonsState = mutableStateOf<List<Movie>>(emptyList())


    val currentContentType = MutableStateFlow(ContentType.MOVIES)

    val moviesPagingFlow = createPagingFlow(ContentType.MOVIES)
    val tvSeriesPagingFlow = createPagingFlow(ContentType.TV_SERIES)
    val cartoonsPagingFlow = createPagingFlow(ContentType.CARTOONS)

    @OptIn(ExperimentalCoroutinesApi::class)
    val currentContentFlow: Flow<PagingData<Movie>> =
        combine(currentContentType.asStateFlow()) { contentType ->
            when (contentType.single()) {
                ContentType.MOVIES -> moviesPagingFlow
                ContentType.TV_SERIES -> tvSeriesPagingFlow
                ContentType.CARTOONS -> cartoonsPagingFlow
            }
        }.flatMapLatest { it }

    private fun createPagingFlow(contentType: ContentType): Flow<PagingData<Movie>> {
        return Pager(
            config = PagingConfig(pageSize = 10, prefetchDistance = 2),
            pagingSourceFactory = { MoviePagingSource(RetrofitInstance.api, apiKey, contentType) }
        ).flow.cachedIn(viewModelScope)
    }

    fun setContentType(type: ContentType) {
        currentContentType.value = type
    }

    fun isInFavorites(id: String): Boolean {
        return favoriteMoviesState.value.any { it.id == id } ||
                favoriteTvSeriesState.value.any { it.id == id } ||
                favoriteCartoonsState.value.any { it.id == id }
    }

    fun isInBookmarks(id: String): Boolean {
        return bookmarkMoviesState.value.any { it.id == id } ||
                bookmarkTvSeriesState.value.any { it.id == id } ||
                bookmarkCartoonsState.value.any { it.id == id }
    }

    fun isInRated(id: String): Boolean {
        return ratedMoviesState.value.any { it.id == id } ||
                ratedTvSeriesState.value.any { it.id == id } ||
                ratedCartoonsState.value.any { it.id == id }
    }

    fun loadFavoriteMovies(db: FirebaseFirestore, uid: String, contentType: ContentType? = null) {
        db.collection("users").document(uid).collection("favorites_movies")
            .addSnapshotListener { snapshot, error ->
                if (error != null) {
                    Log.e("MyLog", "Ошибка загрузки избранных фильмов", error)
                    return@addSnapshotListener
                }
                snapshot?.let {
                    val allFavorites =
                        it.documents.mapNotNull { doc -> doc.toObject(FavoriteMovie::class.java) }
                            .map { favorite ->
                                Movie(
                                    id = favorite.key,
                                    externalId = ExternalId(favorite.tmdbId ?: 0),
                                    name = favorite.name,
                                    year = favorite.year,
                                    poster = favorite.posterUrl?.let { Poster(it) },
                                    backdrop = favorite.backdropUrl?.let { Backdrop(it) },
                                    genres = favorite.genres?.map { Genre(it) },
                                    rating = Rating(favorite.rating ?: 0.0),
                                    persons = favorite.persons?.map { Persons(it) },
                                    description = favorite.description,
                                    type = favorite.type,
                                    isFavorite = true,
                                    isBookMark = favorite.isBookMark,
                                    isRated = favorite.isRated,
                                    userRating = favorite.userRating
                                )
                            }

                    // Если тип контента не указан, обновляем все состояния
                    if (contentType == null) {
                        favoriteMoviesState.value =
                            allFavorites.filter { it.type == ContentType.MOVIES.apiValue }
                        favoriteTvSeriesState.value =
                            allFavorites.filter { it.type == ContentType.TV_SERIES.apiValue }
                        favoriteCartoonsState.value =
                            allFavorites.filter { it.type == ContentType.CARTOONS.apiValue }
                        Log.d(
                            "MyLog",
                            "Все избранное обновлено: фильмы=${favoriteMoviesState.value.size}, " +
                                    "сериалы=${favoriteTvSeriesState.value.size}, мультфильмы=${favoriteCartoonsState.value.size}"
                        )
                    } else {
                        // Если тип указан, обновляем только соответствующее состояние
                        val filtered = allFavorites.filter { it.type == contentType.apiValue }
                        when (contentType) {
                            ContentType.MOVIES -> favoriteMoviesState.value = filtered
                            ContentType.TV_SERIES -> favoriteTvSeriesState.value = filtered
                            ContentType.CARTOONS -> favoriteCartoonsState.value = filtered
                        }
                        Log.d(
                            "MyLog",
                            "Избранное типа ${contentType.apiValue} обновлено, всего: ${filtered.size}"
                        )
                    }
                }
            }
    }

    fun loadBookmarkMovies(db: FirebaseFirestore, uid: String, contentType: ContentType? = null) {
        db.collection("users").document(uid).collection("bookmark_movies")
            .addSnapshotListener { snapshot, error ->
                if (error != null) {
                    Log.e("MyLog", "Ошибка загрузки фильмов из списка посмотреть позже", error)
                    return@addSnapshotListener
                }
                snapshot?.let {
                    val allBookmark =
                        it.documents.mapNotNull { doc -> doc.toObject(BookmarkMovie::class.java) }
                            .map { bookmark ->
                                Movie(
                                    id = bookmark.key,
                                    externalId = ExternalId(bookmark.tmdbId ?: 0),
                                    name = bookmark.name,
                                    year = bookmark.year,
                                    poster = bookmark.posterUrl?.let { Poster(it) },
                                    backdrop = bookmark.backdropUrl?.let { Backdrop(it) },
                                    genres = bookmark.genres?.map { Genre(it) },
                                    rating = Rating(bookmark.rating ?: 0.0),
                                    persons = bookmark.persons?.map { Persons(it) },
                                    description = bookmark.description,
                                    type = bookmark.type,
                                    isFavorite = bookmark.isFavorite,
                                    isBookMark = true,
                                    isRated = bookmark.isRated,
                                    userRating = bookmark.userRating
                                )
                            }

                    // Если тип контента не указан, обновляем все состояния
                    if (contentType == null) {
                        bookmarkMoviesState.value =
                            allBookmark.filter { it.type == ContentType.MOVIES.apiValue }
                        bookmarkTvSeriesState.value =
                            allBookmark.filter { it.type == ContentType.TV_SERIES.apiValue }
                        bookmarkCartoonsState.value =
                            allBookmark.filter { it.type == ContentType.CARTOONS.apiValue }
                        Log.d(
                            "MyLog",
                            "Все посмотреть позже фильмы: фильмы=${bookmarkMoviesState.value.size}, " +
                                    "сериалы=${bookmarkTvSeriesState.value.size}, мультфильмы=${bookmarkCartoonsState.value.size}"
                        )
                    } else {
                        // Если тип указан, обновляем только соответствующее состояние
                        val filtered = allBookmark.filter { it.type == contentType.apiValue }
                        when (contentType) {
                            ContentType.MOVIES -> bookmarkMoviesState.value = filtered
                            ContentType.TV_SERIES -> bookmarkTvSeriesState.value = filtered
                            ContentType.CARTOONS -> bookmarkCartoonsState.value = filtered
                        }
                        Log.d(
                            "MyLog",
                            "посмотреть позже типа ${contentType.apiValue} обновлено, всего: ${filtered.size}"
                        )
                    }
                }
            }
    }

    fun loadRatedMovies(db: FirebaseFirestore, uid: String, contentType: ContentType? = null) {
        db.collection("users").document(uid).collection("rated_movies")
            .addSnapshotListener { snapshot, error ->
                if (error != null) {
                    Log.e("MyLog", "Ошибка загрузки оцененных фильмов", error)
                    return@addSnapshotListener
                }
                snapshot?.let {
                    val allRated =
                        it.documents.mapNotNull { doc -> doc.toObject(RatedMovie::class.java) }
                            .map { rated ->
                                Movie(
                                    id = rated.key,
                                    externalId = ExternalId(rated.tmdbId ?: 0),
                                    name = rated.name,
                                    year = rated.year,
                                    poster = rated.posterUrl?.let { Poster(it) },
                                    backdrop = rated.backdropUrl?.let { Backdrop(it) },
                                    genres = rated.genres?.map { Genre(it) },
                                    rating = Rating(rated.rating ?: 0.0),
                                    persons = rated.persons?.map { Persons(it) },
                                    description = rated.description,
                                    type = rated.type,
                                    isFavorite = rated.isFavorite,
                                    isBookMark = rated.isBookMark,
                                    isRated = true,
                                    userRating = rated.userRating
                                )
                            }

                    // Если тип контента не указан, обновляем все состояния
                    if (contentType == null) {
                        ratedMoviesState.value =
                            allRated.filter { it.type == ContentType.MOVIES.apiValue }
                        ratedTvSeriesState.value =
                            allRated.filter { it.type == ContentType.TV_SERIES.apiValue }
                        ratedCartoonsState.value =
                            allRated.filter { it.type == ContentType.CARTOONS.apiValue }
                        Log.d(
                            "MyLog",
                            "Все оцененные обновлено: фильмы=${ratedMoviesState.value.size}, " +
                                    "сериалы=${ratedTvSeriesState.value.size}, мультфильмы=${ratedCartoonsState.value.size}"
                        )
                    } else {
                        // Если тип указан, обновляем только соответствующее состояние
                        val filtered = allRated.filter { it.type == contentType.apiValue }
                        when (contentType) {
                            ContentType.MOVIES -> ratedMoviesState.value = filtered
                            ContentType.TV_SERIES -> ratedTvSeriesState.value = filtered
                            ContentType.CARTOONS -> ratedCartoonsState.value = filtered
                        }
                        Log.d(
                            "MyLog",
                            "Оцененные типа ${contentType.apiValue} обновлено, всего: ${filtered.size}"
                        )
                    }
                }
            }
    }
}
