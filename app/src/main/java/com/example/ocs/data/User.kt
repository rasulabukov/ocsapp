package com.example.ocs.data

import kotlinx.serialization.Serializable

@Serializable
data class User(
    val user_id: String,
    val firstName: String,
    val lastName: String,
    val email: String,
    val phone: String,
    val password: String,
    val profileImage: String
)
