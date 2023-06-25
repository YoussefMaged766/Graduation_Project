package com.example.graduationproject.ui.main.favorite

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

    private val _stateBook = MutableStateFlow(WishlistState())
    val stateBook = _stateBook.asStateFlow()

    private val _stateFav = MutableStateFlow(WishlistState())
    val stateFav = _stateFav.asStateFlow()



    suspend fun getAllFavorite(token:String,userId:String)=viewModelScope.launch {
        bookRepo.getAllFavorite(token,userId).collectLatest { resource ->
            when(resource){
                is  Status.Loading-> {
                    _stateFav.value = stateFav.value.copy(
                        isLoading = true
                    )
                }
                is   Status.Success-> {
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

    suspend fun getSingleBook(token:String,id:String)=viewModelScope.launch {
        bookRepo.getSingleBook(token,id).collectLatest { resource ->
            when(resource){
                is  Status.Loading-> {
                    _stateBook.value = stateBook.value.copy(
                        isLoading = true
                    )
                }
                is   Status.Success-> {
                    _stateBook.value = stateBook.value.copy(
                        isLoading = false,
                        allBooks = resource.data.books
                    )
                }
                is  Status.Error-> {
                    _stateBook.value = stateBook.value.copy(
                        error = resource.message,
                        isLoading = false
                    )
                }

            }
        }
    }
}