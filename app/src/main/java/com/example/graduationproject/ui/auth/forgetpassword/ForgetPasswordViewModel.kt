package com.example.graduationproject.ui.auth.forgetpassword

import androidx.lifecycle.ViewModel
import com.example.graduationproject.models.User
import com.example.graduationproject.repository.UserRepo
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject

@HiltViewModel
class ForgetPasswordViewModel @Inject constructor(private val userRepo: UserRepo) : ViewModel() {

    suspend fun forgetPassword(email: String) = userRepo.forgetPassword(email)

}