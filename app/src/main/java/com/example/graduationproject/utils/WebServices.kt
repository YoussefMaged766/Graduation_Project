package com.example.graduationproject.utils


import com.example.graduationproject.models.*
import okhttp3.MultipartBody
import okhttp3.RequestBody
import retrofit2.http.Body
import retrofit2.http.GET
import retrofit2.http.Header
import retrofit2.http.Multipart
import retrofit2.http.PATCH
import retrofit2.http.POST
import retrofit2.http.PUT
import retrofit2.http.Part
import retrofit2.http.Path
import retrofit2.http.Query

interface WebServices {


    @POST("auth/login")
    suspend fun loginUser(@Body user: User): UserResponseLogin

    @Multipart

    @POST("auth/signup")
    suspend fun signUpUser(@Part image: MultipartBody.Part? , @Part("firstName") firstName: RequestBody,
                            @Part("lastName") lastName: RequestBody,
                            @Part("email") email: RequestBody,
                            @Part("password") password: RequestBody,
                           ): UserResponseSignUp

    @POST("auth/password-reset")
    suspend fun forgetPassword(@Body user: User): GenerationCodeResponse

    @POST("auth/new-password")
    suspend fun newPassword(@Body user: User): NewPasswordResponse

    @GET("book/search/{query}")
    suspend fun search(
        @Path("query") query: String,
        @Header("token") token: String,
        @Query("page") page: Int
    ): BookResponse

    @GET("book")
    suspend fun getAllBooks(@Header("token") token: String, @Query("page") page: Int): BookResponse

    @PUT("list/addToFavorits")
    suspend fun addFavourite(
        @Header("token") token: String,
        @Body bookId: BookIdResponse
    ): BookIdResponse

    @GET("list/favorits")
    suspend fun getAllFavourite(@Header("token") token: String): WishAndFavResponse

    @PUT("list/removeFromFavorits")
    suspend fun removeFavourite(
        @Header("token") token: String,
        @Body bookId: BookIdResponse
    ): BookIdResponse

    @PUT("list/addToWishlist ")
    suspend fun addToWishlist(
        @Header("token") token: String,
        @Body bookId: BookIdResponse
    ): BookIdResponse

    @GET("list/wishlist")
    suspend fun getAllWishlist(@Header("token") token: String): WishAndFavResponse

    @PUT("list/RemoveFromWishlist")
    suspend fun removeWishlist(
        @Header("token") token: String,
        @Body bookId: BookIdResponse
    ): BookIdResponse

    @Multipart
    @PATCH("auth/updateprofile")
    suspend fun updateProfile(
        @Header("token") token: String,
        @Part image: MultipartBody.Part? = null,
        @Part("firstName") firstName: RequestBody,
        @Part("lastName") lastName: RequestBody,
        @Part("email") email: RequestBody
    ): BookIdResponse

    @GET("auth/updateprofile")
    suspend fun getProfile(@Header("token") token: String):UserResponse

}