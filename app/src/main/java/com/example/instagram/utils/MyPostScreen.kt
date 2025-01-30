package com.example.instagram.utils

import android.util.Log
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
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
import androidx.compose.foundation.lazy.staggeredgrid.LazyVerticalStaggeredGrid
import androidx.compose.foundation.lazy.staggeredgrid.StaggeredGridCells
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Logout
import androidx.compose.material.icons.filled.Person
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.OutlinedCard
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.material3.contentColorFor
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
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import coil.compose.rememberAsyncImagePainter
import com.example.instagram.InstaViewModel
import com.example.instagram.R
import com.example.instagram.data.Posts
import com.example.instagram.data.User
import kotlinx.coroutines.delay

@Composable
fun MyPostScreen(navController: NavController, viewModel: InstaViewModel) {
    var selectedItem by remember { mutableStateOf("Posts") }
    val currUser = viewModel.userData.value

    Log.i("Instagram check", "$currUser")

    Scaffold(
        topBar = {
            ProfileTopBar(currUser, viewModel)
        },
        bottomBar = {
            BottomNavigationBar(selectedItem, onItemSelected = {
                selectedItem = it
                navController.navigate(route = it)
            })
        },        containerColor = Color(0xFFF7F7F7)

    ) { paddingValues ->
        if (!viewModel.inProgress.value) {
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(paddingValues)
                    .background(Color.White)
            ) {
                // Profile Section
                ProfileSection(currUser)
                Spacer(modifier = Modifier.height(16.dp))
                // Edit and Contact Buttons
                EditAndContactButtons(navController)
                Spacer(modifier = Modifier.height(16.dp))
                // Segmented Controls
                //SegmentedControls()
                Spacer(modifier = Modifier.height(16.dp))
                // Grid Section
                ImageGrid(currUser, viewModel, navController)
            }
        } else {
            Spinner()
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ProfileTopBar(currUser: User?, viewModel: InstaViewModel) {
    TopAppBar(
        title = {
            Text(
                if (currUser != null && currUser.userName != "null") currUser.userName else "",
                fontWeight = FontWeight.Bold,
                fontSize = 20.sp,
                modifier = Modifier.fillMaxWidth(),
                textAlign = TextAlign.Center
            )
        },
        colors = TopAppBarDefaults.smallTopAppBarColors(
            containerColor = Color.White,
            contentColorFor(Color.Black)
        ),
        windowInsets = WindowInsets.statusBars,
        actions = {
            IconButton(
                onClick = {
                    viewModel.SignOut()
                },
            ) {
                Icon(
                    Icons.Filled.Logout,
                    contentDescription = "",
                    tint = Color.Black,
                )
            }
        },
        modifier = Modifier.clip(RoundedCornerShape(20.dp))
    )
}


@Composable
fun ProfileSection(currUser: User?) {
    val isPicVisible = remember { mutableStateOf(false) }
    LaunchedEffect(Unit) {
        delay(1000L)
        isPicVisible.value = true
    }

    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(16.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        // Profile Picture
        if (isPicVisible.value) {
            if (currUser?.image != "null") {
                Image(
                    painter = rememberAsyncImagePainter(getImage()), // Replace with a valid image URL
                    contentDescription = "Profile Picture",
                    modifier = Modifier
                        .size(80.dp)
                        .clip(CircleShape).border(3.dp, Color.Gray, CircleShape).padding(3.dp),
                    contentScale = ContentScale.Crop
                )
            } else {
                Icon(
                    Icons.Default.Person,
                    contentDescription = "",
                    modifier = Modifier.size(80.dp).clip(CircleShape)
                        .border(3.dp, Color.Gray, CircleShape).padding(3.dp),
                    tint = Color.Black
                )
            }
        } else {
            OutlinedCard(
                shape = CircleShape,
                modifier = Modifier
                    .size(80.dp).align(Alignment.CenterHorizontally)

            ) {
                Box(Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                    CircularProgressIndicator(modifier = Modifier.size(30.dp))
                }
            }
        }
        Spacer(modifier = Modifier.height(8.dp))
        // Username
        Text(
            text = if (currUser != null && currUser.Name!="null") currUser.Name ?: "" else "",
            fontWeight = FontWeight.Bold,
            fontSize = 20.sp
        )
        Spacer(modifier = Modifier.height(4.dp))
        // Bio
        Text(
            text = if (currUser != null)
                if (currUser.BIO != "null") currUser.BIO else "" else {
                ""
            },
            fontSize = 14.sp,
            color = Color.Gray
        )
        Spacer(modifier = Modifier.height(8.dp))
        Row(
            horizontalArrangement = Arrangement.SpaceEvenly,
            modifier = Modifier.fillMaxWidth()
        ) {
            Column(horizontalAlignment = Alignment.CenterHorizontally) {
                Text(
                    text = "${if (currUser?.Following != null) currUser.Following?.size?.minus(1)  else 0}",
                    fontWeight = FontWeight.Bold, fontSize = 16.sp
                )
                Text(text = "Following", fontSize = 14.sp, color = Color.Gray)
            }
            Column(horizontalAlignment = Alignment.CenterHorizontally) {
                Text(
                    text = "${if (currUser?.Followers != null) currUser.Followers?.size else 0}",
                    fontWeight = FontWeight.Bold, fontSize = 16.sp
                )
                Text(text = "Followers", fontSize = 14.sp, color = Color.Gray)
            }
        }
    }
}

@Composable
fun EditAndContactButtons(navController: NavController) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp),
        horizontalArrangement = Arrangement.SpaceEvenly
    ) {
        Button(
            onClick = { navController.navigate("EditProfile") },
            colors = ButtonDefaults.buttonColors(containerColor = Color.Black),
            shape = RoundedCornerShape(20),
            modifier = Modifier.weight(1f).padding(end = 8.dp)
        ) {
            Text(text = "Edit Profile")
        }
//        Button(
//            onClick = { /*TODO*/ },
//            colors = ButtonDefaults.buttonColors(containerColor = Color.Black),
//            shape = RoundedCornerShape(50),
//            modifier = Modifier.weight(1f).padding(start = 8.dp)
//        ) {
//            Text(text = "Contact", color = Color.White)
//        }
    }
}


@Composable
fun ImageGrid(currUser: User?, viewModel: InstaViewModel, navController: NavController) {
    val posts = remember { viewModel.currUserposts.value }
    val items = remember {
        List(posts.size) { (100..300).random().dp } // Random heights, but fixed during the lifecycle
    }

    if (currUser?.postCount != null && currUser.postCount?.toInt()!! > 0 && posts.isNotEmpty() && posts.isNotEmpty()) {
        LazyVerticalStaggeredGrid(
            modifier = Modifier.padding(start = 5.dp, end = 5.dp).fillMaxSize(),
            columns = StaggeredGridCells.Fixed(2), // Dynamically adapt columns based on screen width
            contentPadding = PaddingValues(0.dp),
            verticalItemSpacing = 8.dp,
            horizontalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            items(posts.size) { index ->
                val post = posts[index]
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
                            viewModel.retrieveComments()
                            navController.navigate(route = "ViewPost")
                        }
                    )
                }
            }
        }
    } else {
        Box(Modifier.fillMaxSize(0.9f), contentAlignment = Alignment.Center) {
            Text("No Content Posted", color = Color.Gray, fontSize = 24.sp)
        }
    }
}

fun getImage(): Int {
    return R.drawable.img
}
