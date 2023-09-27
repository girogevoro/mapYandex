package com.girogevoro.mapyandex

import android.app.Application
import com.girogevoro.mapyandex.di.Di
import org.koin.android.ext.koin.androidContext
import org.koin.core.context.startKoin

class App : Application() {
    override fun onCreate() {
        super.onCreate()
        startKoin {
            androidContext(this@App)
            modules(listOf(Di.repositoryModule, Di.viewModulesModule, Di.storageModule()))
        }
    }
}