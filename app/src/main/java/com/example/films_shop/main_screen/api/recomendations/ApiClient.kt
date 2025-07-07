package com.example.films_shop.main_screen.api.recomendations

import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory

object ApiClient {

    private val retrofit by lazy {
        Retrofit.Builder()
            .baseUrl("http://83.222.27.251:5000/")  // Внешний IP сервера
            .addConverterFactory(GsonConverterFactory.create())
            .build()
    }

    val api: RecommendationApi by lazy {
        retrofit.create(RecommendationApi::class.java)
    }
}
