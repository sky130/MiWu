package com.github.miwu.data.local.database.dao

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.RawQuery
import androidx.room.Transaction
import androidx.sqlite.db.SupportSQLiteQuery
import com.github.miwu.data.local.database.entity.FavoriteDeviceEntity
import com.github.miwu.data.local.database.entity.FavoriteDeviceOrderEntity
import kotlinx.coroutines.flow.Flow

@Dao
interface FavoriteDeviceDao {

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insert(item: FavoriteDeviceEntity): Long

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insert(items: List<FavoriteDeviceEntity>): List<Long>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertOrder(item: FavoriteDeviceOrderEntity)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertOrder(items: List<FavoriteDeviceOrderEntity>)

    @Delete
    suspend fun delete(item: FavoriteDeviceEntity)

    @Query("delete from favorite_device")
    suspend fun deleteAll()

    @Transaction
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun updateSortIndices(refs: List<FavoriteDeviceOrderEntity>)

    @Transaction
    @Query(
        """
        SELECT m.* FROM favorite_device m
        INNER JOIN favorite_device_metadata pmc ON m.did = pmc.did and m.uid = pmc.uid
        WHERE m.uid = :uid
        ORDER BY pmc.sort_index ASC
    """
    )
    suspend fun getList(uid: Long): List<FavoriteDeviceEntity>

    @Transaction
    @Query(
        """
        SELECT m.* FROM favorite_device m
        INNER JOIN favorite_device_metadata pmc ON m.did = pmc.did and m.uid = pmc.uid
        WHERE m.uid = :uid
        ORDER BY pmc.sort_index ASC
    """
    )
    fun observeList(uid: Long): Flow<List<FavoriteDeviceEntity>>

    @Query("SELECT * FROM favorite_device WHERE uid = :uid AND did = :did LIMIT 1")
    suspend fun find(uid: Long, did: String): FavoriteDeviceEntity?
}
