package com.example.graduationproject.ui.main.profile

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

    suspend fun updateProfile(
        token: String,
        image: MultipartBody.Part? = null,
        firstName: RequestBody,
        lastName: RequestBody,
        email: RequestBody
    ) = viewModelScope.launch(Dispatchers.IO) {
        bookRepo.updateProfile(token, image, firstName, lastName, email).collect { resource ->
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

}