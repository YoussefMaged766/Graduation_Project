package com.example.graduationproject.ui.main.profile

import android.content.Context
import android.net.Uri
import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.graduationproject.data.repository.BookRepo
import com.example.graduationproject.ui.main.search.BookState
import com.example.graduationproject.utils.Status
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import okhttp3.MultipartBody
import okhttp3.RequestBody
import javax.inject.Inject

@HiltViewModel
class ProfileViewModel @Inject constructor(
    private val bookRepo: BookRepo
) : ViewModel() {

    private val _stateProfile = MutableStateFlow(BookState())
    val stateProfile = _stateProfile.asStateFlow()

    private val _getProfile = MutableStateFlow(BookState())
    val getProfile = _getProfile.asStateFlow()

    suspend fun updateProfile(
        token: String,
        fileUri: Uri?=null, fileRealPath: String?=null,
        firstName: String,
        lastName: String,
        email: String,
        ctx: Context
    ) = viewModelScope.launch(Dispatchers.IO) {
        bookRepo.updateProfile(token, fileUri ,fileRealPath , firstName, lastName, email , ctx).collect { resource ->
            when (resource) {
                is Status.Loading -> {
                    _stateProfile.value = stateProfile.value.copy(
                        isLoading = true
                    )
                }

                is Status.Success -> {
                    _stateProfile.value = stateProfile.value.copy(
                        isLoading = false,
                        success = resource.data.message
                    )
                }

                is Status.Error -> {
                    _stateProfile.value = stateProfile.value.copy(
                        isLoading = false,
                        error = resource.message
                    )
                }
            }
        }

    }

    suspend fun getProfile(token: String) =viewModelScope.launch(Dispatchers.IO) {
        bookRepo.getProfile(token).collect{
            when (it) {
                is Status.Loading -> {
                    _getProfile.value = getProfile.value.copy(
                        isLoading = true
                    )
                }

                is Status.Success -> {
                    _getProfile.value = getProfile.value.copy(
                        isLoading = false,
                        userResponse = it.data
                    )

                }

                is Status.Error -> {
                    _getProfile.value = getProfile.value.copy(
                        isLoading = false,
                        error = it.message
                    )
                }
            }
        }
    }

}