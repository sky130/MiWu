package com.github.miwu.logic.database.dao

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.github.miwu.logic.database.model.MiwuDatabaseDevice
import kotlinx.coroutines.flow.Flow

@Dao
interface FavoriteDeviceDAO {

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insert(item: MiwuDatabaseDevice)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insert(items: List<MiwuDatabaseDevice>)

    @Delete
    suspend fun delete(item: MiwuDatabaseDevice)

    @Query("select * from favorite_device ORDER BY `position` ASC")
    fun getListFlow(): Flow<List<MiwuDatabaseDevice>>

    @Query("delete from favorite_device")
    suspend fun deleteAll()
}