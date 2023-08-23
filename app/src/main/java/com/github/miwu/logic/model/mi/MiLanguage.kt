package com.github.miwu.logic.model.mi

import androidx.room.Entity
import androidx.room.Index
import androidx.room.PrimaryKey

@Entity(indices = [Index(value = ["type"], unique = true)])
data class MiLanguage(
    val type: String,
    val specJson: String,
) {
    @PrimaryKey
    var id: Int = 0
}