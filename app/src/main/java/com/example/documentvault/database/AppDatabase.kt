package com.example.documentvault.database

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import com.example.documentvault.dao.FoldersDao
import com.example.documentvault.dao.ImageDao
import com.example.documentvault.models.Folders
import com.example.documentvault.models.Images
import kotlin.jvm.java

@Database(entities = [Folders::class,Images::class], version = 3)
abstract class AppDatabase : RoomDatabase(){
    abstract fun folderDao() : FoldersDao
    abstract fun imagesDao(): ImageDao

    companion object{
        @Volatile private var INSTANCE: AppDatabase? = null

        fun getDatabase(context: Context): AppDatabase {
            return INSTANCE ?: synchronized(this) {
                val instance = Room.databaseBuilder(
                    context.applicationContext,
                    AppDatabase::class.java,
                    "app_database"
                ).fallbackToDestructiveMigration()
                    .build()
                INSTANCE = instance
                instance
            }
        }
    }

}