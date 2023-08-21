package io.github.sky130.miwu.logic.model

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity
data class MiInstance(
    val status: String,
    val model: String,
    val version: Int,
    val type: String,
    val ts: Long,
) {
    @PrimaryKey(autoGenerate = true)
    var id: Long = 0
}