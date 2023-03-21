package com.example.graduationproject.ui.main.favorite

import android.util.Log
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import androidx.paging.Pager
import androidx.paging.PagingConfig
import androidx.paging.PagingData
import androidx.paging.cachedIn
import com.example.graduationproject.data.paging.HomePagingSource
import com.example.graduationproject.data.repository.BookRepo
import com.example.graduationproject.models.BookIdResponse
import com.example.graduationproject.models.BooksItem
import com.example.graduationproject.ui.main.search.BookState
import com.example.graduationproject.utils.Status
import com.example.graduationproject.utils.WebServices
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class FavoriteViewModel @Inject constructor(
    private val bookRepo: BookRepo,
    private val webServices: WebServices
) : ViewModel() {
    private val _state = MutableStateFlow(BookState())
     val state = _state.asStateFlow()


    suspend fun setFavorite( token: String,bookId : BookIdResponse) = viewModelScope.launch(Dispatchers.IO) {

        bookRepo.addFavorite(token,bookId).collectLatest { resource ->
            when(resource.status){
                Status.LOADING-> {
                    _state.value = state.value.copy(
                        isLoading = true
                    )
                }
                Status.SUCCESS-> {
                    _state.value = state.value.copy(
                        isLoading = false,
                        success = resource.data?.message.toString()
                    )
                }
                Status.ERROR-> {
                    _state.value = state.value.copy(
                        error = resource.message,
                        isLoading = false

                    )
                    Log.e( "getAllBooks: ", resource.message.toString())
                }
                else -> {}
            }
        }
    }
}