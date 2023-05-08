package com.example.graduationproject.data.repository

import android.util.Log
import com.example.graduationproject.models.*
import com.example.graduationproject.utils.NetworkState
import com.example.graduationproject.utils.Status
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

class UserRepo @Inject constructor(private val webServices: WebServices ) {
    private val gson = Gson()
    private val networkState = NetworkState
    fun loginUser(user: User) = flow {

        try {
            emit(Status.Loading)
            if (networkState.isOnline()){
                val response = webServices.loginUser(user)
                emit(Status.Success(response))

                Log.e("loginUser: ", response.toString())
            } else{
                emit(Status.Error("no Internet"))
            }
        } catch (e: Throwable) {
            when (e) {
                is HttpException -> {
                    val type = object : TypeToken<UserResponseLogin>() {}.type
                    val errorResponse: UserResponseLogin? =
                        gson.fromJson(e.response()?.errorBody()!!.charStream(), type)
                    Log.e("loginUsereeeee: ", errorResponse?.message.toString())
                    emit(Status.Error( errorResponse?.message.toString()))
                }
                is Exception -> {
                    Log.e("loginUsereeeee: ", e.message.toString())
                    emit(Status.Error( e.message.toString()))
                }
            }
        }

    }.flowOn(Dispatchers.IO)

    suspend fun signUpUser(user: User) = flow {

        try {
            emit(Status.Loading)
            if (networkState.isOnline()){
                val response = webServices.signUpUser(user)
                emit(Status.Success(response))
            }else{
                emit(Status.Error("no Internet"))
            }


        } catch (e: Throwable) {
            when(e){
                is HttpException->{
                    val type = object : TypeToken<UserResponseSignUp>() {}.type
                    val errorResponse: UserResponseSignUp? =
                        gson.fromJson(e.response()?.errorBody()!!.charStream(), type)
                    Log.e("signUpUser: ", errorResponse?.data?.get(0)?.msg.toString())
                    emit(Status.Error( errorResponse?.data?.get(0)?.msg.toString()))
                }
                is Exception->{
                    emit(Status.Error( e.message.toString()))
                }
            }

        }

    }.flowOn(Dispatchers.IO)

    fun forgetPassword(user: User): Flow<Status<GenerationCodeResponse>> = flow {

        try {
            emit(Status.Loading)
            if (networkState.isOnline()){
                val response = webServices.forgetPassword(user)
                emit(Status.Success(response))
            }else{
                emit(Status.Error("no Internet"))
            }


        } catch (e: Throwable) {
            when (e) {
                is HttpException->{
                    val type = object : TypeToken<GenerationCodeResponse>() {}.type
                    val errorResponse: GenerationCodeResponse? =
                        gson.fromJson(e.response()?.errorBody()!!.charStream(), type)
                    emit(Status.Error(errorResponse?.message.toString()))
                    Log.e("forgetPassword: ", errorResponse?.message.toString())
                }
                is Exception->{
                    emit(Status.Error(e.message.toString()))
                    Log.e("forgetPassword: ", e.message.toString())
                }
            }

        }
    }.flowOn(Dispatchers.IO)


    fun newPassword(user: User)= flow {

        try {
            emit(Status.Loading)
            if (networkState.isOnline()){
                val response = webServices.newPassword(user)
                emit(Status.Success(response))
            }else{
                emit(Status.Error("no Internet"))
            }


        } catch (e: Throwable) {
            when (e) {
                is HttpException->{
                    val type = object : TypeToken<NewPasswordResponse>() {}.type
                    val errorResponse: NewPasswordResponse? =
                        gson.fromJson(e.response()?.errorBody()!!.charStream(), type)
                    emit(Status.Error( errorResponse?.message.toString()))
                    Log.e("forgetPassword: ", errorResponse?.message.toString())
                }
                is Exception->{
                    emit(Status.Error( e.message.toString()))
                    Log.e("forgetPassword: ", e.message.toString())
                }
            }

        }
    }.flowOn(Dispatchers.IO)
}