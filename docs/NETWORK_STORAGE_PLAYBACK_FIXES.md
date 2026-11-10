# Shared network storage and Android playback

## Shared implementation

The active repository is `artifacts/worktrees/convx-kmp-remote` in the outer
workspace. Its `:network-storage-core` module owns portable source/credential
models, source validation, logical media IDs, library scanning, metadata/artwork
selection, file-source contracts, typed errors, and seek-window bounds. It targets
Android, iOS and desktop. Android retains the existing package names, encrypted
source-record schema, IDs, and java.io.IOException-compatible failures.

`:network-storage-transports` owns one SMBJ/OkHttp implementation used by Android
and desktop through `jvmSharedMain`. The Android `:network-storage` module exports
it and retains encrypted storage, Media3 and Coil integration. The KMP hosts
consume the shared artifacts, but desktop source-management UI and native-player
handoff are not wired yet. iOS currently has the common core only: native WebDAV
and SMB transports, credential storage and player integration remain open.
Neither desktop nor iOS network playback parity is claimed by this extraction.

Unicode URI normalization is a tiny platform adapter (Java Normalizer or
Foundation). JVM compatibility tests compare the new encoding with the former
Android java.net.URI implementation, including reserved characters and Unicode.

## Android fixes

The outer media-source resolver sent `convx-network://` items through YouTube
resolution even though the inner data-source resolver already bypassed them.
`isDirectMediaSource` now supplies the rule for both resolvers and their URL and
analysis-head prefetch entry points. Network items reach the remote data source;
they are still treated as network media when choosing offline tracks.

WebDAV playback now uses a redirect-capable OkHttp client. PROPFIND keeps its
existing non-redirecting client. Range requests survive redirects, same-origin
authentication is retained, and OkHttp strips Authorization across origins.

SMB list/open operations retry once after a transport IOException, using a fresh
connection. Configuration and authentication errors are not retried. Each pooled
connection has its own identity so a failure on an old file handle cannot evict
a newer connection. Each reader retains its own handle and absolute offset.

## BitChord comparison

Reference: [BitChord](https://github.com/kushagrasinghx/BitChord), particularly
`playback/SmbDataSource.kt`, `data/smb/SmbConnection.kt`, and its shared HTTP client.
The repository's NOTICE records the GPL-3.0 attribution.

| Behavior | Convx change |
| --- | --- |
| Route SMB before provider resolution | Fixed the outer resolver and both prefetch paths for SMB and WebDAV. |
| Retry stale SMB transport once | Added to list/open, with connection ownership checks for concurrent readers. |
| Explicit SMB operation timeouts | Read/write/transact are 30 seconds, alongside the existing socket timeout. |
| Separate file handles and absolute seek offsets | Already present; retained and covered by regression tests. |
| Bound SMB reads by file length and requested range | Already present in ReadWindow; retained and tested at EOF and overflow boundaries. |
| Guest login for blank credentials | Already present; retained. |
| HTTP playback follows redirects | Enabled for WebDAV GET playback and general remote-file reads. |
| Credential configuration | Retained Convx's independent saved servers, credentials, ports, shares, and base folders. |

## Validation

The added tests first failed against the original behavior: two routing failures,
two WebDAV redirect failures, and three SMB stale-session/retry failures. The
original fixes passed 18 tests: 3 routing, 4 WebDAV HTTP integration,
7 SMB session/reader tests, and 4 read-window tests. Shared-source migration adds
source validation, legacy-ID compatibility, scanning, limit and cancellation tests.
The extracted production modules pass 25 JVM tests (14 core and 11 transports).
The combined local harness passes 28 tests including the three Android routing
predicate tests. iOS-core compilation was attempted but stopped before source
compilation because its Ktor, kotlinx-io and atomicfu klibs are not cached and
dependency downloads are unavailable in this session.

Run these commands from the active repository, with JDK 21 and an installed
Android SDK for the app test:

```sh
./gradlew -p clients/kmp :network-storage-core:desktopTest :network-storage-transports:desktopTest
./gradlew :app:testUniversalFossDebugUnitTest --tests com.convx.music.playback.NetworkPlaybackRoutingTest
```

Both Android build workflows run these checks, and the KMP workflow runs core
tests on the iOS simulator. The local SDK-restricted session also uses the
standalone JVM harness at the outer workspace's
`artifacts/network-storage-check`. It compiles the production protocol/file-read
sources and routing predicate, and runs the same test classes. WebDAV tests use
real loopback HTTP servers; SMB tests inject failed transports at the SMBJ session
boundary. It does not compile MusicService or execute the Android Media3 player.

Full APK verification remains pending because the Android SDK directory is
inaccessible in this session. No device is attached, so real NAS playback and
seeking have not been verified. Device follow-up should exercise authenticated
SMB/WebDAV, repeated forward/backward seeks, pause/resume after a connection drops,
track changes during analysis, and filenames containing spaces or Unicode.

## Testing without a NAS

A regular computer can act as the server. Start the included read-only WebDAV
fixture using Python 3.10 or later; it needs no third-party packages:

```sh
python tools/network_storage_fixture.py --host 0.0.0.0
```

It generates a one-minute WAV with a different tone every ten seconds. On a
phone connected to the same LAN, add a WebDAV source with address
`http://YOUR_COMPUTER_LAN_IP:8766/music/`, username `convx`, password `test`.
An Android emulator uses `10.0.2.2` for its host computer. Allow the Python server
on the private LAN if the OS firewall asks. Stop it with Ctrl+C when done.

Browse, scan and play the tone; seek to 10, 30 and 50 seconds, then backwards.
Repeat using `/redirect/` instead of `/music/` to exercise redirected GETs.
To test your own MP3/FLAC files and cover artwork, use `--folder PATH`.
This fixture covers read-only DAV and ranges; it is not proof of compatibility
with every NAS server.

For a real SMB test, share that generated folder using Windows file sharing or
Samba on a computer. Add `smb://COMPUTER_IP/SHARE_NAME` in Convx with the account's
username and password. A Windows Hello PIN is not the SMB password. Test play,
seeking, next track and reconnect after briefly stopping the share. No dedicated
NAS hardware is needed.

## Signed release APK in Actions

The existing **CI & Debug Build** workflow now has a `test_release` manual input,
enabled by default. Its test-release job requires the existing KEYSTORE,
KEYSTORE_PASSWORD, KEY_ALIAS and KEY_PASSWORD secrets; runs shared network and
Android routing tests; builds `universalGmsRelease`; verifies its signature; and
uploads the APK, SHA256SUMS and exact commit as an artifact retained for 14 days.
It does not create a stable release or update nightly assets.

Once these local changes are pushed, trigger on their branch:

```sh
gh workflow run build.yml --repo cosmictaserdev-creator/Convx-KMP --ref YOUR_BRANCH -f test_release=true
```

Download `convx-universal-gms-test-release-RUN_NUMBER` from that run's Artifacts.
The current session could not push or dispatch the build because outbound GitHub
API connections are blocked. No new Actions run or APK is claimed.
