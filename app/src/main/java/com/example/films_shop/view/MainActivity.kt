package com.example.films_shop.view

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.wrapContentWidth
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import androidx.navigation.toRoute
import com.example.films_shop.main_screen.MainScreen
import com.example.films_shop.main_screen.add_film_screen.AddFilmScreen
import com.example.films_shop.main_screen.add_film_screen.AddFilmScreenObject
import com.example.films_shop.main_screen.data.Film
import com.example.films_shop.main_screen.details_screen.data.DetailsNavObject
import com.example.films_shop.main_screen.details_screen.ui.DetailsScreen
import com.example.films_shop.main_screen.login.LoginScreen
import com.example.films_shop.main_screen.login.data_nav.LoginScreenObject
import com.example.films_shop.main_screen.login.data_nav.MainScreenDataObject
import com.example.films_shop.ui.theme.BookShopTheme
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            BookShopTheme {
                val navController = rememberNavController()
                NavHost(
                    navController = navController,
                    startDestination = LoginScreenObject
                )
                {
                    composable<LoginScreenObject>
                    {
                        LoginScreen { navData ->
                            navController.navigate(navData)
                        }
                    }
                    composable<MainScreenDataObject>
                    { navEntry ->
                        val navData = navEntry.toRoute<MainScreenDataObject>()
                        MainScreen(
                            navData,
                            onFilmDetailsClick = { film ->
                                navController.navigate(
                                    DetailsNavObject(
                                        title = film.title,
                                        genre = film.genre,
                                        year = film.year,
                                        director = film.director,
                                        description = film.description,
                                        imageUrl = film.imageUrl
                                    )
                                )

                            },
                            onFilmEditClick = { film ->
                                navController.navigate(
                                    AddFilmScreenObject(
                                        key = film.key,
                                        title = film.title,
                                        genre = film.genre,
                                        year = film.year,
                                        director = film.director,
                                        description = film.description,
                                        imageUrl = film.imageUrl
                                    )
                                )
                            }
                        ) {
                            navController.navigate(AddFilmScreenObject())
                        }
                    }
                    composable<AddFilmScreenObject>
                    { navEntry ->
                        val navData = navEntry.toRoute<AddFilmScreenObject>()
                        AddFilmScreen(navData) {
                            navController.popBackStack()
                        }
                    }
                    composable<DetailsNavObject>
                    { navEntry ->
                        val navData = navEntry.toRoute<DetailsNavObject>()
                        DetailsScreen(navData)
                    }
                }
            }
        }
    }
}

@Composable
fun TestScreen() {
    val fs = Firebase.firestore
    val list = remember {
        mutableStateOf(emptyList<Film>())
    }
    fs.collection("films").addSnapshotListener { snapShot, exception ->
        list.value = snapShot?.toObjects(Film::class.java) ?: emptyList()

    }

    Column(
        modifier = Modifier.fillMaxSize(),
        verticalArrangement = Arrangement.Center
    ) {
        LazyColumn(
            modifier = Modifier
                .fillMaxWidth(0.8f)
                .fillMaxHeight(0.8f)
        ) {
            items(list.value) { film ->
                Card(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(10.dp)
                ) {
                    Text(
                        text = film.title,
                        modifier = Modifier
                            .fillMaxWidth()
                            .wrapContentWidth()
                    )
                }
            }
        }
        Spacer(modifier = Modifier.height(10.dp))
        Button(onClick = {
            fs.collection("films").document().set(
                Film(
                    title = "Whiplash",
                    genre = "Drama",
                    year = "2014",
                    director = "Damien Chazelle",
                    description = "t focuses on an ambitious music student and aspiring jazz drummer (Teller), who is pushed to his limit by his abusive instructor (Simmons) at the fictional Shaffer Conservatory in New York City. " +
                            "The film was produced by Bold Films, Blumhouse Productions," +
                            " and Right of Way Films.",
                    imageUrl = "url"
                )
            )

        }) {
            Text("Add film")
        }
    }
}

