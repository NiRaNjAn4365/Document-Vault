package com.example.documentvault.repository

import com.example.documentvault.dao.FoldersDao
import com.example.documentvault.models.Folders

class FolderRepository(private val foldersDao: FoldersDao) {

    suspend fun insertFolder(folders: Folders)=foldersDao.insertFolder(folders)
    suspend fun retrivefolder()=foldersDao.retrivefolder()
    suspend fun deleteFolder(folders: Folders)=foldersDao.deleteFolder(folders)
}