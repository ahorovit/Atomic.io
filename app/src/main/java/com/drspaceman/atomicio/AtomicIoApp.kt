package com.drspaceman.atomicio

import android.app.Application
import com.appspector.sdk.AppSpector

class AtomicIoApp : Application() {
    override fun onCreate() {
        super.onCreate()

        // We recommend to start AppSpector from Application#onCreate method

        // You can start all monitors
        AppSpector
            .build(this)
            .withDefaultMonitors()
            .run("android_MWZiYmIwOWYtNGI1Ny00ZGMwLTg2MTAtNjBmOTZjNTgxMGY5")

        // Or you can select monitors that you want to use
        AppSpector
            .build(this)
            .addPerformanceMonitor()
            .addHttpMonitor() // If specific monitor is not added then this kind of data won't be tracked and available on the web
            .addLogMonitor()
            .addScreenshotMonitor()
            .addSQLMonitor()
            .run("android_MWZiYmIwOWYtNGI1Ny00ZGMwLTg2MTAtNjBmOTZjNTgxMGY5")
    }
}