package com.shareconnect.transmissionconnect.logging

import android.util.Log
import com.google.firebase.crashlytics.FirebaseCrashlytics
import javax.inject.Inject

class Logger @Inject constructor(
    private val crashlytics: FirebaseCrashlytics
) {

    fun log(message: String) {

        Log.d(TAG, message)
    }

    fun log(throwable: Throwable) {

        Log.e(TAG, null, throwable)
    }

    companion object {
        private const val TAG = "Logger"
    }
}
