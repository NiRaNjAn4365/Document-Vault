package com.example.documentvault.viewModels

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.example.documentvault.repository.FilesRepository

class FilesViewModelFactory(private val repo: FilesRepository) : ViewModelProvider.Factory {
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        return FilesViewModel(repo) as T
    }
}