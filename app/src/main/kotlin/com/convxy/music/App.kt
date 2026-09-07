/**
 * Convx Project (C) 2026
 * Licensed under GPL-3.0 | See git history for contributors
 */

package com.convxy.music

import android.app.Application
import android.app.NotificationChannel
import android.app.NotificationManager
import android.content.Context
import android.os.Build
import android.widget.Toast
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
import com.music.innertube.YouTube
import com.music.innertube.YouTubeWeb
import com.music.innertube.models.IpVersion
import com.music.innertube.models.YouTubeLocale
import com.music.kugou.KuGou
import com.music.lastfm.LastFM
import com.convxy.music.constants.*
import com.convxy.music.di.ApplicationScope
import com.convxy.music.extensions.toEnum
import com.convxy.music.extensions.toInetSocketAddress
import com.convxy.music.utils.CrashHandler
import com.convxy.music.utils.DebugLogs
import com.convxy.music.utils.cipher.CipherDeobfuscator
import com.convxy.music.utils.dataStore
import com.convxy.music.utils.get
import com.convxy.music.utils.reportException
import dagger.hilt.android.HiltAndroidApp
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.distinctUntilChanged
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.launch
import kotlinx.coroutines.runBlocking
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

    override fun onCreate() {
        super.onCreate()

        // Restored synchronously, before anything else can start: the async
        // DataStore collector further down (applicationScope.launch { ... }
        // .collect { YouTube.cookie = ... }) doesn't land its first value until
        // some time after onCreate() returns. MusicService can start resolving
        // playback before that — sees YouTube.cookie == null (its default, not
        // "genuinely logged out"), takes the guest-session branch in
        // BotDetectionMitigator, and PERSISTS a freshly-fetched guest
        // visitorData over the real logged-in session's value in DataStore.
        // Every cold start was a fresh chance to re-corrupt it.
        YouTube.cookie = dataStore.get(InnerTubeCookieKey, "").takeIf { it.isNotBlank() && it != "null" }
        YouTube.visitorData = dataStore.get(VisitorDataKey, "").takeIf { it.isNotBlank() && it != "null" }
        // The regular-YouTube (WEB) client shares the session: without a visitor
        // id its browse endpoints (home feed, channels) degrade — slow responses,
        // consent walls, sometimes empty payloads.
        YouTubeWeb.visitorData = YouTube.visitorData
        YouTube.dataSyncId = dataStore.get(DataSyncIdKey, "").takeIf { it.isNotBlank() && it != "null" }

        // Install crash handler first
        CrashHandler.install(this)

        // Initialize cipher deobfuscator for WEB_REMIX streaming
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

        // تهيئة إعدادات التطبيق عند الإقلاع
        applicationScope.launch {
            initializeSettings()

            // Warm the cipher WebView off the first-play critical path
            launch(Dispatchers.IO) {
                delay(1500)
                CipherDeobfuscator.prewarm()
            }

            observeSettingsChanges()
        }
    }

    private suspend fun initializeSettings() {
        val settings = dataStore.data.first()
        val locale = Locale.getDefault()
        val languageTag = locale.language

        val appLocale = YouTubeLocale(
            gl = settings[ContentCountryKey]?.takeIf { it != SYSTEM_DEFAULT }
                ?: locale.country.takeIf { it in CountryCodeToName }
                ?: "US",
            hl = settings[ContentLanguageKey]?.takeIf { it != SYSTEM_DEFAULT }
                ?: locale.language.takeIf { it in LanguageCodeToName }
                ?: languageTag.takeIf { it in LanguageCodeToName }
                ?: "en"
        )
        YouTube.locale = appLocale
        YouTubeWeb.locale = appLocale

        if (languageTag == "zh-TW") {
            KuGou.useTraditionalChinese = true
        }

        // Initialize LastFM with API keys from BuildConfig (GitHub Secrets)
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
        YouTube.ipVersion = settings[IpVersionKey]?.toEnum(defaultValue = IpVersion.AUTO) ?: IpVersion.AUTO

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
                            YouTubeWeb.visitorData = newVisitorData
                            dataStore.edit { settings ->
                                settings[VisitorDataKey] = newVisitorData
                            }
                        }
                }
        }

        applicationScope.launch(Dispatchers.IO) {
            dataStore.data
                .map { it[DataSyncIdKey] }
                .distinctUntilChanged()
                .collect { dataSyncId ->
                    // Assigned raw: blank/"null"/"||" handling lives in
                    // InnerTube.dataSyncId's setter so every path that assigns it
                    // (login, this collector, channel switch, token import) gets the
                    // same normalization. Truncating here — and keeping the wrong
                    // half of "||" — is what silently reverted channel switches back
                    // to primary, since this collector re-fires on every write.
                    YouTube.dataSyncId = dataSyncId
                }
        }

        applicationScope.launch(Dispatchers.IO) {
            dataStore.data
                .map { it[InnerTubeCookieKey] }
                .distinctUntilChanged()
                .collect { cookie ->
                    try {
                        YouTube.cookie = cookie?.takeIf { it.isNotBlank() && it != "null" }
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

                    val webLocale = YouTubeLocale(
                        gl = contentCountry?.takeIf { it != SYSTEM_DEFAULT }
                            ?: effectiveAppLocale.country.takeIf { it in CountryCodeToName }
                            ?: systemLocale.country.takeIf { it in CountryCodeToName }
                            ?: "US",
                        hl = contentLanguage?.takeIf { it != SYSTEM_DEFAULT }
                            ?: effectiveAppLocale.toLanguageTag().takeIf { it in LanguageCodeToName }
                            ?: effectiveAppLocale.language.takeIf { it in LanguageCodeToName }
                            ?: "en"
                    )
                    YouTube.locale = webLocale
                    YouTubeWeb.locale = webLocale
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
    }

    @Volatile
    private var cachedCoilCacheSize: Int? = null

    override fun newImageLoader(context: PlatformContext): ImageLoader {
        val cacheSize = cachedCoilCacheSize ?: runBlocking {
            dataStore.data.map { it[MaxImageCacheSizeKey] ?: 512 }.first()
        }
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
            }
            // Memory cache for fast image loading (prevents network requests on recomposition)
            memoryCache {
                MemoryCache.Builder()
                    .maxSizePercent(context, 0.35)
                    .build()
            }
            if (cacheSize == 0) {
                diskCachePolicy(CachePolicy.DISABLED)
            } else {
                diskCache(
                    DiskCache.Builder()
                        // filesDir, not cacheDir. cacheDir is reclaimable storage: the OS
                        // empties it whenever the device runs low, and any "clear cache"
                        // wipes it outright — which is why artwork kept vanishing after a
                        // while, downloaded songs included, since DownloadUtil pre-caches
                        // their thumbnails here too. Eviction there is driven by device
                        // pressure, not by size, so raising maxSizeBytes never helped.
                        // Growth is still bounded: Coil LRU-evicts against the cap below,
                        // which StorageSettings exposes and can clear on demand.
                        .directory(filesDir.resolve("coil"))
                        .maxSizeBytes(cacheSize * 1024 * 1024L)
                        .build()
                )
            }
        }.build()
    }

    companion object {
        suspend fun forgetAccount(context: Context) {
            Timber.d("forgetAccount: Starting logout process")

            // Clear DataStore preferences
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

            // Immediately clear YouTube object's auth state
            Timber.d("forgetAccount: Clearing YouTube object auth state")
            Timber.d("forgetAccount: Before - cookie=${YouTube.cookie?.take(50)}, visitorData=${YouTube.visitorData?.take(20)}, dataSyncId=${YouTube.dataSyncId?.take(20)}")
            YouTube.cookie = null
            YouTube.visitorData = null
            YouTube.dataSyncId = null
            Timber.d("forgetAccount: After - cookie=${YouTube.cookie}, visitorData=${YouTube.visitorData}, dataSyncId=${YouTube.dataSyncId}")

            // Clear WebView cookies to prevent auto-relogin
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
