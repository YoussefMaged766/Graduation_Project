package com.example.graduationproject.utils


import com.example.graduationproject.models.GenerationCodeResponse
import com.example.graduationproject.models.User
import com.example.graduationproject.models.UserResponseLogin
import com.example.graduationproject.models.UserResponseSignUp
import retrofit2.Response
import retrofit2.http.Body
import retrofit2.http.POST

interface WebServices {


    @POST("login")
    suspend fun loginUser(@Body user: User) : Response<UserResponseLogin>

    @POST("signup")
    suspend fun signUpUser(@Body user: User) : Response<UserResponseSignUp>

    @POST("password-reset")
    suspend fun forgetPassword(@Body email: String) : Response<GenerationCodeResponse>


}