package com.krayapp.buffercompanion.data

import android.content.Context
import android.util.Log
import androidx.room.Room
import com.krayapp.buffercompanion.data.room.MainDB
import com.krayapp.buffercompanion.data.room.StringEntity
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

class RememberedRepo(context: Context) {
    private val database = Room.databaseBuilder(context, MainDB::class.java, "mainDatabase").build()

    private val dao = database.getDao()
    fun addText(text: String) {
        CoroutineScope(Dispatchers.IO).launch {
            dao.insert(StringEntity(text))
        }
    }

    fun remove(text: String) {
        CoroutineScope(Dispatchers.IO).launch {
            dao.delete(StringEntity(text))
        }
    }

    fun loadList(onLoadSuccess: (List<StringEntity>) -> Unit) {
        CoroutineScope(Dispatchers.IO).launch {
            onLoadSuccess(dao.getAll())
        }
    }
}