package com.mirimomekiku.wavvy.db

import Favorite
import androidx.room.Database
import androidx.room.RoomDatabase
import androidx.room.TypeConverters
import com.mirimomekiku.wavvy.db.converters.PlaylistConverters
import com.mirimomekiku.wavvy.db.dao.FavoriteDao
import com.mirimomekiku.wavvy.db.dao.PlaylistDao
import com.mirimomekiku.wavvy.db.entity.Playlists


@Database(entities = [Favorite::class, Playlists::class], version = 2)
@TypeConverters(PlaylistConverters::class)
abstract class AppDatabase : RoomDatabase() {
    abstract fun playlistsDao(): PlaylistDao
    abstract fun favoriteDao(): FavoriteDao
}
