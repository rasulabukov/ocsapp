package com.example.ocs.data

import kotlinx.serialization.Serializable

@Serializable
data class Favorites(
    val user_id: String? = null,
    val product_id: Int? = null,
)