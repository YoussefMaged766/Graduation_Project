package com.example.graduationproject.ui.main.search


import android.util.Log
import androidx.lifecycle.*
import androidx.paging.Pager
import androidx.paging.PagingConfig
import androidx.paging.PagingData
import androidx.paging.cachedIn
import com.example.graduationproject.data.paging.HomePagingSource
import com.example.graduationproject.data.paging.SearchPagingSource
import com.example.graduationproject.data.repository.BookRepo
import com.example.graduationproject.models.BooksItem
import com.example.graduationproject.utils.Status
import com.example.graduationproject.utils.WebServices
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class SearchResultViewModel @Inject constructor(
    private val bookRepo: BookRepo,
    private val webServices: WebServices
    ) : ViewModel(){

    val data:MutableLiveData<PagingData<BooksItem>> = MutableLiveData()

    fun searchResult(token: String , query:String){
        viewModelScope.launch(Dispatchers.IO) {
            Pager(
                config = PagingConfig(
                    pageSize = 20,
                    enablePlaceholders = false
                ),
                pagingSourceFactory = { SearchPagingSource( webServices,token,query) }
            ).flow.cachedIn(viewModelScope).collectLatest {
                data.postValue(it)
            }
        }
    }

}