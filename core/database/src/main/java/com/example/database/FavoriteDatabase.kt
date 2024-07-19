package com.example.database

import androidx.room.Database
import androidx.room.RoomDatabase
import com.example.database.dao.FavoriteDao
import com.example.database.model.FavoriteMealEntity

@Database(entities = [FavoriteMealEntity::class], version = 1)
abstract class FavoriteDatabase: RoomDatabase() {
    abstract fun favoriteDao(): FavoriteDao
}