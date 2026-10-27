# Convx KMP Product and Engineering Plan

**Status:** Proposed execution plan  
**Workspace:** Convx KMP copy  
**Prepared:** 2026-10-23

## 1. Executive decision

Move Convx to Kotlin Multiplatform incrementally. Keep the Android application and release path working while shared modules are extracted. Do not replace the current app with a one-shot rewrite.

First-wave clients:

- **Android:** current app remains the compatibility and release baseline.
- **iPhone and iPad:** shared Convx feature and presentation code with native platform integration where it matters.
- **Desktop:** macOS, Windows, and Linux, with its own navigation and window layout rather than a phone screen stretched across a large window.
- **Browser web app:** out of scope for this migration. Do not add a browser client or Kotlin/Wasm target.

Use Kotlin Multiplatform for shared product logic and Compose Multiplatform for shared mobile screens and a common visual foundation. Desktop shares feature rules, data, tokens, and selected controls, but gets its own screen composition. Audio engines, permissions, files, background behavior, notifications, authentication handoffs, and OS integrations stay behind small interfaces with platform adapters.

Every capability gets an explicit platform-parity entry. Common code owns feature rules, models, state transitions, and tests where those are platform-neutral. Adapters may differ while the product behavior and user-visible outcome remain defined across clients.

**Toolchain is the first release blocker.** The copy uses Kotlin 2.3.10, Compose 1.10.2, AGP 9.4.1, and Gradle 9.6.0. The current Kotlin Multiplatform compatibility guide lists Kotlin 2.4.20–2.4.21 with AGP through 9.3.1, and Kotlin 2.3.10 with AGP through 9.0.0. Do not add production KMP modules until a supported Kotlin, Compose, Gradle, AGP, and Xcode combination is proven in CI.

## 2. Current baseline

The source copy is a multi-module Android music app: :app, :canvas, :innertube, :lyrics, :integrations, :spotify, :artwork, :network-storage, :spine, and :hig. The Listen Together server is a separate JavaScript deployment and should stay independently deployed.

Product capabilities include streaming/local playback, queues, downloads, sleep timer, crossfade, Automix, DJ transitions, equalizer, discovery, search, artists/albums/playlists, local and network libraries, timed lyrics, artwork, Spotify import, Discord, Last.fm, music recognition, and Listen Together. Android-specific capabilities include notifications, lock-screen controls, Android Auto, GMS Cast, widgets, Android file permissions, and the current self-updater.

Android currently supports API 26 and later and ships FOSS and GMS variants. Keep both variants in the Android release gate.

The Android app has 933 Kotlin files and 77 test files. A first-pass text scan found Android references in 361 app Kotlin files. This is a heuristic inventory, not an estimate that 39% of code must be rewritten. Each file still needs classification by behavior, dependencies, and tests.

Good pilots are :canvas and pure parsing/model slices from :lyrics, :innertube, and :integrations. :spotify, :network-storage, :spine, and much of :app contain Android, JVM, native, or vendor dependencies and must not move wholesale.

Room is at schema version 40 with checked-in snapshots 1–40 and a long migration chain. The wrapper uses Android-only SupportSQLiteDatabase and SupportSQLiteOpenHelper. Preserve its database name, schema, and user data during migration.

Hilt is wired throughout the current Android app. Koin is selected for the KMP migration across Android, iOS, and desktop. Move the Android graph incrementally with each feature migration, assemble feature modules at each app composition root, and remove Hilt after all bindings and entry points have migrated. Keep platform SDKs out of shared modules.

Existing architecture audits identify a 2,618-line MainActivity, public mutable playback state, UI access that reaches directly into MusicService, and queue construction inside composables. Improve these in bounded changes. Do not port the Android host directly into common code.

There is no iOS app, Xcode project, or desktop app in this copy. Current landscape and wide-screen behavior informs Android tablet design; it is not the desktop specification.

## 3. Architecture target

### Dependency direction

1. **Platform applications** own startup, native lifecycle, permissions, dependency assembly, windows, and release configuration.
2. **Presentation modules** own screen state and user actions. Mobile screens may share Compose code. Desktop composes the same feature state into desktop layouts.
3. **Domain modules** own product rules, use cases, value models, queue/playlist semantics, and repository interfaces.
4. **Data adapters** implement domain interfaces with Ktor, database adapters, provider parsers, caches, and sync transports.
5. **Platform adapters** implement small interfaces for OS audio, media access, secrets, notifications, and file picking.

Each seam must have a concise interface, explicit error/lifecycle behavior, an Android adapter, and a second-target adapter. Avoid abstractions used by one implementation unless they isolate a real platform dependency.

### Proposed Gradle shape

| Module | Responsibility |
| --- | --- |
| :shared:core | Portable identifiers, results, clocks, coroutine-facing contracts, common serialization conventions. |
| :shared:domain | Product models, use cases, repository interfaces, queue/playlist rules, search/library behavior, feature-state transitions. |
| :shared:data | Portable Ktor clients, common DTOs/parsers, shared repository and sync logic. |
| :shared:design-system | Convx tokens, typography, spacing, motion, and cross-target Compose controls. |
| :shared:mobile-ui | Shared Android/iOS mobile navigation and screens, adaptive for phones and tablets. |
| :app | Existing Android shell; migrate its Hilt graph to Koin as shared modules are adopted. |
| :iosApp | Xcode shell, signing, URL schemes, lifecycle, entitlements, and native integration; may host shared Compose screens. |
| :desktopApp | Compose Desktop shell, window lifecycle, menus, file integration, packaging, and desktop-specific layouts. |

Keep current modules until each has a proven owner and migration path. Convert or split one at a time; preserve the Android-facing interface where practical. Use commonMain/commonTest, plus androidMain, iosMain, and desktopMain only where target code is needed. Keep SDK objects and native builders out of commonMain.

### Interfaces to define first

- **PlaybackEngine:** prepare, play/pause, seek, skip, queue handoff, playback snapshot, typed failure.
- **LocalMediaAccess:** enumerate/import, durable access where supported, and report revocation/missing files.
- **CredentialStore:** token storage without exposing keychain details.
- **NotificationPublisher** and **BackgroundWorkScheduler:** platform work and capability reporting.
- **ArtworkStore:** load/cache by portable identifier with explicit invalidation.
- **DatabaseFactory:** open the platform DB under application support and expose common repository behavior.
- **SystemBrowserAuth:** start OAuth and return a validated callback to common auth state.
- **Clock** and **RandomSource:** inject time/randomness into retries, shuffle, sync, and sleep timer.

Never pass Android Context, Uri, MediaItem, Bitmap, Bundle, SupportSQLiteDatabase, or ExoPlayer through common interfaces. Convert them into Convx models at the adapter seam. Share provider protocols only when dependencies and license terms support every target.

## 4. Feature sharing and client experience

For each capability, record one of: **shared behavior and UI**, **shared behavior with a native adapter**, **intentionally platform-specific**, or **not supported on this client with a documented reason**. No client may silently omit a capability while claiming parity.

The sharing bar is feature-based: every cross-client capability has one common implementation of its domain model, primary use-case rules, state transitions, and data contract. Platform adapters own OS input/output, lifecycle, and target-specific controls. Android and iOS share mobile screen implementations where they fit; desktop shares feature state and design tokens while composing its own screens. Measure duplicated feature rules, not raw shared line count.

| Capability | Shared responsibility | Platform-specific responsibility |
| --- | --- | --- |
| Browse, search, artist/album, playlists, library | Models, filtering, request mapping, use cases, screen state, mobile UI, core accessibility labels | File permissions, provider/browser launch, desktop keyboard/window behavior, artwork decoding |
| Playback stack: engine/service infrastructure, queue, repeat/shuffle, AutoMix, gapless/crossfade, sleep timer, DJ transitions, restoration | Portable engine contract, queue and transition policy, AutoMix requests/models, feature settings, state, failures, and contract tests | Media3 on Android, AVFoundation on Apple, desktop engine; native audio processing, output routing, codecs, lifecycle/background controls |
| Lyrics and karaoke | Provider-neutral models, parsing, timestamp alignment, sync rules, shared mobile presentation | Platform audio clock/lifecycle and desktop lyrics-pane layout |
| Accounts and integrations | DTOs, validation, protocol rules, import transformations, retries, account state | OAuth callbacks, credentials, notifications, vendor SDKs |
| Listen Together | Protocol models, recovery rules, state transitions | Socket lifecycle, background policy, notifications, audio-clock source; server deploy remains separate |
| SMB, WebDAV, local media, downloads, backup/restore | Source model, transfer policy, formats, error states | Android Storage Access Framework, Apple file picker/bookmarks, desktop paths, permissions, durable access |
| Equalizer, spatial/vocal effects, loudness, bit-perfect, stem separation, recognition, analysis | Profile/settings models, effect policy, portable DSP/analysis algorithms, orchestration, capability reporting, and fallback semantics | Audio sinks/effect sessions, device APIs, target DSP/runtime (including ONNX/Core ML), hardware support |
| Widgets, Android Auto, Google Cast, app self-updater | Shared intent/model where useful | Android-specific; equivalents require separate product and native implementation |

All adapters must satisfy shared contract tests for observable behavior. Feature rules should not fork by platform without a documented capability reason.

### Mobile design

Keep Convx's calm, glass-inspired identity. Share tokens and screen structure where that preserves familiar behavior. Keep Android back/notification behavior native; give iOS expected edge navigation, safe areas, keyboard behavior, share sheets, and system media controls. Do not force one platform's gestures or controls onto the other.

Use one design-system module for color roles, typography, spacing, radii, motion, artwork treatment, focus/selection, and accessibility semantics. Glass needs platform-tested implementations and a legible opaque fallback for contrast, reduced transparency, and slower GPUs.

### Desktop design

Use Apple Music's web player as a structural reference for clear navigation, content-led discovery, collections, strong artwork, and accessible playback. Use Apple's macOS Human Interface Guidelines for window, sidebar, toolbar, and keyboard behavior. Keep Convx branding, typography, artwork treatment, and colors; do not reproduce Apple assets or a pixel-identical layout.

Desktop is a workspace:

- Collapsible left sidebar: Home, Search, Library, Playlists, Downloads, and provider/source navigation.
- Flexible center: discovery, results, library lists, artist/album details.
- Persistent bottom player: artwork, track metadata, transport, seek, volume, playback status.
- Optional right inspector for queue, lyrics, and track details. It must be hideable and must not squeeze the center below a usable width.
- Native window resizing, menus, keyboard shortcuts, focus order, hover/context states, drag-and-drop import, and text selection.
- Compact-window behavior collapses panes predictably instead of shrinking every control.
- Opaque/low-transparency mode keeps text readable on all desktop themes.

Landscape Android and tablet designs remain adaptive mobile/tablet layouts, not the desktop specification.

## 5. Toolchain, storage, and dependencies

### Toolchain gate

Before adding KMP plugins, pin Kotlin Multiplatform, Compose Multiplatform/compiler, Gradle, AGP, JDK, and Xcode together. Build a minimal shared model, screen, Android linkage, iOS simulator host, and desktop JVM target on GitHub Actions.

Recheck the official compatibility table at implementation time. As of this plan date, the documented Kotlin 2.4.20–2.4.21 row allows Gradle through 9.7.0 and AGP through 9.3.1; the copy uses AGP 9.4.1 and Kotlin 2.3.10. Evaluate the Android KMP library plugin for shared Android libraries. Keep the application plugin on :app unless the compatibility proof says otherwise. Do not upgrade the whole Android dependency graph because a wizard suggests it.

### Database and preferences

Keep the Android DB name, schema, version, and migrations stable. Test upgrades from released schemas before changing Android persistence. Room 2.8.4 with bundled SQLite 2.6.2 is selected for new clients; it has a separate schema and is wired through `shared-kmp/data/library` behind common repository contracts.

1. Keep the shipping Android Room 2.8.4 database and current migration chain in the Android adapter.
2. Evolve the new-client Room KMP schema only with checked-in snapshots and migration tests on Android, desktop, and iOS.
3. Port data by repository/use-case slice; do not copy Android schema 40 wholesale or claim schema compatibility.
4. Keep common repository interfaces stable while platform hosts provide the database builder and persistent path.
5. Define import/export or cloud-sync requirements before promising portable or byte-for-byte database backups.

DataStore and MMKV are Android-specific. Move preference names, defaults, validation, and feature behavior into common code; select Apple/desktop settings adapters. Preserve Android keys and values.

### Dependency disposition

| Current dependency/module | Initial disposition |
| --- | --- |
| Ktor | Shared candidate. Use target HTTP engines; keep request policy, DTOs, serialization, and retries common. |
| :canvas | First KMP pilot after replacing OkHttp-only wiring and validating common models/tests. |
| :innertube / NewPipe Extractor | JVM extractor blocks iOS. Split transport/models/pure parsers; keep an adapter or replace after behavior/provider constraints are understood. |
| :lyrics / Jsoup | Move timed-lyrics models/parsers first; isolate Jsoup and Android types or replace with portable parsing. |
| :integrations | Share DTOs and protocol logic after Android refs and HTTP engines are separated. |
| :spotify / Rust-NDK engine | Keep Android implementation. Share import/auth/playlist mapping; investigate Apple/Linux/Windows Rust targets and browser handoff separately. |
| :network-storage / SMBJ / Media3 OkHttp source | Keep Android adapter. Share source descriptors/sync policy; prove SMB/WebDAV and media-source support per target. |
| :hig / backdrop effects | Reuse Convx tokens and stable controls first. Audit rendering, shaders, gestures, and target support; provide tested fallbacks. |
| Room, MMKV, DataStore, dependency injection | Keep platform implementations behind shared interfaces until adapters and migrations are proven; Koin is the selected cross-platform DI framework. |
| ONNX, C++, FFTW, Android audio effects | Native-platform work. Measure portable core, per-platform runtime, or explicit reduced capability for each feature. |

## 6. Execution plan and gates

These phases lower risk. They can overlap after the toolchain gate, while production Android work continues. Durations are a planning estimate for a small experienced team; revise after audio and storage prototypes.

| Phase | Work | Exit gate |
| --- | --- | --- |
| 0. Discovery, 1–2 weeks | Lock launch-critical parity and distribution owners; inventory features and dependencies; baseline startup, playback, DB, and UI performance; review licenses/provider constraints; assign audio and persistence risks. | Parity inventory approved; toolchain, audio, and persistence risks have owners; first-wave OS floors and architectures are locked. |
| 1. Toolchain proof, 1–2 weeks | Build a common model/screen, Android linkage, iOS simulator host, and desktop window; validate Kotlin/Compose/Gradle/AGP/Xcode; prototype iOS lifecycle and desktop packaging; run on actual GHA runners. | Android variants still build; iOS simulator and desktop targets build in Actions; supported versions are pinned. |
| 2. Shared seams, 2–4 weeks | Define playback/repository/credential/local-media/artwork/clock interfaces; reduce UI reach-through to MusicService; expose read-only playback snapshots and intent methods; extract queue rules from composables; add Koin composition roots and migrate Hilt bindings feature by feature. | Common interfaces and Koin modules compile on all first-wave targets; Android adapters work without SDK objects leaking into common code. |
| 3. Safe pilots, 2–4 weeks | Convert :canvas and a small lyrics-parser slice; share Ktor policy, serialization, and parsing; add common tests and a second-target adapter. | Two pilots compile and pass common checks on Android, iOS, and desktop. |
| 4. Data and persistence, 4–8 weeks | Extract catalog/library/playlist repositories; integrate the selected Room KMP store; preserve Android schema 40; separate settings policy from DataStore/MMKV; define recovery/import/export. | Android upgrade preserves user data; new clients retain library/settings/queue/playlists; repository contract suite and database migration tests pass on every adapter. |
| 5. Shared mobile slices, 6–10 weeks | Move Home/browse, search, artist/album, library/playlists, lyrics, settings/account, downloads/imports, then secondary features. Keep Android widgets, notifications, Auto, Cast, and updater in its shell. | Android remains releasable; iOS alpha covers agreed core flows and passes device/design/accessibility review. |
| 6. Playback/native parity, 6–12 weeks | Split Android `MusicService` and its playback infrastructure into common policy/data modules and platform adapters. Implement Apple and desktop playback adapters; wire AutoMix and persisted queue behavior; match gapless/crossfade/DJ transitions, sleep timer, EQ profiles, effects, analysis, interruptions, outputs, background controls, restoration, and failures where OS permits. | Per-feature playback matrix passes across Android/iOS/desktop; audio output and transitions are exercised on real runtimes; capability limits are explicit; no Android regression. |
| 7. Desktop product, 6–10 weeks, overlaps phase 6 | Build sidebar/player/inspector layout, keyboard/focus, import/drop, window breakpoints, contrast fallback, and installers on shared feature state. | Compact/standard/wide usability review passes; Windows/macOS/Linux artifacts install and launch in CI. |
| 8. Completion/release, 4–8 weeks | Close parity gaps; run data checks; add signing/release jobs; prepare TestFlight/desktop beta; document recovery; keep server deploy separate. | Privacy, license, signing, accessibility, crash, and rollback reviews pass; all release artifacts come from Actions. |

Order-of-magnitude delivery is **two to four quarters** for a small team, mainly depending on playback parity and provider scope. Phases 0–1 replace this estimate with evidence.

### First vertical slices

1. Portable artwork/catalog request with loading, success, empty, and typed-error states.
2. Timed-lyrics parser and player-independent lyrics state.
3. Artist/album detail on Android and iOS using the same screen model.
4. Library/playlist repository backed by existing Android DB and new-target storage.
5. Search/browse using common network, history, and screen-state behavior.
6. Playback controls after all three playback adapters pass minimum capability proofs.

Do not start with login, native playback, glass shader porting, or wholesale DB conversion; each has a broad platform/data failure surface.

## 7. GitHub Actions model

GitHub Actions is the canonical build path for every shipped target. Local builds remain useful, but no iOS or desktop release depends on a developer machine.

| PR job | Runner | Required work |
| --- | --- | --- |
| Android | Ubuntu | Compile FOSS and GMS variants, JVM tests, lint, optional APK artifact. Use dummy optional keys. |
| Common modules | Ubuntu plus target compile tasks | Common tests, Android/JVM adapter tests, static checks, reports. |
| iOS | Pinned macOS/Xcode image | Unsigned simulator framework and host build, common tests, simulator tests where stable. No release certificate required. |
| Desktop | Ubuntu, Windows, macOS matrix | Compile/test per OS and package smoke artifacts on native OS. Pin images/JDK rather than moving latest labels. |
| Server | Ubuntu, separate job/workflow | Check Listen Together server without coupling client release to server deployment. |

Every supported platform check is required for merge. Cache using wrapper, Kotlin, Compose, native toolchain, and OS versions; cancel stale PR runs; report runner-minute use and CI duration.

Release jobs:

- Preserve Android FOSS/GMS signing policy; keys live only in protected GitHub environments.
- Build Apple simulator/device frameworks on macOS. Sign and distribute only from protected tag/manual release workflows with Apple credentials.
- Package macOS ARM, Windows x64, and Linux x64 on matching runners. Add notarization and code signing only for release.
- Keep secrets unavailable to fork PRs. Upload unsigned PR artifacts, signed release artifacts, symbols, and build metadata.
- Avoid ARM macOS workflows requiring a static UDID. Use simulator builds/signing that do not rely on a fixed runner device.

GitHub-hosted Linux, Windows, and macOS runners exist. iOS requires macOS/Xcode and native installers should be built on matching OSes. Recheck runner images and account-minute policies when implementing workflows.

## 8. Principal risks and responses

| Risk | Impact | Response and stop condition |
| --- | --- | --- |
| Toolchain exceeds documented Kotlin/AGP/Gradle/Compose/Xcode matrix | KMP configuration fails or Android regresses. | Resolve in Phase 1; pin only combinations proven on GHA. If unsupported, keep extracting pure logic behind interfaces while waiting. |
| Android dependencies are embedded through :app | Bulk source move causes hard-to-debug rewrite and release failure. | Classify by behavior/dependency/test; move vertical slices; keep Android release gate active. |
| No identical player engine across Android, Apple, and desktop | Queue, codec, interruption, background, and routing behavior differs. | Prototype each engine early; define observable behavior and supported codecs; show explicit fallback for OS limits. |
| Room/preferences migration loses user data | Library/history/playlists/lyrics/settings can disappear. | Preserve Android DB name/schema; test snapshots 1–40; backup/restore before conversion; no alpha DB in production; define rollback. |
| Room wrapper uses SupportSQLite | Shared DAO conversion may be expensive. | Keep common repository seam and separate adapters until KMP Room or stable alternative passes real schema/query/migration tests. |
| NewPipe, Jsoup, SMBJ, QuickJS, WebView, ONNX are JVM/Android-bound | Features fail to compile on iOS or need replacements. | Audit transitive targets/licenses; split models/algorithms from library; port only after behavior tests exist. |
| C++, FFTW, Rust/librespot, ONNX, Android audio effects need native toolchains | CI/package complexity and performance variance rise. | Inventory native deps; start with portable paths; add one library/target at a time; use capability flags, never silent no-ops. |
| OAuth/secrets differ by OS | Login breaks or tokens leak. | System-browser auth, callback validation, secure-store adapters, PKCE where provider permits, redacted logs, expiry/cancel/retry checks. |
| Local-file access differs across OSes | Imported files become inaccessible. | Separate portable source descriptor from OS handle; test revoked access, moved file, offline share, duplicates, large scans. |
| Shared UI looks identical instead of appropriate | Mobile feels foreign or desktop looks like an enlarged phone. | Share tokens/state, not every layout; preserve platform interactions; separate desktop composition; review real dimensions. |
| Glass/artwork effects vary in rendering/performance | Contrast/frame time regress. | Prototype costly effects per target; set GPU tiers and opaque fallback; collect frame-time baselines; honor reduced motion/transparency. |
| CI/signing cost increases | Slow PRs or blocked release. | Unsigned secret-free PR builds, pinned runners, caching/cancellation, monthly duration review, protected credentials. |
| External providers/protocols change | Search, streaming, login, or import fails. | Isolate provider adapters; test independent behavior; monitor failure; publish support matrix; review terms per platform. |
| Browser scope creep | Unplanned browser storage/audio/security requirements expand scope. | Keep browser and Kotlin/Wasm targets out of this migration; require a separate product decision to revisit. |

## 9. Quality and release gates

A platform is not feature-complete until these pass:

1. **Behavior:** common domain contract tests and adapter contract tests pass; queue, playlist, sync, and error semantics do not silently change by platform.
2. **Data safety:** Android upgrades preserve libraries/settings. New clients recover from interrupted writes, missing files, revoked access, corrupt caches, and provider failure.
3. **Playback:** local/streamed media, long playback, seek, queue edits, repeat/shuffle, output change, interruption, background/resume, sleep timer, cancellation.
4. **Design:** Android phone/tablet/landscape, iPhone/iPad, compact/standard/wide desktop; focus, keyboard, touch, text scaling, localization, contrast, reduced motion/transparency, empty/error/offline.
5. **Performance:** baseline startup, library open, search, playback start, scroll frame time, memory, and battery before migration; compare on representative devices.
6. **Reliability:** no lost queue, duplicate sync, lifecycle crash, insecure token log, or unhandled native crash on supported matrix.
7. **Build/distribution:** every PR target builds in GitHub Actions; release artifacts are traceable to Actions; signing material never enters repository or PR logs.
8. **Parity:** every first-wave target has a documented feature status. Deferred features have explicit decisions.

## 10. Decisions to lock in Phase 0

The first-wave platform floors are locked for this migration:

| Target | Minimum version | Initial architecture |
| --- | --- | --- |
| iOS | 17.0 | arm64 device and arm64 simulator in CI |
| macOS | 14.0 | arm64 and x86_64 |
| Windows | Windows 10, version 22H2 | x86_64 |
| Linux | Ubuntu 22.04 LTS | x86_64 |
| Browser / Kotlin/Wasm | Out of scope | No target |

These are product support floors, not a claim that packaging and runtime checks have passed. Phase 1 must validate the available runner images and package architectures against them.

The first iOS and desktop clients will prioritize browse/search, library/playlists, and the complete playback feature set: queue and restoration, AutoMix, gapless/crossfade/DJ transitions, sleep timer, equalizer, and playback-related audio processing and analysis. Playback infrastructure and Android feature modules must be mapped into common modules plus target adapters; no playback feature may disappear from a client without a documented, explicit capability result.

Remaining Phase 0 decisions:

- First-wave provider coverage and integrations on each target, within the agreed core music flows.
- Local-library expectations on iOS/desktop, including removable/network media.
- Listen Together support on desktop and iOS.
- Desktop playback backend, supported formats, outputs, and background behavior.
- Target-specific implementations and capability reporting for EQ, audio effects, stem separation, and analysis. The feature scope includes these; hardware and runtime capabilities still need proof.
- New-client portable backup/import format and whether any data is transferred from Android schema 40.
- Apple Developer, Windows signing, notarization, and distribution account ownership.

## 11. References

- [Kotlin Multiplatform platform stability](https://kotlinlang.org/docs/multiplatform/supported-platforms.html) — platform and Compose Multiplatform stability.
- [Kotlin Multiplatform compatibility guide](https://kotlinlang.org/docs/multiplatform/multiplatform-compatibility-guide.html) — Kotlin, Gradle, AGP, and Xcode compatibility.
- [Compose Multiplatform compatibility](https://kotlinlang.org/docs/multiplatform/compose-compatibility-and-versioning.html) — supported targets and desktop packaging.
- [Room for Kotlin Multiplatform](https://developer.android.com/kotlin/multiplatform/room) — shared entities/DAOs, target DB builders, migration notes.
- [Apple Music web player](https://music.apple.com/us/browse) — discovery and navigation reference.
- [Apple Human Interface Guidelines: macOS](https://developer.apple.com/design/human-interface-guidelines/designing-for-macos/), [sidebars](https://developer.apple.com/design/human-interface-guidelines/sidebars), [toolbars](https://developer.apple.com/design/human-interface-guidelines/toolbars) — desktop window and navigation behavior.
- [GitHub-hosted runners](https://docs.github.com/en/actions/reference/runners/github-hosted-runners) — available Linux, Windows, and macOS build environments.
