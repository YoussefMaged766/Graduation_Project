package com.example.graduationproject.models

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity (tableName = "history_search")
data class HistorySearchEntity(
    @PrimaryKey(autoGenerate = false)
    val id: Long? = null,
    val query: String? = null,
    val userId: String? = null

)
