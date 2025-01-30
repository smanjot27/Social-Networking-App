package com.example.instagram.utils

import android.net.Uri
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
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
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.BasicTextField
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material.icons.filled.InsertPhoto
import androidx.compose.material3.Divider
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import androidx.compose.material3.TextFieldDefaults
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.colorResource
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.ImeAction
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
    var image = remember { mutableStateOf(user?.image.toString()) }
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
                title = { Text("Edit Profile", fontSize = 18.sp, fontWeight = FontWeight.Bold) },
                navigationIcon = {
                    IconButton(onClick = { navController.popBackStack() }) {
                        Icon(Icons.Filled.ArrowBack, contentDescription = "Back")
                    }
                },
                colors = TopAppBarDefaults.smallTopAppBarColors(
                    containerColor = Color.White,
                ),
            )
        }
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(it)
                .padding(16.dp)
        ) {
            //Profile Image
            Column(
                horizontalAlignment = Alignment.CenterHorizontally,
                modifier = Modifier.fillMaxWidth()
            ) {
                Box(contentAlignment = Alignment.BottomEnd, modifier = Modifier.padding(top = 16.dp)) {
                    if (image.value!="null") {
                        AsyncImage(
                            model = getImage(), // Replace with the actual image URL
                            contentDescription = "Profile Picture",
                            modifier = Modifier
                                .size(100.dp)
                                .clip(CircleShape)
                                .background(Color.LightGray).clickable { launcher.launch("image/*") },
                        )
                    }else{
                        IconButton(
                            onClick = { launcher.launch("image/*") },
                            Modifier
                                .size(100.dp)
                                .clip(CircleShape)
                                .background(Color.LightGray),
                        ) {
                            Icon(
                                Icons.Default.InsertPhoto,
                                contentDescription = "",
                            )
                        }
                    }
                    IconButton(
                        onClick = { launcher.launch("image/*") },
                        modifier = Modifier
                            .size(30.dp)
                            .clip(CircleShape)
                            .background(Color.White)
                    ) {
                        Icon(Icons.Filled.Edit, contentDescription = "Edit", tint = Color.Black, modifier = Modifier.align(Alignment.TopStart))
                    }
                }
            }
            Spacer(modifier = Modifier.height(40.dp))
            //Content
            Column(modifier = Modifier.fillMaxWidth()) {
                TextFieldComponent(
                    textValue = if (name.value != "null") name.value else "",
                    labelValue = stringResource(R.string.first_name),
                    painterResource = painterResource(R.drawable.person),
                    onTextChanged = { name.value = it },
                    errorStatus = false,
                    singleline = true
                )
                Spacer(modifier = Modifier.height(10.dp))
                TextFieldComponent(
                    textValue = if (username.value != "null") username.value else "",
                    labelValue = stringResource(R.string.user_name),
                    painterResource = painterResource(R.drawable.profile),
                    onTextChanged = { username.value = it },
                    errorStatus = false,
                    singleline = true
                )
                Spacer(modifier = Modifier.height(10.dp))
                OutlinedTextField(
                    modifier = Modifier
                        .fillMaxWidth()
                        .fillMaxHeight(0.5f)
                        .border(
                            BorderStroke(1.dp, Color.LightGray),
                            shape = RoundedCornerShape(20.dp)),
                    label = { Text(text = stringResource(R.string.bio)) },
                    colors = OutlinedTextFieldDefaults.colors(
                        focusedBorderColor = Color.Transparent,
                        unfocusedBorderColor = Color.Transparent,
                        cursorColor = MaterialTheme.colorScheme.primary,
                        focusedContainerColor = Color.Transparent,
                        unfocusedContainerColor = Color.Transparent
                    ),
                    keyboardOptions = KeyboardOptions(imeAction = ImeAction.Next),
                    value = if (bio.value != "null") bio.value else "",
                    onValueChange =   { bio.value = it },
                )
            }
            Spacer(modifier = Modifier.weight(1f))
            //Save btn
            ButtonComponent(stringResource(R.string.save), onButtonClicked = {
                viewModel.ManageProfile(
                    username.value,
                    name.value,
                    bio.value,
                    image.value
                )
                navController.popBackStack()})
        }
    }
    val loading = viewModel.inProgress.value
    if (loading) {
        Spinner()
    }
}

