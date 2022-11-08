package com.example.graduationproject.utils


import com.example.graduationproject.models.User
import com.example.graduationproject.models.UserResponse
import com.example.graduationproject.models.UserResponseLogin
import retrofit2.Response
import retrofit2.http.Body
import retrofit2.http.POST

interface WebServices {


    @POST("login")
    suspend fun loginUser(@Body user: User) : Response<UserResponseLogin>


}