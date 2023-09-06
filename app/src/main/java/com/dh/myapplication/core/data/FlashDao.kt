package com.dh.myapplication.core.data

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.Query
import androidx.room.Update

@Dao
interface FlashDao {
    @Insert
    suspend fun insertFlash(flash: Flash)

    @Update
    suspend fun updateFlash(flash: Flash)

    @Query("SELECT * FROM flash")
    suspend fun getAllFlashes(): List<Flash>

    @Query("DELETE FROM flash")
  suspend fun clearAllFlashes()
}
