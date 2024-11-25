package com.example.ocsapp.viewmodel

import android.content.Context
import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.ocsapp.data.SharedPreferenceHelper
import com.example.ocsapp.data.SupabaseClient.supabase
import com.example.ocsapp.data.User
import com.example.ocsapp.data.UserState
import io.github.jan.supabase.auth.auth
import io.github.jan.supabase.auth.providers.builtin.Email
import io.github.jan.supabase.postgrest.from
import io.github.jan.supabase.postgrest.postgrest
import kotlinx.coroutines.launch
import kotlinx.serialization.json.buildJsonObject
import kotlinx.serialization.json.put

class SupabaseAuthViewModel : ViewModel() {
    private val _user = MutableLiveData<User>()
    val user: LiveData<User> get() = _user

    private val _userState = MutableLiveData<UserState>()
    val userState: LiveData<UserState> get() = _userState

    fun signUp(
        context: Context,
        firstName: String,
        lastName: String,
        userEmail: String,
        userPhone: String,
        userPassword: String,
        avatar: String
    ) {
        viewModelScope.launch {
            _userState.value = UserState.Loading
            try {
                val response = supabase.auth.signUpWith(Email) {
                    email = userEmail
                    password = userPassword

                }

                saveToken(context)

                addUserToDatabase(firstName, lastName, userEmail, userPhone, avatar)
                _userState.value = UserState.Success("Регистрация прошла успешно")
            } catch (e: Exception) {
                _userState.value = UserState.Error("Ошибка: ${e.message}")
            }
        }
    }

    private fun saveToken(context: Context) {
        viewModelScope.launch {
            val accessToken = supabase.auth.currentAccessTokenOrNull() ?: ""
            val sharedPref = SharedPreferenceHelper(context)
            sharedPref.saveStringData("accessToken", accessToken)
        }
    }

    private fun getToken(context: Context): String? {
        val sharedPref = SharedPreferenceHelper(context)
        return sharedPref.getStringData("accessToken")
    }

    fun login(
        context: Context,
        userEmail: String,
        userPassword: String
    ) {
        viewModelScope.launch {
            _userState.value = UserState.Loading
            try {
                supabase.auth.signInWith(Email) {
                    email = userEmail
                    password = userPassword
                }
                saveToken(context)
                _userState.value = UserState.Success("Авторизация прошла успешно")
            } catch (e: Exception) {
                _userState.value = UserState.Error("Ошибка: ${e.message}")
            }
        }
    }

    fun logout() {
        viewModelScope.launch {
            _userState.value = UserState.Loading
            try {
                supabase.auth.signOut()
                _userState.value = UserState.Success("Вы вышли из аккаунта")
            } catch (e: Exception) {
                _userState.value = UserState.Error("Ошибка: ${e.message}")
            }
        }
    }

    fun isUserLoggedIn(context: Context) {
        viewModelScope.launch {
            _userState.value = UserState.Loading
            try {
                val token = getToken(context)
                if (token.isNullOrEmpty()) {
                    _userState.value = UserState.Error("Пользователь не авторизован")
                } else {
                    supabase.auth.retrieveUser(token)
                    supabase.auth.refreshCurrentSession()
                    saveToken(context)
                    _userState.value = UserState.Success("Пользователь авторизован")
                }
            } catch (e: Exception) {
                _userState.value = UserState.Error("Ошибка: ${e.message}")
            }
        }
    }

    private suspend fun addUserToDatabase(firstName: String, lastName: String, userEmail: String, userPhone: String, avatar: String) {
        try {
            val newUser = mapOf(

                "firstname" to firstName,
                "lastname" to lastName,
                "email" to userEmail,
                "phone" to userPhone,
                "avatar" to avatar
            )

            supabase.postgrest["users"].upsert(newUser)


        } catch (e: Exception) {
            _userState.value = UserState.Error("Ошибка базы данных: ${e.message}")
            Log.d("error","Ошибка базы данных: ${e.message}")
        }
    }
}
