package com.example.instagram.utils

import android.widget.Toast
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.Logout
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.Search
import androidx.compose.material.icons.filled.Visibility
import androidx.compose.material.icons.filled.VisibilityOff
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Divider
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import androidx.compose.material3.TextFieldDefaults
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.colorResource
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.input.VisualTransformation
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import com.example.instagram.InstaViewModel
import com.example.instagram.R

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun CustomTopBar(navController: NavController, viewModel: InstaViewModel, back: Boolean) {
    TopAppBar(
        title = {
            Row(
                Modifier
                    .fillMaxWidth()
                    .background(color = Color.Transparent),
                horizontalArrangement = Arrangement.Center,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Image(
                    painter = painterResource(R.drawable.instagram_logo),
                    contentDescription = "",
                    contentScale = ContentScale.Crop,
                    modifier = Modifier.size(70.dp)
                )
                Text(
                    text = "Instagram",
                    color = Color.White,
                    fontSize = 40.sp,
                    fontFamily = FontFamily.Cursive,
                    modifier = Modifier.padding(30.dp)
                )
            }
        },
        colors = TopAppBarDefaults.smallTopAppBarColors(
            containerColor = colorResource(R.color.purple_500)
        ),
        actions = {
            if (viewModel.signedIn.value) {
                IconButton(
                    onClick = {
                        viewModel.SignOut()
                        navController.navigate("SignIn")
                    },
                ) {
                    Icon(
                        Icons.Filled.Logout,
                        contentDescription = "",
                        tint = Color.White,
                        modifier = Modifier.size(100.dp)
                    )

                }
            }
        },
        navigationIcon = {
            if (back) {
                IconButton(
                    onClick = {
                        navController.popBackStack()
                    },
                ) {
                    Icon(
                        Icons.Filled.ArrowBack,
                        contentDescription = "",
                        tint = Color.White,
                        modifier = Modifier.size(100.dp)
                    )
                }
            }
        }
    )
}

@Composable
fun AddTextComposable(
    value: String,
    textstyle: TextStyle,
    onclick: () -> Unit,
    modifier: Modifier
) {
    Text(
        text = value,
        style = textstyle,
        modifier = modifier.clickable(onClick = onclick)
    )
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AddTextfieldComposable(
    value: String,
    label: String,
    onValueChange: (String) -> Unit,
    isPassword: Boolean = false,
    showPassword: Boolean = false,
    passwordBtnOnclick: () -> Unit
) {
    TextField(
        value = value,
        onValueChange = onValueChange,
        label = { Text(label, color = Color.Gray) },
        colors = TextFieldDefaults.textFieldColors(
            containerColor = Color.Transparent,
            focusedTextColor = Color.Black,
        ),
        visualTransformation = if (!showPassword) PasswordVisualTransformation() else VisualTransformation.None,
        keyboardOptions = if (isPassword) KeyboardOptions(keyboardType = KeyboardType.Password) else KeyboardOptions(
            keyboardType = KeyboardType.Text
        ),
        trailingIcon = {
            if (isPassword) {
                val icon =
                    if (!showPassword) Icons.Default.Visibility else Icons.Default.VisibilityOff
                IconButton(onClick = passwordBtnOnclick) {
                    Icon(icon, contentDescription = "")
                }
            }
        }
    )
}

@Composable
fun AddButton(text: String, onClick: () -> Unit, modifier: Modifier) {
    Button(
        onClick = onClick,
        shape = RoundedCornerShape(0.dp),
        colors = ButtonDefaults.buttonColors(colorResource(R.color.purple_500)),
        modifier = modifier.height(50.dp)
    ) {
        Text(text = text, fontSize = 18.sp)
    }
}

@Composable
fun NotificationMessage(vm: InstaViewModel) {
    var state = vm.popupnotification.value
    val msg = state?.getContent()
    if (msg != null) {
        Toast.makeText(LocalContext.current, msg, Toast.LENGTH_LONG).show()
    }
}

@Composable
fun Spinner() {
    Box(
        modifier = Modifier
            .alpha(0.5f)
            .background(Color.Gray)
            .fillMaxSize(),
        contentAlignment = Alignment.Center

    ) {
        CircularProgressIndicator()
    }
}

@Composable
@Preview
fun BottomNavigation(navController: NavController, active: String) {

    var icons = listOf<ImageVector>(Icons.Default.Home, Icons.Default.Search, Icons.Default.Person)
    var names = listOf<String>("Feed", "Search", "Posts")

    Row(
        Modifier
            .fillMaxWidth(),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.Bottom,

        ) {
        icons.zip(names).forEach { (icon, name) ->
            IconButton(
                onClick = {
                   navController.navigate(route = name)
                },
            ) {
                Icon(
                    icon,
                    contentDescription = "",
                    modifier = Modifier.padding(horizontal = 10.dp).size(30.dp),
                    tint = if (active.equals(name)) {
                        Color.Black
                    } else Color.Gray
                )
            }
            if (name != "Posts") {
                Divider(
                    color = Color.Black,
                    modifier = Modifier
                        .width(1.dp) // Line width
                        .height(40.dp)
                        .alpha(0.5f)
                        .padding(bottom = 2.dp) // Line height, matching the content
                )
            }


        }

    }

}
