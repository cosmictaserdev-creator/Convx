# Full YouTube Video Playback

Watch a song's actual YouTube video inside the player instead of audio-only.

## How it works

1. **Toggle** — the video button in the player's action row (next to
   download/like) flips the `WatchVideoKey` preference. Default: off.
2. **Stream resolution** — `MusicService` watches the preference. On change
   it clears the current song's cached stream (both cache namespaces) and
   re-resolves the media item at the same position, passing
   `videoMode = true` to `YTPlayerUtils.playerResponseForPlayback`.
3. **Muxed format selection** — in video mode the resolver skips the
   audio-only intercepts (8spine modules, TIDAL, JioSaavn) and picks the
   best **progressive (muxed audio+video) format** via
   `selectMuxedVideoFormat` (`utils/VideoFormatSelector.kt`): ranked by
   resolution, then fps, then bitrate, capped at 1080p. Muxed keeps playback
   on a single URL so the existing cache/data-source pipeline and ExoPlayer
   setup work unchanged. If the current client exposes no muxed format, the
   resolver falls back to the normal audio format — the toggle stays on but
   nothing breaks; the next song tries video again.
4. **Cache namespacing** — video streams cache under `<mediaId>#video` so
   muxed bytes never collide with the audio cache (same pattern as the
   existing `#flac` namespace).
5. **Rendering** — `VideoPlaybackSurface` (ui/player) attaches a
   `TextureView` to the shared service player (same pattern as
   `CanvasArtworkPlayer`) inside an aspect-ratio-fitting frame. The
   `Thumbnail` composable swaps the artwork carousel for the video surface
   when the toggle is on **and** the persisted format for the current song
   is a video mime type — so the swap only happens once a video stream is
   actually playing, never for a song that silently fell back to audio.

## Notes

- Downloads are unaffected: the download pipeline never passes `videoMode`,
  offline copies stay audio-only.
- Toggling mid-song keeps position (same reload mechanics as an audio
  quality change).
- Unit tests: `VideoFormatSelectorTest` pins the muxed ranking rules.

## The native YouTube section (v1.6.0)

Beyond the music-side video toggle, Convxy now has a **full native YouTube
browsing experience** (regular youtube.com content, not YT Music):

- **Browse** — `YouTubeWeb` (innertube module) is a WEB InnerTube client for
  the youtube tab: home feed (`FEwhat_to_watch`), search + suggestions +
  filters, watch page + related videos, channels (tabs), playlists, Shorts.
  It handles consent (SOCS), session visitor data, and — when logged in —
  sends full account credentials (cookie + SAPISIDHASH) like youtube.com
  itself; degraded bot-walled responses fall back to an anonymous
  ANDROID_VR client.
- **Watch screen** — `ui/screens/youtube/YouTubeWatchScreen.kt`: renders
  through the newer `VideoSurface` (a media3 `PlayerView` — SurfaceView on
  API 34+, TextureView below), pinned portrait player + scrolling
  metadata/related, immersive fullscreen, double-tap seek, speed, quality
  cap selector, audio-only toggle. The related videos ARE the ExoPlayer
  queue (same mini player / notification as music).
- **Stream resolution** — same `YTPlayerUtils` pipeline in video mode: when
  the main client serves no muxed format, the muxed-capable native clients
  are probed in parallel and the fallback chain enters at the first that
  can serve video. Every step is written to the shareable playback log.
- **Local data** — watch history / continue-watching / saved videos in Room
  (YouTube tables, migration 37→38).
