package com.example.ocs.data

import kotlinx.serialization.Serializable

@Serializable
data class Product(
    var id: Int? = null,
    val name: String? = null,
    val description: String? = null,
    val price: String? = null,
    val size: String? = null,
    val color: String? = null,
    val quantity: String? = null,
    val image: String? = null,
    val category: String? = null
)