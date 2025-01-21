package com.example.instagram

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.animation.core.tween
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.runtime.Composable
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.example.instagram.auth.SignInScreen
import com.example.instagram.auth.SignUpScreen
import com.example.instagram.utils.CommentScreen
import com.example.instagram.utils.CreatePostScreen
import com.example.instagram.utils.EditProfileScreen
import com.example.instagram.utils.FeedScreen
import com.example.instagram.utils.MyPostScreen
import com.example.instagram.utils.NotificationMessage
import com.example.instagram.utils.SearchScreen
import com.example.instagram.utils.SinglePostScreen
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            InstaApp()
        }
    }
}

@Composable
fun InstaApp() {
    val vm = hiltViewModel<InstaViewModel>()
    var navController = rememberNavController()
    NotificationMessage(vm)
    NavHost(navController, startDestination = if (vm.signedIn.value) "Feed" else "SignIn",
        enterTransition = { fadeIn(animationSpec = tween(200)) },
        exitTransition ={ fadeOut(animationSpec = tween(500))},
        popEnterTransition = {  fadeIn(animationSpec = tween(500)) },
        popExitTransition = { fadeOut(animationSpec = tween(500)) }
    ) {

        composable(
            route = "SignUp",
        ) {
            SignUpScreen(navController, vm)
        }
        composable(route = "SignIn") {
            SignInScreen(navController, vm)
        }
        composable(route = "Feed") {
            FeedScreen(navController, vm)
        }
        composable(route = "Search") {
            SearchScreen(navController, vm)
        }
        composable(route = "Posts") {
            MyPostScreen(navController, vm)
        }
        composable(route = "EditProfile") {
            EditProfileScreen(navController, vm)
        }
        composable(route = "CreatePost") {
            CreatePostScreen(navController, vm)
        }
        composable(
            route = "ViewPost",
        ) {
            SinglePostScreen(navController,vm)

        }
        composable(
            route="CommentSection"
        ){
            CommentScreen(navController,vm)
        }

    }
}

