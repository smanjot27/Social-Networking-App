package com.example.instagram.viewmodels

import androidx.compose.runtime.MutableState
import com.example.instagram.InstaViewModel
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import org.junit.Assert
import org.junit.Before
import org.junit.Test
import org.mockito.Mock
import org.mockito.Mockito
import org.mockito.MockitoAnnotations

class InstaViewModelTest {
    @Mock
    lateinit var auth: FirebaseAuth
    lateinit var store: FirebaseFirestore
    lateinit  var signedIn: MutableState<Boolean>


    @Before
    fun setUp() {
        MockitoAnnotations.openMocks(this)
    }

    @Test
    fun testGetPOSTS() {

    }

    @Test
    fun testSignIn() {
    }
} //Generated with love by TestMe :) Please raise issues & feature requests at: https://weirddev.com/forum#!/testme

