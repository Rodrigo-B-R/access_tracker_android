package com.example.access_tracker

import android.app.Application
import com.example.access_tracker.data.ServiceLocator
import com.example.access_tracker.data.SincronizarChecadasWorker

class AccessTrackerApp : Application() {
    override fun onCreate() {
        super.onCreate()
        ServiceLocator.init(this)
        // Si la app se cerro con checadas sin enviar, se vuelve a pedir el reintento automatico.
        if (ServiceLocator.cola.hayPendientes()) SincronizarChecadasWorker.programar(this)
    }
}
