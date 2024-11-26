package com.example.ocs.data

sealed class UserLoad {
    object Loading: UserLoad()
    data class Success(val message: String) : UserLoad()
    data class Error(val errorMessage: String) : UserLoad()
}