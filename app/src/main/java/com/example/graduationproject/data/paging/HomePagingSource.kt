package com.example.graduationproject.data.paging

import android.util.Log
import androidx.paging.LoadState
import androidx.paging.PagingSource
import androidx.paging.PagingState
import com.example.graduationproject.data.repository.BookRepo
import com.example.graduationproject.models.BookResponse
import com.example.graduationproject.models.BooksItem
import com.example.graduationproject.models.UserResponseLogin
import com.example.graduationproject.utils.WebServices
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import retrofit2.HttpException
import java.io.IOException
import javax.inject.Inject

class HomePagingSource (private val webServices: WebServices, private val token:String) :PagingSource<Int, BooksItem>(){
    override fun getRefreshKey(state: PagingState<Int, BooksItem>): Int? {
        TODO("Not yet implemented")
    }

    override suspend fun load(params: LoadParams<Int>): LoadResult<Int, BooksItem> {

        return  try {

            val currentPage = params.key ?: 1

            val response = webServices.getAllBooks(token, currentPage)


                val data = response.books
                val nextPageNumber = if (data.isNotEmpty()) currentPage + 1 else null

                LoadResult.Page(data, prevKey = null, nextKey = nextPageNumber)
            } catch (e: Throwable) {
                Log.e("load: ", e.message.toString())
                return LoadResult.Error(e)
            }
        }
}