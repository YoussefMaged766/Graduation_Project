package com.example.graduationproject.utils


import com.example.graduationproject.models.*
import retrofit2.Response
import retrofit2.http.Body
import retrofit2.http.FormUrlEncoded
import retrofit2.http.POST

interface WebServices {


    @POST("login")
    suspend fun loginUser(@Body user: User) : UserResponseLogin

    @POST("signup")
    suspend fun signUpUser(@Body user: User) : UserResponseSignUp

    @POST("password-reset")
    suspend fun forgetPassword(@Body user: User) : GenerationCodeResponse

    @POST("new-password")
    suspend fun newPassword(@Body user: User) : NewPasswordResponse


}