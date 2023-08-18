package io.github.sky130.miwu.logic.dao.database

import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import io.github.sky130.miwu.MainApplication.Companion.context
import io.github.sky130.miwu.logic.dao.DeviceDAO
import io.github.sky130.miwu.logic.dao.RoomDAO
import io.github.sky130.miwu.logic.dao.SceneDAO
import io.github.sky130.miwu.logic.model.mi.MiDevice
import io.github.sky130.miwu.logic.model.mi.MiHome
import io.github.sky130.miwu.logic.model.mi.MiHomeEntity
import io.github.sky130.miwu.logic.model.mi.MiRoomEntity
import io.github.sky130.miwu.logic.model.mi.MiScene

@Database(version = 1, entities = [MiDevice::class,MiScene::class,MiRoomEntity::class,MiHomeEntity::class])
abstract class AppDatabase : RoomDatabase() {

    abstract fun deviceDAO(): DeviceDAO
    abstract fun roomDAO(): RoomDAO
    abstract fun sceneDAO(): SceneDAO


    companion object {
        private var instance: AppDatabase? = null
        @Synchronized
        fun getDatabase():AppDatabase{
            instance?.let {
                return it
            }
            return Room.databaseBuilder(context,AppDatabase::class.java,"app.db")
                .allowMainThreadQueries().build().apply {
                instance = this
            }
        }
    }

}