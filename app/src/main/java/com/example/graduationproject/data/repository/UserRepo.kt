package com.example.graduationproject.data.repository

import android.content.Context
import android.net.Uri
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
import okhttp3.MediaType
import okhttp3.MediaType.Companion.toMediaTypeOrNull
import okhttp3.MultipartBody
import okhttp3.RequestBody
import retrofit2.HttpException
import retrofit2.Response
import java.io.File
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

    suspend fun signUpUser(fileUri: Uri, fileRealPath: String ,firstName: RequestBody,lastName:RequestBody , email:RequestBody , password:RequestBody , ctx:Context ) = flow {

        try {
            emit(Status.Loading)
            if (networkState.isOnline()){
                val fileToSend = prepareFilePart("image", fileRealPath,fileUri ,ctx)
                val response = webServices.signUpUser(fileToSend, firstName, lastName, email, password)
                emit(Status.Success(response))
                Log.e( "signUpUser: ",response.toString() )
            }else{
                emit(Status.Error("no Internet"))
            }


        } catch (e: Throwable) {
            when(e){
                is HttpException->{
                    val type = object : TypeToken<UserResponseSignUp>() {}.type
                    val errorResponse: UserResponseSignUp? =
                        gson.fromJson(e.response()?.errorBody()!!.charStream(), type)
                    Log.e("signUpUser: ", errorResponse?.message.toString())
                    emit(Status.Error( errorResponse?.message.toString()))
                }
                is Exception->{
                    emit(Status.Error( e.message.toString()))
                    Log.e( "signUpUser: ",e.message.toString() )
                }
            }

        }

    }.flowOn(Dispatchers.IO)

    private fun prepareFilePart(partName: String,fileRealPath: String,fileUri: Uri , ctx:Context): MultipartBody.Part {
        val file: File = File(fileRealPath)
        val requestFile: RequestBody = RequestBody.create(
            ctx.contentResolver.getType(fileUri)!!.toMediaTypeOrNull(), file)
        return MultipartBody.Part.createFormData(partName, file.name, requestFile)
    }

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