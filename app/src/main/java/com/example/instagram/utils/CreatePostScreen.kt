package com.example.instagram.utils

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import androidx.compose.material3.TextFieldDefaults
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.colorResource
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import coil.compose.AsyncImage
import com.example.instagram.InstaViewModel
import com.example.instagram.R
import java.time.LocalDateTime

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun CreatePostScreen(navController: NavController,viewModel: InstaViewModel){
    var caption = rememberSaveable { mutableStateOf("") }
    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    Row(
                        Modifier
                            .fillMaxWidth()
                            .padding(start = 10.dp, end = 10.dp),
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        Text(
                            "Back",
                            modifier = Modifier.clickable { navController.popBackStack() },
                            fontSize = 18.sp
                        )
                        Text("Create Post", fontSize = 20.sp)
                        Text(
                            "Post",
                            modifier = Modifier
                                .clickable {
                                    if (caption.value.length>120){
                                        viewModel.HandleException(custom = "Caption cannot be greater than 120 characters")
                                    }else{
                                        viewModel.createPost(caption.value, LocalDateTime.now())
                                        navController.popBackStack()
                                    }
                                }
                                .padding(end = 10.dp),
                            fontSize = 18.sp
                        )

                    }
                }
            )
        }
    ){
        Column(Modifier.fillMaxSize().padding(it),
            horizontalAlignment = Alignment.CenterHorizontally){
            AsyncImage(
                model = getImage(),
                contentDescription = "",
                Modifier
                    .fillMaxHeight(0.4f).fillMaxWidth(0.6f)
                    .padding(20.dp)
                    .border(1.dp, Color.Black)
                    .background(Color.Gray),
                contentScale = ContentScale.FillBounds
            )
            TextField(
                value = caption.value,
                onValueChange = { caption.value = it },
                Modifier.fillMaxWidth()
                    .padding(start=20.dp,end = 20.dp),
                colors = TextFieldDefaults.textFieldColors(
                    containerColor = colorResource(R.color.light_gray),
                    focusedTextColor = Color.Black,
                    focusedIndicatorColor = Color.Black

                ),
                textStyle = TextStyle(fontSize = 20.sp),
                singleLine = false,
                placeholder = {
                    Text(
                        "Share your thoughts...",
                        color = Color.Gray,
                        fontSize = 16.sp
                    )
                },
                maxLines = 2,
                shape = TextFieldDefaults.shape
                )
        }
    }

}