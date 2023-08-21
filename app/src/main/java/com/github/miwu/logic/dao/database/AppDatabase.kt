package com.github.miwu.logic.dao.database

import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import com.github.miwu.MainApplication.Companion.context
import com.github.miwu.logic.model.mi.MiDevice
import com.github.miwu.logic.model.mi.MiHomeEntity
import com.github.miwu.logic.model.mi.MiRoomEntity
import com.github.miwu.logic.model.mi.MiScene
import com.github.miwu.logic.model.mi.MiSpecType

@Database(
    version = 2,
    entities = [MiDevice::class, MiScene::class, MiRoomEntity::class, MiHomeEntity::class, MiSpecType::class]
)
abstract class AppDatabase : RoomDatabase() {

    abstract fun deviceDAO(): DeviceDAO
    abstract fun roomDAO(): RoomDAO
    abstract fun sceneDAO(): SceneDAO
    abstract fun homeDAO(): HomeDAO
    abstract fun specDAO(): SpecDAO

    companion object {
        private var instance: AppDatabase? = null

        @Synchronized
        fun getDatabase(): AppDatabase {
            instance?.let {
                return it
            }
            return Room.databaseBuilder(context, AppDatabase::class.java, "app.db")
                .allowMainThreadQueries()
                .fallbackToDestructiveMigration() // 虽然这个代码很不好,但是问题不大的
                .build().apply {
                    instance = this
                }
        }
    }

}