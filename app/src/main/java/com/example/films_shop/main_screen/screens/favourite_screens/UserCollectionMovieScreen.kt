import android.annotation.SuppressLint
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.pager.HorizontalPager
import androidx.compose.foundation.pager.rememberPagerState
import androidx.compose.material3.*
import androidx.compose.material3.TabRowDefaults.tabIndicatorOffset
import androidx.compose.material3.pulltorefresh.PullToRefreshDefaults.indicatorColor
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.input.nestedscroll.NestedScrollConnection
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavController
import com.example.films_shop.main_screen.bottom_menu.BottomMenu
import com.example.films_shop.main_screen.bottom_menu.MainViewModel
import com.example.films_shop.main_screen.objects.main_screens_objects.MainScreenDataObject
import com.example.films_shop.main_screen.screens.favourite_screens.bookmark_screens.BookmarkMovieScreen
import com.example.films_shop.main_screen.screens.favourite_screens.favourite_screens.FavMovieScreen
import com.example.films_shop.main_screen.screens.favourite_screens.rated_screens.RatedMovieScreen
import com.example.films_shop.main_screen.top_bar.TopBarMenu
import com.example.films_shop.ui.theme.BackGroundColor
import com.example.films_shop.ui.theme.mainColorUiGreen
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
    noOpNestedScrollConnection: NestedScrollConnection,
    contentType: ContentType,
    viewModel: MainViewModel
) {
    val isDark = isSystemInDarkTheme()
    val tabColor = if (isDark) BackGroundColor else Color.White
    val textColor = if (isDark) Color.White else Color.Black
    val indicatorColor = if (isDark) mainColorUiGreen else mainColorUiGreen
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
                    email = navData.email,
                    selectedTab = viewModel.selectedTab,
                    onTabSelected = { viewModel.onTabSelected(it) }
                )
            }
        }
    ) {
        Column {
            TabRow(
                selectedTabIndex = pagerState.currentPage,
                containerColor = tabColor,
                contentColor = indicatorColor,
                indicator = { tabPositions ->
                    TabRowDefaults.Indicator(
                        Modifier.tabIndicatorOffset(tabPositions[pagerState.currentPage]),
                        color = indicatorColor
                    )
                }
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
                                color = if (pagerState.currentPage == index) indicatorColor else textColor
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
