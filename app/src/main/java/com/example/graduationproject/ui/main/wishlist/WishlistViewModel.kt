package com.example.graduationproject.ui.main.wishlist

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
class WishlistViewModel @Inject constructor(private val bookRepo: BookRepo):ViewModel() {

    private val _stateWishlist = MutableStateFlow(WishlistState())
    val stateWishlist = _stateWishlist.asStateFlow()

    private val _stateRemoveWishlist = MutableStateFlow(BookState())
    val stateRemoveWishlist = _stateRemoveWishlist.asStateFlow()

    suspend fun getAllWishlist(token:String,userId: String)=viewModelScope.launch {
        bookRepo.getAllWishlist(token,userId).collectLatest { resource ->
            when(resource){
              is  Status.Loading-> {
                    _stateWishlist.value = stateWishlist.value.copy(
                        isLoading = true
                    )
                }
              is  Status.Success-> {
                    _stateWishlist.value = stateWishlist.value.copy(
                        isLoading = false,
                        allLocalBooks = resource.data
                    )


                }
                is   Status.Error-> {
                    _stateWishlist.value = stateWishlist.value.copy(
                        error = resource.message,
                        isLoading = false
                    )
                }
            }
        }
    }

    suspend fun removeWishlist( token: String,bookId : BookIdResponse,booksItem: Int,userId: String) = viewModelScope.launch(
        Dispatchers.IO) {

        bookRepo.removeWishlist(token,bookId,booksItem,userId).collectLatest { resource ->
            when(resource){
                is    Status.Loading-> {
                    _stateRemoveWishlist.value = stateRemoveWishlist.value.copy(
                        isLoading = true
                    )
                }
                is Status.Success-> {
                    _stateRemoveWishlist.value = stateRemoveWishlist.value.copy(
                        isLoading = false,
                        success = resource.data?.message.toString()
                    )
                    Log.e( "getAllBooks: ", resource.data?.message.toString())
                }
                is  Status.Error-> {
                    _stateRemoveWishlist.value = stateRemoveWishlist.value.copy(
                        error = resource.message,
                        isLoading = false

                    )

                }

            }
        }
    }
}