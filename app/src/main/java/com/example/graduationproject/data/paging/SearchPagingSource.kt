package com.example.graduationproject.data.paging

import androidx.paging.PagingSource
import androidx.paging.PagingState
import com.example.graduationproject.data.repository.BookRepo
import com.example.graduationproject.models.BookResponse
import com.example.graduationproject.models.BooksItem
import com.example.graduationproject.utils.WebServices
import javax.inject.Inject

class SearchPagingSource (private val webServices: WebServices ,val query:String,val token:String) :PagingSource<Int, BooksItem>(){
    override fun getRefreshKey(state: PagingState<Int, BooksItem>): Int? {
        TODO("Not yet implemented")
    }

    override suspend fun load(params: LoadParams<Int>): LoadResult<Int, BooksItem> {
        return  try {
            val currentPage = params.key ?: 1

            val response = webServices.search(query,token, currentPage)
                val data = response.books
                val nextPageNumber = if (data.isNotEmpty()) currentPage + 1 else null
                LoadResult.Page(data, prevKey = null, nextKey = nextPageNumber)
            } catch (e: Exception) {
                LoadResult.Error(e)
            }
        }
}