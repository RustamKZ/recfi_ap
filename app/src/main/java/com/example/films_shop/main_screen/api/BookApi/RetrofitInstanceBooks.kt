package com.example.films_shop.main_screen.api.BookApi

import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor // <-- Импорт для HttpLoggingInterceptor

object RetrofitInstanceBooks {
    private const val BASE_URL = "https://www.googleapis.com/books/v1/"

    // 1. Создаём экземпляр HttpLoggingInterceptor
    private val loggingInterceptor = HttpLoggingInterceptor().apply {
        // Устанавливаем уровень логирования. BODY покажет все данные:
        // URL, заголовки запроса/ответа, тела запроса/ответа.
        // Для отладки 403, это самый полезный уровень.
        level = HttpLoggingInterceptor.Level.BODY
    }

    // 2. Создаём OkHttpClient и добавляем наш интерцептор
    private val okHttpClient = OkHttpClient.Builder()
        .addInterceptor(loggingInterceptor) // <-- Добавляем логгирующий интерцептор
        // Если вы хотите добавить заголовок User-Agent для лучшей идентификации вашего приложения
        // или чтобы избежать потенциальных блокировок, можете раскомментировать эту часть:
        /*
        .addInterceptor { chain ->
            val originalRequest = chain.request()
            val requestWithUserAgent = originalRequest.newBuilder()
                .header("User-Agent", "YourAppName/1.0 (Android; ${android.os.Build.VERSION.RELEASE})")
                .build()
            chain.proceed(requestWithUserAgent)
        }
        */
        .build()

    // 3. Используем OkHttpClient при построении Retrofit
    val api: BookApiService by lazy {
        Retrofit.Builder()
            .baseUrl(BASE_URL)
            .addConverterFactory(GsonConverterFactory.create())
            .client(okHttpClient) // <-- Подключаем настроенный OkHttpClient
            .build()
            .create(BookApiService::class.java)
    }
}
