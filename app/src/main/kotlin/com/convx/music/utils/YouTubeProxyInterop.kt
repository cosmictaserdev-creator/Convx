package com.convx.music.utils

import com.music.innertube.InnertubeProxySettings
import java.net.InetSocketAddress
import java.net.Proxy

fun InnertubeProxySettings?.toJavaProxy(): Proxy? = this?.let { settings ->
    Proxy(
        Proxy.Type.valueOf(settings.type.name),
        InetSocketAddress(settings.host, settings.port),
    )
}
