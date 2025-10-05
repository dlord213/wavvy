package com.mirimomekiku.wavvy.db

import Favorite
import androidx.room.Database
import androidx.room.RoomDatabase
import com.mirimomekiku.wavvy.db.dao.FavoriteDao


@Database(entities = [Favorite::class], version = 1)
abstract class AppDatabase : RoomDatabase() {
    abstract fun favoriteDao(): FavoriteDao
}