package com.mirimomekiku.wavvy.db.dao

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Update
import com.mirimomekiku.wavvy.db.entity.Playlists
import kotlinx.coroutines.flow.Flow

@Dao
interface PlaylistDao {
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insert(playlist: Playlists)

    @Update
    suspend fun update(playlist: Playlists)

    @Delete
    suspend fun delete(playlist: Playlists)

    @Query("DELETE FROM playlists WHERE playlistId = :id")
    suspend fun deleteById(id: String)

    @Query("SELECT * FROM playlists")
    fun getAll(): Flow<List<Playlists>>

    @Query("SELECT * FROM playlists WHERE playlistId = :id LIMIT 1")
    suspend fun getOne(id: String): Playlists?

    @Query("SELECT * FROM playlists WHERE playlistId = :id LIMIT 1")
    fun getById(id: String): Flow<Playlists?>

    @Query("DELETE FROM playlists")
    suspend fun clearAll()
}
