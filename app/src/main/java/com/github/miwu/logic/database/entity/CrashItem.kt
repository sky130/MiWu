package com.github.miwu.logic.database.entity

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "crash_item")
data class CrashItem(
    val errorMessage: String,
    val timestamp: Long,
    val path: String
) {
    @PrimaryKey(autoGenerate = true)
    var index: Int = 0
}
