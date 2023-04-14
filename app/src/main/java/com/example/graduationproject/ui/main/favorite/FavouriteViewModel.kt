package com.example.graduationproject.ui.main.favorite

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.graduationproject.data.repository.BookRepo
import com.example.graduationproject.ui.main.wishlist.WishlistState
import com.example.graduationproject.utils.Status
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class FavouriteViewModel @Inject constructor(private val bookRepo: BookRepo):ViewModel() {

    private val _stateFav = MutableStateFlow(WishlistState())
    val stateFav = _stateFav.asStateFlow()



    suspend fun getAllFavorite(token:String)=viewModelScope.launch {
        bookRepo.getAllFavorite(token).collectLatest { resource ->
            when(resource.status){
                Status.LOADING-> {
                    _stateFav.value = stateFav.value.copy(
                        isLoading = true
                    )
                }
                Status.SUCCESS-> {
                    _stateFav.value = stateFav.value.copy(
                        isLoading = false,
                        allBooks = resource.data?.results?.books
                    )


                }
                Status.ERROR-> {
                    _stateFav.value = stateFav.value.copy(
                        error = resource.message,
                        isLoading = false
                    )
                }
                else -> {}
            }
        }
    }
}