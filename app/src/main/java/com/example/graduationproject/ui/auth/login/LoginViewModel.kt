package com.example.graduationproject.ui.auth.login

import android.content.Context
import android.content.Intent
import androidx.fragment.app.FragmentActivity
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.graduationproject.models.User
import com.example.graduationproject.models.UserResponseLogin
import com.example.graduationproject.data.repository.UserRepo
import com.example.graduationproject.ui.main.search.BookState
import com.example.graduationproject.utils.Status
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.receiveAsFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class LoginViewModel @Inject constructor(private val userRepo: UserRepo) : ViewModel() {



    private val _stateLogin = MutableStateFlow(BookState())
    val stateLogin = _stateLogin.asStateFlow()

    fun loginUser(user: User) =
        viewModelScope.launch(Dispatchers.IO) {
            userRepo.loginUser(user).collect {
                when (it) {
                    is Status.Loading -> {
                        _stateLogin.value = stateLogin.value.copy(
                            isLoading = true
                        )
                    }

                    is Status.Success -> {
                        _stateLogin.value = stateLogin.value.copy(
                            isLoading = false,
                            success = it.data.message,
                            userLogin = it.data
                        )
                    }

                    is Status.Error -> {
                        _stateLogin.value = stateLogin.value.copy(
                            isLoading = false,
                            error = it.message
                        )
                    }
                }
            }
        }

}