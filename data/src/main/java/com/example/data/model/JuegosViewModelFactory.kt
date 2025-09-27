package com.example.data.model

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.example.data.repository.JuegoRepository
import com.example.data.repository.ReviewsRepository

class JuegosViewModelFactory(
    private val juegoRepository: JuegoRepository,
    private val reviewsRepository: ReviewsRepository
) : ViewModelProvider.Factory {
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(JuegosViewModel::class.java)) {
            @Suppress("UNCHECKED_CAST")
            return JuegosViewModel(juegoRepository, reviewsRepository) as T
        }
        throw IllegalArgumentException("Unknown ViewModel Class")
    }
}