package com.example.graduationproject.ui.auth.signup

import androidx.lifecycle.ViewModel
import com.example.graduationproject.models.User
import com.example.graduationproject.repository.UserRepo
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject

@HiltViewModel
class SignUpViewModel @Inject constructor(val userRepo: UserRepo):ViewModel() {

    suspend fun signUpUser(user: User) = userRepo.signUpUser(user)

}