package com.example.graduationproject.ui.auth.signup

import androidx.lifecycle.ViewModel
import com.example.graduationproject.models.User
import com.example.graduationproject.repository.UserRepo

class SignUpViewModel : ViewModel() {
    val userRepo = UserRepo()
    suspend fun signUpUser(user: User) = userRepo.signUpUser(user)

}