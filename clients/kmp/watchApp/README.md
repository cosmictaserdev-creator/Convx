# Apple Watch companion

`ConvxWatch` is a watchOS 10+ SwiftUI companion target in
`../iosApp/ConvxKmpHost.xcodeproj`. The iPhone owns authentication, catalog access,
playback and the queue. The watch exchanges versioned JSON through
WatchConnectivity with the Koin-injected shared `WatchCompanion`.

Implemented screens and actions:

- Library: Playlists, Artists, Albums and Songs, with collection detail navigation.
- Now Playing: artwork, track and artist, progress ring, play/pause and skipping.
- Crown and slider volume, shuffle, repeat and Playing Next selection.
- Cached metadata while disconnected, with playback commands requiring a live phone.
- Queue identity includes the original index so repeated tracks remain selectable.
- Volume changes respect the phone's bit-perfect setting.

This target does not provide standalone watch audio, offline watch downloads or
a Wear OS application. It contains no copied phone credentials or independent
playback queue. Library snapshots contain at most 30 entries and queue snapshots
at most 40; outgoing lists are further trimmed to the transport size budget.

## Build and verify on macOS

Build the shared iOS simulator framework from the repository root:

```sh
./gradlew -p clients/kmp :shared:linkDebugFrameworkIosSimulatorArm64
```

Open `clients/kmp/iosApp/ConvxKmpHost.xcodeproj`, select `ConvxWatch`, and choose a watch
simulator paired with an iPhone simulator. The phone target embeds the watch app.
The existing simulator host disables code signing; configure a development team,
signing and the device framework path before installing on physical devices.

```sh
xcodebuild -project clients/kmp/iosApp/ConvxKmpHost.xcodeproj \
  -scheme ConvxWatch -sdk watchsimulator \
  -destination 'generic/platform=watchOS Simulator' \
  CODE_SIGNING_ALLOWED=NO build
```

Native compilation and paired-device behavior have not been verified in the
Windows development environment. Shared protocol tests exercise command routing,
seek limits, version rejection, repeated/stale queue selection, refresh, catalog
browsing and bit-perfect volume behavior. They do not replace the native build.
