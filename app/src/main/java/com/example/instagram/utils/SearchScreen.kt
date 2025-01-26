package com.example.instagram.utils

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.staggeredgrid.LazyVerticalStaggeredGrid
import androidx.compose.foundation.lazy.staggeredgrid.StaggeredGridCells
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
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import coil.compose.rememberAsyncImagePainter
import com.example.instagram.InstaViewModel
import com.example.instagram.data.Posts
import kotlinx.coroutines.delay

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SearchScreen(navController: NavController, viewModel: InstaViewModel) {

    var selectedItem by remember { mutableStateOf("Search") }

    var searchKeyword by remember { mutableStateOf("") }
    val searchedPost = remember { viewModel.searchedposts.value }
    var showResults by remember { mutableStateOf(false) }

    if (showResults) {
        LaunchedEffect(Unit) {
            delay(2000L)
            showResults = false
        }
    }

    Scaffold(bottomBar = {
        BottomNavigationBar(selectedItem, onItemSelected = {
            selectedItem = it
            navController.navigate(route = it)
        })
    }) { it ->
        Column(
            Modifier
                .fillMaxSize()
                .padding(it), horizontalAlignment = Alignment.CenterHorizontally
        ) {
            SearchBar(
                searchKeyword, onValueChanged = {
                    searchKeyword = it
                    if (searchKeyword.length > 2) {
                        //call search function in vm
                        showResults = true
                        viewModel.searchRelatedPosts(searchKeyword.trim())
                    }
                }, showResults
            )
            if (searchedPost.isNotEmpty()) {
                ImageGrid(searchedPost, navController = navController, viewModel)
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SearchBar(searchKeyword: String, onValueChanged: (String) -> Unit, showResults: Boolean) {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(16.dp)
    ) {
        TextField(
            value = searchKeyword,
            onValueChange = onValueChanged,
            placeholder = { Text("What are you thinking today?") },
            leadingIcon = { Icon(Icons.Default.Search, "", tint = Color.Gray) },
            colors = TextFieldDefaults.textFieldColors(
                containerColor = Color(0xFFF2F2F2), // Light gray background
                focusedIndicatorColor = Color.Transparent, // Removes underline
                unfocusedIndicatorColor = Color.Transparent,
                disabledIndicatorColor = Color.Transparent
            ),
            shape = RoundedCornerShape(20.dp), // Curved edges
            modifier = Modifier.fillMaxWidth()
        )
        if (showResults) {
            LinearProgressIndicator(
                modifier = Modifier
                    .fillMaxWidth().height(2.dp)
                    .padding(horizontal = 16.dp),
                color = Color.Black
            )
        }
    }
}

@Composable
fun ImageGrid(searchedPost: List<Posts>, navController: NavController, viewModel: InstaViewModel) {
    val items = remember {
        List(searchedPost.size) { (100..300).random().dp } // Random heights, but fixed during the lifecycle
    }

    LazyVerticalStaggeredGrid(
        modifier = Modifier.padding(start = 5.dp, end = 5.dp).fillMaxSize(),
        columns = StaggeredGridCells.Fixed(2), // Dynamically adapt columns based on screen width
        contentPadding = PaddingValues(0.dp),
        verticalItemSpacing = 8.dp,
        horizontalArrangement = Arrangement.spacedBy(8.dp)
    ) {
        items(searchedPost.size) { index ->
            val post = searchedPost[index]
            Box(
                modifier = Modifier
                    .height(items[index])
                    .clip(RoundedCornerShape(8.dp))
                    .background(Color.LightGray)
            ) {
                Image(
                    painter = rememberAsyncImagePainter(getImage()),
                    contentDescription = "Grid Image $index",
                    contentScale = ContentScale.Crop,
                    modifier = Modifier.fillMaxSize().clickable {
                        viewModel.post.value = post as Posts?
                        navController.navigate(route = "ViewPost")
                    }
                )
            }
        }
    }
}
