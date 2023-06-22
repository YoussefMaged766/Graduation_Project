package com.example.graduationproject.models.mappers

import com.example.graduationproject.models.BookEntity
import com.example.graduationproject.models.BookResponse
import com.example.graduationproject.models.BooksItem

fun BooksItem.toBookEntity(userId:String) = BookEntity(

    title = this.title,
    description = this.description,
    coverImage = this.coverImage,
    url = this.url,
    modTitle = this.modTitle,
//    ratings = this.ratings,
    bookId = this.bookId,
    userId = userId,
    bookIdMongo = this.id
)





