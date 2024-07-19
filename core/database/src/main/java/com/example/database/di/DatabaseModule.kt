package com.example.database.di

import android.content.Context
import androidx.room.Room
import com.example.database.FavoriteDatabase
import com.example.database.source.FavoriteLocalDataSource
import com.example.database.source.FavoriteLocalDataSourceImpl
import com.example.database.util.Constants.FAVORITES_DATABASE
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object DatabaseModule {

    @Provides
    @Singleton
    fun provideDatabase(
        @ApplicationContext context: Context
    ): FavoriteDatabase = Room.databaseBuilder(
        context.applicationContext,
        FavoriteDatabase::class.java,
        FAVORITES_DATABASE
    ).build()

    @Provides
    @Singleton
    fun providesFavoriteLocalDataSource(
        favoriteDatabase: FavoriteDatabase
    ): FavoriteLocalDataSource {
        return FavoriteLocalDataSourceImpl(favoriteDao = favoriteDatabase.favoriteDao())
    }

}