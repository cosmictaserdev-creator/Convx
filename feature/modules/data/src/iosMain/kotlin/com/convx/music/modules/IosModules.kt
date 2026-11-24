package com.convx.music.modules

import io.ktor.client.HttpClient
import io.ktor.client.engine.darwin.Darwin
import platform.Foundation.NSUserDefaults

internal actual fun moduleHttpClient() = HttpClient(Darwin) { configureModuleHttp() }

class IosModuleStore : ModuleStore {
    private val defaults = NSUserDefaults.standardUserDefaults
    override suspend fun read(): String? = defaults.stringForKey("convx.modules.snapshot")
    override suspend fun write(value: String) { defaults.setObject(value, forKey = "convx.modules.snapshot") }
}
