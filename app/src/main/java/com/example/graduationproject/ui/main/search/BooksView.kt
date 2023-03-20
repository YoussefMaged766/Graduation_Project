package com.example.graduationproject.ui.main.search

import androidx.paging.PagingData
import com.example.graduationproject.models.BooksItem

data class BookState(
    val allBooks: PagingData<BooksItem>? = null,
    val isLoading: Boolean = false,
    val error: String? = null
)
