package com.example.documentvault.viewModels

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.example.documentvault.repository.ImageRepository

class PhotoViewModelFactory(private val repo: ImageRepository) : ViewModelProvider.Factory {
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        return PhotosViewModel(repo) as T
    }
}