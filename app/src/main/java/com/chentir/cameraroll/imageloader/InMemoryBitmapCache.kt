package com.chentir.cameraroll.imageloader

import android.app.ActivityManager
import android.graphics.Bitmap
import android.util.LruCache
import com.chentir.cameraroll.PERF_TAG
import timber.log.Timber
import kotlin.math.roundToInt

class InMemoryBitmapCache(activityManager: ActivityManager) : BitmapCache {
    enum class CacheEvent {
        ADD,
        HIT,
        MISS,
        EVICTION
    }

    /**
     * Set the initial cache capacity to be equal to 10% of the App's heap budget
     */
    val maxCacheSize = (0.1 * activityManager.memoryClass).roundToInt() * 1024 * 1024

    private val lruCache = object : LruCache<String, Bitmap>(maxCacheSize) {
        override fun entryRemoved(
            evicted: Boolean,
            key: String,
            oldValue: Bitmap,
            newValue: Bitmap?
        ) {
            if (evicted) {
                log(CacheEvent.EVICTION, key)
            }
        }

        override fun sizeOf(key: String, value: Bitmap): Int {
            return value.byteCount
        }
    }

    override fun get(url: String): Bitmap? {
        val bitmap = lruCache[url]

        if (bitmap != null) {
            log(CacheEvent.HIT, url)
            return bitmap
        }

        log(CacheEvent.MISS, url)
        return null
    }

    override fun put(url: String, bitmap: Bitmap) {
        lruCache.put(url, bitmap)
        log(CacheEvent.ADD, url)
    }

    override fun applyCapacityFactor(factor: Float) {
        val size = (maxCacheSize  * factor).roundToInt()

        if (size <= 0) {
            lruCache.evictAll()
        } else {
            lruCache.resize((maxCacheSize  * factor).roundToInt())
        }
    }

    private fun log(event: CacheEvent, key: String) {
        Timber.d("[$PERF_TAG] [$event] for $key. maxCacheSize $maxCacheSize" +
                " Memory Cache Size:  ${lruCache.size()}Bytes, $lruCache")
    }
}
