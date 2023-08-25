package com.github.miwu.logic.dao.database

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.github.miwu.logic.model.mi.MiHomeEntity
import com.github.miwu.logic.model.mi.MiLanguage
import com.github.miwu.logic.model.mi.MiSpecType

@Dao
interface SpecDAO {

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun addSpec(spec: MiSpecType): Long

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun addSpecLanguage(spec: MiLanguage): Long

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun addSpecs(specs: List<MiSpecType>): List<Long>

    @Query("select * from MiSpecType")
    fun getAllSpecs(): List<MiSpecType>

    @Query("select * from MiSpecType where type == :type")
    fun getSpec(type: String): MiSpecType?

    @Query("select * from MiLanguage where type == :type")
    fun getSpecLanguage(type: String): MiLanguage?

    @Delete
    fun delType(type: MiSpecType)

    @Query("select count(id) from MiSpecType")
    fun getCount(): Int


}