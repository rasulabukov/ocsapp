package com.example.ocs.data

sealed class State {
    object Loading: State()
    data class Success(val message: String) : State()
    data class Error(val errorMessage: String) : State()
}