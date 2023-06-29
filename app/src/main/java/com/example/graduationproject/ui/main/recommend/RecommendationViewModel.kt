package com.example.graduationproject.ui.main.recommend

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.graduationproject.data.repository.BookRepo
import com.example.graduationproject.ui.main.wishlist.WishlistState
import com.example.graduationproject.utils.Status
import com.example.graduationproject.utils.WebServices
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class RecommendationViewModel @Inject constructor(
    private val bookRepo: BookRepo

) : ViewModel() {
    private val _stateReco = MutableStateFlow(WishlistState())
    val stateReco = _stateReco.asStateFlow()

    private var fetchDataJob: Job? = null

    suspend fun getRecommendation(id: String) {
        fetchDataJob = viewModelScope.launch(Dispatchers.IO) {
            bookRepo.getRecommendation(id).collect {
                when (it) {
                    is Status.Loading -> {
                        _stateReco.value = stateReco.value.copy(
                            isLoading = true
                        )
                    }

                    is Status.Success -> {
                        _stateReco.value = stateReco.value.copy(
                            isLoading = false,
                            allBooks = it.data.books
                        )

                    }

                    is Status.Error -> {
                        _stateReco.value = stateReco.value.copy(
                            isLoading = false,
                            error = it.message
                        )
                    }
                }
            }
        }
    }
    fun cancelRequest(){
        fetchDataJob?.cancel()
    }


    override fun onCleared() {
        super.onCleared()
        fetchDataJob?.cancel()
    }
}