package com.example.instagram.utils

import androidx.compose.foundation.Image
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Search
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.LinearProgressIndicator
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import androidx.compose.material3.TextFieldDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.colorResource
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import com.example.instagram.InstaViewModel
import com.example.instagram.R
import com.example.instagram.data.Posts
import kotlinx.coroutines.delay

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SearchScreen(navController: NavController, viewModel: InstaViewModel) {

    val searchKeyword = remember { mutableStateOf("") }
    val searchedPost = viewModel.searchedposts.value
    val showResults = remember { mutableStateOf(false) }

    if (showResults.value) {
        LaunchedEffect(Unit) {
            delay(2000L)
            showResults.value = false
        }
    }

    Scaffold(topBar = {
        CustomTopBar(navController = navController, viewModel = viewModel, false)
    }, bottomBar = {
        BottomNavigation(navController, active = "Search")
    }) { it ->
        Column(
            Modifier
                .fillMaxSize()
                .padding(it), horizontalAlignment = Alignment.CenterHorizontally
        ) {

            TextField(value = searchKeyword.value,
                onValueChange = {
                    searchKeyword.value = it
                    if (searchKeyword.value.length > 3) {
                        //call search function in vm
                        showResults.value = true
                        viewModel.searchRelatedPosts(searchKeyword.value.trim())
                    }
                },
                label = { Text("Type....", color = Color.Gray) },
                colors = TextFieldDefaults.textFieldColors(
                    containerColor = colorResource(R.color.light_gray),
                    focusedTextColor = Color.Black,
                ),
                trailingIcon = {
                    Icon(Icons.Default.Search, contentDescription = "")
                },
                modifier = Modifier
                    .fillMaxWidth(0.9f)
                    .padding(top = 10.dp)
                    .clip(RoundedCornerShape(0.dp)),
                shape = RoundedCornerShape(0.dp)
            )
            if (showResults.value) {
                LinearProgressIndicator(
                    modifier = Modifier
                        .fillMaxWidth(0.9f)
                        .height(2.dp), color = Color.Black
                )
            }

            if (searchedPost.isNotEmpty()) {
                LazyVerticalGrid(
                    modifier = Modifier.fillMaxWidth(0.9f),
                    contentPadding = PaddingValues(top = 5.dp),
                    columns = GridCells.Fixed(3)
                ) {
                    items(count = searchedPost.size, itemContent = { index ->
                        val post = searchedPost[index]
                        Image(painter = painterResource(getImage()),
                            contentDescription = "",
                            contentScale = ContentScale.FillBounds,
                            modifier = Modifier
                                .height(300.dp)
                                .padding(3.dp)
                                .clickable {
                                    viewModel.post.value = post as Posts?
                                    navController.navigate(route = "ViewPost")
                                }

                        )

                    })
                }
            }

        }


    }
}
