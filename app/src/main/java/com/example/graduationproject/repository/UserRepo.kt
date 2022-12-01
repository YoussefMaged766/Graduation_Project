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
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.flowOn
import javax.inject.Inject

class UserRepo @Inject constructor(private val webServices: WebServices){
    val gson = Gson()
    suspend fun loginUser(user: User) = flow {
        emit(Resource.loading(null))
        val response = webServices.loginUser(user)

        if (response.code() == 200 && response.isSuccessful) {
            emit(Resource.success(response))
            Log.e("loginUser: ", response.body().toString())
        } else if (response.code() == 401) {


            val type = object : TypeToken<UserResponseLogin>() {}.type
            val errorResponse: UserResponseLogin? =
                gson.fromJson(response.errorBody()!!.charStream(), type)

            Log.e("loginUsereeeee: ", errorResponse?.message.toString())
            emit(Resource.error(null, errorResponse?.message.toString()))
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

    suspend fun forgetPassword(user: User) = flow {

        val response = webServices.forgetPassword(user)
        emit(Resource.loading(null))
        try {
            if (response.isSuccessful && response.code() ==200){
                emit(Resource.success(response))
            } else{
                val type = object : TypeToken<GenerationCodeResponse>() {}.type
                val errorResponse: GenerationCodeResponse? =
                    gson.fromJson(response.errorBody()!!.charStream(), type)
                emit(Resource.error(null,errorResponse?.message.toString()))
            }
        } catch (e:Exception){
            emit(Resource.error(null,e.message.toString()))
        }

    }.flowOn(Dispatchers.IO)
}