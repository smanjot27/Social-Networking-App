package com.example.instagram.data


data class Posts(
    val caption: String = "",
    val index: Int? = 0,
    val timestamp: String = "",
    val likes: MutableList<String> = mutableListOf<String>(),
    val searchKeyword: List<String> = listOf<String>(),
    val userName: String="",
    val userProfileImage : String="",
    val uid :String = "",
    val comments : MutableList<Comment> = mutableListOf<Comment>()
){
    fun toMap() =
        mapOf(
            "caption" to caption,
            "userName" to userName,
            "index" to index,
            "timestamp" to timestamp,
            "likes" to likes,
            "searchKeyword" to searchKeyword,
            "userProfileImage" to userProfileImage,
            "uid" to uid,
            "comments" to comments
            )
}