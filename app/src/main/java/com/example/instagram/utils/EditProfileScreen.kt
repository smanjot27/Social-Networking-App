package com.example.instagram.utils

import android.net.Uri
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.text.BasicTextField
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.InsertPhoto
import androidx.compose.material3.Divider
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import androidx.compose.material3.TextFieldDefaults
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.colorResource
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.input.VisualTransformation
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import coil.compose.AsyncImage
import com.example.instagram.InstaViewModel
import com.example.instagram.R

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun EditProfileScreen(navController: NavController, viewModel: InstaViewModel) {

    var user = viewModel.userData.value
    var name = rememberSaveable { mutableStateOf(user?.Name.toString()) }
    var username = rememberSaveable { mutableStateOf(user?.userName.toString()) }
    var bio = rememberSaveable { mutableStateOf(user?.BIO.toString()) }
    val image = remember { mutableStateOf(user?.image.toString()) }
    val imageUri = remember { mutableStateOf<Uri?>(if(image.value!=null) Uri.parse(image.value) else null) }
    val launcher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.GetContent()
    ) { uri: Uri? ->
        imageUri.value = uri
        image.value = uri.toString()
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    Row(
                        Modifier
                            .fillMaxWidth()
                            .padding(start = 10.dp, end = 10.dp),
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        Text(
                            "Back",
                            modifier = Modifier.clickable { navController.popBackStack() },
                            fontSize = 18.sp
                        )
                        Text("Edit Profile", fontSize = 20.sp)
                        Text(
                            "Save",
                            modifier = Modifier
                                .clickable {
                                    viewModel.ManageProfile(
                                        username.value,
                                        name.value,
                                        bio.value,
                                        image.value
                                    )
                                    navController.popBackStack()
                                }
                                .padding(end = 10.dp),
                            fontSize = 18.sp
                        )

                    }
                }
            )
        }
    ) { it ->
        Column(
            Modifier
                .fillMaxSize()
                .padding(it)
        )
        {
            Divider(
                color = Color.Black,
                modifier = Modifier
                    .fillMaxWidth()
                    .width(1.dp) // Line width
                    .height(1.dp)
                    .alpha(0.3f)
                // Line height, matching the content
            )
            Column (horizontalAlignment = Alignment.CenterHorizontally){
                if (image.value!="null") {
                    AsyncImage(
                        model = getImage() ,
                        contentDescription = "",
                        Modifier
                            .fillMaxHeight(0.4f)
                            .fillMaxWidth(0.6f)
                            .padding(20.dp)
                            .border(1.dp, Color.Black)
                            .background(Color.Gray)
                            .clickable { launcher.launch("image/*") },
                        contentScale = ContentScale.FillBounds
                    )
                    Text(
                        "Change Profile Picture",
                        fontSize = 18.sp,
                        textAlign = TextAlign.Center,
                        modifier = Modifier.fillMaxWidth()
                    )

                } else {
                    IconButton(
                        onClick = { launcher.launch("image/*") },
                        Modifier
                            .fillMaxHeight(0.4f)
                            .fillMaxWidth()
                            .padding(20.dp)
                            .border(1.dp, Color.Black)
                            .background(Color.Gray)
                    ) {
                        Icon(
                            Icons.Default.InsertPhoto,
                            contentDescription = "",
                            Modifier
                                .fillMaxHeight(0.8f)
                                .fillMaxWidth()
                        )
                    }

                    Text(
                        "Change Profile Picture",
                        fontSize = 18.sp,
                        textAlign = TextAlign.Center,
                        modifier = Modifier.fillMaxWidth()
                    )
                }
            }
            Column(Modifier.padding(20.dp)) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Text("Name", fontSize = 20.sp)
                    Spacer(Modifier.size(70.dp))
                    BasicTextField(
                        value = if (name.value != "null") name.value else "",
                        onValueChange = { name.value = it },
                        modifier = Modifier.fillMaxWidth(),
                        textStyle = TextStyle(fontSize = 20.sp),
                    ) { innerTextField ->
                        if (name.value == "null") {
                            Text(
                                text = "Enter Name",
                                style = TextStyle(fontSize = 20.sp, color = Color.Gray)
                            )
                        }
                        TextFieldDefaults.DecorationBox(
                            value = "value",
                            visualTransformation = VisualTransformation.None,
                            innerTextField = innerTextField,
                            singleLine = true,
                            enabled = true,

                            interactionSource = remember {
                                MutableInteractionSource()
                            },
                            contentPadding = PaddingValues(0.dp),
                            colors = TextFieldDefaults.textFieldColors(
                                containerColor = Color.Transparent,
                                focusedTextColor = Color.Black,
                            ),

                            )
                    }
                }
                Spacer(Modifier.size(20.dp))

                Row(verticalAlignment = Alignment.CenterVertically) {
                    Text("Username", fontSize = 20.sp)
                    Spacer(Modifier.size(30.dp))
                    BasicTextField(
                        value = if (username.value != "null") username.value else "",
                        onValueChange = { username.value = it },
                        modifier = Modifier.fillMaxWidth(),
                        textStyle = TextStyle(fontSize = 20.sp)
                    ) { innerTextField ->
                        if (username.value == "null") {
                            Text(
                                text = "Enter Username",
                                style = TextStyle(fontSize = 20.sp, color = Color.Gray)
                            )
                        }
                        TextFieldDefaults.DecorationBox(
                            value = "value",
                            visualTransformation = VisualTransformation.None,
                            innerTextField = innerTextField,
                            placeholder = { Text("Enter Username", color = Color.Gray) },
                            singleLine = true,
                            enabled = true,
                            interactionSource = remember {
                                MutableInteractionSource()
                            },
                            contentPadding = PaddingValues(0.dp),
                            colors = TextFieldDefaults.textFieldColors(
                                containerColor = Color.Transparent,
                                focusedTextColor = Color.Black,
                            ),

                            )
                    }
                }
                Spacer(Modifier.size(50.dp))


                Row(verticalAlignment = Alignment.CenterVertically) {
                    Text("Bio", fontSize = 20.sp)
                    Spacer(Modifier.size(40.dp))
                    TextField(
                        value = if (bio.value != "null") bio.value else "",
                        onValueChange = { bio.value = it },
                        Modifier
                            .fillMaxHeight(0.6f)
                            .fillMaxWidth(),
                        colors = TextFieldDefaults.textFieldColors(
                            containerColor = colorResource(R.color.gray),
                            focusedTextColor = Color.Black,
                        ),
                        textStyle = TextStyle(fontSize = 20.sp),
                        singleLine = false,
                        placeholder = {
                            Text(
                                "Introduce yourself!",
                                color = Color.Gray,
                                fontSize = 20.sp
                            )
                        },

                        )
                }
            }
        }
    }
    val loading = viewModel.inProgress.value
    if (loading) {
        Spinner()
    }

}