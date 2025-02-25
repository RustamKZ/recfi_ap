package com.example.films_shop.main_screen.add_film_screen

import android.content.ContentResolver
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.net.Uri
import android.util.Base64
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.ActivityNavigatorExtras
import coil.compose.rememberAsyncImagePainter
import com.example.films_shop.R
import com.example.films_shop.main_screen.business_logic.compressAndConvertToBase64
import com.example.films_shop.main_screen.business_logic.saveFilmToFireStore
import com.example.films_shop.main_screen.custom_font
import com.example.films_shop.main_screen.data.Film
import com.example.films_shop.main_screen.login.LoginButton
import com.example.films_shop.main_screen.login.RoundedCornerTextField
import com.example.films_shop.ui.theme.BlueForBackground
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase
import java.io.ByteArrayOutputStream
import kotlin.contracts.contract


@Composable
fun AddFilmScreen(
    navData: AddFilmScreenObject = AddFilmScreenObject(),
    onSaved: () -> Unit = {}
) {
    val cv = LocalContext.current.contentResolver
    val selectedGenre = remember { mutableStateOf(navData.genre) }
    val title = remember { mutableStateOf(navData.title) }
    val description = remember { mutableStateOf(navData.description) }
    val year = remember { mutableStateOf(navData.year) }
    val director = remember { mutableStateOf("") }
    val selectedImageUri = remember { mutableStateOf<Uri?>(null) }

    val imageBitmap = remember {
        val base64Image = Base64.decode(navData.imageUrl, Base64.DEFAULT)
        val bitmap = BitmapFactory.decodeByteArray(
            base64Image, 0,
            base64Image.size
        )
        mutableStateOf(bitmap)
    }
    val firestore = remember {
        Firebase.firestore
    }
    val imageLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.GetContent()
    ) { uri ->
        imageBitmap.value = null
        selectedImageUri.value = uri
    }
    Image(
        painter = rememberAsyncImagePainter(
            model = imageBitmap.value ?: selectedImageUri.value
        ),
        contentDescription = "backgroundImageAddFilm",
        Modifier.fillMaxSize(),
        contentScale = ContentScale.Crop
    )
    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(BlueForBackground)
    )
    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(
                start = 40.dp,
                end = 40.dp
            ),
        verticalArrangement = Arrangement.Center,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Image(
            painter = painterResource(id = R.drawable.img),
            contentDescription = "LogoScreen",
        )
        Spacer(Modifier.height(15.dp))
        Text(
            text = "Add new film",
            fontSize = 35.sp,
            fontWeight = FontWeight.Bold,
            fontFamily = custom_font,
            color = Color.Black
        )
        Spacer(Modifier.height(15.dp))
        RoundedCornerTextField(
            text = title.value,
            label = "Title"
        )
        {
            title.value = it
        }
        Spacer(Modifier.height(15.dp))
        RoundedCornerDropDownMenu(
            selectedGenre.value
        ) { selectedItem ->
            selectedGenre.value = selectedItem
        }
        Spacer(Modifier.height(10.dp))
        RoundedCornerTextField(
            maxLines = 3,
            singLine = false,
            text = description.value,
            label = "Description"
        )
        {
            description.value = it
        }
        Spacer(Modifier.height(15.dp))
        RoundedCornerTextField(
            text = year.value,
            label = "Year"
        )
        {
            year.value = it
        }
        Spacer(Modifier.height(15.dp))
        RoundedCornerTextField(
            text = director.value,
            label = "Director"
        )
        {
            director.value = it
        }
        Spacer(Modifier.height(10.dp))
        LoginButton("Select image") {
            imageLauncher.launch("image/*")
        }
        Spacer(Modifier.height(10.dp))
        LoginButton("Save") {
            saveFilmToFireStore(
                firestore,
                Film(
                    key = navData.key,
                    title = title.value,
                    description = description.value,
                    genre = selectedGenre.value,
                    year = year.value,
                    director = director.value,
                    imageUrl = if (selectedImageUri.value != null) {
                        compressAndConvertToBase64(
                            selectedImageUri.value!!,
                            cv
                        )
                    } else navData.imageUrl
                ),
                onSaved = {
                    onSaved()
                },
                onError = {

                }
            )
        }
    }
}

