package com.convx.storage

import com.convx.storage.domain.*
import java.net.URI
import java.io.IOException
import kotlin.test.*

class AndroidNetworkCompatibilityTest {
    @Test fun existingAndroidMediaIdsKeepTheirExactEncoding() {
        val paths = listOf("Artist/Track #1 50% + ?.mp3", "Album/a:b@c[1];=x&y.flac",
            "A/e\u0301/\u97f3\u697d.flac", "Symbols/" + (1..126).map(Int::toChar).filter { it != '/' && it != '\\' }.joinToString(""))
        for (path in paths) {
            assertEquals(URI("convx-network", "NAS_source", "/" + RemotePath.normalize(path), null, null).toASCIIString(),
                NetworkMediaUri.create("NAS_source", path), path)
        }
    }

    @Test fun storageErrorsRemainIoExceptionsForTheAndroidRetryBoundary() {
        val failure: Throwable = StorageException(StorageError.AUTHENTICATION)
        assertIs<IOException>(failure)
    }
}
