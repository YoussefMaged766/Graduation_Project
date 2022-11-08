package com.example.graduationproject.repository

import com.example.graduationproject.models.User
import com.example.graduationproject.utils.ApiManager
import com.example.graduationproject.utils.Resource
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.flowOn

class UserRepo {

    suspend fun loginUser(user: User) = flow {
        emit(Resource.loading(null))
        try {
            emit(Resource.success(ApiManager.getwebbservices().loginUser(user)))
        } catch (e: Exception) {
            emit(Resource.error(null, e.message.toString()))
        }
    }.flowOn(Dispatchers.IO)
}