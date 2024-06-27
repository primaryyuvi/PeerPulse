package com.example.peer_pulse.domain.model

data class Post(
    val id: String,
    val userId: String,
    val title: String,
    val description: String,
    val imageUrl: String,
    val timestamp: String,
    val likes: Int,
    val preferences : String,
    val preferenceId : String
)
