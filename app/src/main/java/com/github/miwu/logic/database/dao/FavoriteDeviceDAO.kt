package com.github.miwu.logic.database.dao

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.RawQuery
import androidx.room.Transaction
import androidx.sqlite.db.SupportSQLiteQuery
import com.github.miwu.logic.database.entity.FavoriteDevice
import com.github.miwu.logic.database.entity.FavoriteDeviceMetadata
import kotlinx.coroutines.flow.Flow

@Dao
interface FavoriteDeviceDAO {

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insert(item: FavoriteDevice): Long

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insert(items: List<FavoriteDevice>): List<Long>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertMeta(items: FavoriteDeviceMetadata)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertMeta(items: List<FavoriteDeviceMetadata>)

    @Delete
    suspend fun delete(item: FavoriteDevice)

    @Query("delete from favorite_device")
    suspend fun deleteAll()

    @Transaction
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun updateSortIndices(refs: List<FavoriteDeviceMetadata>)

    @Transaction
    @Query(
        """
        SELECT m.* FROM favorite_device m
        INNER JOIN favorite_device_metadata pmc ON m.did = pmc.did and m.uid = pmc.uid
        ORDER BY pmc.sort_index ASC
    """
    )
    suspend fun getList(): List<FavoriteDevice>

    @Transaction
    @Query(
        """
        SELECT m.* FROM favorite_device m
        INNER JOIN favorite_device_metadata pmc ON m.did = pmc.did and m.uid = pmc.uid
        ORDER BY pmc.sort_index ASC
    """
    )
    fun observeList(): Flow<List<FavoriteDevice>>
}