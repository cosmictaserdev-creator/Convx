package com.convx.storage.domain

import io.ktor.http.Url
import io.ktor.http.decodeURLPart
import kotlin.uuid.ExperimentalUuidApi
import kotlin.uuid.Uuid

enum class NetworkProtocol { WEBDAV, SMB }

data class SourceCredentials(val username: String = "", val password: String = "", val domain: String = "") {
    override fun toString(): String = "SourceCredentials(username=$username, domain=$domain)"
}

sealed interface NetworkServer {
    val id: String
    val name: String
    val credentials: SourceCredentials
    val address: String
    val protocol: NetworkProtocol

    data class WebDav(
        override val id: String,
        override val name: String,
        override val address: String,
        override val credentials: SourceCredentials = SourceCredentials(),
    ) : NetworkServer {
        override val protocol = NetworkProtocol.WEBDAV
    }

    data class Smb(
        override val id: String,
        override val name: String,
        val host: String,
        val port: Int,
        val share: String,
        val folder: String,
        override val credentials: SourceCredentials = SourceCredentials(),
    ) : NetworkServer {
        override val protocol = NetworkProtocol.SMB
        override val address: String get() = "smb://$host" + (if (port == 445) "" else ":$port") +
            encodeRemoteUriPath("/$share/$folder".trimEnd('/'))
    }

    companion object {
        @OptIn(ExperimentalUuidApi::class)
        fun create(protocol: NetworkProtocol, name: String, address: String, credentials: SourceCredentials, id: String = Uuid.random().toString()): NetworkServer {
            require(id.matches(Regex("[A-Za-z0-9_-]{1,80}"))) { "Invalid server identifier" }
            require(name.trim().isNotEmpty()) { "Enter a server name" }
            return when (protocol) {
                NetworkProtocol.WEBDAV -> {
                    val input = address.trim().let { if (it.contains("://")) it else "https://$it" }
                    val uri = validatedUrl(input)
                    require(uri.protocol.name.lowercase() in setOf("https", "http")) { "Enter an HTTP or HTTPS folder URL" }
                    WebDav(id, name.trim(), input.trimEnd('/') + "/", credentials)
                }
                NetworkProtocol.SMB -> {
                    val input = address.trim().replace('\\', '/').let {
                        when { it.startsWith("//") -> "smb:$it"; it.startsWith("smb://", true) -> it; else -> "smb://$it" }
                    }
                    val uri = validatedUrl(input.replace(" ", "%20"))
                    require(uri.protocol.name.equals("smb", true)) { "Enter an SMB server and share" }
                    val path = RemotePath.normalize(uri.encodedPath.decodeURLPart())
                    val share = path.substringBefore('/')
                    require(share.isNotBlank()) { "Enter the share name" }
                    val port = uri.specifiedPort.takeUnless { it == 0 } ?: 445
                    require(port in 1..65535) { "Invalid server port" }
                    Smb(id, name.trim(), uri.host, port, share, path.substringAfter('/', ""), credentials)
                }
            }
        }
    }
}

private fun validatedUrl(value: String): Url {
    require(value.none { it.isWhitespace() || it == '\u0000' } && '?' !in value && '#' !in value) { "Enter a server folder URL" }
    val explicitPort = Regex(":([0-9]+)$").find(value.substringAfter("://").substringBefore('/'))?.groupValues?.get(1)
    require(explicitPort == null || explicitPort.toIntOrNull() in 1..65535) { "Invalid server port" }
    val url = Url(value)
    require(url.host.isNotBlank() && url.user == null && url.password == null) { "Enter credentials in the login fields" }
    require(url.specifiedPort == 0 || url.specifiedPort in 1..65535) { "Invalid server port" }
    return url
}
