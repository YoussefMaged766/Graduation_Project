package com.example.graduationproject.ui.main.search

import android.content.Intent
import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.graduationproject.models.Book
import com.example.graduationproject.models.BookResponse
import com.example.graduationproject.models.BookX
import com.example.graduationproject.models.UserResponseLogin
import com.example.graduationproject.repository.BookRepo
import com.example.graduationproject.utils.Status

import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.flow.receiveAsFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class SearchResultViewModel @Inject constructor(private val bookRepo: BookRepo) : ViewModel(){

    private val _response = Channel<BookResponse?>()
    val response = _response.receiveAsFlow()

    private val _progress = Channel<Boolean>()
    val progress = _progress.receiveAsFlow()


    private val _error = Channel<String>()
    val error = _error.receiveAsFlow()

    fun search(query:String,token:String) = viewModelScope.launch(Dispatchers.IO) {

        bookRepo.search(query, token).collect {
            when (it.status) {

                Status.LOADING -> _progress.send(true)

                Status.SUCCESS -> {
                    _progress.send(false)
                    _response.send(it.data)
                    Log.e( "search: ", it.data.toString())

                }
                Status.ERROR -> {
                    _progress.send(false)
                    _error.send(it.message.toString())
                    Log.e( "search: ",it.message.toString() )
                }
            }
        }
    }
}