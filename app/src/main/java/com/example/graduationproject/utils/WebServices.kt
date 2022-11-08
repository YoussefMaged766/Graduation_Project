package com.example.graduationproject.utils


import com.example.graduationproject.models.User
import retrofit2.Response
import retrofit2.http.Body
import retrofit2.http.POST

interface WebServices {


    @POST("login")
    suspend fun loginUser(@Body user: User) : Response<User>
}