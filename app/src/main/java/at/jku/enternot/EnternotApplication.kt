package at.jku.enternot

import android.app.Application
import android.util.Log
import at.jku.enternot.contract.CameraMovementService
import at.jku.enternot.contract.ConfigurationService
import at.jku.enternot.contract.ConnectionService
import at.jku.enternot.contract.SirenService
import at.jku.enternot.service.CameraMovementServiceImpl
import at.jku.enternot.service.ConfigurationServiceImpl
import at.jku.enternot.service.ConnectionServiceIml
import at.jku.enternot.service.SirenServiceImpl
import at.jku.enternot.viewmodel.CalibrationDialogViewModelImpl
import at.jku.enternot.viewmodel.ConfigurationActivityViewModelImpl
import at.jku.enternot.viewmodel.MainActivityViewModelImpl
import com.google.firebase.messaging.FirebaseMessaging
import org.koin.android.architecture.ext.viewModel
import org.koin.android.ext.android.startKoin
import org.koin.android.ext.koin.androidApplication
import org.koin.dsl.module.Module

class EnternotApplication : Application() {

    val configurationService = ConfigurationServiceImpl()
    val connectionService = ConnectionServiceIml(configurationService)

    // Koin module
    val myModule: Module = org.koin.dsl.module.applicationContext {
        viewModel { MainActivityViewModelImpl(this.androidApplication(), get(), get(), get()) } // get() will resolve Repository instance
        bean { SirenServiceImpl(connectionService) as SirenService }
        bean { CameraMovementServiceImpl(this.androidApplication()) as CameraMovementService }
        bean { configurationService as ConfigurationService }
        viewModel { ConfigurationActivityViewModelImpl(this.androidApplication(), get(), get()) }
        bean { configurationService as ConfigurationService }
        bean { connectionService as ConnectionService }
        viewModel { CalibrationDialogViewModelImpl(this.androidApplication(), get()) }
        bean { CameraMovementServiceImpl(this.androidApplication()) as CameraMovementService }
    }

    override fun onCreate() {
        super.onCreate()
        // Start Koin
        startKoin(this, listOf(myModule))
        FirebaseMessaging.getInstance().subscribeToTopic("movement").addOnCompleteListener {
            if (it.isSuccessful) {
                Log.d(this.javaClass.name, "Subscribed to the topic successful")
            } else {
                Log.d(this.javaClass.name, "Could not subscribe to the topic")
            }
        }
    }
}