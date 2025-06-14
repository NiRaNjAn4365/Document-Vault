package com.example.documentvault.viewModels

import android.net.Uri
import androidx.compose.runtime.mutableStateOf
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.documentvault.models.Images
import com.example.documentvault.repository.ImageRepository
import kotlinx.coroutines.launch

class PhotosViewModel(private val repo: ImageRepository) : ViewModel() {

    private val _images = mutableStateOf<List<Images>>(emptyList())
    val images = _images

    fun addImages(folderId: Int, uris: List<Uri>) {
        _images.value = (_images.value + uris) as List<Images>
        viewModelScope.launch {
            uris.forEach { uri ->
                repo.insertImage(Images(folderId = folderId, imageUri = uri.toString()))
            }
        }
    }

    fun deleteImages(images: Images) {
        viewModelScope.launch {
            repo.deleteImage(images)
        }
    }

    fun loadImages(folderId: Int) {
        viewModelScope.launch {
            repo.getImagesByFolder(folderId).collect { imageList ->

                _images.value = imageList.map { Images(folderId=it.folderId, id = it.id, imageUri = Uri.parse(it.imageUri).toString())   }            }
        }
    }

}