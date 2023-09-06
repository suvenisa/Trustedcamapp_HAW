package com.dh.myapplication.core.data

interface DataRepository {

    suspend fun insertFlash(flash: Flash)
    suspend fun updateFlash(flash: Flash)
    suspend fun getAllFlashes(): List<Flash>
    suspend fun clearAllFlashes()


}