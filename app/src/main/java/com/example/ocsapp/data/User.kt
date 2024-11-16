package com.example.ocsapp.data

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "Users")
data class User(
    @PrimaryKey(autoGenerate = true)
    val id: Int,
    var firstname: String,
    var lastname: String,
    var email: String,
    var phone: String,
    var profileimage: String,
    var password: String,
)