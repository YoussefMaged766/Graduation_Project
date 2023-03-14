package com.example.graduationproject.data.repository

import android.util.Log
import androidx.paging.Pager
import androidx.paging.PagingConfig
import androidx.paging.PagingData
import com.example.graduationproject.data.paging.SearchPagingSource
import com.example.graduationproject.db.HistorySearchEntity
import com.example.graduationproject.db.SearchDatabase
import com.example.graduationproject.models.*
import com.example.graduationproject.utils.NetworkState
import com.example.graduationproject.utils.Resource
import com.example.graduationproject.utils.Status
import com.example.graduationproject.utils.WebServices
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.*
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

    fun search(query: String, token: String): Flow<Resource<PagingData<BooksItem>>> {
        return Pager(PagingConfig(pageSize = 20,enablePlaceholders = true)) {
            SearchPagingSource(webServices, query, token)
        }.flow.map {

            Resource.success(it)
        }

            .onStart { emit(Resource.loading(null)) }
            .catch {
                emit(Resource.error(null, it.message.toString()))
            }
            .flowOn(Dispatchers.IO)
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


}