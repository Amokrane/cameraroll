package com.chentir.cameraroll

import android.content.Context
import androidx.room.Room
import com.chentir.cameraroll.data.local.PictureEntity
import com.chentir.cameraroll.data.local.RandomuserDatabase
import com.chentir.cameraroll.data.services.randomuser.Picture
import com.chentir.cameraroll.data.services.randomuser.RandomuserService
import com.dropbox.android.external.store4.Store
import com.dropbox.android.external.store4.StoreBuilder
import okhttp3.OkHttpClient
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory

object DependencyProvider {

    fun provideStore(
        applicationContext: Context,
        results: Int
    ): Store<String, List<PictureEntity>> {
        val db = provideDb(applicationContext)

        return StoreBuilder.fromNonFlow { seed: String ->
            val pictureService = provideRandomuserService()
            pictureService.listPictures(results, seed).results.map { user ->
                toPictureEntity(
                    seed,
                    user.picture
                )
            }
        }.persister(
            reader = db.pictureDao()::getAll,
            writer = db.pictureDao()::insertAll
        ).build()
    }

    fun provideOkHTTPClient(): OkHttpClient {
        return OkHttpClient.Builder().build()
    }

    private fun provideRetrofit(): Retrofit {
        return Retrofit.Builder().baseUrl("https://randomuser.me/")
            .addConverterFactory(GsonConverterFactory.create()).build()
    }

    private fun provideRandomuserService(): RandomuserService {
        val retrofit = provideRetrofit()
        return retrofit.create(RandomuserService::class.java)
    }

    private fun provideDb(applicationContext: Context): RandomuserDatabase {
        return Room.databaseBuilder(
            applicationContext,
            RandomuserDatabase::class.java, "pictures"
        ).build()
    }

    private fun toPictureEntity(seed: String, picture: Picture): PictureEntity {
        return PictureEntity(0, picture.large, picture.medium, picture.thumbnail, seed)
    }
}
