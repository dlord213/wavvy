package com.mirimomekiku.wavvy.db.converters

import androidx.room.TypeConverter
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import com.mirimomekiku.wavvy.db.entity.PlaylistItem

class PlaylistConverters {
    @TypeConverter
    fun fromPlaylistItems(value: List<PlaylistItem>): String {
        return Gson().toJson(value)
    }

    @TypeConverter
    fun toPlaylistItems(value: String): List<PlaylistItem> {
        val listType = object : TypeToken<List<PlaylistItem>>() {}.type
        return Gson().fromJson(value, listType)
    }
}