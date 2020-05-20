package com.chentir.cameraroll

import android.app.Application
import timber.log.Timber

class CameraRollApplication : Application() {
    override fun onCreate() {
        super.onCreate()

        if (BuildConfig.DEBUG) {
            Timber.plant(Timber.DebugTree())
        }
    }
}