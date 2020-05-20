package com.chentir.cameraroll.utils

import okhttp3.OkHttpClient

fun OkHttpClient.cancelWithTag(tag: String): Boolean {
    dispatcher.queuedCalls().forEach {
        if (tag == it.request().tag()) {
            it.cancel()
            return true
        }
    }
    dispatcher.runningCalls().forEach {
        if (tag == it.request().tag()) {
            it.cancel()
            return true
        }
    }
    return false
}