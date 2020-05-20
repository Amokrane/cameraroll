package com.chentir.cameraroll.data.local

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey


@Entity
data class PictureEntity(
    @PrimaryKey(autoGenerate = true) val id: Int,
    @ColumnInfo(name = "large") val large: String,
    @ColumnInfo(name = "medium") val medium: String,
    @ColumnInfo(name = "thumbnail") val thumbnail: String,
    @ColumnInfo(name = "seed") val seed: String
)
