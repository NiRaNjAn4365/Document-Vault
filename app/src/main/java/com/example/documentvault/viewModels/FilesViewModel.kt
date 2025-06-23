package com.example.documentvault.viewModels

import android.content.Context
import android.net.Uri
import androidx.compose.runtime.mutableStateOf
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.documentvault.models.Files
import com.example.documentvault.repository.FilesRepository
import com.example.documentvault.screens.getFileNameFromUri
import kotlinx.coroutines.launch

class FilesViewModel(private val repo: FilesRepository) : ViewModel() {

    private val _files = mutableStateOf<List<Files>>(emptyList())
    val files = _files

    fun addImages(folderId: Int, files: List<Pair<Uri, String>>, context: Context) {
        viewModelScope.launch {
            val newFiles = files.map { (uri, type) ->
                val fileName = getFileNameFromUri(context, uri) ?: "Unknown"
                Files(
                    folderId = folderId,
                    fileUri = uri.toString(),
                    filePath = type,
                    fileName = fileName
                )
            }

            _files.value = _files.value + newFiles

            newFiles.forEach { file ->
                repo.insertImage(file)
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
                        filePath=it.filePath,
                        fileName = it.fileName
                    )
                }
            }
        }
    }
}