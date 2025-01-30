package com.example.instagram.utils

import androidx.compose.animation.ExperimentalAnimationApi
import androidx.compose.foundation.background
import androidx.compose.foundation.border
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
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.ThumbUp
import androidx.compose.material3.Card
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
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


@OptIn(ExperimentalAnimationApi::class)
@Composable
fun SinglePostScreen(navController: NavController, viewModel: InstaViewModel) {
    val post = viewModel.post.observeAsState()
    var selectedItem by remember { mutableStateOf("ViewPost") }

    Scaffold(topBar = {
        TopBar("Post")
    },
        bottomBar = {
            BottomNavigationBar(selectedItem, onItemSelected = {
                selectedItem = it
                navController.navigate(route = it)
            })
        },
    ) {
        Column(
            Modifier
                .padding(it).background(Color.White),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            post.value?.let {
                PostCard(post.value!!, viewModel, navController, false)
            }
            CommentScreen(navController, viewModel)
/*if (isLoading) {
                Spinner()
            } else {
                LazyColumn(Modifier.fillMaxWidth(0.9f)) {
                    items(comments.size) { index ->
                        showComment(comments[index])
                    }
                }
            }
            Spacer(modifier = Modifier.weight(1f))

            OutlinedTextField(
                modifier = Modifier
                    .fillMaxWidth(0.9f)
                    .height(58.dp)
                    .border(
                        BorderStroke(1.dp, Color.LightGray),
                        shape = RoundedCornerShape(20.dp)
                    ),
                label = { Text(text = stringResource(R.string.comment)) },
                colors = OutlinedTextFieldDefaults.colors(
                    focusedBorderColor = Color.Transparent,
                    unfocusedBorderColor = Color.Transparent,
                    cursorColor = MaterialTheme.colorScheme.primary,
                    focusedContainerColor = Color.Transparent,
                    unfocusedContainerColor = Color.Transparent
                ),
                keyboardOptions = KeyboardOptions(imeAction = ImeAction.Next),
                value = commentValue.value,
                onValueChange = {commentValue.value=it },
                trailingIcon = {
                    IconButton(onClick = {
                        viewModel.addComment(pos, commentValue.value, LocalDateTime.now())
                        focusManager.clearFocus()
                        commentValue.value = ""
                    }) {
                        Icon(Icons.Default.Send, contentDescription = "")
                    }
                }
            )*/
        }
    }
}

@Composable
fun showComment(comment: Comment) {
    var likeCount = rememberSaveable{mutableStateOf(0)}
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 4.dp),
        shape = RoundedCornerShape(12.dp),
    ) {
        Column(modifier = Modifier.background(colorResource(R.color.whitish)).padding(6.dp)) {
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
                }else{
                    Icon(
                        Icons.Default.Person,
                        contentDescription = "",
                        modifier = Modifier
                            .size(40.dp).border(1.dp, Color.Gray, CircleShape).padding(2.dp)
                            .clip(CircleShape),
                        tint = Color.Black
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

            Spacer(modifier = Modifier.height(12.dp))
            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceBetween,
                modifier = Modifier.fillMaxWidth()
            ) {
                Row {
                    IconButton(onClick = { likeCount.value++},
                        modifier = Modifier.padding(horizontal=4.dp).size(16.dp)
                    ) {
                        Icon(
                            imageVector = Icons.Default.ThumbUp, // Replace with actual thumbs up icon
                            contentDescription = "Like",
                        )
                    }
                    Spacer(modifier = Modifier.width(8.dp))
                    Text(text = likeCount.value.toString())
                    Spacer(modifier = Modifier.width(8.dp))
                }
            }
        }
    }
}

