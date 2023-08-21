package com.github.miwu.logic.dao.database

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.Query
import com.github.miwu.logic.model.mi.MiHomeEntity
import com.github.miwu.logic.model.mi.MiSpecType

@Dao
interface SpecDAO {

    @Insert
    fun addSpec(spec:MiSpecType):Long

    @Insert
    fun addSpecs(specs:List<MiSpecType>):List<Long>

    @Query("select * from MiSpecType")
    fun getAllSpecs(): List<MiSpecType>

    @Query("select * from MiSpecType where type == :type")
    fun getSpec(type: String): MiSpecType?

    @Delete
    fun delType(type: MiSpecType)

    @Query("select count(id) from MiSpecType")
    fun getCount(): Int


}