package com.example.graduationproject.utils

import okhttp3.HttpUrl
import okhttp3.Interceptor
import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory

class ApiManager {
    companion object{
        private var retrofit: Retrofit? = null
        @Synchronized
        private fun getInstance():Retrofit{
            if (retrofit ==null){
                val httpLoggingInterceptor = HttpLoggingInterceptor()
                httpLoggingInterceptor.level = HttpLoggingInterceptor.Level.BODY

                val client = OkHttpClient.Builder()
                    .addInterceptor(httpLoggingInterceptor)
                    .build()

                retrofit = Retrofit.Builder()
                    .baseUrl("http://192.168.1.7:5000/")
                    .addConverterFactory(GsonConverterFactory.create())
                    .client(client)
                    .build()
                return retrofit!!
            }
            return retrofit!!
        }
        fun getwebbservices(): WebServices {
            return getInstance().create(WebServices::class.java)

        }
    }
}