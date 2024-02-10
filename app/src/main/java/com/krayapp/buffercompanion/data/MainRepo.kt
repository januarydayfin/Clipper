package com.krayapp.buffercompanion.data

import android.content.Context
import androidx.room.Room
import com.krayapp.buffercompanion.data.room.MIGRATION_1_2
import com.krayapp.buffercompanion.data.room.MainDB
import com.krayapp.buffercompanion.data.room.StringEntity
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

class MainRepo(context: Context) {
    private val database =
        Room.databaseBuilder(context, MainDB::class.java, "mainDatabase").addMigrations(
            MIGRATION_1_2
        ).build()

    private val dao = database.getDao()
    fun addText(text: String) {
        CoroutineScope(Dispatchers.IO).launch {
            dao.upsert(StringEntity(text, dao.getCount()))
        }
    }

    fun remove(text: StringEntity) {
        CoroutineScope(Dispatchers.IO).launch {
            dao.delete(text)
        }
    }

    fun removeList(list : List<StringEntity>) {
        CoroutineScope(Dispatchers.IO).launch {
            for (item in list) {
                dao.delete(item)
            }
        }
    }

    fun replace(oldKey: String, new: StringEntity) {
        CoroutineScope(Dispatchers.IO).launch {
            dao.delete(oldKey)
            dao.upsert(new)
        }
    }
    fun loadList(onLoadSuccess: (List<StringEntity>) -> Unit) {
        CoroutineScope(Dispatchers.IO).launch {
            onLoadSuccess(dao.getAll())
        }
    }

    fun updateBaseIndexes(incomData: List<StringEntity>) {
        CoroutineScope(Dispatchers.IO).launch {
            for (entity in incomData) {
                dao.upsert(entity)
            }
        }
    }
}