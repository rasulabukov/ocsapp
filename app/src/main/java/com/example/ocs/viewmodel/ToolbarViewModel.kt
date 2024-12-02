package com.example.ocs.viewmodel

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel

class ToolbarViewModel : ViewModel() {
    private val _toolbarTitle = MutableLiveData<String>()
    val toolbarTitle: LiveData<String> get() = _toolbarTitle

    fun setToolbarTitle(title: String) {
        _toolbarTitle.value = title
    }
}