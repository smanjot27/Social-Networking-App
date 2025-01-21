package com.example.instagram.utils

import android.app.Activity
import android.util.Log
import androidx.activity.compose.BackHandler
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
import androidx.compose.foundation.clickable
import androidx.compose.foundation.gestures.detectTapGestures
import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Favorite
import androidx.compose.material.icons.filled.FavoriteBorder
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.outlined.ModeComment
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.OutlinedCard
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.colorResource
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import coil.compose.AsyncImage
import com.example.instagram.InstaViewModel
import com.example.instagram.R
import com.example.instagram.data.Posts
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

@OptIn(ExperimentalAnimationApi::class)
@Composable
fun FeedScreen(navController: NavController, viewModel: InstaViewModel) {
    val context = LocalContext.current
    val isPicVisible = remember { mutableStateOf(false) }
    viewModel.fetchFollowingData()
    viewModel.getFeedPosts(viewModel.userData.value?.userId.toString())
    val followingData = viewModel.followingDetails.collectAsState()
    val feedPosts = viewModel.feedPosts.value

    Log.d("FollowingDetails", "followingDetails: $followingData")

    LaunchedEffect(Unit) {
        delay(1000L)
        isPicVisible.value = true
    }
    val coroutine = rememberCoroutineScope()

    BackHandler {
        if (context is Activity) {
            context.finish()
        }
    }

    Scaffold(topBar = {
        CustomTopBar(navController = navController, viewModel = viewModel, false)
    },
        bottomBar = {
            BottomNavigation(navController, active = "Feed")
        }
    ) { it ->
        Column(
            Modifier
                .fillMaxSize()
                .padding(it)
                .background(colorResource(R.color.light_gray)),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Row(
                horizontalArrangement = Arrangement.Start,
                modifier = Modifier
                    .fillMaxWidth(0.95f)
                    .padding(top = 20.dp, bottom = 10.dp)
                    .horizontalScroll(rememberScrollState())
            ) {
                Column(
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    if (isPicVisible.value) {
                        OutlinedCard(shape = CircleShape) {
                            if (viewModel.userData.value?.image != "null") {
                                AsyncImage(
                                    model = getImage(), contentDescription = "",
                                    modifier = Modifier
                                        .size(70.dp)
                                        .clip(CircleShape),
                                    contentScale = ContentScale.FillBounds
                                )
                            } else {
                                Icon(
                                    Icons.Default.Person,
                                    contentDescription = "",
                                    modifier = Modifier.size(70.dp),
                                    tint = Color.Black
                                )
                            }
                        }
                    } else {
                        OutlinedCard(
                            shape = CircleShape,
                            modifier = Modifier
                                .size(70.dp),
                        ) {
                            Box(Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                                CircularProgressIndicator(modifier = Modifier.size(30.dp))
                            }
                        }
                    }
                    Text(
                        "${viewModel.userData.value?.userName}",
                        fontSize = 16.sp,
                        fontWeight = FontWeight.Bold,
                        modifier = Modifier.padding(top = 5.dp),
                        textAlign = TextAlign.Center
                    )
                }
                Spacer(Modifier.size(20.dp))
                followingData.value.forEach { (username, hasImage) ->
                    Column(
                        horizontalAlignment = Alignment.CenterHorizontally
                    ) {
                        OutlinedCard(shape = CircleShape) {
                            if (!hasImage) {
                                AsyncImage(
                                    model = getImage(), contentDescription = "",
                                    modifier = Modifier
                                        .size(70.dp)
                                        .clip(CircleShape),
                                    contentScale = ContentScale.FillBounds
                                )
                            } else {
                                Icon(
                                    Icons.Default.Person,
                                    contentDescription = "",
                                    modifier = Modifier.size(70.dp),
                                    tint = Color.Black
                                )
                            }
                        }

                        Text(
                            username,
                            fontSize = 16.sp,
                            fontWeight = FontWeight.Bold,
                            modifier = Modifier.padding(top = 5.dp),
                            textAlign = TextAlign.Center
                        )
                    }
                    Spacer(Modifier.size(20.dp))
                }
            }

            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .border(1.dp, colorResource(R.color.light_gray))
                    .verticalScroll(rememberScrollState()),
            ) {
                if (feedPosts.isNotEmpty()) {
                    Log.d("check", "Posts found: ${feedPosts.size}")
                } else {
                    Log.d("check", "No posts found in the following users.")
                }
                feedPosts.forEach { post ->
                    val isFavourite = remember {
                        mutableStateOf(
                            post.likes.contains(
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
                    val likeCount = remember{mutableStateOf(post.likes.size)}
                    val likedByGesture = remember{mutableStateOf(false)}
                    val commentCount = remember { mutableStateOf(post.comments.size) }

                    Box {
                        Card(
                            Modifier
                                .fillMaxHeight(0.8f)
                                .padding(10.dp)
                                .clickable {
                                    viewModel.post.value = post as Posts?
                                    navController.navigate(route = "ViewPost")
                                },
                            shape = RoundedCornerShape(0.dp),
                            colors = CardDefaults.cardColors(
                                containerColor = Color.White
                            ),
                            elevation = CardDefaults.cardElevation(1.dp),
                        ) {
                            Row(
                                modifier = Modifier
                                    .fillMaxWidth(0.8f)
                                    .padding(10.dp),
                                horizontalArrangement = Arrangement.Start,
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                //USER PROFILE
                                OutlinedCard(shape = CircleShape) {
                                    if (post.userProfileImage != "null") {
                                        AsyncImage(
                                            model = getImage(), contentDescription = "",
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
                                        .fillMaxWidth(0.03f)
                                        .fillMaxHeight(0.04f)
                                )
                                //USERNAME
                                Text(
                                    post.userName,
                                    fontWeight = FontWeight.Bold,
                                    color = Color.Black,
                                    fontSize = 24.sp
                                )

                            }
                            //POST IMAGE
                            Box(contentAlignment = Alignment.Center) {
                                AsyncImage(
                                    model = getImage(),
                                    contentDescription = "",
                                    Modifier
                                        .fillMaxWidth()
                                        .fillMaxHeight(0.5f)
                                        .padding(5.dp)
                                        .pointerInput(Unit) {
                                            detectTapGestures(
                                                onDoubleTap = {
                                                    if(!isFavourite.value) {
                                                        isFavourite.value = true
                                                        likeCount.value = likeCount.value.plus(1)
                                                    }
                                                    likedByGesture.value=true
                                                    coroutine.launch{
                                                        delay(1000L)
                                                        likedByGesture.value = false
                                                    }
                                              //      viewModel.likedByDoubleTap(post)
                                                    viewModel.likePost(post)

                                                }
                                            )
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
                                        likedByGesture.value == true,
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
                                    .padding(start = 10.dp),
                                horizontalArrangement = Arrangement.Start,
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                //LIKE BUTTON
                                AnimatedContent(
                                    targetState = isFavourite,
                                    transitionSpec = {
                                        fadeIn(animationSpec = tween(300)) with fadeOut(
                                            animationSpec = tween(300)
                                        )
                                    }
                                ) { targetValue ->
                                    IconButton(onClick = {
                                        if (!targetValue.value) {
                                            viewModel.likePost(post)
                                            likeCount.value = likeCount.value.plus(1)

                                        } else {
                                            viewModel.dislikePost(post)
                                            likeCount.value = likeCount.value.minus(1)

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
                                Spacer(Modifier.size(5.dp))
                                //LIKE COUNT
                                AnimatedContent(targetState = likeCount, transitionSpec = {
                                    fadeIn(animationSpec = tween(300)) with fadeOut(
                                        animationSpec = tween(
                                            300
                                        )
                                    )
                                }
                                ) { targetValue ->
                                    Text(
                                        "${targetValue.value}",
                                        fontSize = 24.sp,
                                        color = Color.Black
                                    )
                                }
                                Spacer(Modifier.size(10.dp))
                                //COMMENT ICON
                                IconButton(onClick = {
                                    viewModel.post.value = post as Posts?
                                    navController.navigate(route = "ViewPost")

                                }) {
                                    Icon(
                                        Icons.Outlined.ModeComment,
                                        contentDescription = "",
                                        modifier = Modifier.size(30.dp)

                                    )
                                }
                                Spacer(Modifier.size(10.dp))
                                //comment COUNT
                                AnimatedContent(targetState = commentCount, transitionSpec = {
                                    fadeIn(animationSpec = tween(300)) with fadeOut(
                                        animationSpec = tween(
                                            300
                                        )
                                    )
                                }) {targetValue->
                                    Text(
                                        "${targetValue.value}",
                                        fontSize = 24.sp,
                                        color = Color.Black
                                    )
                                }
                            }
                            Spacer(Modifier.size(10.dp))
                            Row(Modifier.padding(start = 10.dp)) {
                                //USERNAME
                                Text(
                                    text = post.userName,
                                    modifier = Modifier.padding(start = 10.dp),
                                    fontWeight = FontWeight.Bold,
                                    fontSize = 24.sp
                                )
                                //CAPTION
                                Text(
                                    text = post.caption,
                                    modifier = Modifier
                                        .fillMaxWidth(0.6f)
                                        .padding(start = 15.dp),
                                    fontSize = 24.sp,
                                    style = TextStyle.Default
                                )
                                Spacer(Modifier.size(50.dp))
                            }
                        }
                    }
                }
            }
        }

    }
}