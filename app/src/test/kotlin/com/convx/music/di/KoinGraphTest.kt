package com.convx.music.di

import android.app.Application
import android.content.Context
import androidx.lifecycle.SavedStateHandle
import org.junit.Test
import org.koin.core.annotation.KoinExperimentalAPI
import org.koin.dsl.module
import org.koin.test.verify.verify

/**
 * Hilt failed the build on a missing binding; Koin fails at runtime, on the
 * user's phone. This puts that failure back in the build.
 */
@OptIn(KoinExperimentalAPI::class)
class KoinGraphTest {

    @Test
    fun appGraphResolves() {
        module { includes(appModules) }.verify(
            // Supplied by the platform or the navigation back stack, not by a module.
            // ValueProvider is built by hand inside lambda definitions to defer lookups.
            extraTypes = listOf(
                Context::class,
                Application::class,
                SavedStateHandle::class,
                ValueProvider::class,
                // Cache directories are resolved from Context inside the definition.
                java.io.File::class,
                androidx.media3.datasource.cache.CacheEvictor::class,
                androidx.media3.database.DatabaseProvider::class,
                // The verifier reads SimpleCache's longest constructor (secret key, flags).
                ByteArray::class,
                Boolean::class,
                Long::class,
                Int::class,
                String::class,
            ),
        )
    }
}
