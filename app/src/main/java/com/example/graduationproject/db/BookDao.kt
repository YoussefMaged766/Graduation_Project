package com.example.graduationproject.db

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.example.graduationproject.models.BookEntity
import com.example.graduationproject.models.HistorySearchEntity
import kotlinx.coroutines.flow.Flow


@Dao
interface BookDao {

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertHistorySearch(historySearchEntity: HistorySearchEntity)

    @Query("SELECT * FROM history_search where userId=:userId")
    fun getAllHistorySearch(userId: String): Flow<List<HistorySearchEntity>>

    @Query("DELETE FROM history_search WHERE `query`=:query")
    suspend fun deleteHistorySearch(query: String)


    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertBook(bookEntity: BookEntity)

    @Query("DELETE FROM BookEntity WHERE bookId =:bookId and userId=:userId")
    suspend fun deleteBook(bookId: Int, userId: String)

    @Query("SELECT * FROM BookEntity where userId=:userId and is_favorite=1")
    fun getAllFavoriteBooksLocal(userId: String): Flow<List<BookEntity>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
   suspend fun insetAllBooks(books: List<BookEntity>)

    @Query("UPDATE BookEntity SET is_favorite = 1 WHERE bookId = :bookId and userId=:userId")
    suspend fun setFavoriteBook(bookId: Int, userId: String)

    @Query("UPDATE BookEntity SET is_favorite = 0 WHERE bookId =:bookId and userId=:userId")
    suspend fun unFavoriteBook(bookId: Int, userId: String)



}