package com.dh.myapplication.core.di

import android.content.Context
import com.dh.myapplication.core.data.AppDatabase
import com.dh.myapplication.core.data.DataRepository
import com.dh.myapplication.core.data.DataRepositoryImpl
import com.dh.myapplication.simple.simpleViewModel
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object AppModule {

    @Singleton
    @Provides
    fun provideAppDatabase(@ApplicationContext context : Context) : AppDatabase {
        return AppDatabase.getDatabase(context )
    }
    @Singleton
    @Provides
    fun provideImpDataRepository( context : AppDatabase) : DataRepository {
        return DataRepositoryImpl(context.flashDao())
    }


  @Singleton
    @Provides
    fun providesimpleViewModel(@ApplicationContext context : Context, dataRepository : DataRepository ) : simpleViewModel {
        return simpleViewModel(context , dataRepository )
    }




}