package com.chentir.cameraroll.data.local

import androidx.room.Database
import androidx.room.RoomDatabase

@Database(entities = [PictureEntity::class], version = 2)
abstract class RandomuserDatabase : RoomDatabase() {
    abstract fun pictureDao(): PictureDao
}
