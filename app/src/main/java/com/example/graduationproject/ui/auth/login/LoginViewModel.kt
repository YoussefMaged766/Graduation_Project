package com.example.graduationproject.ui.auth.login

import android.content.Context
import android.content.Intent
import androidx.core.content.ContextCompat.startActivity
import androidx.fragment.app.FragmentActivity
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.graduationproject.R
import com.example.graduationproject.models.GenerationCodeResponse
import com.example.graduationproject.models.User
import com.example.graduationproject.models.UserResponseLogin
import com.example.graduationproject.repository.UserRepo
import com.example.graduationproject.ui.main.MainActivity
import com.example.graduationproject.ui.main.home.HomeActivity
import com.example.graduationproject.utils.Status
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.receiveAsFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class LoginViewModel @Inject constructor(private val userRepo: UserRepo) : ViewModel() {



    private val _response = Channel<UserResponseLogin>()
    val response = _response.receiveAsFlow()

    private val _progress = Channel<Boolean>()
    val progress = _progress.receiveAsFlow()


    private val _error = Channel<String>()
    val error = _error.receiveAsFlow()

    fun loginUser(user: User, context: Context, Class: Class<*>, activity: FragmentActivity) =
        viewModelScope.launch(Dispatchers.IO) {
            userRepo.loginUser(user).collect {
                when (it.status) {
                    Status.LOADING -> _progress.send(true)

                    Status.SUCCESS -> {
                        _progress.send(false)
                        _response.send(it.data!!)
                        context.startActivity(Intent(context, Class))
                        activity.finish()


                    }
                    Status.ERROR -> {
                        _progress.send(false)
                        _error.send(it.message.toString())
                    }
                }
            }
        }

}