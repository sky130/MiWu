package com.github.miwu.logic.dao.database

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Update
import com.github.miwu.logic.model.mi.MiHome
import com.github.miwu.logic.model.mi.MiHomeEntity

@Dao
interface HomeDAO {

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun addHome(device: MiHomeEntity): Long

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun addHomes(devices: List<MiHomeEntity>): List<Long>

    @Query("select * from MiHome")
    fun getAllHome(): List<MiHomeEntity>

    @Query("select * from MiHome where homeId == :homeId")
    fun getHome(homeId: String): MiHomeEntity

    @Delete
    fun delHome(device: MiHomeEntity)

    @Query("select count(id) from MiHome")
    fun getCount(): Int

}