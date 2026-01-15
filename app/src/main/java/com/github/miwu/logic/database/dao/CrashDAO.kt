package com.github.miwu.logic.database.dao

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.github.miwu.logic.database.entity.CrashItem
import kotlinx.coroutines.flow.Flow

@Dao
interface CrashDAO {

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insert(item: CrashItem)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insert(items: List<CrashItem>)

    @Delete
    suspend fun delete(item: CrashItem)

    @Query("select * from crash_item ORDER BY `timestamp`")
    fun getListFlow(): Flow<List<CrashItem>>

    @Query("delete from favorite_device")
    suspend fun deleteAll()
}
