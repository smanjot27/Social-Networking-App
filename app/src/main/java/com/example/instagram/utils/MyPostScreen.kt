package com.example.instagram.utils

import android.net.Uri
import android.util.Log
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Person
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Icon
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.OutlinedCard
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
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

@Composable
fun MyPostScreen(navController: NavController, viewModel: InstaViewModel) {
    val currUser = viewModel.userData.value
    val posts = viewModel.currUserposts.value


    Log.i("Instagram check", "$currUser")
    Scaffold(topBar = {
        CustomTopBar(navController = navController, viewModel = viewModel,false)
    },
        bottomBar = {
            BottomNavigation(navController, active = "Posts")
        }
    ) { it ->
        //FULL SCREEN
        Column(
            Modifier
                .fillMaxSize()
                .padding(it),
            horizontalAlignment = Alignment.CenterHorizontally,
        ) {
            //PROFILE PIC POST COUNT FOLLOWERS FOLLOWINGS
            Row(
                verticalAlignment = Alignment.Top,
                horizontalArrangement = Arrangement.SpaceEvenly,
                modifier = Modifier.padding(top = 20.dp, start = 20.dp, end = 20.dp, bottom = 0.dp)
            ) {

                ShowProfilePic(currUser?.image, navController, viewModel)
                Row(
                    modifier = Modifier
                        .padding(top = 20.dp, start = 20.dp)
                        .fillMaxWidth(0.9f),
                    horizontalArrangement = Arrangement.SpaceEvenly
                ) {
                    Column {
                        Text(
                            "${if (currUser?.postCount != null) currUser.postCount else 0}",
                            fontSize = 18.sp,
                            textAlign = TextAlign.Center,
                            modifier = Modifier.width(60.dp)
                        )
                        Spacer(Modifier.size(5.dp))
                        Text(
                            text = "Posts",
                            fontSize = 18.sp,
                            textAlign = TextAlign.Center,
                            fontWeight = FontWeight.Bold,
                            modifier = Modifier.width(60.dp)
                        )
                    }

                    Column {
                        Text(
                            "${if (currUser?.Following != null) currUser.Following?.size else 0}",
                            fontSize = 18.sp,
                            modifier = Modifier.width(90.dp),
                            textAlign = TextAlign.Center
                        )
                        Spacer(Modifier.size(5.dp))
                        Text(
                            text = "Followings",
                            fontSize = 18.sp,
                            fontWeight = FontWeight.Bold,
                            modifier = Modifier.width(90.dp),
                            textAlign = TextAlign.Center
                        )
                    }

                    Column {
                        Text(
                            "${if (currUser?.Followers != null) currUser.Followers?.size else 0}",
                            fontSize = 18.sp,
                            modifier = Modifier.width(90.dp),
                            textAlign = TextAlign.Center
                        )
                        Spacer(Modifier.size(5.dp))
                        Text(
                            text = "Followers",
                            fontSize = 18.sp,
                            fontWeight = FontWeight.Bold,
                            modifier = Modifier.width(90.dp),
                            textAlign = TextAlign.Center
                        )

                    }
                }
            }
            Spacer(Modifier.size(10.dp))
            //USERNAME NAME BIO EDIT BUTTON
            Column(Modifier.fillMaxWidth(0.9f)) {
                Text(
                    "${if (currUser?.Name != "null") currUser?.Name else " "}",
                    fontSize = 18.sp,
                    fontWeight = FontWeight.Bold
                )
                Text("@${currUser?.userName}", fontSize = 18.sp)
                Text("${if (currUser?.BIO != "null") currUser?.BIO else " "}", fontSize = 16.sp)
                Spacer(Modifier.size(10.dp))
                OutlinedButton(
                    onClick = {
                        navController.navigate("EditProfile")
                    },
                    shape = RoundedCornerShape(0.dp),
                    modifier = Modifier
                        .height(35.dp)
                        .fillMaxWidth(),
                    colors = ButtonDefaults.buttonColors(colorResource(R.color.white)),
                ) { Text(text = "Edit Profile", color = Color.Black) }
            }
            //POSTS
            if (currUser?.postCount!=null && currUser.postCount?.toInt()!! > 0 && posts.isNotEmpty()) {
                LazyVerticalGrid(
                    modifier = Modifier.fillMaxWidth(0.9f),
                    contentPadding = PaddingValues(top = 5.dp),
                    columns = GridCells.Fixed(3)
                ) {
                    items(count = posts.size , itemContent = { index ->
                        val post = posts[index]
                        Log.i("Instagram Check :","Post showing $post")
                        Image(
                            painter = painterResource(getImage()), contentDescription = "",
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
            } else {
                Box(Modifier.fillMaxSize(0.9f), contentAlignment = Alignment.Center) {
                    Text("No Content Posted", color = Color.Gray, fontSize = 24.sp)
                }
            }
        }
    }
}

@Composable
fun ShowProfilePic(image: String?, navController: NavController, viewModel: InstaViewModel) {
    val imageUri = remember { mutableStateOf<Uri?>(if (image != null) Uri.parse(image) else null) }
    val launcher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.GetContent()
    ) { uri: Uri? ->
        imageUri.value = uri
        if (imageUri.value != null) {
            navController.navigate(route = "CreatePost")
        } else {
            viewModel.HandleException(custom = "No image selected. Select an image to create the post")
        }
    }
    val isPicVisible = remember { mutableStateOf(false) }
    LaunchedEffect(Unit) {
        delay(1000L)
        isPicVisible.value = true
    }
    Box(
        contentAlignment = Alignment.Center,
        modifier = Modifier.clickable {
            launcher.launch("image/*")

        }
    ) {
        if (isPicVisible.value) {
            OutlinedCard(shape = CircleShape) {
                if (image != "null") {
                    Log.i("Instagram Check", "image : $image  imageUri : $imageUri")
                    AsyncImage(
                        model = getImage(), contentDescription = "",
                        modifier = Modifier
                            .size(100.dp)
                            .clip(CircleShape),
                        contentScale = ContentScale.FillBounds
                    )
                } else {
                    Icon(
                        Icons.Default.Person,
                        contentDescription = "",
                        modifier = Modifier.size(100.dp),
                        tint = Color.Black
                    )
                }
            }

            OutlinedCard(
                shape = CircleShape,
                modifier = Modifier
                    .size(30.dp)
                    .align(alignment = Alignment.BottomEnd),
                border = BorderStroke(2.dp, Color.White)
            ) {
                Icon(
                    Icons.Default.Add,
                    contentDescription = "",
                    modifier = Modifier
                        .size(50.dp)
                        .background(Color.Blue),
                    tint = Color.White
                )
            }
        } else {
            OutlinedCard(
                shape = CircleShape,
                modifier = Modifier
                    .size(100.dp)
                    .align(alignment = Alignment.Center),

                ) {
                Box(Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                    CircularProgressIndicator(modifier = Modifier.size(30.dp))
                }
            }
        }
    }

}

fun getImage(): Int {
    return R.drawable.img
}
