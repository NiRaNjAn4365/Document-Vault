package com.example.documentvault.dao

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.Query
import com.example.documentvault.models.Images
import kotlinx.coroutines.flow.Flow

@Dao
interface ImageDao {
    @Insert
    suspend fun insertImage(images: Images)

    @Query("SELECT * FROM images WHERE folderId=:folderId")
    fun retrieveImage(folderId:Int): Flow<List<Images>>

    @Delete
    suspend fun deleteImage(images: Images)
}