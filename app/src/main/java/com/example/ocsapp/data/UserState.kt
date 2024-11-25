package com.example.ocsapp.data

sealed class UserState {
    object Loading: UserState()
    data class Success(val message: String) : UserState()
    data class Error(val errorMessage: String) : UserState()
}