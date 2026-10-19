package com.convx.music.lyrics.bitchord

import okhttp3.OkHttpClient
import java.util.concurrent.TimeUnit

internal val providerHttpClient = OkHttpClient.Builder()
    .connectTimeout(5, TimeUnit.SECONDS)
    .readTimeout(8, TimeUnit.SECONDS)
    .callTimeout(12, TimeUnit.SECONDS)
    .build()
