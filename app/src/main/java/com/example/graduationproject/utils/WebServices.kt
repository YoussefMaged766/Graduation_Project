package com.example.graduationproject.utils


import com.example.graduationproject.models.*
import retrofit2.Response
import retrofit2.http.Body
import retrofit2.http.FormUrlEncoded
import retrofit2.http.GET
import retrofit2.http.Header
import retrofit2.http.Headers
import retrofit2.http.POST
import retrofit2.http.Path

interface WebServices {


    @POST("auth/login")
    suspend fun loginUser(@Body user: User) : UserResponseLogin

    @POST("auth/signup")
    suspend fun signUpUser(@Body user: User) : UserResponseSignUp

    @POST("auth/password-reset")
    suspend fun forgetPassword(@Body user: User) : GenerationCodeResponse

    @POST("auth/new-password")
    suspend fun newPassword(@Body user: User) : NewPasswordResponse


    @GET("book/search/{query}")
    suspend fun search(@Path("query") query:String,@Header("token") token:String): BookResponse


}