package com.example.graduationproject.ui.auth.newpassword

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.graduationproject.R
import com.example.graduationproject.models.GenerationCodeResponse
import com.example.graduationproject.models.NewPasswordResponse
import com.example.graduationproject.models.User
import com.example.graduationproject.repository.UserRepo
import com.example.graduationproject.utils.Status
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.flow.receiveAsFlow
import kotlinx.coroutines.launch
import javax.inject.Inject


@HiltViewModel
class NewPasswordViewModel @Inject constructor(private val userRepo: UserRepo) :ViewModel() {


    private val _response = Channel<NewPasswordResponse>()
    val response = _response.receiveAsFlow()

    private val _progress = Channel<Boolean>()
    val progress = _progress.receiveAsFlow()

    private val eventChannel = Channel<Int>()
    // Receiving channel as a flow
    val eventFlow = eventChannel.receiveAsFlow()



    private val _error = Channel<String>()
    val error = _error.receiveAsFlow()


    fun newPassword(user: User) = viewModelScope.launch(Dispatchers.IO) {

        userRepo.newPassword(user).collect {
            when (it.status) {
                Status.LOADING -> _progress.send(true)

                Status.SUCCESS -> {
                    _progress.send(false)
                    _response.send(it.data!!)
                    eventChannel.send(R.id.action_createNewPasswordFragment_to_loginFragment)
                }

                Status.ERROR -> {
                    _progress.send(false)
                    _error.send(it.message.toString())
                }
            }

        }

    }



}