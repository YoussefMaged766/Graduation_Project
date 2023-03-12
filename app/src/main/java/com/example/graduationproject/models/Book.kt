package com.example.graduationproject.models

data class Book(
    val books: List<BookX>
)

data class BookX(
    val _id: String,
    val book_id: Int,
    val cover_image: String,
    val mod_title: String,
    val ratings: Int,
    val title: String,
    val url: String,
    val message: String
)

