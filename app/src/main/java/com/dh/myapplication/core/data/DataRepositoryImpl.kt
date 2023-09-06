package com.dh.myapplication.core.data

class DataRepositoryImpl(val flashDao: FlashDao) : DataRepository {
    override suspend fun insertFlash(flash: Flash) {
       flashDao.insertFlash(flash)
    }

    override suspend fun updateFlash(flash: Flash) {
      flashDao.updateFlash(flash)
    }

    override suspend fun getAllFlashes(): List<Flash> {
      return flashDao.getAllFlashes()
    }

    override suspend fun clearAllFlashes() {
        flashDao.clearAllFlashes()
    }


}