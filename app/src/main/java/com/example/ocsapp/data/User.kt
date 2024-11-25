package com.example.ocsapp.data

import kotlinx.serialization.Serializable

@Serializable
data class User(
    val firstName: String,
    val lastName: String,
    val email: String,
    val phone: String,
    val password: String,
    val profileImage: String
)
