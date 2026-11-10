package com.convx.music.di

import android.app.Application
import android.content.Context
import androidx.lifecycle.SavedStateHandle
import org.junit.Test
import org.koin.core.annotation.KoinExperimentalAPI
import org.koin.dsl.module
import org.koin.test.verify.verify

@OptIn(KoinExperimentalAPI::class)
class KoinGraphTest {

    @Test
    fun appGraphResolves() {
        module { includes(appModules) }.verify(
            extraTypes = listOf(
                Context::class,
                Application::class,
                SavedStateHandle::class,
                ValueProvider::class,
                java.io.File::class,
                androidx.media3.datasource.cache.CacheEvictor::class,
                androidx.media3.database.DatabaseProvider::class,
                ByteArray::class,
                Boolean::class,
                Long::class,
                Int::class,
                String::class,
            ),
        )
    }
}
