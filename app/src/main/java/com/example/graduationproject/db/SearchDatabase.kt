package com.example.graduationproject.db

import androidx.room.Database
import androidx.room.RoomDatabase
import com.example.graduationproject.models.HistorySearchEntity

@Database(entities = [HistorySearchEntity::class], version = 1)
abstract class SearchDatabase:RoomDatabase() {
abstract fun searchDao():BookDao
}