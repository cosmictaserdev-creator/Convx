package com.convx.storage.domain

object NetworkEmbeddedArtworkUri {
    const val Scheme = "convx-network-art"

    fun create(mediaId: String): String? = NetworkMediaUri.parse(mediaId)?.let {
        NetworkMediaUri.create(it.serverId, it.path).replaceFirst("${NetworkMediaUri.Scheme}://", "$Scheme://")
    }

    fun mediaId(value: String): String? {
        if (!value.startsWith("$Scheme://")) return null
        val id = value.replaceFirst("$Scheme://", "${NetworkMediaUri.Scheme}://")
        return NetworkMediaUri.parse(id)?.let { NetworkMediaUri.create(it.serverId, it.path) }
    }
}
