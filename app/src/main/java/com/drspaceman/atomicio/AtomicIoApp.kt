package com.drspaceman.atomicio

import android.app.Application
import com.appspector.sdk.AppSpector
import com.jakewharton.threetenabp.AndroidThreeTen
import dagger.hilt.android.HiltAndroidApp

@HiltAndroidApp
class AtomicIoApp : Application() {
    override fun onCreate() {
        super.onCreate()

        // Enables monitoring app with AppSpector
        // @todo: hide API KEY
        // @todo: remove appspector
        AppSpector
            .build(this)
            .withDefaultMonitors()
            .run("android_MWZiYmIwOWYtNGI1Ny00ZGMwLTg2MTAtNjBmOTZjNTgxMGY5")

        // Initialize timezone information for ThreeTen Android Backport library support
        // NOTE: This provides JSR-310 Date classes for legacy Android devices (SDK < OREO/26)
        AndroidThreeTen.init(this)
    }
}