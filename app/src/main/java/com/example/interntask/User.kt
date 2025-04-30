package com.example.interntask

data class User(
    val id: Int,
    val title: String,
    val description: String
)
data class PersonalDetails(
    val id: Int = 1,
    val name: String,
    val email: String,
    val location: String,
    val image: ByteArray
)