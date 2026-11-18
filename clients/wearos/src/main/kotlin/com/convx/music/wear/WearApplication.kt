package com.convx.music.wear

import android.app.Application
import android.content.Context
import org.koin.core.KoinApplication
import org.koin.core.module.dsl.singleOf
import org.koin.dsl.koinApplication
import org.koin.dsl.module

class WearApplication : Application() {
    lateinit var graph: KoinApplication
        private set

    override fun onCreate() {
        super.onCreate()
        graph = koinApplication {
            modules(module {
                single<Context> { this@WearApplication }
                singleOf(::WearPhoneLink)
            })
        }
    }
}
