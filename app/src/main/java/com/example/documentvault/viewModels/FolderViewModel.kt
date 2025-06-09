package com.example.documentvault.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.documentvault.models.Folders
import com.example.documentvault.repository.FolderRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch

class FolderViewModel(private val repository: FolderRepository) : ViewModel() {

    private val _folders= MutableStateFlow<List<Folders>>(emptyList())
    val folders: StateFlow<List<Folders>> get() = _folders

    fun loadFolders(){
        viewModelScope.launch {
            _folders.value=repository.retrivefolder()
        }
    }

    fun addFolders(folder: Folders){
        viewModelScope.launch {
            repository.insertFolder(folder)
            loadFolders()
        }
    }

    fun deleteFolders(folder: Folders){
        viewModelScope.launch {
            repository.deleteFolder(folder)
            loadFolders()
        }
    }

}