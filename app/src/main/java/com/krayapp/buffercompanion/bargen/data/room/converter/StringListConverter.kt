package com.krayapp.buffercompanion.bargen.data.room.bargen.converter

import androidx.room.TypeConverter
import kotlinx.serialization.json.Json

class StringListConverter {

    @TypeConverter
    fun fromJson(json: String): List<String> =
        Json.decodeFromString(json)


    @TypeConverter
    fun toJson(list: List<String>): String = Json.encodeToString(list)
}