# Convxy

A free, open-source music player for Android that streams the YouTube Music catalogue and plain
YouTube, and puts Apple Music-style synced lyrics over a UI built on real backdrop blur. Library,
settings, downloads and caches stay on the device — the app contains no analytics SDK and makes no
background requests other than the ones needed to fetch music, artwork and app updates.

Website: https://meyashverma.github.io/Convxy/ ·
Releases: https://github.com/MeYashverma/Convxy/releases ·
Discord: https://discord.gg/GquSGfs2u

<p>
  <img src="docs/screenshots/Screenshot_20260802_205400_Convx-portrait.png" width="230" alt="Home screen with the frosted nav bar">
  <img src="docs/screenshots/lyrics%20screen-portrait.png" width="230" alt="Synced lyrics with the active-line glow">
  <img src="docs/screenshots/mobile%20%287%29-portrait.png" width="230" alt="Multi-singer lyrics, one colour per vocalist">
  <img src="docs/screenshots/tabview%20%281%29-landscape.png" width="330" alt="Tablet layout with a sidebar">
</p>

More screenshots, including the TV and Ambient layouts, are in [docs/screenshots](docs/screenshots).

## What it does

**Playing music.** YouTube Music, JioSaavn and local files in one library: playlists,
subscriptions, search, queues (persistent and offline-safe), background playback, lock-screen and
notification controls, audio focus, and a sleep timer that can stop at the end of the current
track instead of a fixed number of minutes.

**Liquid Glass.** A real glass system, not translucent Material tiles: switches, sliders,
buttons, pills, nav bar, mini player, player controls, volume sliders and long-press menus all
sample the actual pixels behind them and blur and refract them — a menu opened over the player
bends the album art through its body. Blur radius, vibrancy, lens depth, refraction, noise, tint,
shape, iOS-style rubber-band overscroll and spring transitions are all adjustable in Settings, and
dynamic colour is derived from the current artwork. The engine, its scoping rules and the full
component list are documented in [docs/LIQUID_GLASS.md](docs/LIQUID_GLASS.md).

**Lyrics.** Word-by-word karaoke with a per-word fill, several animation styles, progressive
blur, translation and romanisation, and multi-singer synchronisation — each vocalist gets a
colour and a name badge, and shared vocals are treated as a chorus instead of being pinned to the
lead. Provider quirks and the TTML parsing are documented in
[docs/MULTI_SINGER_LYRICS.md](docs/MULTI_SINGER_LYRICS.md).

**Video.** The player can watch a song's actual YouTube video instead of playing audio only, and
Convxy has its own native YouTube section: home feed, search with filters, channels, playlists,
Shorts, a watch screen with quality caps up to 1080p, and local watch history. Related videos join
the same queue as your music. See [docs/FULL_VIDEO_PLAYBACK.md](docs/FULL_VIDEO_PLAYBACK.md).

**Ambient Mode.** A sparse landscape display for a TV or a stand: artwork and synced lyrics side by
side, an accent glow or the track's own animated canvas behind them, a progress ring you can scrub
from the screen edge, and a back button that gets out of the way. Portrait canvases can be pinned to
one side of the frame at their full size with an adjustable gradient — the settings, defaults and
fit rules are in [docs/AMBIENT_MODE.md](docs/AMBIENT_MODE.md).

**Sound and files.** Built-in equalizer, optional high-bitrate and lossless sources where the
provider has them, offline downloads with a per-source quality setting, a Storage screen that breaks
the cache down and clears each part separately, crossfade with tempo-matched Auto-DJ mixing, and a
waveform scrub bar in the mini player.

**Around the app.** Android Auto (browse and search, with configurable launch destination and
resume behaviour), a tablet sidebar layout, material icon and font choices, an in-app updater that
reads GitHub Releases, opt-in Discord Rich Presence over Discord's own gateway, opt-in Last.fm,
ListenBrainz and Spotify scrobbling, and Listen Together for following one queue with friends.

## Where it came from

Convxy is a fork of [Convx](https://github.com/cosmictaserdev-creator/Convx), itself a fork of
[vivi-music](https://github.com/vivizzz007/vivi-music). The upstream projects stay useful for
reference, but this repository is the one under active development: fixes land here first.

## Architecture

A Gradle multi-module Android app, Kotlin and Jetpack Compose throughout.

- **`app/`** — the player. Media3 `ExoPlayer` in `MusicService`, playback state mirrored to the UI
  over AIDL; screens under `ui/screens/`, their viewmodels under `viewmodels/`, navigation in
  `ui/screens/NavigationBuilder.kt`, Room database in `db/`.
- **`ui/component/GlassEffect.kt`** — `Modifier.liquidGlass(...)` and friends: a shared backdrop
  layer, runtime shader blur and refraction, vibrancy. The engine is a vendored, source-included
  copy of [Kyant0/backdrop](https://github.com/Kyant0/backdrop), which is why `liquidGlass` takes a
  `Backdrop` host rather than doing a live `captureBackdrop` per frame. The glass controls
  (`GlassSlider`, `GlassSwitch`, glass buttons, menus) live beside it, and
  [docs/LIQUID_GLASS.md](docs/LIQUID_GLASS.md) covers the recording/sampling model, including why
  overlays must be handed their backdrop explicitly.
- **`innertube/`** — unofficial YouTube Music (InnerTube) client, independent of the app module.
  Several native clients are probed in parallel so one flagged client cannot break playback; the
  same module carries the WEB client used by the YouTube tab, including consent, visitor data and
  the credentials it sends when you are logged in.
- **`kugou/`, `lrclib/`, `youlyplus/`, `betterlyrics/`, `paxsenixlyrics/`, `musixmatchlyrics/`,
  `spotify/`, `simpmusic/`, `jiosaavn/`, `lastfm/`** — one module per integration: lyrics providers,
  scrobbling, and the JioSaavn catalogue. They stay independent of `:app` so one provider can be
  dropped or swapped without touching playback; translation lives in `app/.../lyrics/` instead.
- **`canvas/`, `applecanvas/`, `vivimusiccanvas/`, `artistvideo/`** — the animated-canvas
  providers: Apple Music and Tidal canvases, Echo Music/ViviMusic canvases, and artist videos.
  They feed both the player canvas and the Ambient Mode background.
- **`shazamkit/`** — song recognition. **`spine/`** — the 8spine module loader used to pull extra
  audio sources on top of the built-in clients.
- **`kizzy/`** — the Discord Rich Presence client vendored from
  [dead8309/Kizzy](https://github.com/dead8309/Kizzy) (`com.my.kizzy`). It authenticates against
  `wss://gateway.discord.gg` with your own token, so presence updates need neither the Discord app
  running nor a bot or relay of ours.
- **`listen-together-server/`** — the Listen Together room server (TypeScript on Cloudflare Workers,
  one Durable Object per room; deployed separately, not part of the app build).
- **`scripts/`** — repo maintenance helpers (icon generation, SVG→drawable conversion, CI report).
- The rest of the plumbing is packages inside `app/`: `playback/` (the service, queue, crossfade and
  sleep timer), `db/` (Room), `lyrics/` (fetching, parsing, translation), `listentogether/`,
  `eq/`, `recognition/`, `widget/`, and `vivimusic/` for the updater and the ported player style.

Feature notes for the parts with interesting internals are in [docs/](docs):
[Ambient Mode](docs/AMBIENT_MODE.md), [multi-singer lyrics](docs/MULTI_SINGER_LYRICS.md) and
[full video playback](docs/FULL_VIDEO_PLAYBACK.md).

Build variants: a `foss`/`gms` axis and an `abi` axis (`universal`, `arm64`, `armeabi`, `x86`,
`x86_64`), in `debug` and `optimized`. Only `gms` adds Google Cast — Android Auto works in both,
because it only needs the media-browser service — and neither variant asks for a Google account.
Requires JDK 21, `compileSdk 37`, `minSdk 26` (Android 8.0).

## Installing

Grab the APK from the [latest release](https://github.com/MeYashverma/Convxy/releases/latest) and
install it; Android will ask you to allow installs from this source once. CI builds from `main` are
in the [Actions runs](https://github.com/MeYashverma/Convxy/actions/workflows/build.yml) if you
want something newer than the last tag.

The release asset is one signed universal APK built as `universalGms`, meaning Google Cast is
compiled into it. Android Auto works in either flavour — the media-browser service is in the shared
manifest — and nothing here needs a Google account. Releases and nightly CI only publish the GMS
flavour; for a build with no Play Services dependency at all, run
`./gradlew :app:assembleUniversalFossRelease` yourself.

To build it yourself: clone the repo, open it in Android Studio, let Gradle sync, run the `app`
module. No submodules, native toolchain or codegen step to set up by hand; Gradle handles
everything, including the protobuf codegen a couple of modules need.

### Android Auto

Android Auto only shows media apps it knows about, so a sideloaded player has to be allowed once:

1. Open **Android Auto** on the phone → the hamburger menu → **Settings**.
2. Tap the **version number** at the bottom several times to unlock developer settings.
3. In the three-dot menu, open **Developer settings** and switch on **Unknown sources**.
4. Restart Android Auto (or unplug and replug the car), then in the Auto media launcher choose
   **Explore by voice → Media apps → More** and pick *Convxy*.
5. If it still doesn't appear, install the latest **Android Auto for Machine** APK from
   [androidxr.nl](https://androidxr.nl) and repeat.

Every non-Google media app in Auto needs step 3; the in-app help mirrors these same steps.

### Listen Together

**Profile → Listen Together** opens a room and gives you a code to share. Everyone who joins
follows the host's queue with the position kept in sync over a WebSocket room. The rooms live on a
small Cloudflare Worker (`listen-together-server/` in this repo, one Durable Object per room); the
app ships with a default server and **Settings → Listen Together** lets you point at your own
deployment instead. Playing alone never touches it.

## Privacy

- The app does not collect analytics, and no analytics or ad SDK is compiled in.
- Your library, liked songs, playlists, history, settings, downloads and caches live in a local
  Room database and the app's private storage.
- Network requests are made only to the services you use — YouTube Music's API, JioSaavn, lyrics
  providers, artwork CDNs — plus GitHub for update checks.
- Discord Rich Presence and scrobbling (Last.fm, ListenBrainz, Spotify) are off until you turn
  them on and log in yourself. Presence talks to Discord's gateway directly; scrobbles go to the
  service you connected. Nothing is sent to us, because there is nowhere to send it.

## Disclaimer

- This project is not affiliated with, endorsed by, or funded by YouTube or Google LLC.
- It uses unofficial APIs and is not available on the Play Store.
- No track, album or video is hosted, uploaded or distributed by this repository.
- You are responsible for how you use it and for respecting the terms of service of the services
  it talks to.

## Contributing

Read [.github/CONTRIBUTING.md](.github/CONTRIBUTING.md) — it covers module layout, branch and
commit conventions, and what a pull request should include. Bug reports go in
[issues](https://github.com/MeYashverma/Convxy/issues); anything that isn't a bug yet is better on
[Discord](https://discord.gg/GquSGfs2u) or in [discussions](https://github.com/MeYashverma/Convxy/discussions).
PRs that touch UI should come with a screenshot or a recording, since most regressions here are
visual rather than compile-time.

## License

Convxy is licensed under the [GNU General Public License v3.0](LICENSE), the licence the upstream
projects chose as well. Use it, change it, ship it — anything built from it has to stay under the
same terms and come with its source.

## Credits

- [Aryan (CosmicTaser)](https://github.com/cosmictaserdev-creator) — [Convx](https://github.com/cosmictaserdev-creator/Convx)
  ([cosmictaser.de5.net](https://cosmictaser.de5.net)), the upstream fork this project continues.
- [Vividh P Ashokan](https://github.com/vivizzz007) — [vivi-music](https://github.com/vivizzz007/vivi-music).
- [Kyant0/backdrop](https://github.com/Kyant0/backdrop) — the backdrop blur and refraction engine
  vendored into `app/src/main/kotlin/com/convxy/music/ui/component/backdrop/`.
- [Better Lyrics](https://github.com/better-lyrics/better-lyrics),
  [YouLyPlus](https://github.com/ibratabian17/YouLyPlus) and
  [SimpMusic](https://github.com/maxrave-dev/SimpMusic) — lyrics sources and styling ideas.
- [dead8309/Kizzy](https://github.com/dead8309/Kizzy) — the Discord gateway client in `kizzy/`.
- [Monochrome](https://github.com/monochrome-music/monochrome) — the animated visualiser canvas,
  inherited through vivi-music.
- The Apple Music Player V17 full-screen style (**Settings → Player theme**) is ported from
  vivi-music's Apple Music UI, GPL-3.0.
