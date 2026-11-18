package com.music.spotify.auth

import io.ktor.client.HttpClient
import io.ktor.client.engine.okhttp.OkHttp
import io.ktor.client.plugins.HttpTimeout
import io.ktor.client.request.forms.submitForm
import io.ktor.client.statement.bodyAsText
import io.ktor.http.Parameters
import kotlinx.coroutines.CancellationException
import kotlinx.serialization.json.Json
import kotlinx.serialization.json.JsonObject
import kotlinx.serialization.json.contentOrNull
import kotlinx.serialization.json.jsonPrimitive
import kotlinx.serialization.json.longOrNull

class SpotifyOAuthApi(
    private val client: HttpClient = HttpClient(OkHttp) {
        followRedirects = false
        install(HttpTimeout) { requestTimeoutMillis = 20_000; connectTimeoutMillis = 10_000; socketTimeoutMillis = 15_000 }
    },
    private val tokenUrl: String = "https://accounts.spotify.com/api/token",
    private val nowMillis: () -> Long = System::currentTimeMillis,
) : SpotifyAuthorizationService {
    override suspend fun exchange(clientId: String, code: String, verifier: String, redirectUri: String): SpotifyTokens = request(
        clientId,
        Parameters.build {
            append("grant_type", "authorization_code")
            append("client_id", clientId)
            append("code", code)
            append("code_verifier", verifier)
            append("redirect_uri", redirectUri)
        },
    )

    override suspend fun refresh(tokens: SpotifyTokens): SpotifyTokens {
        val refreshToken = tokens.refreshToken?.takeIf(String::isNotBlank) ?: throw SpotifyAuthException(SpotifyAuthFailure.EXPIRED)
        val updated = request(tokens.clientId, Parameters.build {
            append("grant_type", "refresh_token")
            append("refresh_token", refreshToken)
            append("client_id", tokens.clientId)
        })
        return updated.copy(refreshToken = updated.refreshToken ?: refreshToken)
    }

    private suspend fun request(clientId: String, parameters: Parameters): SpotifyTokens {
        try {
            val response = client.submitForm(tokenUrl, parameters)
            val body = response.bodyAsText()
            if (body.length > 65_536) throw SpotifyAuthException(SpotifyAuthFailure.INVALID_RESPONSE)
            val json = Json.parseToJsonElement(body) as? JsonObject ?: throw SpotifyAuthException(SpotifyAuthFailure.INVALID_RESPONSE)
            if (response.status.value !in 200..299) {
                val reason = when (json["error"]?.jsonPrimitive?.contentOrNull) {
                    "invalid_grant" -> SpotifyAuthFailure.EXPIRED
                    "access_denied" -> SpotifyAuthFailure.DENIED
                    else -> if (response.status.value >= 500 || response.status.value == 429) SpotifyAuthFailure.CONNECTION else SpotifyAuthFailure.INVALID_RESPONSE
                }
                throw SpotifyAuthException(reason)
            }
            val access = json["access_token"]?.jsonPrimitive?.contentOrNull?.takeIf(String::isNotBlank)
                ?: throw SpotifyAuthException(SpotifyAuthFailure.INVALID_RESPONSE)
            if (json["token_type"]?.jsonPrimitive?.contentOrNull?.equals("Bearer", true) != true) throw SpotifyAuthException(SpotifyAuthFailure.INVALID_RESPONSE)
            val expires = json["expires_in"]?.jsonPrimitive?.longOrNull?.takeIf { it in 1..86_400 }
                ?: throw SpotifyAuthException(SpotifyAuthFailure.INVALID_RESPONSE)
            val now = nowMillis()
            if (now <= 0 || now > Long.MAX_VALUE - expires * 1000) throw SpotifyAuthException(SpotifyAuthFailure.INVALID_RESPONSE)
            return SpotifyTokens(clientId, access, json["refresh_token"]?.jsonPrimitive?.contentOrNull?.takeIf(String::isNotBlank), now + expires * 1000)
        } catch (cancelled: CancellationException) {
            throw cancelled
        } catch (error: SpotifyAuthException) {
            throw error
        } catch (error: java.io.IOException) {
            throw SpotifyAuthException(SpotifyAuthFailure.CONNECTION)
        } catch (error: Exception) {
            throw SpotifyAuthException(SpotifyAuthFailure.INVALID_RESPONSE)
        }
    }
}
