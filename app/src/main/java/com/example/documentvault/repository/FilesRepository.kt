package com.example.documentvault.repository

import com.example.documentvault.dao.FilesDao
import com.example.documentvault.models.Files

import kotlinx.coroutines.flow.Flow

class FilesRepository(private val filesDao: FilesDao) {

    suspend fun insertImage(files: Files)=filesDao.insertImage(files)

     fun getImagesByFolder(folderId: Int): Flow<List<Files>> = filesDao.retrieveImage(folderId)


    suspend fun deleteImage(files: Files)=filesDao.deleteImage(files)
}