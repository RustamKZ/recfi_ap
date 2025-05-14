import android.annotation.SuppressLint
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.pager.HorizontalPager
import androidx.compose.foundation.pager.rememberPagerState
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.navigation.NavController
import com.example.films_shop.main_screen.bottom_menu.BottomMenu
import com.example.films_shop.main_screen.objects.main_screens_objects.MainScreenDataObject
import com.example.films_shop.main_screen.screens.favourite_screens.bookmark_screens.BookmarkMovieScreen
import com.example.films_shop.main_screen.screens.favourite_screens.favourite_screens.FavMovieScreen
import com.example.films_shop.main_screen.screens.favourite_screens.rated_screens.RatedMovieScreen
import com.example.films_shop.main_screen.top_bar.TopBarMenu
import kotlinx.coroutines.launch

@SuppressLint("UnusedMaterial3ScaffoldPaddingParameter")
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun UserCollectionMovieScreen(
    navData: MainScreenDataObject,
    movieViewModel: MovieViewModel,
    navController: NavController,
    showTopBar: Boolean = true,
    showBottomBar: Boolean = true,
    scrollBehavior: TopAppBarScrollBehavior,
    contentType: ContentType,
) {
    val tabs = listOf("Избранное", "Посмотреть позже", "Вы оценили")
    val pagerState = rememberPagerState(initialPage = 0, pageCount = { tabs.size })
    val coroutineScope = rememberCoroutineScope()
    Scaffold(
        topBar = {
            if (showTopBar) {
                TopBarMenu(scrollBehavior = scrollBehavior)
            }
        },
        bottomBar = {
            if (showBottomBar) {
                BottomMenu(
                    navController = navController,
                    uid = navData.uid,
                    email = navData.email
                )
            }
        }
    ) {
        Column {
            TabRow(
                selectedTabIndex = pagerState.currentPage,
                containerColor = Color.White,
                contentColor = Color.Black
            ) {
                tabs.forEachIndexed { index, title ->
                    Tab(
                        selected = pagerState.currentPage == index,
                        onClick = {
                            coroutineScope.launch {
                                pagerState.animateScrollToPage(index)
                            }
                        },
                        text = {
                            Text(
                                text = title,
                                color = if (pagerState.currentPage == index) Color.Black else Color.Gray
                            )
                        }
                    )
                }
            }

            // HorizontalPager без параметра pageCount
            HorizontalPager(
                state = pagerState,
                modifier = Modifier.fillMaxSize()
            ) { page ->
                when (page) {
                    0 -> FavMovieScreen(
                        navData,
                        movieViewModel,
                        navController,
                        showTopBar,
                        showBottomBar = false,
                        scrollBehavior,
                        contentType
                    )

                    1 -> BookmarkMovieScreen(
                        navData,
                        movieViewModel,
                        navController,
                        showTopBar,
                        showBottomBar = false,
                        scrollBehavior,
                        contentType
                    )

                    2 -> RatedMovieScreen(
                        navData,
                        movieViewModel,
                        navController,
                        showTopBar,
                        showBottomBar = false,
                        scrollBehavior,
                        contentType
                    )
                }
            }
        }
    }
}
