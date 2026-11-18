# Convx Wear OS companion

The Wear OS 3+ app controls Convx on a paired Android phone and displays the phone's lyrics with line and word timing. Playback, provider lookup, credentials, saved lyrics, and per-song timing offsets remain on the phone. The watch uses the shared protocol and command rules in `:playback-core`.

Controls include play/pause, previous/next, shuffle, repeat, and volume. Turn the crown to scroll between the player controls or lyric lines. Room control restrictions and bit-perfect volume protection apply. Disconnected snapshots remain visible with controls disabled; reconnect performs a live handshake before enabling commands.

## Build

Run from the repository root with JDK 21:

```powershell
.\gradlew.bat :wearApp:assembleDebug :app:assembleUniversalGmsDebug :playback-core:desktopTest --no-configuration-cache --console=plain
```

Watch APK: `clients/wearos/build/outputs/apk/debug/ConvxWear-debug.apk`.

Install the GMS phone APK on the phone and the watch APK on the paired watch. Both use `com.convx.music.debug` in debug and `com.convx.music` in release, and must use the same signing certificate. Release signing reuses the phone's existing keystore configuration without copying signing files. A phone installed from Google Play also needs the matching Play signing identity on the watch; a locally signed APK cannot pair with a differently signed Play installation.

Wearable transport is included only in the phone's GMS variant. The FOSS phone build compiles with an empty adapter and contains no Wearable Data Layer integration. Watch APK names start with `ConvxWear`, separate from the phone release assets.

## Verification

Shared tests cover malformed/oversized messages, protocol versions, stale track/session commands, duplicate commands, bounds, room/volume restrictions, timing, and transport budgets. CI is configured to build the watch plus both phone variants. Pairing, live controls, crown input, round-screen rendering, word timing, Bluetooth loss/reconnect, and app background/foreground still require a paired watch or Wear OS emulator test; build success alone does not prove those behaviors.

Local verification on 2026-10-27 passed the watch debug build, GMS phone debug assembly, FOSS phone compilation, app unit tests (355 passed, one skipped), playback common tests (59 passed), and lyrics-data common tests (20 passed). Phone/watch debug package IDs and signing certificates match. At 192 dp, an Android-emulator fixture rendered player and lyric screens, Back returned to the player, and the actual launcher showed disconnected/reconnect state without a crash. These are not paired Wear OS runtime tests. `WearPreviewActivity` is available only in debug builds for synthetic player and lyric previews.
