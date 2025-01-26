package com.example.instagram

import android.util.Log
import androidx.compose.runtime.mutableStateOf
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.instagram.data.Comment
import com.example.instagram.data.Event
import com.example.instagram.data.Posts
import com.example.instagram.data.User
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FieldValue
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.Query
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.async
import kotlinx.coroutines.awaitAll
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import kotlinx.coroutines.tasks.await
import java.text.SimpleDateFormat
import java.time.LocalDateTime
import java.time.format.DateTimeFormatter
import java.util.Date
import java.util.Locale
import javax.inject.Inject
import kotlin.collections.map
import kotlin.collections.orEmpty
import kotlin.toString

val USERS = "Users"
val POSTS = "Posts"


@HiltViewModel
class InstaViewModel @Inject constructor(
    val auth: FirebaseAuth,
    val store: FirebaseFirestore
) : ViewModel() {

    val post = MutableLiveData<Posts>()
    val inProgress = mutableStateOf(false)
    var signedIn = mutableStateOf(false)
    val userData = mutableStateOf<User?>(null)
    val popupnotification = mutableStateOf<Event<String>?>(null)
    val currUserposts = mutableStateOf(mutableListOf<Posts>())
    val searchedposts = mutableStateOf(mutableListOf<Posts>())
    val feedPosts = mutableStateOf(mutableListOf<Posts>())
    val postComments = mutableStateOf(mutableListOf<Comment>())
    val fillers =
        listOf<String>("to", "be", "a", "an", "the", "who", "were", "is", "was", "which", "where")
    private val _followingDetails = MutableStateFlow<List<Pair<String, Boolean>>>(emptyList())
    val followingDetails: StateFlow<List<Pair<String, Boolean>>> get() = _followingDetails

    val formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss")

    init {
        val currentUser = auth.currentUser
        signedIn.value = currentUser != null
        Log.i("Instagram Check", "viewModel init -> ${currentUser?.email}")
        Log.i("Instagram Check", "viewModel init ->${inProgress.value}")
        Log.i("Instagram Check", "viewModel init ->${signedIn.value}")
        currentUser?.uid.let { uid ->
            getUserData(uid.toString())
            getCurrUserPost(uid.toString())
            getFeedPosts(uid.toString())
            fetchFollowingData()
        }
    }

    fun SignUp(username: String, email: String, password: String) {
        if (username.isEmpty() || email.isEmpty() || password.isEmpty()) {
            HandleException(null, "Please enter all details")
        } else {
            viewModelScope.launch {
                inProgress.value = true
                store.collection(USERS).whereEqualTo("username", username).get()
                    .addOnFailureListener {
                        HandleException(it, "failed to signup")
                        inProgress.value = false
                    }
                    .addOnSuccessListener { documents ->
                        if (documents.size() > 0) {
                            HandleException(custom = "User already Exists")
                            inProgress.value = false
                        } else {
                            auth.createUserWithEmailAndPassword(email, password)
                                .addOnCompleteListener { task ->
                                    if (task.isSuccessful) {
                                        signedIn.value = true
                                        Log.i(
                                            "Instagram Check",
                                            "viewModel signUp -> ${auth.currentUser?.email}"
                                        )
                                        Log.i(
                                            "Instagram Check",
                                            "viewModel signUp ->${inProgress.value}"
                                        )
                                        Log.i(
                                            "Instagram Check",
                                            "viewModel signUp ->${signedIn.value}"
                                        )
                                        ManageProfile(
                                            username,
                                            postCount = 0,
                                            following = mutableListOf(auth.currentUser?.uid.toString())
                                        )
                                    } else {
                                        HandleException(
                                            task.exception,
                                            custom = "Couldn't create account. Try Again Later"
                                        )
                                    }
                                    inProgress.value = false
                                }.addOnFailureListener {
                                    HandleException(it, "failed to signup")
                                    inProgress.value = false
                                }
                        }
                    }
            }
        }

    }

    fun HandleException(exception: Exception? = null, custom: String) {
        exception?.printStackTrace()
        val errormsg = exception?.localizedMessage ?: ""
        val message = "$custom : $errormsg"
        popupnotification.value = Event(message)

    }

    fun ManageProfile(
        username: String? = null,
        name: String? = null,
        bio: String? = null,
        image: String? = null,
        postCount: Int? = null,
        following: MutableList<String>? = null
    ) {
        val uid = auth.currentUser?.uid.toString()
        val user = User(
            userId = uid, userName = (username ?: userData.value?.userName).toString(),
            Name = (name ?: userData.value?.Name).toString(),
            BIO = (bio ?: userData.value?.BIO).toString(),
            Following = following ?: userData.value?.Following,
            Followers = userData.value?.Followers,
            posts = userData.value?.posts,
            image = (image ?: userData.value?.image).toString(),
            postCount = (postCount ?: userData.value?.postCount)?.toInt(),
        )

        uid.let {
            inProgress.value = true
            store.collection(USERS).document(uid).get().addOnSuccessListener {
                if (it.exists()) {
                    it.reference.update(user.toMap())
                        .addOnSuccessListener {
                            this.userData.value = user
                            HandleException(custom = "User Profile updated successfully")
                            inProgress.value = false
                        }.addOnFailureListener {
                            HandleException(it, "cannot update user")
                            inProgress.value = false
                        }
                } else {
                    store.collection(USERS).document(uid).set(user.toMap())
                    getUserData(uid)
                    inProgress.value = false
                }
            }.addOnFailureListener {
                HandleException(it, "cannot create user")
                inProgress.value = false
            }
        }
    }

    fun getUserData(uid: String) {
        inProgress.value = true
        store.collection(USERS).document(uid).get().addOnSuccessListener {
            val r = it.toObject(User::class.java)
            userData.value = r
            inProgress.value = false
            Log.i("Instagram Check", "viewModel userData -> ${auth.currentUser?.email}")
            Log.i("Instagram Check", "viewModel userData ->${inProgress.value}")
            Log.i("Instagram Check", "viewModel userData ->${r}")
            //popupnotification.value = Event("User Data retrieved successfully")
        }.addOnFailureListener {
            HandleException(it, "cannot retrieve data")
            inProgress.value = false
        }
    }

    fun SignIn(email: String, password: String) {
        if (email.isEmpty() || password.isEmpty()) {
            HandleException(null, "Please enter all details")
        } else {
            inProgress.value = true
            auth.signInWithEmailAndPassword(email, password).addOnCompleteListener { task ->
                if (task.isSuccessful) {
                    signedIn.value = true
                    inProgress.value = false
                    Log.i("Instagram Check", "viewModel signIn -> ${auth.currentUser?.email}")
                    Log.i("Instagram Check", "viewModel signIn ->${inProgress.value}")
                    Log.i("Instagram Check", "viewModel signIn ->${signedIn.value}")
                    auth.currentUser?.uid?.let { uid ->
                        getUserData(uid)
                        getCurrUserPost(uid)
                    }
                } else {
                    HandleException(task.exception, "Failed to login")
                    inProgress.value = false

                }
            }.addOnFailureListener {
                HandleException(it, "Failed to login")
                inProgress.value = false
            }
        }
    }

    fun SignOut() {
        auth.signOut()
        currUserposts.value.clear()
        searchedposts.value.clear()
        signedIn.value = false
        inProgress.value = false
        userData.value = null
        Log.i("Instagram Check", "viewModel signout -> ${auth.currentUser?.email}")
        Log.i("Instagram Check", "viewModel signout ->${inProgress.value}")
        Log.i("Instagram Check", "viewModel signout ->${signedIn.value}")


    }

    fun createPost(caption: String, currentTime: LocalDateTime?) {
        val uid = auth.currentUser?.uid.toString()
        val formattedDateTime = currentTime?.format(formatter)
        val search: List<String> =
            caption.split(" ", ",", ".", "!", "@", "#", "?", "/").map { it.lowercase() }
                .filter { it.isNotEmpty() and !fillers.contains(it) }
        val currPost = Posts(
            caption = caption,
            index = userData.value?.postCount,
            timestamp = formattedDateTime.toString(),
            likes = mutableListOf(),
            searchKeyword = search,
            userName = userData.value?.userName.toString(),
            userProfileImage = userData.value?.image.toString(),
            uid = uid
        )
        //adding post in post collection
        viewModelScope.launch {
            inProgress.value = true
            store.collection(POSTS).add(currPost.toMap()).addOnSuccessListener {
                val postId = it.id
                //adding post to user profile
                uid.let {
                    userData.value?.postCount = userData.value?.postCount!! + 1
                    if (userData.value?.posts == null) {
                        userData.value?.posts = mutableListOf<String>()
                    }
                    userData.value?.posts?.add(postId)
                    getPost(postId)
                    ManageProfile()
                    inProgress.value = false
                }
                HandleException(custom = "Posts created successfully")
                inProgress.value = false

            }.addOnFailureListener {
                HandleException(exception = it, custom = "Posts not created")
                inProgress.value = false
            }
        }
        getFeedPosts(uid)
    }

    fun formatTimestamp(timestamp: String): String {
        val inputFormat = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss", Locale.getDefault())
        val outputFormat = DateTimeFormatter.ofPattern("dd MMMM yyyy", Locale.getDefault())
        // Parse and format the timestamp
        val dateTime = LocalDateTime.parse(timestamp, inputFormat)
        return dateTime.format(outputFormat)
    }

    fun searchRelatedPosts(searchKeyword: String) {
        searchedposts.value.clear()
        viewModelScope.launch {
            inProgress.value = true
            store.collection(POSTS).whereArrayContains("searchKeyword", searchKeyword).get()
                .addOnSuccessListener { documents ->
                    Log.i("Instagram check", "$documents")
                    documents.mapNotNull { doc ->
                        val post = doc.toObject(Posts::class.java)
                        if (!searchedposts.value.contains(post)) searchedposts.value.add(post)
                    }


                }.addOnFailureListener { exception ->
                    HandleException(
                        exception,
                        "Failed to search posts"
                    )// Handle failure (e.g., network error)
                }

        }
        Log.i("Instagram check", "${searchedposts.value}")

    }

    fun getCurrUserPost(uid: String = auth.currentUser?.uid.toString()) {
        viewModelScope.launch {
            inProgress.value = true
            if (uid != "null") {
                store.collection(USERS).document(uid).get().addOnSuccessListener { document ->
                    Log.i("Instagram check", document.toString())
                    if (document.get("posts") != null) {
                        val postRef = document.get("posts") as List<String> ?: emptyList()
                        if (postRef.isNotEmpty()) {
                            Log.i("Instagram check", postRef.toString())
                            postRef.forEach { pid ->
                                getPost(pid)
                            }
                        }
                    }
                }
            }
            inProgress.value = false
        }
    }

    fun getPost(pid: String) {
        viewModelScope.launch {
            inProgress.value = true
            store.collection(POSTS).document(pid).get().addOnSuccessListener {
                val r = it.toObject(Posts::class.java)
                r?.let { posts ->
                    // Create a new list to trigger recomposition
                    if (!currUserposts.value.contains(posts)) {
                        val updatedPosts = currUserposts.value.toMutableList()
                        updatedPosts.add(posts)
                        currUserposts.value =
                            updatedPosts.sortedByDescending { it.timestamp }.toMutableList()
                    }
                }
                inProgress.value = false
                Log.i("Instagram Check", "post added -> ${r}")
                //popupnotification.value = Event("User Data retrieved successfully")
            }.addOnFailureListener {
                Log.i("Instagram Check", "Failed to get post ${it}")
            }

        }
    }

    fun isFollowing(user: String?): Boolean {
        return userData.value?.Following?.contains(user) == true
    }

    fun addFollowing(userToFollow: String?) {
        if (userData.value?.Following == null) {
            userData.value?.Following = mutableListOf<String>()
        }
        userData.value?.Following?.add(userToFollow.toString())
        ManageProfile()
        addFollower(userToFollow.toString(), auth.currentUser?.uid.toString())
        getFeedPosts(auth.currentUser?.uid.toString())
    }

    fun addFollower(addTo: String, addIt: String) {
        viewModelScope.launch {
            inProgress.value = true
            store.collection(USERS).document(addTo)
                .update("Followers", FieldValue.arrayUnion(addIt)).addOnSuccessListener {
                    HandleException(custom = "Data Updated Successfully")
                }.addOnFailureListener {
                    HandleException(it, "cannot create user")
                }
        }

    }

    fun removeFollowing(userToUnfollow: String?) {
        userData.value?.Following?.remove(userToUnfollow)
        removeFollower(userToUnfollow.toString(), auth.currentUser?.uid.toString())
        ManageProfile()
        getFeedPosts(auth.currentUser?.uid.toString())
    }

    fun removeFollower(addTo: String, addIt: String) {
        viewModelScope.launch {
            inProgress.value = true
            store.collection(USERS).document(addTo)
                .update("Followers", FieldValue.arrayRemove(addIt)).addOnSuccessListener {
                    HandleException(custom = "Data Updated Successfully")
                }.addOnFailureListener {
                    HandleException(it, "cannot create user")
                }
        }
    }

    fun getFeedPosts(uid: String) {
        val timestamp24HoursAgo = System.currentTimeMillis() - 24 * 60 * 60 * 1000
        val date24HoursAgo = Date(timestamp24HoursAgo) // Convert to Date
        val dateFormat = SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.getDefault())
        val Following = userData.value?.Following
        if (uid != "null") {
            viewModelScope.launch {
                inProgress.value = true
                val deferredPosts = Following?.map { uid ->
                    async {
                        val document = store.collection(USERS).document(uid).get().await()
                        if (document.get("posts") != null) {
                            val postRef = document.get("posts") as List<String> ?: emptyList()
                            postRef.mapNotNull { pid ->
                                store.collection(POSTS).document(pid).get().await()
                                    .toObject(Posts::class.java)
                            }
                        } else {
                            emptyList<Posts>()
                        }
                    }
                } ?: emptyList()

                val allPosts = deferredPosts.awaitAll().flatten()
                if (allPosts.isNotEmpty()) {
                    Log.d("check", "Posts found: ${allPosts.size}")
                } else {
                    Log.d("check", "No posts found in the following users.")
                }
                val recentPosts: MutableList<Posts> = mutableListOf()
                //        val formattedDateTime = currentTime?.format(formatter)

                val document = store.collection(POSTS)
                    .whereGreaterThanOrEqualTo(
                        "timestamp",
                        dateFormat.format(date24HoursAgo)
                    ) // Fetch posts from the last 24 hours
                    .orderBy(
                        "timestamp",
                        Query.Direction.DESCENDING
                    ) // Order by timestamp descending
                    .get()
                    .await()
                document.mapNotNull { doc ->
                    val post = doc.toObject(Posts::class.java)
                    Log.d("check", "Fetched post: $post")
                    if (!allPosts.contains(post)) recentPosts.add(post)
                }
                if (recentPosts.isNotEmpty()) {
                    Log.d("check", "Posts found: ${recentPosts.size}")
                } else {
                    Log.d("check", "No posts found recently.")
                }
                val updatedList = (allPosts + recentPosts).sortedByDescending { it.timestamp }
                feedPosts.value = updatedList.toMutableList()
                inProgress.value = false

            }
        }
    }

    fun fetchFollowingData() {
        viewModelScope.launch {
            val followingList = userData.value?.Following.orEmpty()
            val details = followingList.map { uid ->
                async {
                    val document = store.collection(USERS).document(uid).get().await()
                    val user = document.toObject(User::class.java)
                    val username = user?.userName ?: "Unknown"
                    val hasImage = user?.image == "null"
                    username to hasImage
                }
            }.awaitAll()
            Log.d("FollowingData", "Fetched following details: $details")

            _followingDetails.value = details
        }
    }

    fun likePost(posts: Posts?) {
        viewModelScope.launch {
            val document =
                store.collection(POSTS).whereEqualTo("timestamp", posts?.timestamp).get().await()
            if (!document.isEmpty) {
                val postref = document.documents.first()
                postref.reference.update("likes", FieldValue.arrayUnion(userData.value?.userId))
                    .await()
                async {
                    getFeedPosts(userData.value?.userId.toString())
                }.await()
                store.collection(POSTS).document(postref.reference.id).get().addOnSuccessListener {
                    post.value = it.toObject(Posts::class.java)
                }
            }
        }

    }

    /*fun likedByDoubleTap(posts: Posts?) {
        viewModelScope.launch {
            val document =
                store.collection(POSTS).whereEqualTo("timestamp", posts?.timestamp).get().await()
            if (!document.isEmpty) {
                val postref = document.documents.first()
                postref.reference.update("likedbyGesture", !posts?.likedbyGesture!!)
//                async{
//                    getFeedPosts(userData.value?.userId.toString())
//                }.await()
                store.collection(POSTS).document(postref.reference.id).get().addOnSuccessListener{
                    post.value = it.toObject(Posts::class.java)
                }
                delay(2000L)
                postref.reference.update("likedbyGesture", false)
                store.collection(POSTS).document(postref.reference.id).get().addOnSuccessListener{
                    post.value = it.toObject(Posts::class.java)
                }
            }
        }
    }*/

    fun dislikePost(posts: Posts) {
        viewModelScope.launch {
            val document =
                store.collection(POSTS).whereEqualTo("timestamp", posts.timestamp).get().await()
            if (!document.isEmpty) {
                val postref = document.documents.first()
                postref.reference.update("likes", FieldValue.arrayRemove(userData.value?.userId))
                    .await()
                async {
                    getFeedPosts(userData.value?.userId.toString())
                }.await()
                store.collection(POSTS).document(postref.reference.id).get().addOnSuccessListener {
                    post.value = it.toObject(Posts::class.java)
                }

            }
        }

    }

    fun retrieveComments(post: Posts?) {
        postComments.value.clear()
        viewModelScope.launch {
            var commentList: MutableList<Comment> = mutableListOf<Comment>()
            val document =
                store.collection(POSTS).whereEqualTo("timestamp", post?.timestamp).get().await()
            if (!document.isEmpty) {
                val postref = document.documents.first()
                val r = postref.toObject(Posts::class.java)
                if (r != null) {
                    commentList = r.comments
                }
            }
            if (!commentList.isEmpty()) postComments.value = commentList.toMutableList()
        }
    }

    fun addComment(posts: Posts?, comment: String, timestamp: LocalDateTime) {
        val formattedDateTime = timestamp.format(formatter)
        val commentObject = Comment(comment, userData.value?.userName.toString(), formattedDateTime)
        viewModelScope.launch {
            val document =
                store.collection(POSTS).whereEqualTo("timestamp", posts?.timestamp).get().await()
            if (!document.isEmpty) {
                val postref = document.documents.first()
                postref.reference.update("comments", FieldValue.arrayUnion(commentObject))
                    .addOnSuccessListener {
                        HandleException(custom = "Data Updated Successfully")
                    }.addOnFailureListener {
                        HandleException(it, "cannot create user")
                    }

                store.collection(POSTS).document(postref.reference.id).get().addOnSuccessListener {
                    post.value = it.toObject(Posts::class.java)
                }
                async {
                    retrieveComments(post.value)
                    getFeedPosts(userData.value?.userId.toString())
                }

            }
        }
    }
}
