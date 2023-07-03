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
    ratings = this.ratings_count,
    bookId = this.bookId,
    userId = userId,
    bookIdMongo = this.id,
    isbn13 = this.isbn13,

)

fun BookEntity.toBookItem() = BooksItem(

    title = this.title,
    description = this.description,
    coverImage = this.coverImage,
    url = this.url,
    modTitle = this.modTitle,
    ratings_count = this.ratings,
    bookId = this.bookId,
    id = this.bookIdMongo,
    isbn13 = this.isbn13
)





