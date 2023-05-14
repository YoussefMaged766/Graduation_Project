package com.example.graduationproject.ui.main.read

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.graduationproject.data.repository.BookRepo
import com.example.graduationproject.models.BookIdResponse
import com.example.graduationproject.ui.main.search.BookState
import com.example.graduationproject.ui.main.wishlist.WishlistState
import com.example.graduationproject.utils.Status
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class ReadViewModel @Inject constructor(private val bookRepo: BookRepo):ViewModel() {

    private val _stateRead = MutableStateFlow(WishlistState())
    val stateRead = _stateRead.asStateFlow()

    private val _stateRemoveRead = MutableStateFlow(BookState())
    val stateRemoveRead = _stateRemoveRead.asStateFlow()

    suspend fun getAllRead(token:String,userId: String)=viewModelScope.launch {
        bookRepo.getAllRead(token,userId).collectLatest { resource ->
            when(resource){
              is  Status.Loading-> {
                    _stateRead.value = stateRead.value.copy(
                        isLoading = true
                    )
                }
              is  Status.Success-> {
                    _stateRead.value = stateRead.value.copy(
                        isLoading = false,
                        allLocalBooks = resource.data
                    )


                }
                is   Status.Error-> {
                    _stateRead.value = stateRead.value.copy(
                        error = resource.message,
                        isLoading = false
                    )
                }
            }
        }
    }

    suspend fun removeRead( token: String,bookId : BookIdResponse,booksItem: Int,userId: String) = viewModelScope.launch(
        Dispatchers.IO) {

        bookRepo.removeRead(token,bookId,booksItem,userId).collectLatest { resource ->
            when(resource){
                is    Status.Loading-> {
                    _stateRemoveRead.value = stateRemoveRead.value.copy(
                        isLoading = true
                    )
                }
                is Status.Success-> {
                    _stateRemoveRead.value = stateRemoveRead.value.copy(
                        isLoading = false,
                        success = resource.data?.message.toString()
                    )
                    Log.e( "getAllBooks: ", resource.data?.message.toString())
                }
                is  Status.Error-> {
                    _stateRemoveRead.value = stateRemoveRead.value.copy(
                        error = resource.message,
                        isLoading = false

                    )

                }

            }
        }
    }
}