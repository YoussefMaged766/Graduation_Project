package com.example.graduationproject.di

import android.content.Context
import androidx.room.Room
import com.example.graduationproject.db.BookDatabase
import com.example.graduationproject.utils.RecommendationService
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
import javax.inject.Named
import javax.inject.Qualifier
import javax.inject.Singleton


@Qualifier annotation class ServerA
@Qualifier annotation class ServerB


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
            .readTimeout(5, TimeUnit.MINUTES)
            . connectTimeout(5, TimeUnit.MINUTES)
            .writeTimeout(15, TimeUnit.SECONDS)
            .addInterceptor(httpLoggingInterceptor)
            .build()

    }


    @Provides
    @Singleton
   @ServerA
    fun retrofitA(okHttpClient: OkHttpClient): Retrofit {
        return Retrofit.Builder()
            .baseUrl("http://192.168.1.7:3000/")
            .addConverterFactory(GsonConverterFactory.create())
            .client(okHttpClient)
            .build()
    }

    @Provides
    @Singleton
    @ServerB
    fun retrofitB(okHttpClient: OkHttpClient): Retrofit {
        return Retrofit.Builder()
            .baseUrl("http://192.168.1.7:5000/")
            .addConverterFactory(GsonConverterFactory.create())
            .client(okHttpClient)
            .build()
    }


    @Provides
    @Singleton
    fun provideRetrofitA(@ServerA retrofit: Retrofit): WebServices {
        return retrofit.create(WebServices::class.java)
    }

    @Provides
    @Singleton
    fun provideRetrofitB(@ServerB retrofit: Retrofit): RecommendationService {
        return retrofit.create(RecommendationService::class.java)
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
    ) : BookDatabase {
        return  Room.databaseBuilder(context,BookDatabase::class.java,"Search_DB")
            .fallbackToDestructiveMigration()
            .build()
    }


}