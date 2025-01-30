package com.example.instagram.utils

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Send
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalFocusManager
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.unit.dp
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
    Column(
        Modifier
            .fillMaxWidth(0.95f).background(Color.White),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        if (!viewModel.inProgress.value) {
            LazyColumn(
                modifier = Modifier
                    .fillMaxWidth()
                    .weight(1f)
            ) {
                items(comments.size) { idx ->
                   showComment(comments[idx])
                    /*Row(modifier = Modifier.padding(8.dp)) {
                        //USERNAME
                        Text(
                            text = comment.userName,
                            fontWeight = FontWeight.Bold,
                            fontSize = 16.sp
                        )
                        //CAPTION
                        Text(
                            text = comment.comment,
                            modifier = Modifier
                                .fillMaxWidth(0.6f)
                                .padding(start = 8.dp),
                            fontSize = 14.sp,
                            style = TextStyle.Default
                        )
                    }
                    Spacer(
                        Modifier
                            .fillMaxWidth()
                            .height(1.dp)
                            .border(1.dp, colorResource(R.color.light_gray))
                    )*/
                }
            }
        } else {
            Spinner()
        }
        /*OutlinedTextField(
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
        )*/
        OutlinedTextField(
            modifier = Modifier
                .height(58.dp).fillMaxWidth()
                .border(
                    BorderStroke(1.dp, Color.LightGray),
                    shape = RoundedCornerShape(20.dp)
                ),
            label = { Text(text = stringResource(R.string.comment)) },
            colors = OutlinedTextFieldDefaults.colors(
                focusedBorderColor = Color.Transparent,
                unfocusedBorderColor = Color.Transparent,
                cursorColor = MaterialTheme.colorScheme.primary,
                focusedContainerColor = Color.Transparent,
                unfocusedContainerColor = Color.Transparent
            ),
            keyboardOptions = KeyboardOptions(imeAction = ImeAction.Next),
            value = commentValue.value,
            onValueChange = {commentValue.value=it },
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