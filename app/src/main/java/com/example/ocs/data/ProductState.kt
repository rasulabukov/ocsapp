package com.example.ocs.data

sealed class ProductState {
    object Loading: ProductState()
    data class Success(val message: String) : ProductState()
    data class Error(val errorMessage: String) : ProductState()
}