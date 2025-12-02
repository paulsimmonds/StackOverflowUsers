package com.magictorch.stackoverflowtest

import android.app.Application
import com.magictorch.stackoverflowtest.di.dataModule
import com.magictorch.stackoverflowtest.di.dataStoreModule
import com.magictorch.stackoverflowtest.di.domainModule
import com.magictorch.stackoverflowtest.di.platformModule
import com.magictorch.stackoverflowtest.di.presentationModule
import org.koin.android.ext.koin.androidContext
import org.koin.core.context.startKoin

class MainApplication : Application() {
    override fun onCreate() {
        super.onCreate()

        startKoin {
            androidContext(this@MainApplication)
            modules(presentationModule, domainModule, dataModule, platformModule, dataStoreModule)
        }
    }
}
