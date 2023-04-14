package com.example.graduationproject.ui.main.wishlist

import androidx.paging.PagingData
import com.example.graduationproject.models.BooksItem

data class WishlistState(
    val allBooks: List<BooksItem>? = null,
    val isLoading: Boolean = false,
    val error: String? = null,
    val success : String? = null
)
