package io.github.sky130.miwu.logic.dao.database

import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import io.github.sky130.miwu.MainApplication.Companion.context
import io.github.sky130.miwu.logic.dao.InstanceDAO
import io.github.sky130.miwu.logic.model.MiInstance

@Database(version = 1, entities = [MiInstance::class])
abstract class InstanceDatabase : RoomDatabase() {

    abstract fun instanceDAO(): InstanceDAO

    companion object {
        private var instance: InstanceDatabase? = null
        @Synchronized
        fun getDatabase():InstanceDatabase{
            instance?.let {
                return it
            }
            return Room.databaseBuilder(context,InstanceDatabase::class.java,"instance_database.db")
                .allowMainThreadQueries().build().apply {
                instance = this
            }
        }
    }

}