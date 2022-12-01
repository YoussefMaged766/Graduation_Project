package com.example.graduationproject.utils


import com.example.graduationproject.models.GenerationCodeResponse
import com.example.graduationproject.models.User
import com.example.graduationproject.models.UserResponseLogin
import com.example.graduationproject.models.UserResponseSignUp
import retrofit2.Response
import retrofit2.http.Body
import retrofit2.http.POST

interface WebServices {


    @POST("auth/login")
    suspend fun loginUser(@Body user: User) : Response<UserResponseLogin>

    @POST("auth/signup")
    suspend fun signUpUser(@Body user: User) : Response<UserResponseSignUp>

    @POST("auth/password-reset")
    suspend fun forgetPassword(@Body user: User) : Response<GenerationCodeResponse>


}