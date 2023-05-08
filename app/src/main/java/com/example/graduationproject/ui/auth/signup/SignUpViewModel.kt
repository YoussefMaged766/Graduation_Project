package com.example.graduationproject.ui.auth.signup

import android.view.WindowManager
import androidx.fragment.app.FragmentActivity
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.graduationproject.R
import com.example.graduationproject.models.GenerationCodeResponse
import com.example.graduationproject.models.User
import com.example.graduationproject.models.UserResponseSignUp
import com.example.graduationproject.data.repository.UserRepo
import com.example.graduationproject.utils.Status
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.flow.receiveAsFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class SignUpViewModel @Inject constructor(val userRepo: UserRepo) : ViewModel() {

//    suspend fun signUpUser(user: User) = userRepo.signUpUser(user)

    private val _response = Channel<UserResponseSignUp>()
    val response = _response.receiveAsFlow()

    private val _progress = Channel<Boolean>()
    val progress = _progress.receiveAsFlow()

    private val eventChannel = Channel<Int>()

    // Receiving channel as a flow
    val eventFlow = eventChannel.receiveAsFlow()


    private val _error = Channel<String>()
    val error = _error.receiveAsFlow()
    fun signUpUser(user: User, activity: FragmentActivity) = viewModelScope.launch(Dispatchers.IO) {

        userRepo.signUpUser(user).collect {
            when (it) {
                is  Status.Loading -> {
                    _progress.send(true)
//                    activity.window.setFlags(
//                        WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE,
//                        WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE
//                    )
                }

                is Status.Success -> {
                    _progress.send(false)
                    _response.send(it.data!!)
                    eventChannel.send(R.id.action_signUpFragment_to_loginFragment)
//                    activity.window.clearFlags(WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE)
                }

                is Status.Error -> {
                    _progress.send(false)
                    _error.send(it.message.toString())
//                    activity.window?.clearFlags(WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE)
                }


            }

        }

    }

}