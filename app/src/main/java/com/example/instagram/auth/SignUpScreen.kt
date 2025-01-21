package com.example.instagram.auth

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import com.example.instagram.InstaViewModel
import com.example.instagram.ui.theme.customTextStyleMedium
import com.example.instagram.ui.theme.customTextStyleSmall
import com.example.instagram.utils.AddButton
import com.example.instagram.utils.AddTextComposable
import com.example.instagram.utils.AddTextfieldComposable
import com.example.instagram.utils.CustomTopBar
import com.example.instagram.utils.Spinner

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SignUpScreen(navController: NavController, viewModel: InstaViewModel) {


    val email = rememberSaveable { mutableStateOf("") }
    val password = rememberSaveable { mutableStateOf("") }
    val username = rememberSaveable { mutableStateOf("") }
    val showPassword = rememberSaveable { mutableStateOf(false) }
    val signedIn = viewModel.signedIn.value
    LaunchedEffect(signedIn) {
        if (signedIn) {
            navController.navigate(route = "Feed") {
                popUpTo("signUpScreen") { inclusive = true }
            }
        }
    }

    Scaffold(topBar = { CustomTopBar(navController = navController, viewModel = viewModel,false) }) { it ->

        Column(
            Modifier
                .fillMaxSize()
                .padding(it),
            verticalArrangement = Arrangement.Center,
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            AddTextComposable(
                "Create your Account",
                customTextStyleMedium,
                { },
                modifier = Modifier
            )
            Spacer(Modifier.size(20.dp))

            AddTextfieldComposable(
                username.value,
                "Username",
                { username.value = it },
                false,
                true,
                {}
            )

            Spacer(Modifier.size(5.dp))

            AddTextfieldComposable(
                email.value,
                "Email",
                { email.value = it },
                false,
                true,
                {}
            )

            Spacer(Modifier.size(5.dp))

            AddTextfieldComposable(
                password.value,
                "Password",
                { password.value = it },
                true,
                showPassword.value,
                { showPassword.value = !showPassword.value })

            Spacer(Modifier.size(20.dp))

            AddButton("Sign Up", {
                viewModel.SignUp(username.value.trim(), email.value.trim(), password.value)
            }, Modifier)

            Spacer(Modifier.size(25.dp))

            Row {
                AddTextComposable(
                    "Already have an account?.",
                    customTextStyleSmall,
                    { },
                    modifier = Modifier
                )
                AddTextComposable(
                    "Click here to Login",
                    customTextStyleSmall,
                    {
                        try {
                            navController.navigate(route = "SignIn")
                        } catch (e: IllegalStateException) {
                            viewModel.HandleException(e, "Cannot move to login page")
                        }
                    },
                    modifier = Modifier
                )
            }
        }
        val loading = viewModel.inProgress.value
        if (loading) {
            Spinner()
        }
    }
}