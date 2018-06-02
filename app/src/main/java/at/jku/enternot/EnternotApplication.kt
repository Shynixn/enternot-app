package at.jku.enternot

import android.app.Application
import at.jku.enternot.contract.SirenService
import at.jku.enternot.service.ConnectionServiceIml
import at.jku.enternot.service.SirenServiceImpl
import at.jku.enternot.viewmodel.MainActivityViewModelImpl
import org.koin.android.architecture.ext.viewModel
import org.koin.android.ext.android.startKoin
import org.koin.android.ext.koin.androidApplication
import org.koin.dsl.module.Module

class EnternotApplication : Application() {

    // Koin module
    val myModule: Module = org.koin.dsl.module.applicationContext {
        viewModel { MainActivityViewModelImpl(this.androidApplication(), get()) } // get() will resolve Repository instance
        bean { SirenServiceImpl(ConnectionServiceIml("ws://127.0.0.1")) as SirenService }
    }

    override fun onCreate() {
        super.onCreate()
        // Start Koin
        startKoin(this, listOf(myModule))
    }
}