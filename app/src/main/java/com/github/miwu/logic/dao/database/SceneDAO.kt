package com.github.miwu.logic.dao.database

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Update
import com.github.miwu.logic.model.mi.MiScene

@Dao
interface SceneDAO {

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun addScene(scene: MiScene): Long

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun addScenes(scenes: List<MiScene>): List<Long>

    @Update
    fun updateScene(scene: MiScene)

    @Query("select * from MiScene")
    fun getAllScene(): List<MiScene>

    @Query("select * from MiScene where homeId == :homeId")
    fun getScene(homeId: String): List<MiScene>

    @Delete
    fun delScene(scene: MiScene)

    @Delete
    fun delScenes(scenes:List<MiScene>)

    @Query("select count(id) from MiScene")
    fun getCount(): Int

}