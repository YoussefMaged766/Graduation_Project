package com.example.graduationproject.ui.auth.forgetpassword

import android.content.Context
import android.util.Log
import android.view.View
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.graduationproject.R
import com.example.graduationproject.models.GenerationCodeResponse
import com.example.graduationproject.models.User
import com.example.graduationproject.repository.UserRepo
import com.example.graduationproject.utils.Status
import dagger.hilt.android.lifecycle.HiltViewModel
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.receiveAsFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class ForgetPasswordViewModel @Inject constructor(private val userRepo: UserRepo) : ViewModel() {




    private val _response = MutableLiveData<GenerationCodeResponse>()
    val response: LiveData<GenerationCodeResponse> get() = _response

    private val _progress = MutableStateFlow<Int>(0)
    val progress: StateFlow<Int> get() = _progress

    private val eventChannel = Channel<Int>()
    // Receiving channel as a flow
    val eventFlow = eventChannel.receiveAsFlow()



    private val _error = MutableStateFlow<String>("")
    val error: StateFlow<String> get() = _error


    fun forgetPassword(user: User) = viewModelScope.launch(Dispatchers.IO) {

        userRepo.forgetPassword(user).collect {
            when (it.status) {
                Status.SUCCESS -> {
                    _response.postValue(it.data?.body()!!)
                    _progress.value = View.INVISIBLE
                    eventChannel.send(R.id.action_forgetPasswordFragment_to_verifyAccountFragment)
                }
                Status.LOADING -> {
                    _progress.value = View.VISIBLE
                }
                Status.ERROR -> {
                    _progress.value = View.INVISIBLE
                    _error.value = it.message.toString()
                }
            }

        }

    }

}