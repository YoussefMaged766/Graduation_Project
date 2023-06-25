package com.example.graduationproject.ui.main.wishlist

import com.example.graduationproject.models.BookEntity
import com.example.graduationproject.models.BooksItem

data class WishlistState(
    val allBooks: List<BooksItem>? = null,
    val isLoading: Boolean = false,
    val error: String? = null,
    val success: String? = null,
    val allLocalBooks: List<BookEntity?>? = null,
)
