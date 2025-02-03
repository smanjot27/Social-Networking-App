package com.example.instagram.utils

import androidx.compose.animation.ExperimentalAnimationApi
import androidx.compose.animation.animateColorAsState
import androidx.compose.animation.core.tween
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Favorite
import androidx.compose.material.icons.filled.FavoriteBorder
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.ThumbUp
import androidx.compose.material3.Card
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.livedata.observeAsState
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.colorResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import coil.compose.AsyncImage
import com.example.instagram.InstaViewModel
import com.example.instagram.R
import com.example.instagram.data.Comment
import com.example.instagram.ui.theme.lightGray


@OptIn(ExperimentalAnimationApi::class)
@Composable
fun SinglePostScreen(navController: NavController, viewModel: InstaViewModel) {
    val post = viewModel.post.observeAsState()
    var selectedItem by remember { mutableStateOf("ViewPost") }

    Scaffold(
        topBar = {
            TopBar("Post")
        },
        bottomBar = {
            BottomNavigationBar(selectedItem, onItemSelected = {
                selectedItem = it
                navController.navigate(route = it)
            })
        },
        containerColor = if (isSystemInDarkTheme()) Color.Black else lightGray
    ) {
        Column(
            Modifier
                .padding(it),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            post.value?.let {
                PostCard(post.value!!, viewModel, navController, false)
            }
            CommentScreen(navController, viewModel)
        }
    }
}

@Composable
fun showComment(comment: Comment) {
    var likeCount = rememberSaveable { mutableStateOf(0) }
    val isFavourite = remember {
        mutableStateOf(
            false)
    }
    val color = animateColorAsState(
        targetValue = if (isFavourite.value)
            Color.Red else MaterialTheme.colorScheme.onBackground,
        animationSpec = tween(
            durationMillis = 500, // Animation duration
            // Custom easing
        )
    )
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 4.dp),
        shape = RoundedCornerShape(12.dp),
    ) {
        Row(
            modifier = Modifier.background(color = MaterialTheme.colorScheme.background)
                .padding(6.dp)
        ) {
            Row(
                verticalAlignment = Alignment.CenterVertically
            ) {
                if (comment.userProfileImage != "null") {
                    AsyncImage(
                        model = getImage(),
                        contentDescription = "Profile Image",
                        modifier = Modifier
                            .size(25.dp).border(1.dp, Color.Gray, CircleShape).padding(2.dp)
                            .clip(CircleShape),
                        contentScale = ContentScale.FillBounds
                    )
                } else {
                    Icon(
                        Icons.Default.Person,
                        contentDescription = "",
                        modifier = Modifier
                            .size(40.dp).border(1.dp, Color.Gray, CircleShape).padding(2.dp)
                            .background(MaterialTheme.colorScheme.background)
                            .clip(CircleShape),
                        tint = MaterialTheme.colorScheme.onBackground
                    )
                }
                Spacer(modifier = Modifier.width(8.dp))
                Column {
                    Spacer(modifier = Modifier.height(2.dp))
                    Text(
                        text = comment.userName,
                        fontWeight = FontWeight.Bold,
                        fontSize = 16.sp
                    )
                    Spacer(modifier = Modifier.height(4.dp))
                    Text(
                        text = comment.comment,
                        fontSize = 14.sp,
                    )
                }
            }

            Spacer(modifier = Modifier.width(10.dp))
            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceBetween,
                modifier = Modifier.fillMaxWidth()
            ) {
                Row (modifier = Modifier.padding(vertical = 6.dp)){
                    IconButton(
                        onClick = { likeCount.value++
                            isFavourite.value = !isFavourite.value},
                        modifier = Modifier.padding(top=2.dp).size(16.dp)
                    ) {
                        Icon(
                            imageVector = if (isFavourite.value)
                                Icons.Default.Favorite else Icons.Default.FavoriteBorder, // Replace with actual thumbs up icon
                            contentDescription = "Like", tint = color.value,
                        )
                    }
                    Spacer(modifier = Modifier.width(10.dp))
                    Text(text = likeCount.value.toString(), )
                    Spacer(modifier = Modifier.width(8.dp))
                }
            }
        }
    }
}

