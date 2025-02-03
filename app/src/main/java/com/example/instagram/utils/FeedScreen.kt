package com.example.instagram.utils

import android.annotation.SuppressLint
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
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.gestures.detectTapGestures
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.statusBars
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Favorite
import androidx.compose.material.icons.filled.FavoriteBorder
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.outlined.ChatBubbleOutline
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.material3.contentColorFor
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.State
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.derivedStateOf
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import coil.compose.AsyncImage
import coil.compose.rememberAsyncImagePainter
import com.example.instagram.InstaViewModel
import com.example.instagram.data.Posts
import com.example.instagram.ui.theme.lightGray
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
    val darkTheme = isSystemInDarkTheme()

    Log.d("FollowingDetails", "followingDetails: $followingData")

    LaunchedEffect(Unit) {
        delay(1000L)
        isPicVisible.value = true
    }

    BackHandler {
        if (context is Activity) {
            context.finish()
        }
    }
    var selectedItem by remember { mutableStateOf("Feed") }

    Scaffold(
        topBar = {
            TopBar("Home")
        },
        bottomBar = {
            BottomNavigationBar(selectedItem, onItemSelected = {
                selectedItem = it
                navController.navigate(route = it)
            })
        },
        containerColor = if(darkTheme) Color.Black else lightGray
    ) { it ->
        LazyColumn(
            Modifier
                .padding(it)
        ) {
            item {
                FollowerList(followingData)
                Spacer(modifier = Modifier.height(8.dp))
            }
            if (feedPosts.isNotEmpty()) {
                Log.d("check", "Posts found: ${feedPosts.size}")
            } else {
                Log.d("check", "No posts found in the following users.")
            }
            //  Post List
            items(feedPosts.size) { post ->
                PostCard(feedPosts[post], viewModel, navController, true)
            }
        }

    }
}


@Composable
fun FollowerList(followers: State<List<Pair<String, Boolean>>>) {
    LazyRow(
        contentPadding = PaddingValues(horizontal = 16.dp, vertical = 8.dp),
        horizontalArrangement = Arrangement.spacedBy(12.dp)
    ) {
        items(followers.value.size) { follower ->
            Column(
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                if (!followers.value[follower].second) {
                    Image(
                        painter = rememberAsyncImagePainter(getImage()),
                        contentDescription = "Profile Image",
                        modifier = Modifier
                            .size(70.dp)
                            .clip(CircleShape)
                            .border(3.dp, brush = getStoryBrush(), CircleShape).padding(3.dp)
                    )
                } else {
                    Icon(
                        Icons.Default.Person,
                        contentDescription = "",
                        modifier = Modifier
                            .size(70.dp)
                            .clip(CircleShape)
                            .border(3.dp, getStoryBrush(), CircleShape).padding(3.dp)
                            .background(MaterialTheme.colorScheme.background),
                        tint = MaterialTheme.colorScheme.onBackground
                    )
                }
                Text(
                    text = followers.value[follower].first,
                    fontSize = 14.sp,
                    fontWeight = FontWeight.Bold, color = MaterialTheme.colorScheme.onBackground
                )
            }
        }
    }
}

@SuppressLint("UnrememberedMutableState")
@OptIn(ExperimentalAnimationApi::class)
@Composable
fun PostCard(post: Posts, viewModel: InstaViewModel, navController: NavController, click: Boolean) {
    val coroutine = rememberCoroutineScope()

    val isFavourite = remember {
        mutableStateOf(
            post.likes.contains(
                viewModel.userData.value?.userId
            )
        )
    }

    val color = animateColorAsState(
        targetValue = if (isFavourite.value)
            Color.Red else MaterialTheme.colorScheme.onBackground,
        animationSpec = tween(
            durationMillis = 500, // Animation duration
            // Custom easing
        )
    )

    val likeCount = remember { mutableStateOf(post.likes.size) }
    val likedByGesture = remember { mutableStateOf(false) }
    val commentCount by derivedStateOf { post.comments.size }


    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(8.dp)
            .clip(RoundedCornerShape(16.dp))
            .background(color = MaterialTheme.colorScheme.background),
        verticalArrangement = Arrangement.Bottom,

        ) {
        // User Info (Profile Image + Name)
        Row(
            modifier = Modifier.padding(8.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            if (post.userProfileImage != "null") {
                AsyncImage(
                    model = getImage(),
                    contentDescription = "Profile Image",
                    modifier = Modifier
                        .size(40.dp).border(1.dp, getStoryBrush(), CircleShape).padding(2.dp)
                        .clip(CircleShape),
                    contentScale = ContentScale.FillBounds
                )
                Spacer(modifier = Modifier.width(8.dp))
            } else {
                Icon(
                    Icons.Default.Person,
                    contentDescription = "",
                    modifier = Modifier
                        .size(40.dp).border(1.dp, getStoryBrush(), CircleShape).padding(2.dp)
                        .background(MaterialTheme.colorScheme.background)
                        .clip(CircleShape),
                    tint = MaterialTheme.colorScheme.onBackground
                )
            }
            Spacer(modifier = Modifier.width(8.dp))
            // Username
            Text(
                text = post.userName,
                fontWeight = FontWeight.Bold,
                fontSize = 16.sp, color = MaterialTheme.colorScheme.onBackground,
            )
        }
        Box(modifier = Modifier.height(250.dp).fillMaxWidth().clickable {
            viewModel.post.value = post as Posts?
            viewModel.retrieveComments()
            navController.navigate(route = "ViewPost")
        }) {
            // Post Image
            AsyncImage(
                model = getImage(),
                contentDescription = "Post Image",
                modifier = Modifier
                    .fillMaxWidth()
                    .height(250.dp).padding(3.dp)
                    .clip(RoundedCornerShape(12.dp))
                    .pointerInput(Unit) {
                        detectTapGestures(
                            onDoubleTap = {
                                if (!isFavourite.value) {
                                    isFavourite.value = true
                                    likeCount.value = likeCount.value.plus(1)
                                }
                                likedByGesture.value = true
                                coroutine.launch {
                                    delay(1000L)
                                    likedByGesture.value = false
                                }
                                //      viewModel.likedByDoubleTap(post)
                                viewModel.likePost(post)
                            },
                        )
                    },
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
                        modifier = Modifier.fillMaxSize(0.5f)
                    )
                }
            }
            //likes and comments
            Row(
                modifier = Modifier
                    .fillMaxWidth().align(Alignment.BottomStart)
                    .padding(bottom = 8.dp, start = 16.dp),
                horizontalArrangement = Arrangement.Start,
            ) {
                //comment
                Box(
                    modifier = Modifier
                        .size(30.dp)
                        .clip(CircleShape)
                        .background(MaterialTheme.colorScheme.surfaceVariant)
                        .clickable {
                            if (click == true) {
                                viewModel.post.value = post as Posts?
                                viewModel.retrieveComments()
                                navController.navigate(route = "ViewPost")
                            } else null
                        },
                    contentAlignment = Alignment.Center
                ) {
                    Icon(
                        imageVector = Icons.Outlined.ChatBubbleOutline,
                        contentDescription = "Comment",
                        tint = MaterialTheme.colorScheme.onBackground,
                        modifier = Modifier.size(15.dp)
                    )
                }
                Spacer(modifier = Modifier.width(8.dp))

                //comment COUNT
                AnimatedContent(targetState = commentCount, transitionSpec = {
                    fadeIn(animationSpec = tween(300)) with fadeOut(
                        animationSpec = tween(
                            300
                        )
                    )
                }) { targetValue ->
                    Text(
                        "${targetValue}",
                        fontWeight = FontWeight.Bold,
                        fontSize = 14.sp,
                        modifier = Modifier.padding(top = 5.dp),
                        color = MaterialTheme.colorScheme.onBackground,
                    )
                }

                Spacer(modifier = Modifier.width(16.dp))
                //likes icon
                AnimatedContent(
                    targetState = isFavourite,
                    transitionSpec = {
                        fadeIn(animationSpec = tween(300)) with fadeOut(
                            animationSpec = tween(300)
                        )
                    }
                ) { targetValue ->
                    Box(
                        modifier = Modifier
                            .size(30.dp)
                            .clip(CircleShape)
                            .background(MaterialTheme.colorScheme.surfaceVariant)
                            .clickable {
                                if (!targetValue.value) {
                                    viewModel.likePost(post)
                                    likeCount.value = likeCount.value.plus(1)

                                } else {
                                    viewModel.dislikePost(post)
                                    likeCount.value = likeCount.value.minus(1)

                                }
                                isFavourite.value = !isFavourite.value
                            },
                        contentAlignment = Alignment.Center
                    ) {
                        Icon(
                            imageVector = if (targetValue.value)
                                Icons.Default.Favorite else Icons.Default.FavoriteBorder,
                            contentDescription = "Like",
                            tint = color.value,
                            modifier = Modifier.size(15.dp)
                        )
                    }
                }
                Spacer(modifier = Modifier.width(8.dp))

                //like count
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
                        fontWeight = FontWeight.Bold,
                        fontFamily = FontFamily.SansSerif,// Ensures italic support
                        fontSize = 14.sp,
                        color = MaterialTheme.colorScheme.onBackground,
                        modifier = Modifier.padding(top = 5.dp)
                    )
                }

                Spacer(modifier = Modifier.width(16.dp))
            }
        }
        Text(
            text = post.caption,
            fontSize = 14.sp,
            fontFamily = FontFamily.Serif,// Ensures italic support
            fontStyle = FontStyle.Italic,
            modifier = Modifier.padding(10.dp),
            color = MaterialTheme.colorScheme.onBackground // Padding inside the box
        )

    }
}
