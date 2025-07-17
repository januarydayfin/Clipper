package com.krayapp.buffercompanion.utils

import androidx.room.Room
import androidx.room.RoomDatabase
import androidx.room.migration.Migration
import com.krayapp.buffercompanion.ClipperApp
import com.krayapp.buffercompanion.data.room.bargen.BargenDB
import java.util.concurrent.ConcurrentHashMap


object DatabaseCacheMap {
    private val map = ConcurrentHashMap<String, RoomDatabase>()
    fun provideDatabaseMap() = map
}

inline fun <reified DB : RoomDatabase> provideDatabase(): DB {
    val config = getDatabaseConfig<DB>()
        ?: throw IllegalStateException("${DB::class} создается без конфиги")

    return DatabaseCacheMap.provideDatabaseMap().getOrPut(config.dbName) {
        val builder = Room.databaseBuilder(
            ClipperApp.getApplication(),
            DB::class.java,
            config.dbName
        )

        with(builder) {
            fallbackToDestructiveMigration(config.destructiveMigrationEnabled)
            addMigrations(*config.migrations.toTypedArray())
            build()
        }

    } as DB
}

inline fun <reified T : RoomDatabase> getDatabaseConfig(): DatabaseConfig? =
    when (T::class) {
        BargenDB::class -> DatabaseConfig(dbName = BargenDB.DB_NAME)

        else -> null
    }

data class DatabaseConfig(
    val dbName: String,
    val destructiveMigrationEnabled: Boolean = false,
    val migrations: List<Migration> = emptyList()
)