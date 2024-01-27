package com.github.miwu.logic.database.dao

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.github.miwu.logic.database.model.MiwuDevice
import kotlinx.coroutines.flow.Flow

@Dao
interface FavoriteDeviceDAO {

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insert(item: MiwuDevice)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insert(items: List<MiwuDevice>)

    @Delete
    suspend fun delete(item: MiwuDevice)

    @Query("select * from miwu_device ORDER BY `index`")
    fun getList(): Flow<List<MiwuDevice>>

    @Query("delete from miwu_device")
    suspend fun deleteAll()
}