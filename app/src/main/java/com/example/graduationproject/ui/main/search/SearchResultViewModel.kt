package com.example.graduationproject.ui.main.search


import android.util.Log
import androidx.lifecycle.*
import androidx.paging.PagingData
import com.example.graduationproject.data.repository.BookRepo
import com.example.graduationproject.models.BooksItem
import com.example.graduationproject.utils.Status
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class SearchResultViewModel @Inject constructor(
    private val bookRepo: BookRepo
    ) : ViewModel(){

    private val _response = MutableSharedFlow<PagingData<BooksItem>?>()
    val response : MutableSharedFlow<PagingData<BooksItem>?> get() = _response

    private val _state = MutableStateFlow(BookState())
    val state = _state.asStateFlow()

    private val _progress = Channel<Boolean>()
    val progress = _progress.receiveAsFlow()

    private val _noData = Channel<Boolean>()
    val noData = _noData.receiveAsFlow()


    private val _error = Channel<String>()
    val error = _error.receiveAsFlow()

//     var result: Flow<Resource<PagingData<BooksItem>>> = MutableSharedFlow()
//
//    fun search1(query: String,token:String){
//        result = bookRepo.search(query,token)
//    }

    fun search(query:String,token:String) = viewModelScope.launch(Dispatchers.IO) {
        bookRepo.search(query, token).collectLatest { resource ->
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
                        isLoading = false,
                        error = resource.message
                    )
                    Log.e( "searchError: ", resource.message ?: "Unknown Error")
                }

                else->{}
            }

        }
        bookRepo.search(query, token).collect {
            when (it.status) {
                Status.LOADING -> _progress.send(true)

                Status.SUCCESS -> {
                    _progress.send(false)
                    _response.emit(it.data)
                    Log.e( "search: ", it.data.toString())

                }
                Status.ERROR -> {
                    _progress.send(false)
                    _error.send(it.message.toString())
                    Log.e( "search: ",it.message.toString() )
                }
                Status.NO_DATA->{
                    _noData.send(true)
                }
            }
        }
    }
}