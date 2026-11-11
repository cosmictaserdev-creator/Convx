package com.convx.music.utils

import com.music.innertube.models.YouTubeClient
import okhttp3.Interceptor
import okhttp3.Response

/**
 * Sends googlevideo byte fetches with the User-Agent of the client that minted the URL. The URL
 * names that client (`c=`, `cver=`) and googlevideo refuses a fetch whose headers claim another,
 * which surfaced as streams that validated for no client and downloads that failed in bulk.
 */
object StreamClientHeaders : Interceptor {
    override fun intercept(chain: Interceptor.Chain): Response {
        val request = chain.request()
        if (!(request.url.host == "googlevideo.com" || request.url.host.endsWith(".googlevideo.com")) || request.url.queryParameter("c") == null) {
            return chain.proceed(request)
        }
        return chain.proceed(
            request.newBuilder()
                .header("User-Agent", YouTubeClient.userAgentForStreamUrl(request.url.toString()))
                .build()
        )
    }
}
