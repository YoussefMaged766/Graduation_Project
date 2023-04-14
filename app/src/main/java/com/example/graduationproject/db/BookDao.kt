package com.example.graduationproject.db

import androidx.room.Dao
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

    @Query("SELECT * FROM HistorySearchEntity where userId=:userId")
    fun getAllHistorySearch(userId:String): Flow<List<HistorySearchEntity>>

   @Query("DELETE FROM HistorySearchEntity WHERE `query`=:query")
   suspend fun deleteHistorySearch(query: String)


}