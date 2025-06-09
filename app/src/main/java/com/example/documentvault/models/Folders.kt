package com.example.documentvault.models

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "folder")
data class Folders(
    @PrimaryKey(autoGenerate = true) val id: Int? = null,
    val name: String
)
