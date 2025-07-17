package com.krayapp.buffercompanion.data.room.bargen.converter

import androidx.room.TypeConverter
import kotlinx.serialization.json.Json

class StringListConverter {

    @TypeConverter
    fun fromJson(json: String): List<String> =
        Json.decodeFromString(json)


    fun toJson(list: List<String>): String = Json.encodeToString(list)
}