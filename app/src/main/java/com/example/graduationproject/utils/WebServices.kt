package com.example.graduationproject.utils


import com.example.graduationproject.models.*
import retrofit2.http.Body
import retrofit2.http.GET
import retrofit2.http.Header
import retrofit2.http.POST
import retrofit2.http.PUT
import retrofit2.http.Path
import retrofit2.http.Query

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
    suspend fun search(@Path("query") query:String,@Header("token") token:String , @Query("page") page:Int): BookResponse
//    @GET("list/favorits")
//    suspend fun favorits()

    @GET("book")
    suspend fun getAllBooks(@Header("token") token:String,@Query("page") page:Int): BookResponse

    @PUT("list/addToFavorits")
    suspend fun addFavoirate(@Header("token") token:String, @Body book: String)

}