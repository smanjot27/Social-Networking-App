package com.example.instagram.data

data class User(
    val userId: String = "",
    val userName: String = "",
    val Name: String = "",
    val BIO: String = "",
    var Following: MutableList<String>? = mutableListOf<String>(),
    var Followers: MutableList<String>? = mutableListOf<String>(),
    var posts: MutableList<String>? = mutableListOf<String>(),
    val image: String? = "",
    var postCount: Int? = 0
) {
    fun toMap() =
        mapOf(
            "userId" to userId,
            "userName" to userName,
            "Name" to Name,
            "BIO" to BIO,
            "Following" to Following,
            "Followers" to Followers,
            "image" to image,
            "posts" to posts,
            "postCount" to postCount
        )
}