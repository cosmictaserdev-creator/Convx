# Canvas, Backdrop and Player Motion Overhaul

Plan and session handoff. Written for the engineer (or agent) who executes it. Follow
[`AGENT_CONTRIBUTION_GUIDE.md`](../../AGENT_CONTRIBUTION_GUIDE.md) for every change; section 8 lists the rules that matter most here.

## Implementation status · 2026-11-19

The work is on `port/kmp-work-to-android`, based on Android-only integration commit
`de452e6f`. The review destination is [draft PR #196](https://github.com/cosmictaserdev-creator/Convx/pull/196).
Build PR, Universal GMS Debug and the Unit Tests job passed on the earlier lyrics
and NAS commit `6846a269`. Those results apply to that revision.

| Area | Implemented | Verification remaining |
|---|---|---|
| Android port | Ported the playback domain modules, album disc index, Fisheye renderer, lyric stickers, animated mesh and first-run onboarding. Connected NAS tag reading, batch imports and embedded art to the Android scan and browser flows. Connected the web-login sheet to the existing sign-in flow. | Exercise NAS rescans, tag timeouts, onboarding and account handoff. |
| Canvas identity | One playback presentation serves V2 and Classic. Every media transition advances a generation, including a return to the same song ID. Provider results, first-frame callbacks, texture binding and palette samples check that generation. Cover and gradient caches are bounded. | Exercise provider responses arriving out of order and rapid skips, including a repeated media ID. |
| Video | Shared arbitration for the foreground video decoders, tuned buffering and startup delay, loop-specific bitrate and frame-rate caps, drift correction, 16:9 inline layout, conservative letterbox zoom, and retry with URL refresh. Fullscreen uses its existing transition and the shared controller. Removed the fullscreen polling log. | Measure physical decoder handoff. Verify buffering, fullscreen, PiP, stretch and retry on device. |
| Extended backdrop | Wired a decoder-frame callback to the shared backdrop. Apple uses a ten-frame ring with direction-based lag. Other canvases use single-frame extension and official video uses reflection. Added CPU, API 33+ AGSL and colour-only paths with adaptive tier choice. Live hero blur and mesh work are suppressed when the extended path replaces them. | Compare the AGSL candidate and CPU path on both test phones. Measure latency, draw cost, memory and thermal recovery. |
| Gestures and motion | Both styles use cover-axis arbitration and queue swipe. Horizontal drags cancel on return to the origin. A locked vertical drag follows the finger through reversals and uses a shorter release threshold for dismissal. The sheet drives the artwork path, mini title and transport, and bottom nav slide and fade. The dock closes with a critically damped spring. | Verify the full gesture matrix, including queue behavior, reversals, wide layouts and device timing. |
| Lyrics | Wired the opt-in timed preview, Fisheye mode, Cupertino line picker, lyrics edge fades and player header band. The snapshot versions of `LyricsV2.kt`, `MetroLyrics.kt` and `PlayerCoverStyle.kt` have no semantic diff to port. | Verify lyric timing, share selection, scroll, RTL, reduced-motion and small-screen layout. |
| Preferences and prefetch | Added Auto, Ask and Off, Extended video and Colour only, brief feedback with Undo, and per-song menu actions in both styles. Room migration 40 → 41 persists choices, with generated schema and an instrumentation case. Prefetch is limited to three items and skips authenticated providers. Public MP4 warming uses a separate 6 MiB cache namespace on validated unmetered networks. | Run the Room proof and check queue changes, offline behavior, metered networks and saver transitions. |
| Provider security | Enforced HTTPS at the media boundary, restricted headers and redirects, bounded provider responses, and removed request details from canvas and video error logs. The existing Android `:spotify` module remains in use for its native engine and existing credential and auth flows. | Smoke-test provider coverage and the authentication path. |

The final local `:app:compileUniversalFossDebugKotlin` check passed. The shared host owns playback presentation, backdrop and common interaction layers. Existing styles
still supply their artwork, transport and page layouts. The device measurements, screenshot and
instrumented interaction checks, and full CI verification were deferred at the user's request. No 120 Hz
jank rate, 50 ms latency bound, GPU or main-thread budget, or 20-minute thermal result has been established.
The Galaxy M34's installed APK matched the baseline CI artifact, and device measurements were unavailable.
The captions alignment feature is optional and remains deferred.

The sections below preserve the original scope, handoff and acceptance criteria.

## 1. Context

- Convx is an Android Compose music player with a Liquid Glass look. It must run at 120 fps where the
  hardware allows, degrade gracefully on low-end phones, and support Android 12 and up. `minSdk` is 26.
- Work happens on a branch off `integrate/night-debug-2026-11-18`, the newest line that has **no Kotlin
  Multiplatform**. KMP is being removed on purpose; nothing in this plan may reintroduce it.
- The earlier KMP work lives on `origin/snapshot/pending-work-2026-11-18`. It is preserved there and is
  mined for unique work (section 3). It is not merged wholesale.
- Findings from the first review of the player code (all from reading; nothing profiled yet):
  - Six `ExoPlayer.Builder` sites with no arbiter; background hero video can decode up to 4K at the highest
    bitrate; the shared canvas controller caps *YouTube video* at 30 fps and 2.5 Mbps.
  - Sync hard-seeks at 500 ms drift; retry reuses a possibly expired stream URL; a 1 s debug log loop runs
    in fullscreen video.
  - The live blur paths (hero foot blur, `LIVE_MESH` full-screen blur of rotating layers) are the likely
    heat sources.
  - `PlayerBackdrop` (CPU stack-expand) is written but unwired and does not compile against the controller.
  - Stale canvas after a song change: suspected causes are a fade-out awaited before ids are reassigned, a
    player bound once in an `AndroidView` factory, and `remember{}` state surviving track changes.
    These are hypotheses until reproduced by a failing test.

## 2. Prompt for the executing engineer

> You are a senior Android engineer who owns the player experience at a streaming-music company. Make
> the Convx player feel instant and calm on every supported phone. Target 120 fps; where a device cannot
> hold it, remove work (recomposition, allocation, overdraw, GPU passes) instead of dropping visual
> quality. No UI glitch, no logic bug, no behaviour bug: before declaring anything done, list its edge
> cases and test them. Use Compose with draw-phase reads for per-frame values, Hilt for injection, the
> Convx/Cupertino components for UI, and string resources for all text. Do not use Material components for
> new UI. Do not copy code, assets or names from other projects; describe behaviour in Convx's own terms.
> Measure before and after on a low-end and a 120 Hz device and keep the numbers in the PR.

## 3. Original port-session handoff

Base: `port/kmp-work-to-android` (from `integrate/night-debug-2026-11-18`). Files are copied from
`src/commonMain`/`src/commonTest` of the KMP branch to `src/main`/`src/test`. Everything is uncommitted
so the diff can be reviewed.

| Area | Module | Result |
|---|---|---|
| Release Radar, Smart Shuffle, Daily Mix, SponsorBlock, Made For You, Play History, Speed Dial, Wear protocol | `:playback-core` | Compiles, tests pass |
| Album disc index | `:catalog-core` | Compiles, tests pass (test dependencies added) |
| Fisheye lyrics, lyric stickers, held-word growth | `:playback-presentation` | Compiles, tests pass. The platform `expect` was replaced by a plain Android `fisheyeLens` (`FisheyeLens.kt`). |
| Onboarding screen | `:account-presentation` | Compiles. Compose plugin and dependencies added; logo font falls back to the wordmark font unless the app provides `LocalLogoFont`. |
| Animated mesh backdrop | `:hig` | Compiles. Ported into tracked `core/hig`; it existed only in an ignored local folder and a backup branch. |
| Network import | `:network-storage-core` | Compiles. `NetworkTrack` gained optional `albumArtist`, `trackNumber`, `discNumber`, `year`, `durationSeconds`; `NetworkEmbeddedArtworkUri` ported from `origin/feat/nas-and-requests-on-174`. **One test fails:** `NetworkImportTest.discFoldersBecomeDiscsOfTheParentAlbum`. |
| Lyrics lookup session | `:lyrics-data` | **Does not compile, and not because of the port.** The base line crashes the Kotlin compiler on a suspend lambda that calls the internal `lyricsGet` (first `BiniLyrics.kt`, then `LrcRed.kt`). `BiniLyrics` default lambdas were replaced by a nullable `get`; the same pattern in `LrcRed.kt` remains. |

Repository fixes made while porting:
- `.gitignore`: `/core` also ignored the tracked `core/` directory; `!/core/` re-includes it.
  `!docs/plans/*.md` lets this plan be tracked (markdown is otherwise local-only here).
- The first copy created directories with literal backslashes; they were removed and the copy redone.

Held back on purpose:
- **Duplicates of types that already exist in `app/`:** `AudioQuality`, `LyricsAnimationStyle`, `HomeSection`
  (and `HomeSectionPlan`, `AudioQualityPill` and their tests). Porting them would create parallel
  implementations. Migrate `app/` onto them later, or drop them.
- **`catalog-presentation` extraction:** album screen and state, charts state, discovery and history
  shelves, speed-dial shelf, hero and spotlight cards, filter row. They depend on `HomeSectionOrder`, new
  catalog model fields and chart-page models that exist only in the KMP refactor. Needs its own job.
- **`AlbumScreen`, `SpotlightCard`, `HomeHeroCard`, `PlaybackPosition`:** the app already has versions that
  differ by 100 to 1100 lines. Needs a three-way merge.
- **`InnertubeReleaseArtistSource`:** its module (`feature/playback/data`) is not registered in
  `settings.gradle.kts`. Handle with the Release Radar reconciliation against `snapshot/pending-release-radar`.
- **`clients/wearos`, `clients/kmp/desktopApp`:** not ported (Wear OS deferred by decision; desktop is KMP).

Still to port from the KMP branch (app layer; each needs an `:app` compile):
- Canvas: `CanvasResolver`, `CanvasDecisionEntity` (needs a Room migration), `ArtistVideoCanvasProvider`
  changes, `canvas/` provider rewrites (Apple, Tidal, Apple artist background) and their tests.
- Player: `PlayerBackdrop`, `PlayerHandle`, `SpotifyCanvasStage`, `MusicVideoCanvas`, `PlayerInlineLyrics`,
  `PlayerLyricsPreview`, `PlayerCoverStyle` rewrite.
- Lyrics: app-side `FisheyeLyrics`, `LyricsLinePicker`, `LyricsV2`/`MetroLyrics` changes.
- Network and onboarding: `NetworkTagReader`, `NetworkLibraryAdapters`, `NetworkEmbeddedArt*`,
  `AndroidOnboardingHost`, `WebLoginSheet`.
- Spotify: `feature/spotify/data` (check whether it duplicates the existing `:spotify` module first).
Method: `git diff <merge-base> <kmp-branch> -- <paths> | git apply --3way`, then compile.

Open licensing flags: the fonts `bd_console_remix_italic.otf` and the SF Pro files have no license note in
the repo; the design reference media in `docs/` (screenshots, a forum video) must not be committed.

## 4. Requirements

### 4.1 Extended video backdrop

Goal: the video appears to continue behind the player, as if the picture were stretched into the blur.

Geometry (stack-expand): take the lower third of the frame next to the seam, reduce it to very low
resolution, and stack copies beneath it, packed tightly. Each copy is slightly larger than the one above and
offset slightly down, so the stack widens. Blur the stack heavily; no object is recognisable, only moving
colour. Anchors follow the layout: top (square cover), hero, left half (landscape), centre (tablet).

Per source:
| Source | Backdrop |
|---|---|
| Apple Music canvas | Full stack-expand with the temporal rule below |
| Spotify and YT Music canvas | Lighter single pass: frame reduced to very low resolution in the shader and extended; no frame history, no direction estimate |
| YouTube video | Reflection backdrop: mirrored under the frame, blurred at reduced resolution |

Temporal rule (Apple canvas):
- Colour change travelling **top to bottom**: the lower layers show the same change **later by the same time
  difference**, so it reads as moving downward.
- Change travelling **left to right or right to left**: all layers update **together**, so it sweeps sideways.
- Keep a ring of the last 8 to 12 tiny frames. Estimate flow direction cheaply by comparing row and column
  brightness profiles between frames (no ML). Layer *k* uses frame lag `k x delta` for vertical flow and 0
  for horizontal; blend by confidence for mixed motion.
- The layer at the seam always uses the live frame so the seam matches the video exactly.

Latency (hard acceptance test): decode to screen at most about one video frame plus one display frame
(about 50 ms). Capture on the decoder's frame callback, not on a timer. Ease only on cuts and track changes,
never during continuous motion.

Detection: every ~250 ms analyse a 16 px frame for dominant colour and a saliency centroid (high contrast or
saturation). The centroid biases the stack offset; the dominant colour sets the scrim and text contrast.
No ML model.

Tiers, chosen automatically, overridable in settings:
- **A, API 33+:** AGSL shader on a 1/8-scale layer (or a ring of tiny layers); first spike to choose between
  layer recording and bitmap readback by measuring latency and cost.
- **B, API 26 to 32 and low-end:** CPU stack bitmap (the `PlayerBackdrop` design), same ring.
- **C:** the existing colour gradient. Selected by the setting, reduced motion, thermal throttling or battery saver.
Settings: "Extended video" or "Colour only". Thermal status (API 29+), battery saver and frame-time drops step
the tier down; recovery is slow to avoid flapping. Zero allocations in draw; no work while collapsed, hidden or
backgrounded.

### 4.2 Canvas identity and behaviour

Rule: a canvas, frame, colour sample or backdrop result is shown only if its `(mediaId, epoch)` equals the
player's current one. `epoch` increases on every media transition; async results carry it and are dropped on mismatch.
- Per-song states: `Thumbnail`, `Resolving`, `Found`, `Showing`, `Disabled`.
- Fallback chain: canvas, then this song's thumbnail, then this song's cached palette, then a neutral surface.
  Never the previous song's picture, even if the new thumbnail or canvas is still being found.
- A track change cancels in-flight loads at once; do not await a fade before reassigning ids.
- Applies to Spotify, YouTube and Apple canvases alike. Write failing JVM tests first: rapid skips,
  out-of-order fetches, three providers.
- **Default is automatic.** When a canvas is found it is applied, with a brief animated pill ("Canvas on" with
  Undo) so the user can return to the cover. A global Auto / Ask / Off setting sits above it.
- Player menu: a per-song Canvas on/off entry (Cupertino menu components), stored per song (Room migration).
- Idle prefetch: the next up to 3 queue items. Metadata only by default; warm the first seconds of video only
  on unmetered networks with battery saver and data saver off. Capped in bytes, cancelled on queue change.
  "Idle" means no gesture for about 2 s, or the screen off while playing.

### 4.3 Video

- The YouTube canvas also renders in the player's cover area, not only as a backdrop (confirmed by the owner).
- Inline video is 16:9 with side padding; fullscreen opens and closes with animation.
- Pipeline: quality chosen by use (background 720 short side; the 30 fps and 2.5 Mbps cap applies to canvas loops
  only); tuned buffering (about 12 to 40 s, short start threshold); sync by nudging speed for small drift and
  seeking only for large gaps, following audio buffering; a settle delay of about 700 ms before a decoder
  starts; stall detection with a loader only after the first frame; re-resolve the stream URL on retry;
  letterbox detection and zoom (pure, unit-tested); a `ui/player/video/` package split; one decoder owner
  replacing the six `ExoPlayer.Builder` sites. Optional last: align song lyrics to a separately found video's captions.

### 4.4 Motion

- One `PlayerMotionSpec` holds springs, durations and easing used by the nav bar, mini to full player and the
  thumbnail, identically in every player style and every canvas state.
- All of it interruptible, roughly 15 to 20 percent faster than today, draw-phase reads only.
- Measure the existing feel by frame-stepping a recording and write the result down as Convx numbers.
- Respect the system animator scale and the reduce-motion setting.

### 4.5 Lyrics, gestures, consistency

- Lyrics open: the bottom lyrics and the top band (where the cover shrinks upward) use the existing progressive
  blur; lines get progressively clearer toward the active line. API 33+ uses the AGSL path; below that, a gradient scrim.
- Swipe up opens the queue with the same sheet motion in every style, through one gesture owner
  (`Idle`, `Dragging(axis)`, `Settling`) with axis lock after touch slop. A drag that returns to its origin
  never fires its action.
- **Bug to fix:** a swipe in the middle of the cover opens the menu. Not yet located in code; reproduce first with
  gesture logging. Suspect overlapping pointer handlers with no slop or axis arbitration.
- Architecture: a `PlayerHost` owns transition progress, gestures, media state and canvas state, the backdrop
  layer and the edge blur. A `PlayerStyle` supplies only slots and geometry (cover, info, transport, actions;
  rects for mini, full and lyrics header). New styles then inherit every behaviour. Migrate incrementally:
  V2 first, then Classic. No big-bang rewrite.

## 5. Phases

| # | Phase | Output |
|---|---|---|
| 0 | Finish the port (section 3), baseline traces, reproduce the menu and stale-canvas bugs | Compiling base, failing tests, numbers |
| 1 | Canvas identity protocol and its fixes | State machine with unit tests |
| 2 | Video pipeline | Decoder owner, caps, sync, link refresh, letterbox |
| 3 | Backdrop tiers B, C, then A (spike first) | Wired, compiled backdrop; settings option |
| 4 | `PlayerHost`, motion spec, gestures | Unified morph and queue swipe |
| 5 | Edge blur and lyrics | Shared blur, lyrics refit |
| 6 | Found pill, menu toggle, prefetch | Settings, strings, persistence |
| 7 | Lyrics-to-video (optional) | Caption aligner |

## 6. Performance budget and verification

- Backdrop: under 0.5 ms GPU for the band pass; under 1 ms average main-thread for tier B; zero draw allocations.
- Zero recompositions of the player during a morph (Layout Inspector counts).
- Jank under 2 percent on a 120 Hz phone and a 4 GB Android 12 phone (Macrobenchmark); thermal and battery logs
  over a 20-minute video session.
- Unit tests: state machine, letterbox, sync maths, tier policy, flow-direction estimate.
- Instrumented tests: gesture arbitration, rapid skip. Screenshot tests per style, light and dark, with canvas
  found, on and off.
- **All builds and tests run in GitHub Actions.** Do not rely on a local build; push the branch and read the
  workflow results. Report only what CI actually ran and passed.
- Existing workflows: `build_pr.yml` (pull requests: `assembleUniversalFossDebug`, lint, and unit tests for
  `:network-storage-core`, `:network-storage-transports`, `:network-storage-presentation` only),
  `build.yml` (CI and debug build), `android-room-migration-proof.yml` (Room migrations on pull requests),
  `nightly.yml` and `release.yml` (release builds).
- **CI gaps to close in phase 0.** `build_pr.yml` does not run unit tests for `:playback-core`, `:catalog-core`,
  `:playback-presentation`, `:account-presentation`, `:lyrics-data`, `:hig` or `:app`, so the ported and new tests
  would never run. Add those test tasks to `build_pr.yml` (keep `--no-configuration-cache --console=plain`), plus
  `:app:testUniversalFossDebugUnitTest` once the app-layer port lands.
- The per-song canvas decision (Room entity) must pass `android-room-migration-proof.yml`; commit the new schema
  JSON and a migration with it.
- Performance numbers (frame timing, thermal) cannot come from CI. Collect them on a low-end and a 120 Hz device,
  and attach them to the pull request.

## 7. Security and privacy

- HTTPS only for canvas and video URLs; block cleartext. Origin and user-agent headers only for known media hosts.
- Timeouts and size caps on every fetch and prefetch. No URLs, tokens or cookies in logs (remove the fullscreen
  debug log loop).
- The shader is a static string with bounded uniforms. Prefetch sends no personal data.
- Do not add secrets, personal data or tracking. Respect upstream service terms.

## 8. Contribution rules that apply

- Keep the diff focused; preserve unrelated changes; no drive-by reformatting.
- Direct, readable Kotlin; small functions; reuse existing abstractions; no duplicate state or parallel
  implementations; minimal comments that explain why.
- Hilt for injection; do not introduce Koin (some ported modules still reference it; leave them alone).
- Preserve coroutine cancellation; no unmanaged coroutines from composables.
- Cupertino and Convx components, theme values, user-facing text in string resources, accessibility labels,
  large font scale, RTL, tablets and landscape.
- Motion follows `contributor/skills/convx-android-motion`: purposeful, interruptible, per-frame work in the draw
  phase, animator scale respected.
- Do not name or copy another application, its code, artwork or assets.
- Edge cases for every flow: rapid taps, cancellation, offline, process death, rotation, empty and stale data.
- Before finishing: review the diff for stray files and secrets, then state exactly which builds and tests ran.

## 9. Decisions made by the owner

1. **Land the no-KMP line on `origin/main`: yes, by a separate agent in a new session.** This plan does not
   do that merge. Handoff for that agent: merge `integrate/night-debug-2026-11-18` (which contains
   `refactor/drop-kmp`) into `origin/main` through a pull request; resolve conflicts without touching player or
   canvas work; keep `.reference/`, `docs/mac os refrence` and `docs/design` media out of the commit; confirm
   `build_pr.yml` is green. The overhaul branch rebases onto `origin/main` afterwards.
2. **Spotify module: use whichever is best.** Compare the existing `:spotify` module with `feature/spotify/data`
   (from the KMP branch) and keep one. Criteria: it compiles in CI under the pinned Kotlin version; auth and token
   handling is secure (no tokens in logs, credentials in the existing credential store); canvas lookup works;
   tests pass; it does not duplicate code. Record the choice and the reason in the pull request.
3. **Three-way merges of the held files** (`AlbumScreen`, `SpotlightCard`, `HomeHeroCard`, `PlaybackPosition`,
   plus the held type duplicates `AudioQuality`, `LyricsAnimationStyle`, `HomeSection`): owned by another agent, not
   this plan.
4. **YouTube canvas also renders in the player's cover area, not only as a backdrop: yes.** Reflected in 4.3.
5. **Builds run in GitHub Actions.** Reflected in section 6.

Open items that remain: none blocking. The owner of items 1 and 3 should keep this file's section 3 as the
reference for what was held back and why.

## 10. Original next steps and notes after the port session

These were the next steps at the time of the handoff:
1. **`:lyrics-data`:** fix the remaining suspend-lambda compiler crash in `LrcRed.kt` the same way as
   `BiniLyrics.kt` (a nullable `get` that calls `lyricsGet` directly, no default suspend lambda). The base line
   breaks this module on its own, so the fix belongs on the base too. Then build and test the module.
2. **`NetworkImportTest.discFoldersBecomeDiscsOfTheParentAlbum`:** find the cause. It probably relies on NAS work
   that lives on `origin/feat/nas-and-requests-on-174` and was not pulled in.
3. **App-layer port**, canvas and player files first because the canvas work builds on them: `CanvasResolver`,
   `CanvasDecisionEntity` (with a Room migration), `PlayerBackdrop`, `PlayerHandle`, `SpotifyCanvasStage`,
   `MusicVideoCanvas`, `PlayerInlineLyrics`; then lyrics, network and onboarding hosts; then the canvas providers.
   Method in section 3. Compile `:app` after each group.
4. Land the result on the base branch as a reviewable diff before starting phase 1.

Notes on decisions made in this session:
- **`core/` in the project.** A shared `core/` folder (`design-system`, `hig`, `innertube`) is a normal Gradle
  layout and stays. The defect was `.gitignore` ignoring `/core`, which kept new files there out of git (this is
  how `AnimatedMesh` was left only in a local folder). The rule is narrowed with `!/core/`. The module table in
  `AGENT_CONTRIBUTION_GUIDE.md` still lists `hig/` and `innertube/` at the repository root and should be updated
  to `core/`.
- **`catalog-presentation`.** The base already holds the catalog shell, browser screen and state holders. The
  KMP version adds album screen and state, charts state, discovery and history shelves, a speed-dial shelf, hero
  and spotlight cards, a filter row, an audio-quality pill and six tests. These depend on `HomeSectionOrder`, new
  catalog model fields and chart-page models that exist only in the KMP refactor, so they are a separate job.
- **Wear OS** is deferred by decision. **Desktop (KMP)** is not ported.

## 11. Working rule: implementer and reviewer agents

Code is written by a lower-capability, lower-reasoning agent (the implementer). A larger model (the reviewer,
named by the owner as "Sol 6.2") reviews and guides every change. The model names are configuration, not part of
the design; swap them without changing the rules below.

**Implementer**
- Takes one small task at a time: one behaviour, a few files, one acceptance test. If a task needs more than that,
  it stops and asks the reviewer to split it.
- Receives, for each task, a written spec from the reviewer: the goal, the files to touch, the names and signatures
  to use, the edge cases to cover, and the test that must pass.
- Writes code and tests only inside that scope. It does not redesign, rename across modules, add dependencies,
  change build files, or touch unrelated code.
- When something is unclear, or a test fails in a way it cannot explain, it stops and reports. It does not guess,
  and it does not weaken a test to make it pass.
- Follows section 8 and the contribution guide on every change.

**Reviewer**
- Owns design decisions and writes the spec and any skeleton (types, interfaces, state machine, test cases) before
  the implementer starts on these high-risk areas: the canvas identity protocol (4.2), the backdrop shader and
  frame-lag logic (4.1), the gesture state machine (4.5), the decoder owner (4.3), threading, cancellation, and
  anything that touches security or persistence.
- Reviews each diff before the next task begins: correctness, edge cases, recomposition and allocation in draw
  paths, cancellation, accessibility, licensing and naming rules, and whether the change matches the spec.
- Sends back concrete change requests, not rewrites. It writes code itself only for small fixes or when the
  implementer is blocked twice on the same issue.
- Confirms CI is green (section 6) and records its decisions and any rejected approaches in the pull request.

**Gates**
1. Spec written by the reviewer.
2. Implementer writes the change and its tests.
3. Reviewer reviews the diff and the CI result.
4. Fixes, then re-review. Nothing moves to the next task, and nothing is merged, without the reviewer's sign-off.
5. The owner approves merges to `origin/main`.

**Stop conditions**
- Two failed review rounds on the same task: the reviewer takes over that piece or rewrites the spec.
- A change that needs a new module, a new dependency, a schema change, or a public API change: back to the
  reviewer and the owner first.
- Any finding about secrets, tokens, logging of URLs, or cleartext traffic: stop and escalate immediately.
