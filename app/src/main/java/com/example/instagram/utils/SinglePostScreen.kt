package com.example.instagram.utils

import androidx.compose.animation.AnimatedContent
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.ExperimentalAnimationApi
import androidx.compose.animation.animateColorAsState
import androidx.compose.animation.core.tween
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.scaleIn
import androidx.compose.animation.scaleOut
import androidx.compose.animation.with
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.gestures.detectTapGestures
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Favorite
import androidx.compose.material.icons.filled.FavoriteBorder
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.outlined.ModeComment
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.OutlinedCard
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.livedata.observeAsState
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import coil.compose.AsyncImage
import com.example.instagram.InstaViewModel
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch


@OptIn(ExperimentalAnimationApi::class)
@Composable
fun SinglePostScreen(navController: NavController, viewModel: InstaViewModel) {
    val post = viewModel.post.observeAsState()
    val image = remember { mutableStateOf(post.value?.userProfileImage) }
    val coroutine = rememberCoroutineScope()
    var selectedItem by remember { mutableStateOf("ViewPost") }
    val btnText =
        remember { mutableStateOf(if (viewModel.isFollowing(post.value?.uid)) "Following" else "Follow") }
    val isFavourite = remember {
        mutableStateOf(
             post.value?.likes!!.contains(
                viewModel.userData.value?.userId
            )
        )
    }
    val color = animateColorAsState(
        targetValue = if (isFavourite.value)
            Color.Red else Color.Black,
        animationSpec = tween(
            durationMillis = 500, // Animation duration
            // Custom easing
        )
    )
    val followColor = animateColorAsState(
        targetValue = if (btnText.value=="Follow")
            Color.Blue else Color.Gray,
        animationSpec = tween(
            durationMillis = 500, // Animation duration
            // Custom easing
        )
    )

    val likeCount = remember { mutableStateOf(post.value?.likes?.size) }
    val commentCount = remember { mutableStateOf(post.value?.comments?.size) }
    val likedByGesture = remember{mutableStateOf(false)}

    Scaffold(topBar = {
        CustomTopBar(navController = navController, viewModel = viewModel, true)
    },
        bottomBar = {
            BottomNavigationBar( selectedItem, onItemSelected = {
                selectedItem = it
                navController.navigate(route = it)
            })    }
    ) {
        Column(
            Modifier
                .fillMaxSize()
                .padding(it),
        ) {
            // USER PROFILE WITH USER NAME
            Row(
                modifier = Modifier
                    .fillMaxWidth(0.8f)
                    .padding(start = 40.dp, top = 40.dp),
                horizontalArrangement = Arrangement.Start,
                verticalAlignment = Alignment.CenterVertically
            ) {
                //USER PROFILE
                OutlinedCard(shape = CircleShape) {
                    if (image.value != "null") {
                        AsyncImage(
                            model = getImage(),
                            contentDescription = "",
                            modifier = Modifier
                                .size(50.dp)
                                .clip(CircleShape),
                            contentScale = ContentScale.FillBounds
                        )
                    } else {
                        Icon(
                            Icons.Default.Person,
                            contentDescription = "",
                            modifier = Modifier.size(50.dp),
                            tint = Color.Black
                        )
                    }
                }
                Spacer(
                    modifier = Modifier
                        .fillMaxWidth(0.05f)
                        .fillMaxHeight(0.04f)
                )
                //USERNAME
                Text(
                    "${post.value?.userName}",
                    fontWeight = FontWeight.Bold,
                    color = Color.Black,
                    fontSize = 24.sp
                )
                //FOLLOW BUTTON IF USERNAME IS NOT CURR USER
                if (post.value?.userName != viewModel.userData.value?.userName) {
                    AnimatedContent(targetState = btnText,transitionSpec = {
                        fadeIn(animationSpec = tween(300)) with fadeOut(
                            animationSpec = tween(
                                300
                            )
                        )
                    }) {targetValue->
                        Button(
                            onClick = {
                                if (targetValue.value == "Follow") {
                                    targetValue.value = "Following"
                                    viewModel.addFollowing(post.value?.uid)
                                } else {
                                    targetValue.value = "Follow"
                                    viewModel.removeFollowing(post.value?.uid)
                                }
                            },
                            shape = RoundedCornerShape(0.dp),
                            modifier = Modifier.height(50.dp),
                            colors = ButtonDefaults.buttonColors(
                                Color.Transparent
                            )
                        ) {
                            Text(
                                text = targetValue.value,
                                fontSize = 20.sp,
                                color = followColor.value,
                                fontWeight = FontWeight.Bold,
                            )
                        }
                    }

                }

            }
            //POST IMAGE
            Box(contentAlignment = Alignment.Center) {
                AsyncImage(
                    model = getImage(),
                    contentDescription = "",
                    Modifier
                        .fillMaxWidth()
                        .fillMaxHeight(0.5f)
                        .padding(start = 40.dp, end = 40.dp, top = 20.dp)
                        .border(1.dp, Color.Black)
                        .background(Color.Black)
                        .pointerInput(Unit) {
                            detectTapGestures(onDoubleTap = {
                                if (!isFavourite.value) {
                                    isFavourite.value = true
                                    likeCount.value = likeCount.value?.plus(1)

                                }
                                likedByGesture.value=true
                                coroutine.launch{
                                    delay(1000L)
                                    likedByGesture.value = false
                                }
                                //viewModel.likedByDoubleTap(post.value)
                                viewModel.likePost(post.value)
                            })
                        },
                    contentScale = ContentScale.FillBounds
                )
                Box(
                    modifier = Modifier
                        .matchParentSize() // Ensure it occupies the entire space of the Box
                        .padding(16.dp),
                    contentAlignment = Alignment.Center // Optional padding around the animated element
                ) {
                    this@Column.AnimatedVisibility(
                        likedByGesture.value==true,
                        enter = fadeIn(animationSpec = tween(durationMillis = 300)) + scaleIn(
                            animationSpec = tween(durationMillis = 300)
                        ),
                        exit = fadeOut(animationSpec = tween(durationMillis = 300)) + scaleOut(
                            animationSpec = tween(durationMillis = 300)
                        )
                    ) {
                        Icon(
                            Icons.Default.Favorite,
                            tint = Color.White,
                            contentDescription = "",
                            modifier = Modifier.size(200.dp)
                        )
                    }
                }
            }
            //ICONS
            Row(
                modifier = Modifier
                    .fillMaxWidth(0.8f)
                    .padding(start = 50.dp),
                horizontalArrangement = Arrangement.Start,
                verticalAlignment = Alignment.CenterVertically
            ) {
                //LIKE BUTTON
                AnimatedContent(
                    targetState = isFavourite,
                    transitionSpec = {
                        fadeIn(animationSpec = tween(300)) with fadeOut(
                            animationSpec = tween(
                                300
                            )
                        )
                    }
                ) { targetValue ->
                    IconButton(onClick = {
                        if (!targetValue.value) {
                            viewModel.likePost(post.value)
                            likeCount.value = likeCount.value?.plus(1)

                        } else {
                            viewModel.dislikePost(post.value!!)
                            likeCount.value = likeCount.value?.minus(1)

                        }
                        isFavourite.value = !isFavourite.value
                    }) {
                        Icon(
                            imageVector = if (targetValue.value)
                                Icons.Default.Favorite else Icons.Default.FavoriteBorder,
                            contentDescription = "",
                            modifier = Modifier.size(35.dp),
                            tint = color.value
                        )
                    }
                }
                Spacer(Modifier.size(10.dp))
                //LIKE COUNT
                AnimatedContent(targetState = likeCount, transitionSpec = {
                    fadeIn(animationSpec = tween(300)) with fadeOut(animationSpec = tween(300))
                }
                ) { targetValue ->
                    Text("${targetValue.value}", fontSize = 24.sp, color = Color.Black)
                }
                Spacer(Modifier.size(20.dp))
                //COMMENT ICON
                IconButton(onClick = {
                    viewModel.retrieveComments(post.value!!)
                    navController.navigate(route = "CommentSection")
                }) {
                    Icon(
                        Icons.Outlined.ModeComment,
                        contentDescription = "",
                        modifier = Modifier.size(35.dp)

                    )
                }
                Spacer(Modifier.size(10.dp))
                //comment COUNT
                AnimatedContent(targetState = commentCount, transitionSpec = {
                    fadeIn(animationSpec = tween(300)) with fadeOut(animationSpec = tween(300))
                }) {targetValue->
                    Text(
                        "${targetValue.value}",
                        fontSize = 24.sp,
                        color = Color.Black
                    )

                }

            }
            Spacer(Modifier.size(10.dp))
            Row {
                //USERNAME
                Text(
                    text = "${post.value?.userName}",
                    modifier = Modifier.padding(start = 40.dp),
                    fontWeight = FontWeight.Bold,
                    fontSize = 25.sp
                )
                //CAPTION
                Text(
                    text = if (post.value != null) post.value!!.caption else "",
                    modifier = Modifier
                        .fillMaxWidth(0.6f)
                        .padding(start = 5.dp),
                    fontSize = 25.sp,
                    style = TextStyle.Default
                )
            }
            Spacer(Modifier.size(30.dp))
            //TIMESTAMP
            Text(
                text =
                if (post.value != null) viewModel.formatTimestamp(post.value!!.timestamp) else "",
                modifier = Modifier
                    .fillMaxWidth(0.6f)
                    .padding(start = 40.dp),
                fontSize = 14.sp,
                color = Color.Gray
            )
        }
    }
}

