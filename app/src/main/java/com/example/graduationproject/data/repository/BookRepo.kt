package com.example.graduationproject.data.repository

import android.content.Context
import android.net.Uri
import android.util.Log
import androidx.paging.Pager
import androidx.paging.PagingConfig
import androidx.paging.PagingData
import com.example.graduationproject.data.paging.HomePagingSource
import com.example.graduationproject.data.paging.SearchPagingSource
import com.example.graduationproject.models.HistorySearchEntity
import com.example.graduationproject.db.BookDatabase
import com.example.graduationproject.models.*
import com.example.graduationproject.models.mappers.toBookEntity
import com.example.graduationproject.utils.NetworkState
import com.example.graduationproject.utils.RecommendationService
import com.example.graduationproject.utils.Status
import com.example.graduationproject.utils.WebServices
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.*
import okhttp3.MediaType.Companion.toMediaTypeOrNull
import okhttp3.MultipartBody
import okhttp3.RequestBody
import okio.IOException
import retrofit2.HttpException
import retrofit2.Retrofit
import java.io.File
import javax.inject.Inject
import javax.inject.Named

class BookRepo @Inject constructor(
    private val webServices: WebServices,
    private val database: BookDatabase,
    private val recommendationService: RecommendationService

    ) {
    private val gson = Gson()
    private val networkState = NetworkState



    suspend fun getAllBooks(token: String): Flow<Status<PagingData<BooksItem>>> = flow {

        emit(Status.Loading)
        delay(2000)

        val page = Pager(PagingConfig(pageSize = 20, enablePlaceholders = true)) {
            HomePagingSource(webServices, token)
        }
        page.flow.map { pagingData ->
            emit(Status.Success(pagingData))
        }.catch { e ->
            if (e is HttpException) {
                val type = object : TypeToken<UserResponseLogin>() {}.type
                val errorResponse: UserResponseLogin? =
                    gson.fromJson(e.response()?.errorBody()!!.charStream(), type)
                Log.e("loginUsereeeeerrrrr: ", e.message().toString())
                emit(Status.Error(errorResponse?.message.toString()))
            } else if (e is IOException) {
                Log.e("loginUsereeeee: ", e.message.toString())
                emit(Status.Error(e.message.toString()))
            }
            Log.e("getAllBooks: ", e.message.toString())

        }.collect()


    }


    fun search(query: String, token: String): Flow<Status<PagingData<BooksItem>>> = flow {
        emit(Status.Loading)
        delay(2000)
        try {
            val page = Pager(PagingConfig(pageSize = 20, enablePlaceholders = true)) {
                SearchPagingSource(webServices, query, token)
            }

            page.flow.map { pagingData ->
                emit(Status.Success(pagingData))
            }.collect()

        } catch (e: Throwable) {
            when (e) {
                is HttpException -> {
                    val type = object : TypeToken<BooksItem>() {}.type
                    val errorResponse: BooksItem? =
                        gson.fromJson(e.response()?.errorBody()!!.charStream(), type)
                    Log.e("loginUsereeeeerrrrr: ", errorResponse?.message.toString())
                    emit(Status.Error(errorResponse?.message.toString()))
                }

                is Exception -> {
                    Log.e("loginUsereeeee: ", e.message.toString())
                    emit(Status.Error(e.message.toString()))
                }
            }
        }
    }


    fun saveSearchHistory(searchHistory: HistorySearchEntity) = flow {
        emit(Status.Loading)
        try {
            val mDao = database.searchDao()
            mDao.insertHistorySearch(searchHistory)
            emit(Status.Success(""))
        } catch (e: Exception) {
            emit(Status.Error(e.message.toString()))
        }
    }.flowOn(Dispatchers.IO)


    suspend fun getAllHistorySearch(userId: String) =
        database.searchDao().getAllHistorySearch(userId)

    fun deleteHistorySearch(query: String) = flow {
        try {
            emit(Status.Loading)
            val mDao = database.searchDao()
            mDao.deleteHistorySearch(query)
            emit(Status.Success(""))
        } catch (e: Exception) {
            emit(Status.Error(e.message.toString()))

        }
    }.flowOn(Dispatchers.IO)

    suspend fun addFavorite(
        key: String,
        bookId: BookIdResponse,
        booksItem: BooksItem,
        userId: String
    ) = flow {
        try {
            emit(Status.Loading)
            if (networkState.isOnline()) {
                val response = webServices.addFavourite(key, bookId)

                emit(Status.Success(response))
                if (database.searchDao().bookExist(booksItem.bookId!!, userId)) {
                    database.searchDao().setFavoriteBook(booksItem.bookId, userId)
                } else {
                    database.searchDao().insertBook(booksItem.toBookEntity(userId))
                    database.searchDao().setFavoriteBook(booksItem.bookId, userId)
                }

//                database.searchDao().setFavoriteBook(booksItem.bookId!!,userId)
                Log.e("loginUser: ", response.toString())
            } else {

                emit(Status.Error("no Internet"))
                Log.e("addFavorite: ", "no Internet")

            }

        } catch (e: Exception) {
            emit(Status.Error(e.message.toString()))
        }

    }

    suspend fun removeFavorite(
        key: String,
        bookId: BookIdResponse,
        booksItem: BooksItem,
        userId: String
    ) = flow {
        try {
            emit(Status.Loading)
            if (networkState.isOnline()) {
                val response = webServices.removeFavourite(key, bookId)
                emit(Status.Success(response))
                database.searchDao().unFavoriteBook(booksItem.bookId!!, userId)

                Log.e("loginUser: ", response.toString())
            } else {
                emit(Status.Error("no Internet"))
            }

        } catch (e: Exception) {
            emit(Status.Error(e.message.toString()))
        }

    }

    suspend fun getAllFavorite(token: String, userId: String) = flow {
        emit(Status.Loading)

        try {
            if (networkState.isOnline()) {
                val response = webServices.getAllFavourite(token)
                val books = response.results?.map { it.toBookEntity(userId) }

                emit(Status.Success(books))
            } else {
                val books = database.searchDao().getAllFavoriteBooksLocal(userId).map { it }
                books.collect {
                    emit(Status.Success(it))
                }

            }

        } catch (e: Throwable) {
            when (e) {
                is HttpException -> {
                    val type = object : TypeToken<BooksItem>() {}.type
                    val errorResponse: BooksItem? =
                        gson.fromJson(e.response()?.errorBody()!!.charStream(), type)
                    Log.e("loginUsereeeeerrrrr: ", errorResponse?.message.toString())
                    emit(Status.Error(errorResponse?.message.toString()))
                }

                is Exception -> {
                    Log.e("loginUsereeeee: ", e.message.toString())
                    emit(Status.Error(e.message.toString()))
                }
            }

        }
    }

    suspend fun addToWishlist(
        key: String,
        bookId: BookIdResponse,
        booksItem: BooksItem,
        userId: String
    ) = flow {
        try {
            emit(Status.Loading)
            if (networkState.isOnline()) {
                val response = webServices.addToWishlist(key, bookId)
                emit(Status.Success(response))
                if (database.searchDao().bookExist(booksItem.bookId!!, userId)) {
                    database.searchDao().setWishBook(booksItem.bookId, userId)
                } else {
                    database.searchDao().insertBook(booksItem.toBookEntity(userId))
                    database.searchDao().setWishBook(booksItem.bookId, userId)
                }
//                database.searchDao().setWishBook(booksItem.bookId!!,userId)

                Log.e("loginUser: ", response.toString())
            } else {
                emit(Status.Error("no Internet"))
            }

        } catch (e: Exception) {
            emit(Status.Error(e.message.toString()))
        }

    }

    suspend fun insertBookLocal(booksItem: BooksItem, userId: String) =
        database.searchDao().insertBook(booksItem.toBookEntity(userId))

    suspend fun getAllBooksLocal(userId: String) = database.searchDao().getAllBooksLocal(userId)

    suspend fun getAllWishlist(token: String, userId: String) = flow {
        emit(Status.Loading)

        try {
            if (networkState.isOnline()) {
                val response = webServices.getAllWishlist(token)
                val books = response.results?.map { it.toBookEntity(userId) }
                emit(Status.Success(books))

            } else {
                val books = database.searchDao().getAllWishListBooksLocal(userId).map { it }
                books.collect {
                    emit(Status.Success(it))
                }
            }

        } catch (e: Throwable) {
            when (e) {
                is HttpException -> {
                    val type = object : TypeToken<BooksItem>() {}.type
                    val errorResponse: BooksItem? =
                        gson.fromJson(e.response()?.errorBody()!!.charStream(), type)
                    Log.e("loginUsereeeeerrrrr: ", errorResponse?.message.toString())
                    emit(Status.Error(errorResponse?.message.toString()))
                }

                is Exception -> {
                    Log.e("loginUsereeeee: ", e.message.toString())
                    emit(Status.Error(e.message.toString()))
                }
            }

        }
    }

    suspend fun removeWishlist(
        key: String,
        bookId: BookIdResponse,
        bookIdInRoom: Int,
        userId: String
    ) = flow {
        try {
            emit(Status.Loading)
            if (networkState.isOnline()) {
                val response = webServices.removeWishlist(key, bookId)
                emit(Status.Success(response))
                database.searchDao().unWishBook(bookIdInRoom, userId)

                Log.e("loginUser: ", response.toString())
            } else {
                emit(Status.Error("no Internet"))
            }

        } catch (e: Exception) {
            emit(Status.Error(e.message.toString()))
        }

    }

    suspend fun getAllRead(token: String, userId: String) = flow {
        emit(Status.Loading)

        try {
            if (networkState.isOnline()) {
                val response = webServices.getAllAlreadyRead(token)
                val books = response.results?.map { it.toBookEntity(userId) }
                emit(Status.Success(books))

            } else {
                val books = database.searchDao().getAllReadBooksLocal(userId).map { it }
                books.collect {
                    emit(Status.Success(it))
                }
            }

        } catch (e: Throwable) {
            when (e) {
                is HttpException -> {
                    val type = object : TypeToken<BooksItem>() {}.type
                    val errorResponse: BooksItem? =
                        gson.fromJson(e.response()?.errorBody()!!.charStream(), type)
                    Log.e("loginUsereeeeerrrrr: ", errorResponse?.message.toString())
                    emit(Status.Error(errorResponse?.message.toString()))
                }

                is Exception -> {
                    Log.e("loginUsereeeee: ", e.message.toString())
                    emit(Status.Error(e.message.toString()))
                }
            }

        }
    }

    suspend fun addToRead(
        key: String,
        bookId: BookIdResponse,
        booksItem: BooksItem,
        userId: String
    ) = flow {
        try {
            emit(Status.Loading)
            if (networkState.isOnline()) {
                val response = webServices.addToAlreadyRead(key, bookId)
                emit(Status.Success(response))
                if (database.searchDao().bookExist(booksItem.bookId!!, userId)) {
                    database.searchDao().setReadBook(booksItem.bookId, userId)
                } else {
                    database.searchDao().insertBook(booksItem.toBookEntity(userId))
                    database.searchDao().setReadBook(booksItem.bookId, userId)
                }
//                database.searchDao().setWishBook(booksItem.bookId!!,userId)

                Log.e("loginUser: ", response.toString())
            } else {
                emit(Status.Error("no Internet"))
            }

        } catch (e: Exception) {
            emit(Status.Error(e.message.toString()))
        }

    }

    suspend fun removeRead(
        key: String,
        bookId: BookIdResponse,
        bookIdInRoom: Int,
        userId: String
    ) = flow {
        try {
            emit(Status.Loading)
            if (networkState.isOnline()) {
                val response = webServices.removeAlreadyRead(key, bookId)
                emit(Status.Success(response))
                database.searchDao().unReadBook(bookIdInRoom, userId)

                Log.e("loginUser: ", response.toString())
            } else {
                emit(Status.Error("no Internet"))
            }

        } catch (e: Exception) {
            emit(Status.Error(e.message.toString()))
        }

    }

    suspend fun updateProfile(
        token: String,
        fileUri: Uri?,
        fileRealPath: String?,
        firstName: String,
        lastName: String,
        email: String,
        ctx: Context
    ) = flow {
        emit(Status.Loading)
        try {
            if (networkState.isOnline()) {
                if (fileUri != null && fileRealPath != null){
                    val fileToSend = prepareFilePart("image", fileRealPath,fileUri ,ctx)
                    val firstNameRequestBody: RequestBody = RequestBody.create("text/plain".toMediaTypeOrNull(), firstName)
                    val lastNameRequestBody: RequestBody = RequestBody.create("text/plain".toMediaTypeOrNull(), lastName)
                    val emailRequestBody: RequestBody = RequestBody.create("text/plain".toMediaTypeOrNull(), email)
                    val response = webServices.updateProfile( token, fileToSend, firstNameRequestBody, lastNameRequestBody, emailRequestBody)
                    emit(Status.Success(response))
                }else{
                    val firstNameRequestBody: RequestBody = RequestBody.create("text/plain".toMediaTypeOrNull(), firstName)
                    val lastNameRequestBody: RequestBody = RequestBody.create("text/plain".toMediaTypeOrNull(), lastName)
                    val emailRequestBody: RequestBody = RequestBody.create("text/plain".toMediaTypeOrNull(), email)

                    val response = webServices.updateProfile(firstName =  firstNameRequestBody,
                        lastName = lastNameRequestBody,
                        email =  emailRequestBody,
                        token = token
                       )
                    emit(Status.Success(response))
                }

            }
        } catch (e: Exception) {
            emit(Status.Error(e.message.toString()))
        }
    }.flowOn(Dispatchers.IO)


    suspend fun getProfile(token: String) = flow {
        emit(Status.Loading)
        try {
            if (networkState.isOnline()) {
                val response = webServices.getProfile(token)
                emit(Status.Success(response))

            }
        } catch (e: Exception) {
            emit(Status.Error(e.message.toString()))

        }
    }

    private fun prepareFilePart(partName: String,fileRealPath: String,fileUri: Uri , ctx:Context): MultipartBody.Part {
        val file: File = File(fileRealPath)
        val requestFile: RequestBody = RequestBody.create(
            ctx.contentResolver.getType(fileUri)!!.toMediaTypeOrNull(), file)
        return MultipartBody.Part.createFormData(partName, file.name, requestFile)
    }

    fun getRecommendation(id:String)= flow {
        emit(Status.Loading)
        try {

                val response = recommendationService.getRecommendations(id)
                emit(Status.Success(response))
                Log.e( "getRecommendation: ",response.toString() )


        } catch (e: Exception) {
            emit(Status.Error(e.message.toString()))
            Log.e( "getRecommendation: ",e.message.toString() )

        }
    }
}