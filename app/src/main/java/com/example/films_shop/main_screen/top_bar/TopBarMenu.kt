package com.example.films_shop.main_screen.top_bar

import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.material3.TopAppBarScrollBehavior
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.sp
import androidx.compose.material3.Icon
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import com.example.films_shop.R
import com.example.films_shop.ui.theme.AppColor

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun TopBarMenu(
    modifier: Modifier = Modifier,
    scrollBehavior: TopAppBarScrollBehavior
) {
    TopAppBar(
        modifier = modifier
            .padding(horizontal = 10.dp)
            .clip(RoundedCornerShape(100.dp)),
        scrollBehavior = scrollBehavior,
        colors = TopAppBarDefaults.topAppBarColors(
            containerColor = AppColor,
            scrolledContainerColor = Color.Transparent,
            titleContentColor = Color.Gray
        ),
        title = {
            Text(
                text = "Введите название..",
                color = MaterialTheme.colorScheme.onBackground.copy(0.7f),
                fontSize = 17.sp
            )
        },
        navigationIcon = {
            Icon(
                painter = painterResource(R.drawable.baseline_search_24),
                contentDescription = "menu_top_bar",
                modifier = Modifier
                    .padding(start = 16.dp, end = 8.dp)
                    .size(27.dp)
            )
        },
        actions = {
            Icon(
                painter = painterResource(R.drawable.notifications_top_bar),
                contentDescription = "menu_top_bar",
                modifier = Modifier
                    .padding(start = 16.dp, end = 8.dp)
                    .size(27.dp)
            )
        }
    )
}