package com.github.miwu.logic.database

import androidx.room.AutoMigration
import androidx.room.Database
import androidx.room.RoomDatabase
import com.github.miwu.logic.database.dao.CrashDAO
import com.github.miwu.logic.database.dao.FavoriteDeviceDAO
import com.github.miwu.logic.database.entity.CrashItem
import com.github.miwu.logic.database.entity.FavoriteDevice
import com.github.miwu.logic.database.entity.FavoriteDeviceMetadata

@Database(
    version = 2,
    entities = [
        FavoriteDevice::class,
        FavoriteDeviceMetadata::class,
        CrashItem::class
    ],
    exportSchema = true,
    autoMigrations = [AutoMigration(from = 1, to = 2)]
)
abstract class AppDatabase : RoomDatabase() {
    abstract fun deviceDAO(): FavoriteDeviceDAO
    abstract fun crashDAO(): CrashDAO
}