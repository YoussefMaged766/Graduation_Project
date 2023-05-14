package com.example.graduationproject.db

import androidx.room.Database
import androidx.room.RoomDatabase
import com.example.graduationproject.models.BookEntity
import com.example.graduationproject.models.HistorySearchEntity

@Database(entities = [HistorySearchEntity::class, BookEntity::class], version = 1)
abstract class BookDatabase:RoomDatabase() {
abstract fun searchDao():BookDao
}

