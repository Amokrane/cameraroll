package com.chentir.cameraroll.data.services.randomuser

import retrofit2.http.GET
import retrofit2.http.Query

interface RandomuserService {
    @GET("api/")
    suspend fun listPictures(@Query("results") results: Int,
                     @Query("seed") seed: String): Results

}
