package com.mirimomekiku.wavvy.db.dao

import Favorite
import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import kotlinx.coroutines.flow.Flow

@Dao
interface FavoriteDao {
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insert(item: Favorite)

    @Delete
    suspend fun delete(item: Favorite)

    @Query("SELECT * FROM favorites")
    fun getAll(): Flow<List<Favorite>>

    @Query("SELECT * FROM favorites WHERE mediaId = :id")
    fun getById(id: String): Flow<Favorite?>
}