package com.example.graduationproject.utils

import com.example.graduationproject.models.BookResponse
import retrofit2.http.Header
import retrofit2.http.POST
import retrofit2.http.Path

interface RecommendationService {


    @POST("recommendations/{id}")
    suspend fun getRecommendations(@Path("id") id: String): BookResponse

    @POST("itembased/{title}")
    suspend fun getItemBased(@Path("title") title: String): BookResponse


}