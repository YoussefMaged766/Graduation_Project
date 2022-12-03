package com.example.graduationproject.repository

import android.util.Log
import com.example.graduationproject.models.GenerationCodeResponse
import com.example.graduationproject.models.User
import com.example.graduationproject.models.UserResponseLogin
import com.example.graduationproject.models.UserResponseSignUp
import com.example.graduationproject.utils.Resource
import com.example.graduationproject.utils.WebServices
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.flowOn
import retrofit2.HttpException
import retrofit2.Response
import javax.inject.Inject

class UserRepo @Inject constructor(private val webServices: WebServices) {
    private val gson = Gson()
    fun loginUser(user: User) = flow {
        try {
            emit(Resource.loading(null))
            val response = webServices.loginUser(user)
            emit(Resource.success(response))

            Log.e("loginUser: ", response.toString())
        } catch (e: Throwable) {
            when (e) {
                is HttpException -> {
                    val type = object : TypeToken<UserResponseLogin>() {}.type
                    val errorResponse: UserResponseLogin? =
                        gson.fromJson(e.response()?.errorBody()!!.charStream(), type)
                    Log.e("loginUsereeeee: ", errorResponse?.message.toString())
                    emit(Resource.error(null, errorResponse?.message.toString()))
                }
                is Exception -> {
                    Log.e("loginUsereeeee: ", e.message.toString())
                    emit(Resource.error(null, e.message.toString()))
                }
            }
        }

    }.flowOn(Dispatchers.IO)

    suspend fun signUpUser(user: User) = flow {
        emit(Resource.loading(null))
        val response = webServices.signUpUser(user)
        try {
            if (response.code() == 201 && response.isSuccessful) {
                emit(Resource.success(response))
            } else {
                val type = object : TypeToken<UserResponseSignUp>() {}.type
                val errorResponse: UserResponseSignUp? =
                    gson.fromJson(response.errorBody()!!.charStream(), type)
                Log.e("signUpUser: ", errorResponse?.data?.get(0)?.msg.toString())
                emit(Resource.error(null, errorResponse?.data?.get(0)?.msg.toString()))
            }
        } catch (e: Exception) {
            emit(Resource.error(null, e.message.toString()))
        }

    }.flowOn(Dispatchers.IO)

    fun forgetPassword(user: User): Flow<Resource<GenerationCodeResponse>> = flow {

        try {
            emit(Resource.loading(null))
            val response = webServices.forgetPassword(user)
            emit(Resource.success(response))

        } catch (e: Throwable) {
            when (e) {
                is HttpException->{
                    val type = object : TypeToken<GenerationCodeResponse>() {}.type
                    val errorResponse: GenerationCodeResponse? =
                        gson.fromJson(e.response()?.errorBody()!!.charStream(), type)
                    emit(Resource.error(null, errorResponse?.message.toString()))
                    Log.e("forgetPassword: ", errorResponse?.message.toString())
                }
                is Exception->{
                    emit(Resource.error(null, e.message.toString()))
                    Log.e("forgetPassword: ", e.message.toString())
                }
            }

        }
    }
}