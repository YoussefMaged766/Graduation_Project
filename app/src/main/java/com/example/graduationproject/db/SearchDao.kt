package com.example.graduationproject.db

import androidx.lifecycle.LiveData
import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import kotlinx.coroutines.flow.Flow


@Dao
interface SearchDao {

    @Insert
    suspend fun insertHistorySearch(historySearchEntity: HistorySearchEntity)

    @Query("SELECT * FROM HistorySearchEntity")
    fun getAllHistorySearch(): Flow<List<HistorySearchEntity>>

   @Query("DELETE FROM HistorySearchEntity WHERE `query`=:query")
   suspend fun deleteHistorySearch(query: String)
}