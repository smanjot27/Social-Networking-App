package com.example.instagram.data
/* class for showing one time events*/
open class Event<out T>(private val content:T) {
    var hasbeenhandled=false
        private set
    fun getContent():T?{
        return if(hasbeenhandled){
            null
        } else {
            hasbeenhandled=true
            content
        }
    }
}