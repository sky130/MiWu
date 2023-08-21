package com.github.miwu.logic.dao.database

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Update
import com.github.miwu.logic.model.mi.MiDevice

@Dao
interface DeviceDAO {

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun addDevice(device: MiDevice): Long

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun addDevices(devices: List<MiDevice>): List<Long>

    @Update
    fun updateDevice(device: MiDevice)

    @Query("select * from MiDevice")
    fun getAllDevice(): List<MiDevice>

    @Query("select * from MiDevice where homeId == :homeId")
    fun getDeviceFromHome(homeId: String): List<MiDevice>

    @Query("select * from MiDevice where roomId == :roomId")
    fun getDeviceFromRoom(roomId: String): List<MiDevice>

    @Delete
    fun delDevice(device: MiDevice)

    @Query("select count(id) from MiDevice")
    fun getCount(): Int

}