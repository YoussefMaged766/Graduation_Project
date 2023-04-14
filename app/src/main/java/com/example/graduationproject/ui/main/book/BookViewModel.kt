package com.example.graduationproject.ui.main.book

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.graduationproject.data.repository.BookRepo
import com.example.graduationproject.models.BookIdResponse
import com.example.graduationproject.ui.main.search.BookState
import com.example.graduationproject.utils.Status
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class BookViewModel @Inject constructor(
    private val bookRepo: BookRepo,
) : ViewModel() {
    private val _stateFavourite = MutableStateFlow(BookState())
     val stateFavourite = _stateFavourite.asStateFlow()

    private val _stateRemoveFavourite = MutableStateFlow(BookState())
    val stateRemoveFavourite = _stateRemoveFavourite.asStateFlow()

    private val _stateWishlist = MutableStateFlow(BookState())
    val stateWishlist = _stateWishlist.asStateFlow()


    suspend fun setFavorite( token: String,bookId : BookIdResponse) = viewModelScope.launch(Dispatchers.IO) {

        bookRepo.addFavorite(token,bookId).collectLatest { resource ->
            when(resource.status){
                Status.LOADING-> {
                    _stateFavourite.value = stateFavourite.value.copy(
                        isLoading = true
                    )
                }
                Status.SUCCESS-> {
                    _stateFavourite.value = stateFavourite.value.copy(
                        isLoading = false,
                        success = resource.data?.message.toString()
                    )
                    Log.e( "getAllBooks: ", resource.data?.message.toString())
                }
                Status.ERROR-> {
                    _stateFavourite.value = stateFavourite.value.copy(
                        error = resource.message,
                        isLoading = false

                    )

                }
                else -> {}
            }
        }
    }

    suspend fun removeFavorite( token: String,bookId : BookIdResponse) = viewModelScope.launch(Dispatchers.IO) {

        bookRepo.removeFavorite(token,bookId).collectLatest { resource ->
            when(resource.status){
                Status.LOADING-> {
                    _stateRemoveFavourite.value = stateRemoveFavourite.value.copy(
                        isLoading = true
                    )
                }
                Status.SUCCESS-> {
                    _stateRemoveFavourite.value = stateRemoveFavourite.value.copy(
                        isLoading = false,
                        success = resource.data?.message.toString()
                    )
                    Log.e( "getAllBooks: ", resource.data?.message.toString())
                }
                Status.ERROR-> {
                    _stateRemoveFavourite.value = stateRemoveFavourite.value.copy(
                        error = resource.message,
                        isLoading = false

                    )

                }
                else -> {}
            }
        }
    }

    suspend fun setWishlist( token: String,bookId : BookIdResponse) = viewModelScope.launch(Dispatchers.IO) {

        bookRepo.addToWishlist(token,bookId).collectLatest { resource ->
            when(resource.status){
                Status.LOADING-> {
                    _stateWishlist.value = stateWishlist.value.copy(
                        isLoading = true
                    )
                }
                Status.SUCCESS-> {
                    _stateWishlist.value = stateWishlist.value.copy(
                        isLoading = false,
                        success = resource.data?.message.toString()
                    )
                    Log.e( "getAllBooks: ", resource.data?.message.toString())
                }
                Status.ERROR-> {
                    _stateWishlist.value = stateWishlist.value.copy(
                        error = resource.message,
                        isLoading = false

                    )

                }
                else -> {}
            }
        }
    }
}