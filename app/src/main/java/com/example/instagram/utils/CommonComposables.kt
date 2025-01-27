package com.example.instagram.utils

import android.util.Log
import android.widget.Toast
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.ClickableText
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AddCircle
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
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.NavigationBarItemDefaults
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import androidx.compose.material3.TextFieldDefaults
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.painter.Painter
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.colorResource
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.SpanStyle
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.buildAnnotatedString
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.input.VisualTransformation
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.withStyle
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import com.example.instagram.InstaViewModel
import com.example.instagram.R
import com.example.instagram.ui.theme.*
import androidx.compose.material3.*


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
            .fillMaxSize(),
        contentAlignment = Alignment.Center

    ) {
        CircularProgressIndicator()
    }
}
@Composable
fun BottomNavigationBar(
    selectedItem: String,
    onItemSelected: (String) -> Unit
) {
    NavigationBar(
        containerColor = Color.White,
        contentColor = Color.Black
    ) {
        val items = listOf<String>("Feed", "Search", "CreatePost", "Posts")
        val icons = listOf<ImageVector>(
            Icons.Default.Home,
            Icons.Default.Search,
            Icons.Default.AddCircle,
            Icons.Default.Person
        )

        icons.zip(items).forEach { (icon, item) ->
            NavigationBarItem(
                icon = {
                    Icon(imageVector = icon, contentDescription = "")
                },
                label = { },
                selected = selectedItem == item,
                onClick = { onItemSelected(item) },
                colors = NavigationBarItemDefaults.colors(
                    selectedIconColor = Color.Black,
                    unselectedIconColor = Color.Gray
                )
            )
        }
    }
}

//plk
@Composable
fun NormalTextComponent(value: String) {
    Text(
        text = value,
        modifier = Modifier
            .fillMaxWidth()
            .heightIn(min = 40.dp),
        style = TextStyle(
            fontSize = 24.sp,
            fontWeight = FontWeight.Normal,
            fontStyle = FontStyle.Normal
        ),
        color = TextColor,
        textAlign = TextAlign.Center
    )
}
@Composable
fun ButtonComponent(value: String, onButtonClicked: () -> Unit) {
    Button(
        modifier = Modifier
            .fillMaxWidth()
            .heightIn(48.dp),
        onClick =onButtonClicked,
        contentPadding = PaddingValues(),
        colors = ButtonDefaults.buttonColors(Color.Transparent),
        shape = RoundedCornerShape(50.dp),
    ) {
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .heightIn(48.dp)
                .background(
                    color = Color.Black,
                    shape = RoundedCornerShape(50.dp)
                ),
            contentAlignment = Alignment.Center
        ) {
            Text(
                text = value,
                fontSize = 18.sp,
                color = Color.White,
                fontWeight = FontWeight.Bold
            )
        }
    }
}

@Composable
fun HeadingTextComponent(value: String) {
    Text(
        text = value,
        modifier = Modifier
            .fillMaxWidth()
            .heightIn(),
        style = TextStyle(
            fontSize = 30.sp,
            fontWeight = FontWeight.Bold,
            fontStyle = FontStyle.Normal
        ),
        color = TextColor,
        textAlign = TextAlign.Center
    )
}

@Composable
fun TextFieldComponent(textValue:String,
    labelValue: String, painterResource: Painter,
    onTextChanged: (String) -> Unit,
    errorStatus: Boolean = false
) {
    OutlinedTextField(
        modifier = Modifier
            .fillMaxWidth()
            .height(60.dp)
            .border(
                BorderStroke(1.dp, Color.LightGray),
                shape = RoundedCornerShape(20.dp)),
        label = { Text(text = labelValue) },
        colors = OutlinedTextFieldDefaults.colors(
            focusedBorderColor = Color.Transparent,
            unfocusedBorderColor = Color.Transparent,
            cursorColor = MaterialTheme.colorScheme.primary,
            focusedContainerColor = Color.Transparent,
            unfocusedContainerColor = Color.Transparent
        ),
        keyboardOptions = KeyboardOptions(imeAction = ImeAction.Next),
        singleLine = true,
        value = textValue,
        onValueChange = onTextChanged,
        leadingIcon = {
            Icon(painter = painterResource, contentDescription = null)
        },
        // TODO: change this to get value from view model
        isError = errorStatus
    )
}

@Composable
fun PasswordFieldComponent( password:String,
    labelValue: String, painterResource: Painter,
    onTextChanged: (String) -> Unit,
    errorStatus: Boolean = false
) {
    val passwordVisible = remember { mutableStateOf(false) }

    OutlinedTextField(
        modifier = Modifier
            .fillMaxWidth()
            .height(60.dp)
            .border(
                BorderStroke(1.dp, Color.LightGray),
                shape = RoundedCornerShape(20.dp)),
        label = { Text(text = labelValue) },
        colors = OutlinedTextFieldDefaults.colors(
            focusedBorderColor = Color.Transparent,
            unfocusedBorderColor = Color.Transparent,
            cursorColor = MaterialTheme.colorScheme.primary,
            focusedContainerColor = Color.Transparent,
            unfocusedContainerColor = Color.Transparent
        ),
        keyboardOptions = KeyboardOptions(
            keyboardType = KeyboardType.Password,
            imeAction = ImeAction.Done
        ),
        singleLine = true,
        value = password,
        onValueChange = onTextChanged,
        leadingIcon = {
            Icon(painter = painterResource, contentDescription = null)
        },
        trailingIcon = {
            val icon = if (passwordVisible.value) Icons.Filled.Visibility else Icons.Filled.VisibilityOff
            IconButton(onClick = { passwordVisible.value = !passwordVisible.value }) {
                Icon(imageVector = icon, contentDescription = null)
            }
        },
        visualTransformation = if (passwordVisible.value) VisualTransformation.None else PasswordVisualTransformation(),
        isError = errorStatus
    )
}

@Composable
fun ClickableLoginTextComponent(tryingToLogin: Boolean = true, onTextSelected: (String) -> Unit) {
    val initialText =
        if (tryingToLogin) "Already have an account? " else "Don’t have an account yet? "
    val loginText = if (tryingToLogin) "Login" else "Register"

    val annotatedString = buildAnnotatedString {
        append(initialText)
        withStyle(style = SpanStyle(color = Primary)) {
            pushStringAnnotation(tag = loginText, annotation = loginText)
            append(loginText)
        }
    }

    ClickableText(
        modifier = Modifier
            .fillMaxWidth()
            .heightIn(min = 24.dp),
        style = TextStyle(
            fontSize = 18.sp,
            fontWeight = FontWeight.Normal,
            fontStyle = FontStyle.Normal,
            textAlign = TextAlign.Center
        ),
        text = annotatedString,
        onClick = { offset ->
            annotatedString.getStringAnnotations(offset, offset)
                .firstOrNull()?.also { span ->
                    Log.d("ClickableTextComponent", "{${span.item}}")

                    if (span.item == loginText) {
                        onTextSelected(span.item)
                    }
                }

        },
    )
}
