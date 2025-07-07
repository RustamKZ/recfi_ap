
import android.annotation.SuppressLint
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.pager.HorizontalPager
import androidx.compose.foundation.pager.rememberPagerState
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Tab
import androidx.compose.material3.TabRow
import androidx.compose.material3.TabRowDefaults
import androidx.compose.material3.TabRowDefaults.tabIndicatorOffset
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBarScrollBehavior
import androidx.compose.runtime.Composable
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.navigation.NavController
import com.example.films_shop.main_screen.api.BookApi.BookViewModel
import com.example.films_shop.main_screen.bottom_menu.BottomMenu
import com.example.films_shop.main_screen.bottom_menu.MainViewModel
import com.example.films_shop.main_screen.objects.main_screens_objects.MainScreenDataObject
import com.example.films_shop.main_screen.screens.favourite_screens.bookmark_screens.BookmarkBookScreen
import com.example.films_shop.main_screen.screens.favourite_screens.favourite_screens.FavBookScreen
import com.example.films_shop.main_screen.screens.favourite_screens.rated_screens.RatedBookScreen
import com.example.films_shop.main_screen.top_bar.TopBarMenu
import com.example.films_shop.ui.theme.BackGroundColor
import com.example.films_shop.ui.theme.mainColorUiGreen
import kotlinx.coroutines.launch

@SuppressLint("UnusedMaterial3ScaffoldPaddingParameter")
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun UserCollectionBookScreen(
    navData: MainScreenDataObject,
    bookViewModel: BookViewModel,
    navController: NavController,
    showTopBar: Boolean = true,
    showBottomBar: Boolean = true,
    scrollBehavior: TopAppBarScrollBehavior,
    viewModel: MainViewModel
) {
    val isDark = isSystemInDarkTheme()
    val tabColor = if (isDark) BackGroundColor else Color.White
    val textColor = if (isDark) Color.White else Color.Black
    val indicatorColor = if (isDark) mainColorUiGreen else mainColorUiGreen
    val tabs = listOf("Избранное", "Прочесть позже", "Вы оценили")
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

            HorizontalPager(
                state = pagerState,
                modifier = Modifier.fillMaxSize()
            ) { page ->
                when (page) {
                    0 -> FavBookScreen(
                        navData,
                        bookViewModel,
                        navController,
                        showTopBar,
                        showBottomBar = false,
                        scrollBehavior
                    )

                    1 -> BookmarkBookScreen(
                        navData,
                        bookViewModel,
                        navController,
                        showTopBar,
                        showBottomBar = false,
                        scrollBehavior
                    )

                    2 -> RatedBookScreen(
                        navData,
                        bookViewModel,
                        navController,
                        showTopBar,
                        showBottomBar = false,
                        scrollBehavior
                    )
                }
            }
        }
    }
}
