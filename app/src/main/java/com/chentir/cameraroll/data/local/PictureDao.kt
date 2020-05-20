package com.chentir.cameraroll.data.local

import androidx.room.*
import kotlinx.coroutines.flow.Flow

@Dao
abstract class PictureDao {

    @Query("SELECT * FROM PictureEntity WHERE seed = :seed")
    abstract fun getAll(seed: String): Flow<List<PictureEntity>>

    @Transaction
    open suspend fun insertAll(seed: String, pictures: List<PictureEntity>) {
        deletePictureBySeed(seed)
        insertAll(pictures)
    }

    @Insert
    protected abstract suspend fun insertAll(pictureEntities: List<PictureEntity>)

    @Query("DELETE FROM PictureEntity WHERE seed = :seed")
    protected abstract suspend fun deletePictureBySeed(seed: String)
}
