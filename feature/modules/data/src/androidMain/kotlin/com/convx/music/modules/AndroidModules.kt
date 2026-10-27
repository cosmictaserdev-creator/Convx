package com.convx.music.modules

import android.content.Context
import io.ktor.client.HttpClient
import io.ktor.client.engine.okhttp.OkHttp
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext

internal actual fun moduleHttpClient() = HttpClient(OkHttp) { configureModuleHttp() }

class AndroidModuleStore(context: Context) : ModuleStore {
    private val preferences = context.applicationContext.getSharedPreferences("convx_modules", Context.MODE_PRIVATE)
    override suspend fun read(): String? = withContext(Dispatchers.IO) { preferences.getString("snapshot", null) }
    override suspend fun write(value: String) = withContext(Dispatchers.IO) {
        check(preferences.edit().putString("snapshot", value).commit()) { "Could not save module settings" }
    }
}
