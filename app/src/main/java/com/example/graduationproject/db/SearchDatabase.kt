package com.example.graduationproject.db

import androidx.room.Database
import androidx.room.RoomDatabase

@Database(entities = [HistorySearchEntity::class], version = 2)
abstract class SearchDatabase:RoomDatabase() {
abstract fun searchDao():SearchDao
}