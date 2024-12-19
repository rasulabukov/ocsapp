package com.example.ocs.viewmodel

import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.ocs.data.Favorites
import com.example.ocs.data.Product
import com.example.ocs.data.ProductState
import com.example.ocs.data.SupabaseClient.supabase
import io.github.jan.supabase.auth.auth
import io.github.jan.supabase.postgrest.from
import io.github.jan.supabase.postgrest.query.Columns
import io.github.jan.supabase.postgrest.query.Order
import kotlinx.coroutines.launch

class SupabaseProductsViewModel(): ViewModel() {
    private val _productList = MutableLiveData<List<Product>>()
    val productList: LiveData<List<Product>> get() = _productList

    private val _favoriteList = MutableLiveData<List<Product>>()
    val favoriteList: LiveData<List<Product>> get() = _favoriteList

    private val _product = MutableLiveData<Product?>()
    val product: LiveData<Product?> get() = _product

    private val _productState = MutableLiveData<ProductState>()
    val productState: LiveData<ProductState> get() = _productState

    private val _favoriteState = MutableLiveData<ProductState>()
    val favoriteState: LiveData<ProductState> get() = _favoriteState

    fun loadProductInfo() {
        viewModelScope.launch {
            _productState.value = ProductState.Loading // Устанавливаем состояние загрузки
            try {
                // Получаем данные из таблицы products
                val response = supabase.from("products")
                    .select(columns = Columns.list("id", "name", "description", "price", "size", "quantity", "image")){
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

    fun loadProductInfoItemActivity(productId: Int) {
        viewModelScope.launch {
            _productState.value = ProductState.Loading // Устанавливаем состояние загрузки
            try {
                // Получаем данные из таблицы products
                val response = supabase.from("products")
                    .select(columns = Columns.list("id", "name", "description", "price", "size", "quantity", "image")){
                        filter {
                            Product::id eq productId
                        }
                    }
                    .decodeSingle<Product>() // Декодируем результат в список объектов Product

                _product.value = response // Обновляем LiveData
                _productState.value = ProductState.Success("Успех")// Устанавливаем состояние успешной загрузки
            } catch (e: Exception) {
                _productState.value = ProductState.Error(e.message ?: "Ошибка загрузки данных") // Обрабатываем ошибки
            }
        }
    }


    fun addProductToFavorites(product: Int?) {
        viewModelScope.launch {
            try {
                val userId = supabase.auth.currentUserOrNull()?.id
                if (userId != null) {
                    val favourite = Favorites(user_id = userId, product_id = product)
                    // Добавляем товар в таблицу favourite, используя upsert для предотвращения дублирования
                    supabase.from("favorites")
                        .upsert(favourite).decodeSingle<Favorites>()


                    _productState.value = ProductState.Success("Товар добавлен в избранное")
                } else {
                    _productState.value = ProductState.Error("Вы не авторизованы")
                }
            } catch (e: Exception) {
            }
        }
    }



    fun loadFavoriteProducts() {
        viewModelScope.launch {
            _favoriteState.value = ProductState.Loading
            try {
                val userId = supabase.auth.currentUserOrNull()?.id
                if (userId != null) {
                    val favouriteProductIdsResponse = supabase.from("favorites")
                        .select(columns = Columns.list("product_id")){
                            filter {
                                Favorites::user_id eq userId
                            }
                        }

                        .decodeList<Favorites>()

                    val favouriteProductIds = favouriteProductIdsResponse.map { it.product_id }

                    if (favouriteProductIds.isNotEmpty()) {
                        val response = supabase.from("products")
                            .select(columns = Columns.list("id", "name", "description", "price", "size", "quantity", "image")){
                                filter {
                                    Product::id isIn favouriteProductIds
                                }
                            }

                            .decodeList<Product>()

                        _favoriteList.value = response
                        _favoriteState.value = ProductState.Success("Успех")
                    } else {
                        _favoriteList.value = emptyList()
                    }
                } else {

                }
            } catch (e: Exception) {

            }
        }
    }


    fun removeProductFromFavorites(product: Int?) {
        viewModelScope.launch {
            try {
                val userId = supabase.auth.currentUserOrNull()?.id
                if (userId != null) {

                    supabase.from("favorites")
                        .delete {
                            filter {
                                Favorites::user_id eq userId
                                Favorites::product_id eq product
                            }
                        }

                    _productState.value = ProductState.Success("Товар удален из избранного")
                } else {
                    _productState.value = ProductState.Error("Вы не авторизованы")
                }
            } catch (e: Exception) {
            }
        }
    }

}