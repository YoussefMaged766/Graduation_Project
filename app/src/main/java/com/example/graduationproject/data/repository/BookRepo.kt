package com.example.graduationproject.data.repository

import android.util.Log
import androidx.paging.Pager
import androidx.paging.PagingConfig
import androidx.paging.PagingData
import com.example.graduationproject.data.paging.HomePagingSource
import com.example.graduationproject.data.paging.SearchPagingSource
import com.example.graduationproject.db.HistorySearchEntity
import com.example.graduationproject.db.SearchDatabase
import com.example.graduationproject.models.*
import com.example.graduationproject.utils.NetworkState
import com.example.graduationproject.utils.Resource
import com.example.graduationproject.utils.WebServices
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.*
import okio.IOException
import retrofit2.HttpException
import javax.inject.Inject

class BookRepo @Inject constructor(
    private val webServices: WebServices,
    private val database: SearchDatabase
) {
    private val gson = Gson()
    private val networkState = NetworkState
//    fun search(query: String, token: String, page:Int) = flow {
//        try {
//            emit(Resource.loading(null))
//
//            if (networkState.isOnline()) {
//                val response = webServices.search(query, token,page)
//                emit(Resource.success(response))
//
//                Log.e("loginUser: ", response.toString())
//            } else {
//                emit(Resource.error(null, "no Internet"))
//            }
//        } catch (e: Throwable) {
//            when (e) {
//                is HttpException -> {
//                    val type = object : TypeToken<BooksItem>() {}.type
//                    val errorResponse: BooksItem? =
//                        gson.fromJson(e.response()?.errorBody()!!.charStream(), type)
//                    Log.e("loginUsereeeeerrrrr: ", errorResponse?.message.toString())
//                    emit(Resource.error(null, errorResponse?.message.toString()))
//                }
//                is Exception -> {
//                    Log.e("loginUsereeeee: ", e.message.toString())
//                    emit(Resource.error(null, e.message.toString()))
//                }
//            }
//        }
//
//    }.flowOn(Dispatchers.IO)


    suspend fun getAllBooks(token: String): Flow<Resource<PagingData<BooksItem>>> = flow {

        emit(Resource.loading(null))
        delay(2000)

            val page = Pager(PagingConfig(pageSize = 20, enablePlaceholders = true)) {
                HomePagingSource(webServices, token)
            }
            page.flow.map { pagingData ->
                emit(Resource.success(pagingData))
            }.catch  {e->
                if (e is HttpException){
                    val type = object : TypeToken<UserResponseLogin>() {}.type
                    val errorResponse: UserResponseLogin? =
                        gson.fromJson(e.response()?.errorBody()!!.charStream(), type)
                    Log.e("loginUsereeeeerrrrr: ", e.message().toString())
                    emit(Resource.error(null, errorResponse?.message.toString()))
                }
                else if (e is IOException){
                    Log.e("loginUsereeeee: ", e.message.toString())
                    emit(Resource.error(null, e.message.toString()))
                }
                Log.e( "getAllBooks: ",e.message.toString() )

        } .collect()


    }


    fun search(query: String, token: String): Flow<Resource<PagingData<BooksItem>>> = flow {
        emit(Resource.loading(null))
        kotlinx.coroutines.delay(2000)
        try {
            val page = Pager(PagingConfig(pageSize = 20, enablePlaceholders = true)) {
                SearchPagingSource(webServices, query, token)
            }

            page.flow.map { pagingData ->
                emit(Resource.success(pagingData))
            }.collect()

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
    }


    fun saveSearchHistory(searchHistory: HistorySearchEntity) = flow {
        emit(Resource.loading(null))
        try {
            val mDao = database.searchDao()
            mDao.insertHistorySearch(searchHistory)
            emit(Resource.success(""))
        } catch (e: Exception) {
            emit(Resource.error(null, e.message.toString()))
        }
    }.flowOn(Dispatchers.IO)


    suspend fun getAllHistorySearch(userId: String) =
        database.searchDao().getAllHistorySearch(userId)

//        try {
//            emit(Resource.loading(null))
//            val mDao = database.searchDao()
//            val historySearch = mDao.getAllHistorySearch()
//            Log.e( "getAllHistorySearch: ",historySearch.toString() )
//            emit(Resource.success(historySearch))
//        } catch (e: Exception) {
//            emit(Resource.error(null, e.message.toString()))
//
//        }


    fun deleteHistorySearch(query: String) = flow {
        try {
            emit(Resource.loading(null))
            val mDao = database.searchDao()
            mDao.deleteHistorySearch(query)
            emit(Resource.success(""))
        } catch (e: Exception) {
            emit(Resource.error(null, e.message.toString()))

        }
    }.flowOn(Dispatchers.IO)

    suspend fun addFavorite(key:String, bookId:String) = flow{
        try {
            emit(Resource.loading(null))
            if (networkState.isOnline()) {
                val response = webServices.addFavoirate(key,bookId)
                emit(Resource.success(response))

                Log.e("loginUser: ", response.toString())
            } else {
                emit(Resource.error(null, "no Internet"))
            }

        }catch (e: Exception){
            emit(Resource.error(null, e.message.toString()))
        }
        webServices.addFavoirate(key,bookId)
    }


}