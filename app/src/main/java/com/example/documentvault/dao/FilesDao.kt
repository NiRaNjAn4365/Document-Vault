package com.example.documentvault.dao

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.Query
import com.example.documentvault.models.Files
import kotlinx.coroutines.flow.Flow

@Dao
interface FilesDao {
    @Insert
    suspend fun insertImage(files: Files)

    @Query("SELECT * FROM images WHERE folderId=:folderId")
    fun retrieveImage(folderId:Int): Flow<List<Files>>

    @Delete
    suspend fun deleteImage(files: Files)
}