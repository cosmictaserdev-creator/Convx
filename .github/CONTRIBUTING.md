# Contributing to Convxy

Thanks for considering a contribution. This document is the real, current process — if something
here goes stale, please send a PR fixing it rather than adding a second guide.

## Project layout

Convxy is a single Android Studio project (Gradle multi-module). The pieces you'll actually touch:

- **`app/`** — the app itself. Compose UI lives under `app/src/main/kotlin/com/convxy/music/ui/`,
  screens under `ui/screens/`, `viewmodels/` per screen, navigation wired in
  `ui/screens/NavigationBuilder.kt`.
- **`ui/component/GlassEffect.kt`** + **`ui/component/backdrop/`** — the Liquid Glass system (a
  vendored, source-included copy of [Kyant0/backdrop](https://github.com/Kyant0/backdrop)).
  `Modifier.liquidGlass(...)` is the entry point most UI code needs; see the doc comment on
  `GlassCircleButton` for a worked example of a glass surface sampling a local backdrop.
- **`innertube/`** — the unofficial YouTube Music (InnerTube) API client, kept independent of the
  app module.
- Other top-level modules (`kugou`, `lrclib`, `kizzy`, `lastfm`, `betterlyrics`, `simpmusic`,
  `youlyplus`, `shazamkit`, `spotify`, `canvas`, `spine`, ...) are individually-scoped integrations —
  a lyrics/Discord-RPC/canvas provider, etc. Keep changes to one of these scoped to that module.

## Building locally

- **JDK 21**, latest stable **Android Studio**.
- `compileSdk 37`, `minSdk 26` — install those platforms via the SDK Manager if Android Studio
  prompts you to.
- Clone and open the project in Android Studio, let Gradle sync, then run/debug the `app` module as
  normal (`./gradlew :app:assembleUniversalFossDebug` from the CLI works too). No manual native
  toolchain, submodules, or codegen scripts are required — Gradle handles everything, including the
  protobuf codegen used by a couple of modules.

## Branches & commits

- Branch names: `feature/short-description`, `fix/short-description`.
- Commit messages: a short type prefix (`feat:`, `fix:`, `refactor:`, `docs:`) followed by an
  imperative summary — see `git log` for the house style. Explain *why* in the body when it isn't
  obvious from the diff.

## Pull requests

1. Fork the repo and branch off `main`.
2. Keep the diff scoped to the PR's stated purpose — avoid drive-by reformatting or unrelated
   refactors, they make review harder.
3. Actually run the change on a device/emulator before opening the PR; this is a UI-heavy app and
   most regressions here are visual, not compile-time.
4. Describe *what* changed and *why* in the PR description; screenshots/recordings are expected for
   anything UI-visible.
5. If the change adds a user-facing feature, it needs a string in
   `app/src/main/res/values/vivi_strings.xml` (never a hard-coded label), a settings row when it is
   toggleable, and a line in the relevant doc. Anything with non-obvious internals — fitting,
   caching, provider quirks — gets a note in `docs/`, the way `AMBIENT_MODE.md`,
   `FULL_VIDEO_PLAYBACK.md` and `MULTI_SINGER_LYRICS.md` document their features.

## Reporting bugs

Open an [issue](https://github.com/MeYashverma/Convxy/issues) with repro steps, your Android
version/device, and `adb logcat` output if it's a crash. For anything real-time (playback glitches,
Listen Together sync), a screen recording helps a lot.

## Questions

Ask in the [Discord](https://discord.gg/GquSGfs2u) before starting anything large — happy to point
you at the right file instead of you reverse-engineering it.
