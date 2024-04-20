package com.github.miwu.logic.database

import androidx.room.AutoMigration
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import com.github.miwu.logic.database.dao.FavoriteDeviceDAO
import com.github.miwu.logic.database.model.MiwuDevice
import kndroidx.extension.isDebug

@Database(
    version = 11,
    entities = [MiwuDevice::class],
    exportSchema = true,
)
abstract class AppDatabase : RoomDatabase() {

    abstract fun deviceDAO(): FavoriteDeviceDAO

    companion object {
        val instance by lazy {
            Room.databaseBuilder(
                kndroidx.KndroidX.context, AppDatabase::class.java, "app_database"
            ).apply {
                fallbackToDestructiveMigration()
            }.build()
        }
    }
}