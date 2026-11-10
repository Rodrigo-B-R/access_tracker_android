package com.example.access_tracker

import android.app.Application
import com.example.access_tracker.data.ServiceLocator

class AccessTrackerApp : Application() {
    override fun onCreate() {
        super.onCreate()
        ServiceLocator.init(this)
    }
}
