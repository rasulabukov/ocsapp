package com.example.ocs.data

import kotlinx.serialization.Serializable

@Serializable
data class User(
    val user_id: String? = null,
    val firstname: String? = null,
    val lastname: String? = null,
    val email: String? = null,
    val phone: String? = null,
    val password: String? = null,
    val profileImage: String? = null,
    val gender: String? = null,
)
