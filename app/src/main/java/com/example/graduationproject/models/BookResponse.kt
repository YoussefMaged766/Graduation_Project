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

//    @field:SerializedName("ratings")
//    val ratings: Int? = null,

    @field:SerializedName("_id")
    val id: String? = null,

    @field:SerializedName("book_id")
    val bookId: Int? = null,
    @field:SerializedName("description")
    val description: String? = null,

    @field:SerializedName("cover_image")
    val coverImage: String? = null,

    @field:SerializedName("title")
    val title: String? = null,

    @field:SerializedName("url")
    val url: String? = null,

    @field:SerializedName("mod_title")
    val modTitle: String? = null,

    @field:SerializedName("message")
    val message: String? = null,

    @field:SerializedName("publication_year")
    val publication_year: Any? = null,

    @field:SerializedName("country_code")
    val country_code: String? = null,

    @field:SerializedName("publisher")
    val publisher: String? = null,

    @field:SerializedName("isbn13")
    val isbn13: String? = null,



) : java.io.Serializable

data class BookIdResponse(
    @field:SerializedName("bookId")
    val bookId: String? = null,
    @field:SerializedName("message")
    val message: String? = null

)
data class WishAndFavResponse(

    @field:SerializedName("message")
    val message: String? = null,

    @field:SerializedName("results")
    val results: Results? = null
)

data class Results(

    @field:SerializedName("books")
    val books: List<BooksItem1>? = null
)

data class BooksItem1(

    @field:SerializedName("is_read")
    val isRead: Int? = null,

    @field:SerializedName("book_item")
    val bookItem: BooksItem? = null,

    @field:SerializedName("is_reviewed")
    val isReviewed: Int? = null,

    @field:SerializedName("rating")
    val rating: Int? = null,

    @field:SerializedName("_id")
    val id: String? = null
)