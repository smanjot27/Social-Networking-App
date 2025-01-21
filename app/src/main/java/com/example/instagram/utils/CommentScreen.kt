package com.example.instagram.utils

import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.imePadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Send
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TextFieldDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalFocusManager
import androidx.compose.ui.res.colorResource
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import com.example.instagram.InstaViewModel
import com.example.instagram.R
import java.time.LocalDateTime

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun CommentScreen(navController: NavController, viewModel: InstaViewModel) {

    val post = viewModel.post.value
    val comments = viewModel.postComments.value
    val commentValue = remember { mutableStateOf("") }
    val focusManager = LocalFocusManager.current

    Scaffold(
        topBar = {
            CustomTopBar(navController = navController, viewModel = viewModel, true)
        },
    ) {
        Column(
            Modifier
                .padding(it)
                .fillMaxSize().imePadding()
        ) {
            Column(
                verticalArrangement = Arrangement.Top,
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(top = 30.dp)
                    .weight(1f)
                    .verticalScroll(rememberScrollState())
            ) {
                comments.forEach { comment ->
                    Row(modifier = Modifier.padding(10.dp)) {
                        //USERNAME
                        Text(
                            text = comment.userName,
                            modifier = Modifier.padding(start = 40.dp),
                            fontWeight = FontWeight.Bold,
                            fontSize = 25.sp
                        )
                        //CAPTION
                        Text(
                            text = comment.comment,
                            modifier = Modifier
                                .fillMaxWidth(0.6f)
                                .padding(start = 20.dp),
                            fontSize = 25.sp,
                            style = TextStyle.Default
                        )
                    }
                    Spacer(
                        Modifier
                            .fillMaxWidth()
                            .height(1.dp)
                            .padding(start = 40.dp)
                            .border(1.dp, colorResource(R.color.light_gray))
                    )
                }
            }
            OutlinedTextField(
                value = commentValue.value,
                onValueChange = { commentValue.value = it },
                label = { Text("Add Comment", color = Color.Gray) },
                colors = TextFieldDefaults.textFieldColors(
                    containerColor = Color.Transparent,
                    focusedTextColor = Color.Black,
                ),
                keyboardActions = KeyboardActions(
                    onDone = {
                        focusManager.clearFocus() // Clears focus to hide the keyboard
                    }
                ),
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(start = 20.dp, end = 20.dp)
                    .imePadding(),
                trailingIcon = {
                    IconButton(onClick = {
                        viewModel.addComment(post, commentValue.value, LocalDateTime.now())
                        focusManager.clearFocus()
                        commentValue.value = ""
                    }) {
                        Icon(Icons.Default.Send, contentDescription = "")
                    }
                }
            )
        }

    }
}