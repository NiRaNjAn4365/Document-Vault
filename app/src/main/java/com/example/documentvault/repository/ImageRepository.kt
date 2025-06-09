package com.example.documentvault.repository

import com.example.documentvault.dao.ImageDao
import com.example.documentvault.models.Images
import kotlinx.coroutines.flow.Flow

class ImageRepository(private val imageDao: ImageDao) {

    suspend fun insertImage(images: Images)=imageDao.insertImage(images)

     fun getImagesByFolder(folderId: Int): Flow<List<Images>> = imageDao.retrieveImage(folderId)


    suspend fun deleteImage(images: Images)=imageDao.deleteImage(images)
}