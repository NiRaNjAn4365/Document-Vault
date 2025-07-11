package com.example.documentvault.models

import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.PrimaryKey

@Entity(tableName = "images", foreignKeys =  [ForeignKey(
    entity = Folders::class,
    parentColumns = ["id"],
    childColumns = ["folderId"],
    onDelete = ForeignKey.CASCADE
)])
data class Files(

    @PrimaryKey(autoGenerate = true) val id:Int?=null,
    val folderId: Int,
    val fileUri: String,
    val filePath: String,
    val fileName:String
)
