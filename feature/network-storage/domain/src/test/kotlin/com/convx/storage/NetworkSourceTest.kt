package com.convx.storage

import com.convx.storage.domain.*
import kotlin.test.*

class NetworkSourceTest {
    private val credentials = SourceCredentials("listener", "secret", "DOMAIN")

    @Test fun smbAddressesAcceptUncAndRetainPortShareFolderAndCredentials() {
        val server = NetworkServer.create(NetworkProtocol.SMB, " NAS ",
            "\\\\nas.example\\Music\\Albums Here", credentials, "nas") as NetworkServer.Smb
        assertEquals("NAS", server.name)
        assertEquals("nas.example", server.host)
        assertEquals(445, server.port)
        assertEquals("Music", server.share)
        assertEquals("Albums Here", server.folder)
        assertEquals("smb://nas.example/Music/Albums%20Here", server.address)
        assertEquals(credentials, server.credentials)
        val custom = NetworkServer.create(NetworkProtocol.SMB, "Other", "smb://nas.example:1445/Music", credentials, "other")
        assertEquals(1445, (custom as NetworkServer.Smb).port)
        assertEquals("smb://nas.example:1445/Music", custom.address)
    }

    @Test fun webDavDefaultsToHttpsAndPreservesEncodedBaseFolders() {
        val server = NetworkServer.create(NetworkProtocol.WEBDAV, "Cloud", "cloud.example/Music%20Here", credentials, "cloud")
        assertEquals("https://cloud.example/Music%20Here/", server.address)
        assertEquals(credentials, server.credentials)
    }

    @Test fun rejectsEmbeddedCredentialsQueriesFragmentsBadPortsAndEscapingFolders() {
        for (url in listOf("smb://user:pass@nas/Music", "smb://nas/Music?query", "smb://nas/Music#fragment",
            "smb://nas:0/Music", "smb://nas:65536/Music", "smb://nas/Music/../Other", "smb://nas/Music/%2e%2e/Other")) {
            assertFailsWith<IllegalArgumentException>(url) {
                NetworkServer.create(NetworkProtocol.SMB, "NAS", url, credentials, "nas")
            }
        }
        assertFalse(credentials.toString().contains("secret"))
    }

    @Test fun ipv6AddressesRoundTripWithoutChangingTheShareOrPort() {
        val server = NetworkServer.create(NetworkProtocol.SMB, "IPv6", "smb://[::1]:1445/Music/Album", credentials, "ipv6")
        assertEquals("smb://[::1]:1445/Music/Album", server.address)
    }

    @Test fun mediaIdentityRoundTripsEscapesUnicodeAndSourceCase() {
        val path = "Artist/Album/Track #1 + 50% \u00e9\u97f3.flac"
        val uri = NetworkMediaUri.create("NAS_Source", path)
        assertEquals(NetworkMediaLocation("NAS_Source", path), NetworkMediaUri.parse(uri))
        assertTrue("%23" in uri && "%25" in uri)
        assertTrue("+" in uri)
        assertNull(NetworkMediaUri.parse("convx-network://nas/../secret"))
        assertNull(NetworkMediaUri.parse("convx-network://nas/%2e%2e/secret"))
        assertNull(NetworkMediaUri.parse("convx-network://nas/song?password=hidden"))
        assertNull(NetworkMediaUri.parse("convx-network://user:pass@nas/song"))
        assertNull(NetworkMediaUri.parse("convx-network://nas:445/song"))
        assertNull(NetworkMediaUri.parse("convx-network://nas/song#fragment"))
    }
}
