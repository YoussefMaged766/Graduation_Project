package com.example.graduationproject.ui.main.search

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.graduationproject.models.HistorySearchEntity
import com.example.graduationproject.data.repository.BookRepo
import com.example.graduationproject.utils.Status
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.receiveAsFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class SearchViewModel @Inject constructor(var bookRepo: BookRepo) : ViewModel() {

    private val _searchResponse = Channel<List<HistorySearchEntity>>()
    val searchResponse = _searchResponse.receiveAsFlow()

    private val _error = Channel<String>()
    val error = _error.receiveAsFlow()

    private val _successDelete = MutableStateFlow<String>("Deleted")
    val successDelete  :MutableStateFlow<String> get() = _successDelete

    fun saveSearchHistory(searchHistory: HistorySearchEntity)=viewModelScope.launch(Dispatchers.IO) {

        bookRepo.saveSearchHistory(searchHistory).collect{
            when(it.status){
                Status.ERROR -> _error.send(it.message.toString())
                else->{}
            }
        }
    }

     fun getAllHistorySearch(userId:String)=viewModelScope.launch(Dispatchers.IO) {
        bookRepo.getAllHistorySearch(userId).collect{
            _searchResponse.send(it)
        }
    }

    fun deleteHistorySearch(query: String)=viewModelScope.launch(Dispatchers.IO) {
        bookRepo.deleteHistorySearch(query).collect{
            when(it.status){
                Status.SUCCESS->{
                    _successDelete.value = "Deleted"

                }
                Status.ERROR -> _error.send(it.message.toString())
                else->{}
            }
        }
    }


}