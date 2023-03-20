package com.example.graduationproject.ui.main.home

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
class HomeViewModel @Inject constructor(
    private val bookRepo: BookRepo,
    private val webServices: WebServices
) : ViewModel() {
    private val _state = MutableStateFlow(BookState())
     val state = _state.asStateFlow()

    val data:MutableLiveData<PagingData<BooksItem>> = MutableLiveData()


    suspend fun getAllBooks( token: String) = viewModelScope.launch(Dispatchers.IO) {

        bookRepo.getAllBooks(token).collectLatest { resource ->
            when(resource.status){
                Status.LOADING-> {
                    _state.value = state.value.copy(
                        isLoading = true
                    )
                }
                Status.SUCCESS-> {
                    _state.value = state.value.copy(
                        allBooks = resource.data,
                        isLoading = false
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

     fun Books(token: String){
        viewModelScope.launch {
            Pager(
                config = PagingConfig(
                    pageSize = 20,
                    enablePlaceholders = false
                ),
                pagingSourceFactory = { HomePagingSource( webServices,token) }
            ).flow.cachedIn(viewModelScope).collectLatest {
                data.postValue(it)
            }
        }
    }

}