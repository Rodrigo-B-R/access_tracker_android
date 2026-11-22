package com.example.access_tracker

import android.app.Application
import com.example.access_tracker.data.ServiceLocator
import com.example.access_tracker.data.SincronizarChecadasWorker
import kotlinx.coroutines.launch

class AccessTrackerApp : Application() {
    override fun onCreate() {
        super.onCreate()
        ServiceLocator.init(this)
        // Si la app se cerro con checadas sin enviar, se vuelve a pedir el reintento automatico.
        // Room no se puede leer en el hilo principal: se revisa en segundo plano.
        ServiceLocator.appScope.launch {
            if (ServiceLocator.cola.hayPendientes()) SincronizarChecadasWorker.programar(this@AccessTrackerApp)
        }
    }
}
