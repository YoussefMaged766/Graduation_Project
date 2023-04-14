package com.example.graduationproject.ui.main.wishlist

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.graduationproject.data.repository.BookRepo
import com.example.graduationproject.utils.Status
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class WishlistViewModel @Inject constructor(private val bookRepo: BookRepo):ViewModel() {

    private val _stateWishlist = MutableStateFlow(WishlistState())
    val stateWishlist = _stateWishlist.asStateFlow()



    suspend fun getAllWishlist(token:String)=viewModelScope.launch {
        bookRepo.getAllWishlist(token).collectLatest { resource ->
            when(resource.status){
                Status.LOADING-> {
                    _stateWishlist.value = stateWishlist.value.copy(
                        isLoading = true
                    )
                }
                Status.SUCCESS-> {
                    _stateWishlist.value = stateWishlist.value.copy(
                        isLoading = false,
                        allBooks = resource.data?.results?.books
                    )


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