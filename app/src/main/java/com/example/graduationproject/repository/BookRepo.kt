package com.example.graduationproject.repository

import android.util.Log
import com.example.graduationproject.models.*
import com.example.graduationproject.utils.NetworkState
import com.example.graduationproject.utils.Resource
import com.example.graduationproject.utils.WebServices
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.flowOn
import retrofit2.HttpException
import javax.inject.Inject

class BookRepo @Inject constructor(private val webServices: WebServices) {
    private val gson = Gson()
    private val networkState = NetworkState
    fun search (query:String,token:String) = flow {

        try {
            emit(Resource.loading(null))

            if (networkState.isOnline()){
                val response = webServices.search(query, token)
                emit(Resource.success(response))

                Log.e("loginUser: ", response.toString())
            } else{
                emit(Resource.error(null,"no Internet"))
            }
        } catch (e: Throwable) {
            when (e) {
                is HttpException -> {
                    val type = object : TypeToken<BooksItem>() {}.type
                    val errorResponse: BooksItem? =
                        gson.fromJson(e.response()?.errorBody()!!.charStream(), type)
                    Log.e("loginUsereeeeerrrrr: ", errorResponse?.message.toString())
                    emit(Resource.error(null, errorResponse?.message.toString()))
                }
                is Exception -> {
                    Log.e("loginUsereeeee: ", e.message.toString())
                    emit(Resource.error(null, e.message.toString()))
                }
            }
        }

    }.flowOn(Dispatchers.IO)

}