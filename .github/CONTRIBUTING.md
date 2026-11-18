# Contributing to Convx

Thanks for helping with Convx. This guide describes how the project is laid out, how to build it, and how issues and pull requests are handled. If something here is out of date, please send a pull request that fixes it.

By taking part you agree to follow the [Code of Conduct](../CODE_OF_CONDUCT.md).

AI agents should also follow the [Agent Contribution Guide](../AGENT_CONTRIBUTION_GUIDE.md) and use the bundled skills in [contributor/skills/](../contributor/skills/).

## Quick start

1. Fork the repo and clone your fork.
2. Install **JDK 21** and the latest stable **Android Studio**. The project uses `compileSdk 37` and `minSdk 26`; Android Studio will offer to install the SDK platforms.
3. Open the project in Android Studio and let Gradle sync.
4. Run the `app` module, or build from the command line:

   ```
   ./gradlew :app:assembleUniversalFossDebug
   ./gradlew :app:testUniversalFossDebugUnitTest
   ```

No manual code generation is needed. Gradle runs the protobuf generation and builds the native analyzer for you; the NDK is installed through the SDK Manager if you do not have it yet.

### Build flavors

| Flavor | Use |
| --- | --- |
| `foss` | No Google Play services. Use this for most development. |
| `gms` | Adds Google Cast. This is what the nightly and stable releases ship. |
| `universal` | One APK for all CPU types. The nightly and stable builds use this. |
| `arm64`, `x86_64` | Smaller single-ABI builds. |

A flavor is a pair, for example `UniversalFoss` or `UniversalGms`, plus a build type: `Debug` or `Release`. Release builds are signed with a keystore that only the maintainer has, so contributors should build debug.

## Project layout

| Path | What it is |
| --- | --- |
| `app/` | The Android app. Compose UI is in `ui/`, screens in `ui/screens/`, one ViewModel per screen in `viewmodels/`, and routes in `ui/screens/NavigationBuilder.kt`. |
| `app/.../playback/` | The Media3 player service, stream resolving, crossfade, and the Automix and DJ engine. |
| `app/.../db/` | The Room database for the library, playlists and history. |
| `app/.../ui/component/` | Shared UI: the Liquid Glass system (`GlassEffect.kt`, `backdrop/`), the floating nav bar, sheets and menus. |
| `app/.../ui/ios/` | The iOS-style theme: colors, type, and the compat layer the screens use. |
| `app/.../ui/screens/settings/` | Settings. The root, category pages and search index live here (`SettingsScreen.kt`, `SettingsHubs.kt`, `SearchableSettings.kt`). |
| `innertube/` | The unofficial YouTube Music API client. Keep it free of app code. |
| `lyrics/` | Lyrics providers and parsing. |
| `canvas/` | Animated artwork and Apple Music catalog lookups. |
| `artwork/` | Artwork loading, caching and motion. |
| `spotify/` | Spotify sign-in and playlist import. |
| `integrations/` | Discord, Last.fm, JioSaavn and music recognition. |
| `network-storage/` | SMB and WebDAV music sources. |
| `spine/` | The module host: loading and managing add-on modules. |
| `hig/` | Cupertino-style UI components. |
| `listen-together-server/` | The Listen Together server (Cloudflare Worker, TypeScript). |

Keep a change inside one module when you can. Features should talk to each other through small public APIs, not by reaching into another feature's internals.

## Finding something to work on

- Issues labelled [`good first issue`](https://github.com/cosmictaserdev-creator/Convx/labels/good%20first%20issue) or [`help wanted`](https://github.com/cosmictaserdev-creator/Convx/labels/help%20wanted) are a good place to start.
- For anything large, open an issue or ask in the [Discord](https://discord.gg/Ejeb4cmzfd) first, so we agree on the approach before you spend time on it.

## Issues and labels

Use the issue forms for bugs and feature requests. Search first, to avoid duplicates. For a crash, include your Android version, device and `adb logcat` output. For playback glitches or Listen Together sync, a screen recording helps a lot.

The maintainer triages every issue with labels:

| Label | Meaning |
| --- | --- |
| `crash` | The app crashes or loses data. |
| `hard bug` | A main feature is broken and there is no easy workaround. |
| `normal bug` | Something is broken, but there is a workaround. |
| `minor bug` | A small or cosmetic problem. |
| `regression` | It worked in an older version. |
| `performance` | Slow, laggy, or high battery or memory use. |
| `area: ...` | Where it happens: player, playback, lyrics, library, search, settings, ui, accounts, listen-together, network-storage. |
| `status: needs repro` | We can't reproduce it yet and need more detail. |
| `status: confirmed` | Reproduced and accepted. |
| `status: in progress` | Someone is working on it. |
| `status: fixed in nightly` | Fixed in the latest nightly, not yet in stable. |

Please test against the latest [nightly build](https://github.com/cosmictaserdev-creator/Convx/releases/tag/2.0.0-nightly) before reporting. Issues with no activity for 60 days are marked stale and closed a week later.

## Branches and commits

- Branch from `main`: `feature/short-description` or `fix/short-description`.
- Commit messages start with a type and a short imperative summary: `feat:`, `fix:`, `perf:`, `refactor:`, `docs:`, `chore:`, `build:`. Add a scope when it helps, for example `fix(player): stop the cover glitching on collapse`. Explain why in the body when it isn't obvious from the diff.
- Keep commits focused. Don't mix formatting changes with behaviour changes.

## Pull requests

1. Open the PR against `main`.
2. Keep the diff to one purpose. Avoid drive-by reformatting and unrelated refactors.
3. Run the change on a device or emulator before opening the PR. This is a UI-heavy app and most regressions are visual.
4. Run the unit tests (`./gradlew :app:testUniversalFossDebugUnitTest`) and add a test when you change logic.
5. Fill in the PR template. Add screenshots or a short recording for anything visible, and check light mode, dark mode and a tablet or wide window where it matters.
6. New user-facing text goes in `strings.xml` so it can be translated (translations are managed through Crowdin).
7. Do not commit secrets, signing keys, `local.properties`, or build output.

CI builds every pull request. A PR needs a green build before it is merged.

### Code style

- Kotlin with Jetpack Compose. Follow the style of the file you are editing.
- Colours, spacing and type come from the theme (`IosTheme`, `AppTheme`), not hard-coded values, so light, dark and accent colours keep working.
- Keep comments for the *why*. Don't leave commented-out code.

## Releases

- **Nightly:** every push to `main` builds a signed universal GMS APK and updates the single rolling pre-release `2.0.0-nightly`. The release notes come from `.github/release-notes/`.
- **Stable:** a tag named `v*` runs the stable release workflow.

Contributors don't need to do anything for releases. If your change should appear in the notes, mention it in the PR.

## Security

Please do not open a public issue for a vulnerability. Follow [SECURITY.md](../SECURITY.md).

## Questions

Ask in the [Discord](https://discord.gg/Ejeb4cmzfd) or in [Discussions](https://github.com/cosmictaserdev-creator/Convx/discussions).
