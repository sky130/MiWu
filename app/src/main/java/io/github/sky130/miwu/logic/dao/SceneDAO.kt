package io.github.sky130.miwu.logic.dao

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Update
import io.github.sky130.miwu.logic.model.mi.MiDevice

@Dao
interface SceneDAO {

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun addDevice(device: MiDevice): Long

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun addDevices(devices: List<MiDevice>): List<Long>

    @Update
    fun updateDevice(device: MiDevice)

    @Query("select * from MiDevice")
    fun getAllDevice(): List<MiDevice>

    @Query("select * from MiDevice where homeId == :homeId")
    fun getDevice(homeId: String): List<MiDevice>

    @Delete
    fun delDevice(device: MiDevice)

    @Query("select count(id) from MiDevice")
    fun getCount(): Int

}