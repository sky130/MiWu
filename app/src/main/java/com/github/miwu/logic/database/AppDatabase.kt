package com.github.miwu.logic.database

import androidx.room.AutoMigration
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import com.github.miwu.logic.database.dao.CrashDAO
import com.github.miwu.logic.database.dao.FavoriteDeviceDAO
import com.github.miwu.logic.database.model.CrashItem
import com.github.miwu.logic.database.model.MiwuDatabaseDevice

@Database(
    version = 1,
    entities = [MiwuDatabaseDevice::class, CrashItem::class],
    exportSchema = true,
    autoMigrations = [    ]
)
abstract class AppDatabase : RoomDatabase() {
    abstract fun device(): FavoriteDeviceDAO
    abstract fun crash(): CrashDAO
}