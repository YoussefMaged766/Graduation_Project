package com.example.graduationproject.repository

import android.util.Log
import com.example.graduationproject.models.User
import com.example.graduationproject.models.UserResponseLogin
import com.example.graduationproject.utils.ApiManager
import com.example.graduationproject.utils.Resource
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.flowOn

class UserRepo {

    suspend fun loginUser(user: User) = flow {
        emit(Resource.loading(null))
        val response = ApiManager.getwebbservices().loginUser(user)

        if (response.code() == 200 && response.isSuccessful) {
            emit(Resource.success(response))
            Log.e("loginUser: ", response.body().toString())
        } else if (response.code() == 401) {

            val gson = Gson()
            val type = object : TypeToken<UserResponseLogin>() {}.type
            val errorResponse: UserResponseLogin? = gson.fromJson(response.errorBody()!!.charStream(), type)

            Log.e("loginUsereeeee: ", errorResponse?.message.toString())
            emit(Resource.error(null,errorResponse?.message.toString()))
        }

    }.flowOn(Dispatchers.IO)


    suspend fun signUpUser(user: User) = flow {
        emit(Resource.loading(null))
        val response = ApiManager.getwebbservices().signUpUser(user)
        try {
            if (response.code() == 201 && response.isSuccessful) {
                emit(Resource.success(response))
            }
        } catch (e:Exception){
            emit(Resource.error(null,e.message.toString()))
        }

    }.flowOn(Dispatchers.IO)

}