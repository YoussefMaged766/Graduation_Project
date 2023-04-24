package com.example.graduationproject.models

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey
import com.google.gson.annotations.SerializedName

@Entity(tableName = "BookEntity")
data class BookEntity(

    val ratings: Int? = null,


    @PrimaryKey(autoGenerate = false)
    val bookId: Int? = null,

    val description: String? = null,


    val coverImage: String? = null,


    val title: String? = null,


    val url: String? = null,


    val modTitle: String? = null,


    val message: String? = null,

    @ColumnInfo(name = "is_favorite")
    val IsFavorite: Int = 0,

    val userId: String? = null


)
