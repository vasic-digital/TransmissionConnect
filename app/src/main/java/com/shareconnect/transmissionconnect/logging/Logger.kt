package com.shareconnect.transmissionconnect.logging

import android.util.Log
import com.google.firebase.crashlytics.FirebaseCrashlytics
import com.shareconnect.transmissionconnect.BuildConfig
import javax.inject.Inject

class Logger @Inject constructor(
    private val crashlytics: FirebaseCrashlytics
) {
    fun log(message: String) {
        if (BuildConfig.DEBUG) {
            Log.d(TAG, message)
        } else {
            crashlytics.log(message)
        }
    }

    fun log(throwable: Throwable) {
        if (BuildConfig.DEBUG) {
            Log.e(TAG, null, throwable)
        } else {
            crashlytics.recordException(throwable)
        }
    }

    companion object {
        private const val TAG = "Logger"
    }
}
