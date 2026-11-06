package com.convx.music.core.updater

import android.os.Build
import com.convx.music.BuildConfig
import kotlinx.coroutines.CancellationException
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import org.json.JSONArray
import org.json.JSONObject
import java.net.HttpURLConnection
import java.net.URI
import javax.inject.Inject
import javax.inject.Singleton
import java.io.ByteArrayOutputStream

@Singleton
class GitHubReleaseSource @Inject constructor() {
    private var cachedBody: String? = null
    private var etag: String? = null
    private var blockedUntil = 0L

    suspend fun latest(includePrerelease: Boolean): UpdateRelease? = withContext(Dispatchers.IO) {
        if (System.currentTimeMillis() < blockedUntil) throw UpdateException(UpdateFailure.RATE_LIMIT)
        val releases = JSONArray(read(RELEASES_URL, conditional = true))
        val eligible = mutableListOf<UpdateRelease>()
        var examined = 0
        for (index in 0 until releases.length()) {
            val release = releases.getJSONObject(index)
            if (release.optBoolean("draft") || (!includePrerelease && release.optBoolean("prerelease"))) continue
            if (examined >= 3) break
            val assets = release.optJSONArray("assets") ?: continue
            val manifest = (0 until assets.length()).map(assets::getJSONObject).firstOrNull { it.optString("name") == "updater.json" } ?: continue
            examined++
            try {
                val metadata = JSONObject(read(manifest.getString("browser_download_url")))
                require(metadata.getInt("schemaVersion") == 1)
                require(metadata.getString("packageName") == "com.convx.music")
                val variant = if (BuildConfig.CAST_AVAILABLE) "gms" else "foss"
                val assetArray = metadata.getJSONArray("assets")
                val candidates = (0 until assetArray.length()).map { i ->
                    val item = assetArray.getJSONObject(i)
                    val name = item.getString("name")
                    val published = (0 until assets.length()).map(assets::getJSONObject).firstOrNull { it.optString("name") == name }
                        ?: throw IllegalArgumentException("Missing asset")
                    val checksum = item.getString("sha256")
                    require(UpdatePolicy.validDigest(checksum))
                    val assetUrl = published.getString("browser_download_url")
                    require(isTrustedUrl(assetUrl))
                    val apiDigest = published.optString("digest").removePrefix("sha256:")
                    require(apiDigest.isBlank() || apiDigest.equals(checksum, ignoreCase = true))
                    val size = item.getLong("size")
                    require(size > 0 && size == published.getLong("size"))
                    UpdateAsset(name, assetUrl, size, checksum,
                        item.getLong("versionCode"), item.getString("variant"),
                        item.getJSONArray("abis").let { a -> (0 until a.length()).map(a::getString) }, item.getInt("minSdk"))
                }
                val selected = UpdatePolicy.selectAsset(candidates, variant, Build.SUPPORTED_ABIS.toList(), Build.VERSION.SDK_INT, BuildConfig.VERSION_CODE.toLong()) ?: continue
                val changes = metadata.optJSONArray("changelog")
                val sections = if (changes == null) emptyList() else (0 until minOf(changes.length(), 8)).map { i ->
                    val section = changes.getJSONObject(i)
                    val items = section.getJSONArray("items")
                    ChangelogSection(section.getString("title").take(100),
                        (0 until minOf(items.length(), 32)).map { items.getString(it).take(500) })
                }
                val image = metadata.optString("image").takeIf { it.isNotBlank() }?.takeIf { isTrustedUrl(it) }
                eligible += UpdateRelease(
                    "${release.getLong("id")}:${selected.sha256}", metadata.getString("version"),
                    release.optString("published_at"), metadata.optString("description").ifBlank { release.optString("body") }.take(4_000),
                    image, sections, selected, release.optString("html_url").takeIf { isTrustedUrl(it) }.orEmpty(),
                )
            } catch (cancelled: CancellationException) {
                throw cancelled
            } catch (failure: UpdateException) {
                throw failure
            } catch (_: IllegalArgumentException) {
                throw UpdateException(UpdateFailure.METADATA)
            } catch (_: org.json.JSONException) {
                throw UpdateException(UpdateFailure.METADATA)
            }
        }
        eligible.maxByOrNull { it.asset.versionCode }
    }

    private fun read(url: String, conditional: Boolean = false): String {
        require(isTrustedUrl(url))
        var current = url
        repeat(6) {
            val connection = URI(current).toURL().openConnection() as HttpURLConnection
            connection.instanceFollowRedirects = false
            connection.connectTimeout = 15_000
            connection.readTimeout = 15_000
            connection.setRequestProperty("Accept", "application/vnd.github+json")
            if (conditional) etag?.let { connection.setRequestProperty("If-None-Match", it) }
            try {
                when (connection.responseCode) {
                    301, 302, 303, 307, 308 -> {
                        current = URI(current).resolve(connection.getHeaderField("Location")).toString()
                        require(isTrustedUrl(current))
                    }
                    304 -> return cachedBody ?: throw UpdateException(UpdateFailure.METADATA)
                    403, 429 -> {
                        val retry = connection.getHeaderField("Retry-After")?.toLongOrNull()?.times(1000)?.plus(System.currentTimeMillis())
                        val reset = connection.getHeaderField("X-RateLimit-Reset")?.toLongOrNull()?.times(1000)
                        blockedUntil = maxOf(retry ?: 0L, reset ?: 0L, System.currentTimeMillis() + 60_000)
                        throw UpdateException(UpdateFailure.RATE_LIMIT)
                    }
                    200 -> {
                        val body = connection.inputStream.use { input ->
                            val output = ByteArrayOutputStream()
                            val buffer = ByteArray(8192)
                            var total = 0
                            while (true) {
                                val read = input.read(buffer)
                                if (read == -1) break
                                total += read
                                if (total > 2 * 1024 * 1024) throw UpdateException(UpdateFailure.METADATA)
                                output.write(buffer, 0, read)
                            }
                            String(output.toByteArray(), Charsets.UTF_8)
                        }
                        if (conditional) { cachedBody = body; etag = connection.getHeaderField("ETag") }
                        return body
                    }
                    else -> throw UpdateException(UpdateFailure.NETWORK)
                }
            } finally { connection.disconnect() }
        }
        throw UpdateException(UpdateFailure.NETWORK)
    }

    companion object {
        private const val RELEASES_URL = "https://api.github.com/repos/cosmictaserdev-creator/Convx/releases?per_page=30"
        fun isTrustedUrl(value: String): Boolean = runCatching {
            val uri = URI(value)
            uri.scheme == "https" && uri.userInfo == null && (uri.port == -1 || uri.port == 443) &&
                uri.host in setOf("api.github.com", "github.com", "raw.githubusercontent.com", "release-assets.githubusercontent.com", "objects.githubusercontent.com")
        }.getOrDefault(false)
    }
}
