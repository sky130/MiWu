package com.github.miwu.logic.database.entity

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.Index

@Entity(
    tableName = "favorite_device_metadata",
    foreignKeys = [
        ForeignKey(
            entity = FavoriteDevice::class,
            parentColumns = ["uid", "did"],
            childColumns = ["uid", "did"],
            onDelete = ForeignKey.CASCADE,
            onUpdate = ForeignKey.CASCADE,
        ),
    ],
    indices = [
        Index(value = ["uid", "did"]),
    ],
    primaryKeys = ["uid", "did"],
)
data class FavoriteDeviceMetadata(
    @ColumnInfo(name = "uid")
    val uid: Long,
    @ColumnInfo(name = "did")
    val did: String,
    @ColumnInfo(name = "sort_index", defaultValue = "${Int.MAX_VALUE}")
    val sortIndex: Int = Int.MAX_VALUE,
)
