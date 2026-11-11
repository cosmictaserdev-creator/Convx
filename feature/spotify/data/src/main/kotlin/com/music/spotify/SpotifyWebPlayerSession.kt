package com.music.spotify

import com.music.spotify.models.SpotifyInternalToken
import kotlinx.serialization.json.Json

/** Browser-independent Spotify token validation and capture shared by Android and desktop. */
object SpotifyWebPlayerSession {
    const val ORIGIN = "https://open.spotify.com"
    const val BRIDGE = "ConvxSpotifyToken"
    const val LOGIN_URL = "https://accounts.spotify.com/login?continue=https%3A%2F%2Fopen.spotify.com%2F"
    private val json = Json { ignoreUnknownKeys = true; isLenient = true }

    fun browserUserAgent(agent: String): String = agent.replace("; wv", "")
        .replace(Regex("\\s*Version/\\d+(?:\\.\\d+)*"), "").trim()

    fun parseWebPlayerToken(payload: String, nowMillis: Long = System.currentTimeMillis()): SpotifyInternalToken? {
        if (payload.length > 65_536) return null
        return runCatching { json.decodeFromString<SpotifyInternalToken>(payload) }.getOrNull()?.takeIf {
            !it.isAnonymous && it.accessToken.isNotBlank() && it.clientId.isNotBlank() &&
                it.accessToken.none(Char::isISOControl) && it.clientId.none(Char::isISOControl) &&
                it.accessTokenExpirationTimestampMs > nowMillis
        }
    }

    val captureScript = """
        (function() {
          if (location.origin !== '$ORIGIN' || window.__convxSpotifyHook) return;
          window.__convxSpotifyHook = true;
          function report(body) { try { window.$BRIDGE.postMessage(body); } catch (_) {} }
          function isToken(url) { try { var u = new URL(String(url), location.href); return u.origin === '$ORIGIN' && u.pathname === '/api/token'; } catch (_) { return false; } }
          var fetchOriginal = window.fetch;
          window.fetch = function(input) {
            var response = fetchOriginal.apply(this, arguments);
            if (isToken(input && input.url ? input.url : input)) response.then(function(r) { r.clone().text().then(report).catch(function() {}); }).catch(function() {});
            return response;
          };
          var open = XMLHttpRequest.prototype.open, send = XMLHttpRequest.prototype.send;
          XMLHttpRequest.prototype.open = function(method, url) { this.__convxTokenUrl = url; return open.apply(this, arguments); };
          XMLHttpRequest.prototype.send = function() {
            var xhr = this;
            xhr.addEventListener('load', function() { if (isToken(xhr.__convxTokenUrl)) { try { report(xhr.responseText); } catch (_) {} } });
            return send.apply(this, arguments);
          };
          try { var session = document.getElementById('session'); if (session) report(session.textContent); } catch (_) {}
        })();
    """.trimIndent()
}
