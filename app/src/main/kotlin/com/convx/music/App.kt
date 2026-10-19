/**
 * Convx Project (C) 2026
 * Licensed under GPL-3.0 | See git history for contributors
 */

package com.convx.music

import android.app.Application
import android.app.NotificationChannel
import android.app.NotificationManager
import android.content.Context
import android.os.Build
import android.os.StrictMode
import android.widget.Toast
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.edit
import coil3.ImageLoader
import coil3.PlatformContext
import coil3.SingletonImageLoader
import coil3.svg.SvgDecoder
import coil3.disk.DiskCache
import coil3.disk.directory
import coil3.gif.AnimatedImageDecoder
import coil3.gif.GifDecoder
import coil3.memory.MemoryCache
import coil3.request.CachePolicy
import android.graphics.Bitmap
import coil3.request.allowHardware
import coil3.request.bitmapConfig
import coil3.request.crossfade
import com.convx.music.utils.ArtworkQualityInterceptor
import com.music.innertube.YouTube
import com.music.innertube.models.IpVersion
import com.music.innertube.models.YouTubeLocale
import com.music.kugou.KuGou
import com.music.lastfm.LastFM
import com.convx.music.constants.*
import com.convx.music.di.ApplicationScope
import com.convx.music.extensions.toEnum
import com.convx.music.extensions.toInetSocketAddress
import com.convx.music.utils.CrashHandler
import com.convx.music.utils.DebugLogs
import com.convx.music.utils.cipher.CipherDeobfuscator
import com.convx.music.utils.dataStore
import com.convx.music.utils.get
import com.convx.music.utils.migrateSharedPreferencesToMMKV
import com.convx.music.utils.warmPreferenceMirror
import com.convx.music.utils.reportException
import com.tencent.mmkv.MMKV
import dagger.hilt.android.HiltAndroidApp
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.distinctUntilChanged
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import okhttp3.Credentials
import timber.log.Timber
import java.net.Authenticator
import java.net.PasswordAuthentication
import java.net.Proxy
import java.util.Locale
import javax.inject.Inject

@HiltAndroidApp
class App : Application(), SingletonImageLoader.Factory {

    @Inject
    @ApplicationScope
    lateinit var applicationScope: CoroutineScope

    @Inject
    lateinit var networkRepository: dagger.Lazy<com.convx.storage.data.NetworkMusicRepository>

    override fun onCreate() {
        super.onCreate()
        com.music.spotify.SpotifyAuth.initialize(this)
        // The recovery activity runs in its own process. Do not reopen the database,
        // preferences, native caches or playback integrations that may have crashed.
        val processName = if (Build.VERSION.SDK_INT >= 28) {
            Application.getProcessName()
        } else {
            (getSystemService(Context.ACTIVITY_SERVICE) as android.app.ActivityManager)
                .runningAppProcesses?.firstOrNull { it.pid == android.os.Process.myPid() }?.processName
        }
        if (processName == "$packageName:crash") return
        com.convx.music.canvas.AppleMusicCatalog.cacheDir = filesDir.resolve("apple_catalog")
        com.convx.music.core.UpdateNotificationHelper.cancelUpdateNotification(this)
        // Eclipse addons identify clients by UA; they require "Convx/<app-version>".
        com.music.spine.ModuleManager.userAgent = "Convx/${BuildConfig.VERSION_NAME}"

        // StrictMode: debug-only, catches main-thread IO before it ships.
        // Release untouched — zero behavior change there.
        if (BuildConfig.DEBUG) {
            StrictMode.setThreadPolicy(
                StrictMode.ThreadPolicy.Builder()
                    .detectDiskReads()
                    .detectDiskWrites()
                    .detectNetwork()
                    .detectCustomSlowCalls()
                    .penaltyLog()
                    .build()
            )
            StrictMode.setVmPolicy(
                StrictMode.VmPolicy.Builder()
                    .detectLeakedClosableObjects()
                    .detectActivityLeaks()
                    .penaltyLog()
                    .build()
            )
        }

        // Initialize MMKV before migrating preferences or restoring session state.
        MMKV.initialize(this)
        com.convx.music.ui.theme.colorengine.ColorEngine.init(this)
        // Avoid per-artwork diagnostic work in release builds.
        com.convx.music.ui.theme.colorengine.ColorEngine.setDebugEnabled(BuildConfig.DEBUG)
        migrateSharedPreferencesToMMKV(this, "widget_config")
        migrateSharedPreferencesToMMKV(this, "nanosonic_eq_profiles")
        migrateSharedPreferencesToMMKV(this, "vivi_eq_prefs")
        migrateSharedPreferencesToMMKV(this, "settings")

        // Seed auth before DataStore emits. MusicService can start playback first; a null
        // cookie would trigger guest fallback and overwrite the saved visitorData.
        // authSeed() provides this synchronous read.
        YouTube.cookie = authSeed(InnerTubeCookieKey)
        YouTube.visitorData = authSeed(VisitorDataKey)
        YouTube.dataSyncId = authSeed(DataSyncIdKey)

        // Warm the preference mirror off Main so the first composition avoids its blocking read.
        applicationScope.launch(Dispatchers.IO) { dataStore.warmPreferenceMirror() }
        // Spotify's native engine: report whether it loaded, without ever failing startup over it.
        applicationScope.launch(Dispatchers.IO) {
            val loaded = runCatching { com.music.spotify.nativecore.NativeBridge.initContext(this@App) }
            timber.log.Timber.tag("SpotifyEngine").i(
                if (loaded.isSuccess) "native engine loaded" else "native engine unavailable: ${loaded.exceptionOrNull()}",
            )
        }

        CrashHandler.install(this)

        CipherDeobfuscator.initialize(this)

        if (BuildConfig.DEBUG) {
            Timber.plant(Timber.DebugTree())
            Timber.plant(DebugLogs.tree(this))
        }

        // Load the coil cache size on a background thread so the first image
        // render never blocks on a DataStore read.
        applicationScope.launch(Dispatchers.IO) {
            cachedCoilCacheSize = dataStore.data.map { it[MaxImageCacheSizeKey] ?: 512 }.first()
        }

        applicationScope.launch {
            initializeSettings()
            observeSettingsChanges()
        }
    }

    /** Mirrors session keys in MMKV to avoid a blocking DataStore read during startup. */
    private val authCache: MMKV by lazy { MMKV.mmkvWithID(AUTH_CACHE_ID) }

    /** Reads DataStore once when the MMKV mirror has no value yet, such as after an upgrade. */
    private fun authSeed(key: Preferences.Key<String>): String? {
        val raw = if (authCache.containsKey(key.name)) {
            authCache.decodeString(key.name, "").orEmpty()
        } else {
            dataStore.get(key, "").also { authCache.encode(key.name, it) }
        }
        // A blank or literal "null" is a broken value, not a session — same test the
        // collectors apply, kept identical so a restore and a live update agree.
        return raw.takeIf { it.isNotBlank() && it != "null" }
    }

    /** Mirrors one session key into [authCache]. Called from the collectors. */
    private fun cacheAuthValue(key: Preferences.Key<String>, value: String?) {
        authCache.encode(key.name, value.orEmpty())
    }

    private suspend fun initializeSettings() {
        val settings = dataStore.data.first()
        val locale = Locale.getDefault()
        val languageTag = locale.language

        YouTube.locale = YouTubeLocale(
            gl = settings[ContentCountryKey]?.takeIf { it != SYSTEM_DEFAULT }
                ?: locale.country.takeIf { it in CountryCodeToName }
                ?: "US",
            hl = settings[ContentLanguageKey]?.takeIf { it != SYSTEM_DEFAULT }
                ?: locale.language.takeIf { it in LanguageCodeToName }
                ?: languageTag.takeIf { it in LanguageCodeToName }
                ?: "en"
        )

        if (languageTag == "zh-TW") {
            KuGou.useTraditionalChinese = true
        }

        LastFM.initialize(
            apiKey = BuildConfig.LASTFM_API_KEY.takeIf { it.isNotEmpty() } ?: "",
            secret = BuildConfig.LASTFM_SECRET.takeIf { it.isNotEmpty() } ?: ""
        )

        if (settings[ProxyEnabledKey] == true) {
            val username = settings[ProxyUsernameKey].orEmpty()
            val password = settings[ProxyPasswordKey].orEmpty()
            val type = settings[ProxyTypeKey].toEnum(defaultValue = Proxy.Type.HTTP)

            if (username.isNotEmpty() || password.isNotEmpty()) {
                if (type == Proxy.Type.HTTP) {
                    YouTube.proxyAuth = Credentials.basic(username, password)
                } else {
                    Authenticator.setDefault(object : Authenticator() {
                        override fun getPasswordAuthentication(): PasswordAuthentication =
                            PasswordAuthentication(username, password.toCharArray())
                    })
                }
            }
            try {
                settings[ProxyUrlKey]?.takeIf { it.isNotBlank() }?.let {
                    YouTube.proxy = Proxy(type, it.toInetSocketAddress())
                }
            } catch (e: Exception) {
                withContext(Dispatchers.Main) {
                    Toast.makeText(this@App, getString(R.string.failed_to_parse_proxy), Toast.LENGTH_SHORT).show()
                }
                reportException(e)
            }
        }

        YouTube.useLoginForBrowse = settings[UseLoginForBrowse] ?: true
        YouTube.ipVersion = settings[IpVersionKey]?.toEnum(defaultValue = IpVersion.IPV4) ?: IpVersion.IPV4

        val channel = NotificationChannel(
            "updates",
            getString(R.string.update_channel_name),
            NotificationManager.IMPORTANCE_DEFAULT
        ).apply {
            description = getString(R.string.update_channel_desc)
        }
        val nm = getSystemService(NotificationManager::class.java)
        nm.createNotificationChannel(channel)
    }

    private fun observeSettingsChanges() {
        applicationScope.launch(Dispatchers.IO) {
            dataStore.data
                .map { it[VisitorDataKey] }
                .distinctUntilChanged()
                .collect { visitorData ->
                    // A blank value is not "no account", it is a broken one: it still
                    // gets sent as an empty X-Goog-Visitor-Id and YouTube rejects every
                    // request. Treat it as absent so a fresh one is fetched and stored.
                    YouTube.visitorData = visitorData?.takeIf { it.isNotBlank() && it != "null" }
                        ?: YouTube.visitorData().getOrNull()?.also { newVisitorData ->
                            dataStore.edit { settings ->
                                settings[VisitorDataKey] = newVisitorData
                            }
                        }
                    cacheAuthValue(VisitorDataKey, YouTube.visitorData)
                }
        }

        applicationScope.launch(Dispatchers.IO) {
            dataStore.data
                .map { it[DataSyncIdKey] }
                .distinctUntilChanged()
                .collect { dataSyncId ->
                    // Keep the full sync ID; truncating it resets channel selection.
                    YouTube.dataSyncId = dataSyncId
                    cacheAuthValue(DataSyncIdKey, dataSyncId)
                }
        }

        applicationScope.launch(Dispatchers.IO) {
            dataStore.data
                .map { it[InnerTubeCookieKey] }
                .distinctUntilChanged()
                .collect { cookie ->
                    try {
                        YouTube.cookie = cookie?.takeIf { it.isNotBlank() && it != "null" }
                        cacheAuthValue(InnerTubeCookieKey, cookie)
                    } catch (e: Exception) {
                        Timber.e(e, "Could not parse cookie. Clearing existing cookie.")
                        forgetAccount(this@App)
                    }
                }
        }

        applicationScope.launch(Dispatchers.IO) {
            dataStore.data
                .map { it[LastFMSessionKey] }
                .distinctUntilChanged()
                .collect { session ->
                    try {
                        LastFM.sessionKey = session
                    } catch (e: Exception) {
                        Timber.e("Error while loading last.fm session key. %s", e.message)
                    }
                }
        }

        applicationScope.launch(Dispatchers.IO) {
            dataStore.data
                .map { Triple(it[ContentCountryKey], it[ContentLanguageKey], it[AppLanguageKey]) }
                .distinctUntilChanged()
                .collect { (contentCountry, contentLanguage, appLanguage) ->
                    val systemLocale = Locale.getDefault()
                    val effectiveAppLocale = appLanguage
                        ?.takeUnless { it == SYSTEM_DEFAULT }
                        ?.let { Locale.forLanguageTag(it) }
                        ?: systemLocale

                    YouTube.locale = YouTubeLocale(
                        gl = contentCountry?.takeIf { it != SYSTEM_DEFAULT }
                            ?: effectiveAppLocale.country.takeIf { it in CountryCodeToName }
                            ?: systemLocale.country.takeIf { it in CountryCodeToName }
                            ?: "US",
                        hl = contentLanguage?.takeIf { it != SYSTEM_DEFAULT }
                            ?: effectiveAppLocale.toLanguageTag().takeIf { it in LanguageCodeToName }
                            ?: effectiveAppLocale.language.takeIf { it in LanguageCodeToName }
                            ?: "en"
                    )
                }
        }

        applicationScope.launch(Dispatchers.IO) {
            dataStore.data
                .map { it[IpVersionKey] }
                .distinctUntilChanged()
                .collect { ipVersion ->
                    YouTube.ipVersion = ipVersion?.toEnum(defaultValue = IpVersion.AUTO) ?: IpVersion.AUTO
                }
        }

        applicationScope.launch(Dispatchers.IO) {
            dataStore.data
                .map { (it[AppleArtworkKey] ?: true) && it[DataSaverEnabledKey] != true }
                .distinctUntilChanged()
                .collect { com.convx.music.utils.AppleArt.enabled = it }
        }
    }

    @Volatile
    private var cachedCoilCacheSize: Int? = null

    override fun newImageLoader(context: PlatformContext): ImageLoader {
        // Use the startup-seeded value to avoid a synchronous DataStore read on Main.
        val cacheSize = cachedCoilCacheSize ?: 512
        return ImageLoader.Builder(this).apply {
            crossfade(false)
            // Use HARDWARE bitmap configuration for direct zero-copy GPU texture uploads
            allowHardware(true)
            bitmapConfig(Bitmap.Config.HARDWARE)
            // Animated GIF backgrounds (Home background picker allows GIF): the
            // platform decoder needs API 28+, GifDecoder covers everything below.
            components {
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.P) {
                    add(AnimatedImageDecoder.Factory())
                } else {
                    add(GifDecoder.Factory())
                }
                // User-supplied SVGs (player icons, DIY stickers). Vectors rasterise at the
                // size they are drawn at, so a custom icon never goes soft when scaled up.
                add(SvgDecoder.Factory())
                // Steps YouTube artwork down a size when the requested filename 404s, so
                // asking for maxresdefault is safe on the videos that do not have one.
                add(ArtworkQualityInterceptor())
                // Cover art that lives on an SMB or WebDAV source.
                add(com.convx.music.utils.NetworkArtworkFetcher.Factory { networkRepository.get() })
            }
            // Reuse decoded images while scrolling.
            memoryCache {
                // Limit memory use more on low-RAM devices.
                val activityManager = getSystemService(android.app.ActivityManager::class.java)
                val percent = if (activityManager?.isLowRamDevice == true) 0.15 else 0.25
                MemoryCache.Builder()
                    .maxSizePercent(context, percent)
                    .build()
            }
            if (cacheSize == 0) {
                diskCachePolicy(CachePolicy.DISABLED)
            } else {
                diskCache(
                    DiskCache.Builder()
                        // Keep artwork in filesDir: Android can clear cacheDir under storage pressure,
                        // including downloaded thumbnails. Coil still enforces the configured size cap.
                        .directory(filesDir.resolve("coil"))
                        .maxSizeBytes(cacheSize * 1024 * 1024L)
                        .build()
                )
            }
        }.build()
    }

    // Best-effort memory relief. Coil re-fetches from disk cache — no state lost.
    @Suppress("DEPRECATION")
    override fun onTrimMemory(level: Int) {
        super.onTrimMemory(level)
        if (level >= android.content.ComponentCallbacks2.TRIM_MEMORY_RUNNING_LOW) {
            runCatching { coil3.SingletonImageLoader.get(this).memoryCache?.clear() }
        }
    }

    companion object {
        /** MMKV file backing [authSeed]. Cache only — DataStore owns these values. */
        private const val AUTH_CACHE_ID = "auth_seed"

        suspend fun forgetAccount(context: Context) {
            Timber.d("forgetAccount: Starting logout process")

            Timber.d("forgetAccount: Clearing DataStore preferences")
            context.dataStore.edit { settings ->
                settings.remove(InnerTubeCookieKey)
                settings.remove(VisitorDataKey)
                settings.remove(DataSyncIdKey)
                settings.remove(AccountNameKey)
                settings.remove(AccountEmailKey)
                settings.remove(AccountChannelHandleKey)
            }
            Timber.d("forgetAccount: DataStore preferences cleared")

            Timber.d("forgetAccount: Clearing YouTube object auth state")
            YouTube.cookie = null
            YouTube.visitorData = null
            YouTube.dataSyncId = null
            Timber.d("forgetAccount: YouTube auth state cleared")

            // Remove WebView cookies so logout does not silently sign back in.
            Timber.d("forgetAccount: Clearing WebView CookieManager")
            withContext(Dispatchers.Main) {
                android.webkit.CookieManager.getInstance().apply {
                    removeAllCookies { removed ->
                        Timber.d("forgetAccount: CookieManager.removeAllCookies callback: removed=$removed")
                    }
                    flush()
                }
            }
            Timber.d("forgetAccount: Logout process complete")
        }
    }
}
