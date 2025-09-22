package com.example.data.model

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.example.data.repository.JuegoRepository

class JuegosViewModelFactory(
    private val repository: JuegoRepository
) : ViewModelProvider.Factory {
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(JuegosViewModel::class.java)) {
            @Suppress("UNCHECKED_CAST")
            return JuegosViewModel(repository) as T
        }
        throw IllegalArgumentException("Unknown ViewModel Class")
    }
}