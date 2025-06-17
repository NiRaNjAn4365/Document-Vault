package com.example.documentvault.viewModels

import android.net.Uri
import androidx.compose.runtime.mutableStateOf
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.documentvault.models.Files
import com.example.documentvault.repository.FilesRepository
import kotlinx.coroutines.launch

class FilesViewModel(private val repo: FilesRepository) : ViewModel() {

    private val _files = mutableStateOf<List<Files>>(emptyList())
    val files = _files

    fun addImages(folderId: Int, files: List<Pair<Uri, String>>) {
        _files.value = _files.value + files.map { (uri, type) ->
            Files(
                folderId = folderId,
                fileUri = uri.toString(),
                filePath = type
            )
        }

        viewModelScope.launch {
            files.forEach { (uri, type) ->
                repo.insertImage(
                    Files(
                        folderId = folderId,
                        fileUri = uri.toString(),
                        filePath = type
                    )
                )
            }
        }
    }


    fun deleteImages(files: Files) {
        viewModelScope.launch {
            repo.deleteImage(files)
        }
    }

    fun loadImages(folderId: Int) {
        viewModelScope.launch {
            repo.getImagesByFolder(folderId).collect { imageList ->
                _files.value = imageList.map {
                    Files(
                        folderId = it.folderId,
                        id = it.id,
                        fileUri = it.fileUri,
                        filePath=it.filePath
                    )
                }
            }
        }
    }
}