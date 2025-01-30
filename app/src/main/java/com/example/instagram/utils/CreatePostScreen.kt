package com.example.instagram.utils

import android.net.Uri
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.InsertPhoto
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import androidx.compose.material3.TextFieldDefaults
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.colorResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import coil.compose.AsyncImage
import com.example.instagram.InstaViewModel
import com.example.instagram.R
import java.time.LocalDateTime
import kotlin.compareTo

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun CreatePostScreen(navController: NavController,viewModel: InstaViewModel){
    var caption = rememberSaveable { mutableStateOf("") }
    val image = remember { mutableStateOf("") }
    val imageUri = remember { mutableStateOf<Uri?>(if(image.value!=null) Uri.parse(image.value) else null) }
    var selectedItem by remember { mutableStateOf("CreatePost") }
    val launcher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.GetContent()
    ) { uri: Uri? ->
        imageUri.value = uri
        image.value = uri.toString()
    }

    Scaffold(
        topBar = {
            TopBar("New Post")
        }, bottomBar = {
            BottomNavigationBar( selectedItem, onItemSelected = {
                selectedItem = it
                navController.navigate(route = it)
            })
        }
    ) {it
        Column(Modifier.fillMaxSize().padding(it).background(
            Brush.radialGradient(
                colors = listOf(
                    Color(0xFFe2d1c3),
                    Color(0xFFfdfcfb),
                ),
                radius = 300f

            )),
            horizontalAlignment = Alignment.CenterHorizontally){
            if (image.value!="") {
                AsyncImage(
                    model = getImage(),
                    contentDescription = "",
                    Modifier.padding(20.dp).fillMaxWidth(0.7f).fillMaxHeight(0.35f)
                        .shadow(elevation = 2.dp)
                        .background(Color.LightGray),
                    contentScale = ContentScale.FillBounds

                )
            }else{
                IconButton(
                    onClick = {launcher.launch("image/*")   },
                    Modifier
                        .padding(20.dp).fillMaxWidth(0.7f).fillMaxHeight(0.35f)
                        .shadow(elevation = 2.dp)
                        .background(Color.LightGray)
                ) {
                    Icon(
                        Icons.Default.InsertPhoto,
                        contentDescription = "",
                        Modifier
                            .fillMaxHeight(0.8f)
                            .fillMaxWidth()
                    )
                }
            }
            Spacer(Modifier.height(10.dp))

            OutlinedTextField(
                modifier = Modifier
                    .fillMaxWidth(0.9f)
                    .fillMaxHeight(0.2f),
                label = { Text(text = stringResource(R.string.post)) },
                colors = OutlinedTextFieldDefaults.colors(
                    focusedBorderColor = Color.Transparent,
                    unfocusedBorderColor = Color.Transparent,
                    cursorColor = MaterialTheme.colorScheme.primary,
                    focusedContainerColor = Color.Transparent,
                    unfocusedContainerColor = Color.Transparent
                ),
                keyboardOptions = KeyboardOptions(imeAction = ImeAction.Next),
                value = caption.value,
                onValueChange =   { caption.value = it },
            )
            Spacer(Modifier.fillMaxWidth(0.9f).height(1.dp).border(BorderStroke(1.dp,Color.LightGray)))
            Spacer(modifier = Modifier.height(20.dp))
            //Save btn
            ButtonComponent(stringResource(R.string.save), onButtonClicked = {
                if (caption.value.length>120){
                    viewModel.HandleException(custom = "Caption cannot be greater than 120 characters")
                }else{
                    viewModel.createPost(caption.value, LocalDateTime.now())
                    navController.popBackStack()
                }
            })
        }
    }

}