package com.example.documentvault.dao

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.Query
import com.example.documentvault.models.Folders

@Dao
interface FoldersDao {
    @Insert
    suspend fun insertFolder(folders: Folders)

    @Query("SELECT * FROM folder")
    suspend fun retrivefolder(): List<Folders>

    @Delete
    suspend fun deleteFolder(folders: Folders)

}