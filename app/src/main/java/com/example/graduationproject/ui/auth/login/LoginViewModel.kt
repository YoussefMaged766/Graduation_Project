package com.example.graduationproject.ui.auth.login

import androidx.lifecycle.ViewModel
import com.example.graduationproject.models.User
import com.example.graduationproject.repository.UserRepo
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject

@HiltViewModel
class LoginViewModel @Inject constructor(val userRepo: UserRepo) :ViewModel()  {

    suspend fun loginUser(user: User) = userRepo.loginUser(user)



}