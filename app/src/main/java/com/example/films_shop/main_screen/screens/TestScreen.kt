package com.example.films_shop.main_screen.screens

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.material3.Button
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.compose.foundation.text.BasicTextField
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.films_shop.main_screen.api.recomendations.RecommendationViewModel

@Composable
fun RecommendationScreen(viewModel: RecommendationViewModel = viewModel()) {
    var inputId by remember { mutableStateOf("") }
    //val recommendations by viewModel.recommendations
    //val error by viewModel.error

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp)
    ) {
        Text("Введите ID фильма:", fontSize = 16.sp)
        Spacer(modifier = Modifier.height(8.dp))

        BasicTextField(
            value = inputId,
            onValueChange = { inputId = it },
            modifier = Modifier
                .fillMaxWidth()
                .padding(8.dp),
            singleLine = true
        )

        Spacer(modifier = Modifier.height(16.dp))

        Button(
            onClick = {
                val id = inputId.toIntOrNull()
                if (id != null) {
                    viewModel.fetchRecommendations(id)
                }
            },
            modifier = Modifier.fillMaxWidth()
        ) {
            Text("Получить рекомендации")
        }

        Spacer(modifier = Modifier.height(16.dp))

//        error?.let {
//            Text(text = it, color = Color.Red)
//        }
//
//        LazyColumn {
//            items(recommendations.size) { index ->
//                Text(
//                    text = recommendations[index],
//                    fontSize = 18.sp,
//                    modifier = Modifier.padding(8.dp)
//                )
//            }
//        }
    }
}

