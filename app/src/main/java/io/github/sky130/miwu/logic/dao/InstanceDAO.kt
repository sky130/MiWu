package io.github.sky130.miwu.logic.dao

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.Query
import androidx.room.Update
import io.github.sky130.miwu.logic.model.MiInstance

@Dao
interface InstanceDAO {

    @Insert
    fun addInstance(instance: MiInstance): Long

    @Insert
    fun addInstances(instances: List<MiInstance>): List<Long>

    @Update
    fun updateInstance(instance: MiInstance)

    @Query("select * from MiInstance")
    fun getAllInstance(): List<MiInstance>

    @Query("select * from MiInstance where model == :model")
    fun getInstance(model: String): List<MiInstance>

    @Delete
    fun delInstance(instance: MiInstance)

    @Query("select count(id) from MiInstance")
    fun getCount(): Int

}