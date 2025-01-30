package com.example.instagram.auth

import CurvedHangingImages
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import com.example.instagram.InstaViewModel
import com.example.instagram.R
import com.example.instagram.utils.ButtonComponent
import com.example.instagram.utils.ClickableLoginTextComponent
import com.example.instagram.utils.HeadingTextComponent
import com.example.instagram.utils.NormalTextComponent
import com.example.instagram.utils.PasswordFieldComponent
import com.example.instagram.utils.Spinner
import com.example.instagram.utils.TextFieldComponent

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SignInScreen(navController: NavController,viewModel: InstaViewModel) {

    val email = rememberSaveable { mutableStateOf("") }
    val password = rememberSaveable { mutableStateOf("") }
    val showPassword = rememberSaveable { mutableStateOf(false) }
    val signedIn = viewModel.signedIn.value
    LaunchedEffect(signedIn) {
        if (signedIn) {
            navController.navigate(route = "Feed") {
                popUpTo("signInScreen") { inclusive = true }
            }
        }
    }
    Box(
        modifier = Modifier.fillMaxSize()
            .background(
                Brush.radialGradient(
                colors = listOf(
                    Color(0xFFe2d1c3),
                    Color(0xFFfdfcfb),
                ),
                radius = 300f

            )))
    {

        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(start = 16.dp, top = 42.dp, end = 24.dp, bottom = 12.dp) // Different paddings for each side
        ) {

            NormalTextComponent(value = stringResource(id = R.string.login))
            HeadingTextComponent(value = stringResource(id = R.string.welcome))
            Spacer(modifier = Modifier.height(40.dp))

            TextFieldComponent(email.value, labelValue = stringResource(id = R.string.email),
                painterResource(id = R.drawable.message),
                onTextChanged = {
                    email.value = it
                },
                errorStatus = false,
                singleline = true
            )
            Spacer(modifier = Modifier.height(10.dp))
            PasswordFieldComponent(password.value,
                labelValue = stringResource(id = R.string.password),
                painterResource(id = R.drawable.lock),
                onTextChanged = {
                    password.value = it
                },
                errorStatus = false
            )

            Spacer(modifier = Modifier.height(40.dp))

            ButtonComponent(
                value = stringResource(id = R.string.login),
                onButtonClicked = {
                    viewModel.SignIn(email.value.trim(),password.value)
                    if (viewModel.signedIn.value){
                        navController.navigate(route = "Feed")
                    }
                },
            )
            Spacer(modifier = Modifier.height(20.dp))
            ClickableLoginTextComponent(tryingToLogin = false, onTextSelected = {
                navController.navigate(route="SignUp")
            })
            Spacer(modifier = Modifier.height(40.dp))
            CurvedHangingImages()
        }
        val loading = viewModel.inProgress.value
        if (loading) {
            Spinner()
        }
    }
}