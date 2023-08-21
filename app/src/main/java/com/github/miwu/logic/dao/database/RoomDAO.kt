package com.github.miwu.logic.dao.database

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Update
import com.github.miwu.logic.model.mi.MiRoom
import com.github.miwu.logic.model.mi.MiRoomEntity

@Dao
interface RoomDAO {

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun addRoom(room: MiRoomEntity): Long

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun addRooms(rooms: List<MiRoomEntity>): List<Long>

    @Query("select * from MiRoom")
    fun getAllRoom(): List<MiRoomEntity>

    @Query("select * from MiRoom where homeId == :homeId")
    fun getRoom(homeId: String): List<MiRoomEntity>

    @Delete
    fun delRoom(room: MiRoomEntity)

    @Query("select count(id) from MiRoom")
    fun getCount(): Int

}