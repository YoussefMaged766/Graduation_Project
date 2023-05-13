package com.example.graduationproject.ui.main.search

import androidx.paging.PagingData
import com.example.graduationproject.models.BooksItem
import com.example.graduationproject.models.UserResponse

data class BookState(
    val allBooks: PagingData<BooksItem>? = null,
    val isLoading: Boolean = false,
    val error: String? = null,
    val success : String? = null,
    val loadingState:String? = null,
    val userResponse: UserResponse? = null
)
