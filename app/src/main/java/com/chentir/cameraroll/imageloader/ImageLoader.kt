package com.chentir.cameraroll.imageloader

import android.app.ActivityManager
import android.content.ComponentCallbacks2
import android.content.ComponentCallbacks2.*
import android.content.Context
import android.content.res.Configuration
import android.graphics.BitmapFactory
import android.graphics.drawable.Drawable
import android.os.Handler
import android.os.Looper
import android.widget.ImageView
import com.chentir.cameraroll.DependencyProvider
import com.chentir.cameraroll.R
import com.chentir.cameraroll.utils.cancelWithTag
import okhttp3.*
import timber.log.Timber
import java.io.BufferedInputStream
import java.io.IOException

class ImageLoader private constructor(
    private val context: Context,
    private val bitmapCache: InMemoryBitmapCache,
    private val httpClient: OkHttpClient
) : ComponentCallbacks2 {

    companion object {
        private lateinit var imageLoader: ImageLoader

        fun init(context: Context): ImageLoader {
            synchronized(this) {
                if (!this::imageLoader.isInitialized) {
                    val activityService =
                        context.getSystemService(Context.ACTIVITY_SERVICE) as ActivityManager
                    val bitmapCache = InMemoryBitmapCache(activityService)

                    imageLoader = ImageLoader(
                        context,
                        bitmapCache,
                        DependencyProvider.provideOkHTTPClient()
                    )

                    context.registerComponentCallbacks(imageLoader)
                }
                return imageLoader
            }
        }
    }

    fun load(url: String, placeholder: Drawable? = null, imageView: ImageView) {
        imageView.setImageDrawable(placeholder ?: context.getDrawable(R.drawable.placeholder))
        val bitmap = bitmapCache.get(url)
        if (bitmap != null) {
            imageView.setImageBitmap(bitmap)
            return
        }

        Timber.d("Requesting $url with tag ${getOkHttpTag(imageView)}")
        val request = Request.Builder().url(url).tag(getOkHttpTag(imageView)).build()
        httpClient.newCall(request).enqueue(object : Callback {
            override fun onFailure(call: Call, e: IOException) {
                Timber.e("Error while fetching bitmap $e")
            }

            override fun onResponse(call: Call, response: Response) {
                response.body?.let {
                    val inputStream = it.byteStream()
                    val bufferedInputStream = BufferedInputStream(inputStream)

                    val bitmapOptions = BitmapFactory.Options()
                    bitmapOptions.inJustDecodeBounds = false

                    val bitmap =
                        BitmapFactory.decodeStream(bufferedInputStream, null, bitmapOptions)

                    Handler(Looper.getMainLooper()).post {
                        bitmap?.let { bitmap ->
                            bitmapCache.put(url, bitmap)
                            imageView.setImageBitmap(bitmap)
                        }
                    }
                }
            }
        })
    }

    fun cancel(imageView: ImageView) {
        Timber.d("Cancelling any request with tag: ${getOkHttpTag(imageView)}")
        val canceled = httpClient.cancelWithTag(getOkHttpTag(imageView))
        imageView.setImageDrawable(context.getDrawable(R.drawable.placeholder))
        Timber.d("Cancellation status: $canceled")
    }

    private fun getOkHttpTag(imageView: ImageView) = "${System.identityHashCode(imageView)}"


    override fun onLowMemory() {
        bitmapCache.applyCapacityFactor(0f)
    }

    override fun onConfigurationChanged(newConfig: Configuration) {
    }

    override fun onTrimMemory(level: Int) {
        if (level >= TRIM_MEMORY_BACKGROUND) {
            bitmapCache.applyCapacityFactor(0f)
        } else if (level in TRIM_MEMORY_RUNNING_LOW..TRIM_MEMORY_UI_HIDDEN) {
            bitmapCache.applyCapacityFactor(0.5f)
        }
    }
}
