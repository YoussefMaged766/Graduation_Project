package com.example.graduationproject.models

import com.google.gson.annotations.SerializedName

data class BookResponse(
    @field:SerializedName("count")
    val count: Int? = null,

    @field:SerializedName("page")
    val page: String? = null,

    @field:SerializedName("results")
    val books: List<BooksItem>
)

data class BooksItem(

    @field:SerializedName("ratings")
    val ratings: Int? = null,

    @field:SerializedName("_id")
    val id: String? = null,

    @field:SerializedName("book_id")
    val bookId: Int? = null,

    @field:SerializedName("cover_image")
    val coverImage: String? = null,

    @field:SerializedName("title")
    val title: String? = null,

    @field:SerializedName("url")
    val url: String? = null,

    @field:SerializedName("mod_title")
    val modTitle: String? = null,

    @field:SerializedName("message")
    val message: String? = null
)
