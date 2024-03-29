package com.example.graduationproject.ui.main.home

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
import com.example.graduationproject.ui.main.wishlist.WishlistState
import com.example.graduationproject.utils.RecommendationService
import com.example.graduationproject.utils.Status
import com.example.graduationproject.utils.WebServices
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Dispatchers
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

    private val _stateReco = MutableStateFlow(WishlistState())
    val stateReco = _stateReco.asStateFlow()

    val data:MutableLiveData<PagingData<BooksItem>> = MutableLiveData()

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

    suspend fun getRecommendation(id: String) =viewModelScope.launch(Dispatchers.IO) {
        bookRepo.getRecommendation(id).collect{
            when (it) {
                is Status.Loading -> {
                    _stateReco.value = stateReco.value.copy(
                        isLoading = true
                    )
                }

                is Status.Success -> {
                    _stateReco.value = stateReco.value.copy(
                        isLoading = false,
                        allBooks = it.data.books
                    )

                }

                is Status.Error -> {
                    _stateReco.value = stateReco.value.copy(
                        isLoading = false,
                        error = it.message
                    )
                }
            }
        }
    }

}