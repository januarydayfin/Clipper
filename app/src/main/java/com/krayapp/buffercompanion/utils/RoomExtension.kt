package com.krayapp.buffercompanion.utils

import android.util.Log
import androidx.room.RoomDatabase
import java.util.concurrent.Executors

fun <Type : RoomDatabase> RoomDatabase.Builder<Type>.addQueryLog(): RoomDatabase.Builder<Type> {
    setQueryCallback({ sqlQuery, bindArgs ->
        Log.d("ROOM_LOG_FATA", "SQL Query: $sqlQuery | Args: $bindArgs")
    }, Executors.newSingleThreadExecutor())
    return this
}