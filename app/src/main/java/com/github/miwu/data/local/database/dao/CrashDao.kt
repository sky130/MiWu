package com.github.miwu.data.local.database.dao

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.github.miwu.data.local.database.entity.CrashEntity
import kotlinx.coroutines.flow.Flow

@Dao
interface CrashDao {

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insert(item: CrashEntity)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insert(items: List<CrashEntity>)

    @Delete
    suspend fun delete(item: CrashEntity)

    @Query("select * from crash_item ORDER BY `timestamp`")
    fun getListFlow(): Flow<List<CrashEntity>>

    @Query("delete from crash_item")
    suspend fun deleteAll()
}
