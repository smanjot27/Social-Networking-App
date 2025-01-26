package com.example.instagram.utils

import androidx.compose.animation.ExperimentalAnimationApi
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.livedata.observeAsState
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.navigation.NavController
import com.example.instagram.InstaViewModel


@OptIn(ExperimentalAnimationApi::class)
@Composable
fun SinglePostScreen(navController: NavController, viewModel: InstaViewModel) {
    val post = viewModel.post.observeAsState()
    var selectedItem by remember { mutableStateOf("ViewPost") }
    Scaffold(topBar = {
        TopBar("Post")
    },
        bottomBar = {
            BottomNavigationBar( selectedItem, onItemSelected = {
                selectedItem = it
                navController.navigate(route = it)
            })
        }
    ) {
        Column(
            Modifier
                .padding(it),
        ) {
            post.value?.let {
                PostCard(post.value!!,viewModel,navController,false)
            }
        }
    }
}

