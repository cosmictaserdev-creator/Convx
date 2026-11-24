# Desktop parity implementation — 2026-10-26

Current reference-driven changes and verification limits are recorded in
[KMP_REFERENCE_PARITY.md](KMP_REFERENCE_PARITY.md). The visual descriptions below
are historical and do not describe every part of the latest preview.

The requested order is desktop first, then iOS. The production Android client is the visual reference for the shared typography, symbols, detail compositions, artwork colors, and customization behavior. Full Android feature parity is still incomplete.

## Current changes

- Adopted Nucleus `compose-macos-ui` 1.1.0 and desktop runtime 2.5.2. The host uses its decorated window with the Tao backend. Playback and browser/file-dialog adapters remain separate from window ownership. Desktop scaffold, native title bar, sidebar, fields, buttons, sliders, segmented controls, switches, menus, and standard dialogs now consume toolkit components.
- Updated both active Compose catalogs to 1.12.0 for the toolkit. Android font files are packaged in the shared design system; desktop defaults and the shared mobile shell use those same four weights. Custom desktop font selection remains supported. Navigation and transport controls retain the project's SF glyphs.
- Added a dedicated desktop music-search page with a top field, grouped result filters, provider suggestions, recommended entries, persistent recent searches, individual history removal, and clear/disable-history settings. Suggestions debounce and cancel on edits, submission, and offline-mode changes. Sidebar search filters navigation and playlist names independently.
- Pinned Liked Music and My Playlists in the sidebar. Remote playlist navigation now follows continuation pages; local playlists are exposed independently of the current catalog page.
- Distinguished the navigation-only filter (with a filter glyph) from the Search Music destination. The sidebar account row now renders the account thumbnail, with an initial on image failure and a profile symbol when signed out.
- Restored standard Windows Mica through Nucleus's native backdrop API. The toolkit sidebar leaves the native material exposed; the music content retains its opaque background. Navigation and music search fields now use rounded capsules while retaining the existing SF glyphs, clear action, and focus behavior.
- Added the mini-player seek slider and separated seeking from opening the full player. Full-player artwork, title sizing, artwork-derived colors, and pause/playing scale feedback have been revised. Existing layout order, hidden sections, stickers, custom icons, and presets remain connected.
- The mini-player retains a 60 dp capsule: the seek bar and elapsed/remaining time sit beneath the song details inside the center column. Hover thickens only the track. The SF star toggles persisted local favorites or the signed-in provider's liked state, with duplicate-write protection and account-change cancellation.
- Moved desktop EQ and sleep-timer interactions into toolkit dialogs while retaining the shared settings and timer rules.
- Fixed the canvas test module's missing coroutine test dependency.

## Verification and limits

The native Windows check reads back `DWMWA_SYSTEMBACKDROP_TYPE = 2` (standard Mica) successfully from the preview window. Offscreen pixels confirm that the sidebar is transparent and the content pane is opaque. Rounded search fields passed text-entry, focus, clearing, navigation-filter, and destination-selection checks through the production shell. The offscreen check cannot establish the user's wallpaper appearance.

The canvas and playback domain suites passed locally: 12 and 46 tests respectively. Playback data/persistence passed 12 tests, including search-history settings after closing and reopening the database; InnerTube passed 32 tests. Four new suggestion/history tests and four track-like state tests passed using Kotlin 2.4.20 and cached dependencies. The independent Compose compiler check against cached Compose 1.12.0 and Nucleus artifacts passes the desktop sources. Offscreen renders of the production shell exercised Home, Search, Settings, Appearance, and Equalizer through their actual semantic actions; the resulting images were visually inspected. A sidebar sizing error found during this check was fixed. The production mini-player also passed actual pointer seeking, no accidental expansion, star toggling, accessible seeking, unknown-duration handling, unchanged capsule bounds on hover, and narrow/light layout checks with a fixture controller. The account photo decoded and rendered from a local fixture. Live provider-like mutations, playback output, and full-player interaction still require native runtime coverage. This is not a packaged-release claim.

Normal Gradle desktop verification currently cannot resolve missing Compose/Nucleus metadata and artifacts in offline mode. An online attempt also failed to resolve the Compose plugin in this restricted environment. Native package creation, Android linkage after the Compose update, and iOS compilation still need successful normal build runs. The Nucleus native window launches in the execution session, but the user confirmed it did not appear in their Windows session. The outer workspace contains `Launch Convx Desktop.cmd`, which opens the checked classes and cached runtime dependencies from the user session using an independent preview profile. This is a local development preview, not a distributable installer.

Network storage is present but reported unreliable on Android; it is not a proven working parity target. Remaining ports include radio, recognition, full DJ/effects workflows, Listen Together/chat, Spotify imports, scrobbling/Discord, network media, complete backup/restore, statistics/Wrapped, ambient/video canvas, and the remaining Android settings/customizations. iOS has received the shared font foundation; its screen and interaction parity remains open.

---

Historical checks below describe the earlier Windows preview and must not be read as verification of the new Nucleus UI.

# Desktop migration progress

Updated 2026-10-25 from the active checkout, current build logs, tests and running Windows preview. Earlier migration notes are not evidence for this report.

Desktop is an incomplete alpha. iOS remains after desktop. A module compiling does not establish that its complete Android feature is available on desktop.

## Working and checked

- Desktop packages and launches on Windows. Account profile and signed-in library have been observed after restarting the preview.
- Sidebar, search results, browsing, playlist details and paging use real catalog data. The sidebar loads account playlists independently of the page being browsed.
- Full-window compact-player input interception was fixed. A desktop mouse-input regression exercises clicks and wheel scrolling after three expansions/collapses. Window movement was measured from the sidebar header.
- Dark mode has a black content background. Sidebar, queue and lyrics use the actual Windows DWM Mica backdrop; panels attach to the window without floating rounded cards. Back controls stay inside the content pane. Native window controls stay at the outer edge.
- The glass player captures the complete page including its background. Its blur/refraction is visible over artwork in the running preview.
- Artwork requests now use measured pixel sizes and retry lower resolutions when high-resolution resources are unavailable.
- Home loads its primary feed before supplementary shelves, preserving continuation paging. New releases, real listening history, charts where available, account playlists and related recommendations supply distinct sections.
- Desktop artist/album/playlist headers now follow the Android tablet compositions. The existing JVM artwork catalog and signature lookup are reused. Artist favorites issue real provider mutations; account-required actions retain sign-in handling.
- Playlist details use the centered artwork/controls layout and song search. Album/playlist related entries and artist descriptions/more links are carried through shared models.
- Expanded player has a wide layout, playback controls, seek, volume, EQ, transition and sleep-timer actions. Queue/lyrics remain on the right. Space, Escape and Ctrl+Left/Right are supported in the expanded player.
- Lyrics support multiple actual providers, word/syllable timing, seeking, scroll-following, manual scrolling and reduced motion. Provider availability and API-key configuration still require broader live coverage.

## Verification

The Windows package plus desktop host, catalog presentation, playback presentation/data, InnerTube and canvas tests passed in `artifacts/kmp-desktop-audit/desktop-tablet-package.log` in the outer workspace. The earlier Android KMP host compilation passed. Production Android compilation is being rerun with the installed SDK path; its final result is not yet claimed.

Runtime captures in the outer workspace include `tablet-home.png`, `tablet-expanded-player.png`, `tablet-player-queue.png`, and the centered playlist detail in `tablet-player.png`. Those show real data and are not mocks. Artist artwork/signature loading still needs a complete live visual check. Changes after the packaged build require repackaging.

The linked web reference has now been rendered through the controlled browser tool. Its navigation and player were visible. Its populated Home feed was gated on a browser extension; no populated web-feed visual comparison is claimed.

## Priority queue

1. Finish interaction/runtime checks and fix regressions in the current UI pass.
2. Implement desktop local mode, folder scanning, metadata/artwork, offline playback, downloads, progress/pause/resume and storage management.
3. Implement theme customization and DIY player editing with persistent layouts and assets.
4. Complete local/remote library and playlist management, history, imports, backup/restore and statistics.
5. Port remaining radio, recognition, advanced DJ, social/listen-together, network storage and integration flows through shared contracts and desktop adapters.
6. Complete a full desktop parity audit, then begin iOS adapters and visual verification.

`docs/kmp-desktop-parity.json` records 90 route registrations extracted from the current Android sources. It is a starting inventory, not a claim of 90 desktop screens. Dynamic settings registrations and non-route player features also need explicit coverage.

## Sharing approach

Reuse domain rules, provider mapping, request/cancellation logic, queue policy, parsing, persistence contracts and feature state. Keep native file dialogs, filesystem access, decoders, OS integration and window materials behind platform adapters. Prefer the existing JVM implementations where they already run on desktop; do not duplicate their provider clients.
