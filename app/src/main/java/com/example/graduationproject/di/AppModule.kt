package com.example.graduationproject.di

import android.content.Context
import androidx.room.Room
import com.example.graduationproject.db.SearchDatabase
import com.example.graduationproject.utils.WebServices
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import java.util.concurrent.TimeUnit
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object AppModule {


    @Provides
    @Singleton
    fun provideHttp(): OkHttpClient {
        val httpLoggingInterceptor = HttpLoggingInterceptor()
        httpLoggingInterceptor.level = HttpLoggingInterceptor.Level.BODY
        return OkHttpClient.Builder()
            . retryOnConnectionFailure(true)
            .readTimeout(1, TimeUnit.MINUTES)
            . connectTimeout(1, TimeUnit.MINUTES)
            .writeTimeout(5, TimeUnit.MINUTES)
            .addInterceptor(httpLoggingInterceptor)
            .build()

    }


    @Provides
    @Singleton
    fun retrofit(okHttpClient: OkHttpClient): Retrofit {
        return Retrofit.Builder()
            .baseUrl("http://192.168.1.6:9797/")
            .addConverterFactory(GsonConverterFactory.create())
            .client(okHttpClient)
            .build()
    }


    @Provides
    @Singleton
    fun provideRetrofit(retrofit: Retrofit): WebServices {
        return retrofit.create(WebServices::class.java)
    }
    @Provides
    @Singleton
    fun getJson():GsonConverterFactory{
        return GsonConverterFactory.create()
    }

    @Provides
    @Singleton
    fun databaseProvider(
        @ApplicationContext context : Context
    ) : SearchDatabase {
        return  Room.databaseBuilder(context,SearchDatabase::class.java,"Search_DB")
            .fallbackToDestructiveMigration()
            .build()
    }
}