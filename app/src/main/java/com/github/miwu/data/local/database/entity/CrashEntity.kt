package com.github.miwu.data.local.database.entity

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "crash_item")
data class CrashEntity(
    val errorMessage: String,
    val timestamp: Long,
    val path: String
) {
    @PrimaryKey(autoGenerate = true)
    var index: Int = 0
}
