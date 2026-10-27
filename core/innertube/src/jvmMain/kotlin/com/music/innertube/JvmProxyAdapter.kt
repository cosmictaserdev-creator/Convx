package com.music.innertube

import java.net.InetSocketAddress
import java.net.Proxy

internal fun InnertubeProxySettings.toJavaProxy(): Proxy =
    Proxy(
        Proxy.Type.valueOf(type.name),
        InetSocketAddress(host, port),
    )
