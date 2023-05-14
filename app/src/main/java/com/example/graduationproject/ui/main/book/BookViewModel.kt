package com.example.graduationproject.ui.main.book

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.graduationproject.data.repository.BookRepo
import com.example.graduationproject.models.BookIdResponse
import com.example.graduationproject.models.BooksItem
import com.example.graduationproject.ui.main.search.BookState
import com.example.graduationproject.ui.main.wishlist.WishlistState
import com.example.graduationproject.utils.Status
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.collect
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

    private val _stateRead = MutableStateFlow(BookState())
    val stateRead = _stateRead.asStateFlow()

    private val _stateFav = MutableStateFlow(WishlistState())
    val stateFav = _stateFav.asStateFlow()





    suspend fun getAllFavorite(token:String, userId: String)=viewModelScope.launch {
        bookRepo.getAllFavorite(token,userId).collectLatest { resource ->
            when(resource){
                is Status.Loading-> {
                    _stateFav.value = stateFav.value.copy(
                        isLoading = true
                    )
                }
                is  Status.Success-> {
                    _stateFav.value = stateFav.value.copy(
                        isLoading = false,
                        allLocalBooks = resource.data
                    )


                }
                is  Status.Error-> {
                    _stateFav.value = stateFav.value.copy(
                        error = resource.message,
                        isLoading = false
                    )
                }

            }
        }
    }

    suspend fun setFavorite( token: String,bookId : BookIdResponse,booksItem: BooksItem,userId: String) = viewModelScope.launch(Dispatchers.IO) {

        bookRepo.addFavorite(token,bookId,booksItem,userId).collectLatest { resource ->
            when(resource){
             is   Status.Loading-> {
                    _stateFavourite.value = stateFavourite.value.copy(
                        isLoading = true
                    )
                }
                is   Status.Success-> {
                    _stateFavourite.value = stateFavourite.value.copy(
                        isLoading = false,
                        success = resource.data?.message.toString()
                    )
                    Log.e( "getAllBooks: ", resource.data?.message.toString())
                }
                is   Status.Error-> {
                    _stateFavourite.value = stateFavourite.value.copy(
                        error = resource.message,
                        isLoading = false

                    )

                }

            }
        }
    }

    suspend fun removeFavorite( token: String,bookId : BookIdResponse,booksItem: BooksItem,userId: String) = viewModelScope.launch(Dispatchers.IO) {

        bookRepo.removeFavorite(token,bookId,booksItem,userId).collectLatest { resource ->
            when(resource){
                is   Status.Loading-> {
                    _stateRemoveFavourite.value = stateRemoveFavourite.value.copy(
                        isLoading = true
                    )
                }
                is  Status.Success-> {
                    _stateRemoveFavourite.value = stateRemoveFavourite.value.copy(
                        isLoading = false,
                        success = resource.data?.message.toString()
                    )
                    Log.e( "getAllBooks: ", resource.data?.message.toString())
                }
                is  Status.Error-> {
                    _stateRemoveFavourite.value = stateRemoveFavourite.value.copy(
                        error = resource.message,
                        isLoading = false

                    )

                }

            }
        }
    }

    suspend fun setWishlist( token: String,bookId : BookIdResponse,booksItem: BooksItem,userId: String) = viewModelScope.launch(Dispatchers.IO) {

        bookRepo.addToWishlist(token,bookId,booksItem, userId).collectLatest { resource ->
            when(resource){
                is  Status.Loading-> {
                    _stateWishlist.value = stateWishlist.value.copy(
                        isLoading = true
                    )
                }
                is   Status.Success-> {
                    _stateWishlist.value = stateWishlist.value.copy(
                        isLoading = false,
                        success = resource.data?.message.toString()
                    )
                    Log.e( "getAllBooks: ", resource.data?.message.toString())
                }
                is Status.Error-> {
                    _stateWishlist.value = stateWishlist.value.copy(
                        error = resource.message,
                        isLoading = false

                    )

                }
            }
        }
    }


    suspend fun setRead( token: String,bookId : BookIdResponse,booksItem: BooksItem,userId: String) = viewModelScope.launch(Dispatchers.IO) {

        bookRepo.addToRead(token,bookId,booksItem, userId).collectLatest { resource ->
            when(resource){
                is  Status.Loading-> {
                    _stateRead.value = stateRead.value.copy(
                        isLoading = true
                    )
                }
                is   Status.Success-> {
                    _stateRead.value = stateRead.value.copy(
                        isLoading = false,
                        success = resource.data?.message.toString()
                    )
                    Log.e( "getAllBooks: ", resource.data?.message.toString())
                }
                is Status.Error-> {
                    _stateRead.value = stateRead.value.copy(
                        error = resource.message,
                        isLoading = false

                    )

                }
            }
        }
    }



}