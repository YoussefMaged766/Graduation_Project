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




    private val _response = Channel<GenerationCodeResponse>()
    val response = _response.receiveAsFlow()

    private val _progress = Channel<Boolean>()
    val progress = _progress.receiveAsFlow()

    private val eventChannel = Channel<Int>()
    // Receiving channel as a flow
    val eventFlow = eventChannel.receiveAsFlow()



    private val _error = Channel<String>()
    val error = _error.receiveAsFlow()


    fun forgetPassword(user: User) = viewModelScope.launch(Dispatchers.IO) {

        userRepo.forgetPassword(user).collect {
            when (it.status) {
                Status.LOADING -> _progress.send(true)

                Status.SUCCESS -> {
                    _progress.send(false)
                    _response.send(it.data!!)
                    eventChannel.send(R.id.action_forgetPasswordFragment_to_verifyAccountFragment)
                }

                Status.ERROR -> {
                    _progress.send(false)
                    _error.send(it.message.toString())
                }
            }

        }

    }

}