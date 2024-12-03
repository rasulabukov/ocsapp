package com.example.ocs.viewmodel

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.ocs.data.Product
import com.example.ocs.data.ProductState
import com.example.ocs.data.SupabaseClient.supabase
import io.github.jan.supabase.postgrest.from
import io.github.jan.supabase.postgrest.query.Columns
import io.github.jan.supabase.postgrest.query.Order
import kotlinx.coroutines.launch

class SupabaseProductsViewModel(): ViewModel() {
    private val _productList = MutableLiveData<List<Product>>()
    val productList: LiveData<List<Product>> get() = _productList

    private val _productState = MutableLiveData<ProductState>()
    val productState: LiveData<ProductState> get() = _productState


    fun loadProductInfo() {
        viewModelScope.launch {
            _productState.value = ProductState.Loading // Устанавливаем состояние загрузки
            try {
                // Получаем данные из таблицы products
                val response = supabase.from("products")
                    .select(columns = Columns.list("name", "description", "price", "size", "quantity", "image")){
                        order(column = "id", order = Order.ASCENDING)
                    }
                    .decodeList<Product>() // Декодируем результат в список объектов Product

                _productList.value = response // Обновляем LiveData
                _productState.value = ProductState.Success("Успех")// Устанавливаем состояние успешной загрузки
            } catch (e: Exception) {
                _productState.value = ProductState.Error(e.message ?: "Ошибка загрузки данных") // Обрабатываем ошибки
            }
        }
    }
}