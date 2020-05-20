package com.chentir.cameraroll.imageloader

import android.graphics.Bitmap

interface BitmapCache {
    fun get(url: String): Bitmap?

    fun put(url: String, bitmap: Bitmap)

    fun applyCapacityFactor(factor: Float)
}
