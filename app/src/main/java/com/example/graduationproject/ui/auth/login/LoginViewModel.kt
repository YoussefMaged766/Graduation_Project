package com.example.graduationproject.ui.auth.login

import androidx.lifecycle.ViewModel
import com.example.graduationproject.models.User
import com.example.graduationproject.repository.UserRepo

class LoginViewModel : ViewModel() {
    var userRepo = UserRepo()
    suspend fun loginUser(user: User) = userRepo.loginUser(user)



}