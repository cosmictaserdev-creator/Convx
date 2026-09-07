# Multi-Singer Synced Lyrics — Research & Architecture

Feature branch: Apple Music-style synchronized lyrics with **per-singer highlighting**
(duets, shared vocals, "who is singing right now"), implemented on top of Convxy's
existing lyrics pipeline and Liquid Glass UI.

---

## 1. Analysis of the Existing Codebase

| File | Responsibility |
|---|---|
| `app/.../lyrics/LyricsEntry.kt` | Core line model: `time`, `text`, word timestamps, romanization/translation `StateFlow`s, `agent: String?`, `isBackground: Boolean`. |
| `app/.../lyrics/LyricsUtils.kt` | Parsing: standard LRC (`[mm:ss.xx] text`), "rich sync" word format, Musixmatch-style `{agent:v1}` / `{bg}` inline markers, the `<word\|start\|end>` sidecar word format, `findCurrentLineIndex()` binary search, romanization engine. |
| `app/.../lyrics/LyricsProvider.kt` | Provider interface (`getLyrics` / `getAllLyrics`). |
| `app/.../lyrics/LyricsProviderRegistry.kt` | Name→provider map + user-configurable priority order. |
| `app/.../lyrics/LyricsHelper.kt` | Orchestrates providers in priority order, in-memory `LruCache(3)`, network gating. |
| Providers (`BetterLyricsProvider`, `PaxSenixLyricsProvider`, `MusixmatchLyricsProvider`, `LrcLibLyricsProvider`, `KuGouLyricsProvider`, `SimpMusicLyricsProvider`, `YouLyPlusLyricsProvider`, `YouTube*`) | Each returns **a normalized LRC-like string**. TTML sources are flattened to LRC inside `betterlyrics/TTMLParser.kt` and `paxsenixlyrics/Paxsenix.kt` (which synthesizes `{agent:v1}`/`{agent:v2}`/`{bg}` markers). |
| `betterlyrics/.../TTMLParser.kt` | Apple-Music-style TTML → internal `ParsedLine` → Convxy LRC dialect (`{agent:vN}` + `<word|start|end>` lines). Already reads per-line `ttm:agent`, `ttm:role="x-bg"`, skips `x-translation`/`x-roman`. |
| `app/.../db/entities/LyricsEntity.kt` | Room entity (`lyrics` table): raw lyrics text + provider + cached translation. |
| `app/.../playback/PlayerConnection.kt` | `currentLyrics` flow: `mediaMetadata.flatMapLatest { database.lyrics(id) }`. |
| `app/.../viewmodels/LyricsMenuViewModel.kt` | Provider search / refetch / upsert into DB. |
| `app/.../ui/component/Lyrics.kt` | Main lyrics screen: 10 Hz position polling, `findCurrentLineIndex`, LazyColumn, auto-scroll & seek, 9 animation styles (NONE/FADE/GLOW/SLIDE/KARAOKE/APPLE/APPLE_V2/LYRICS_V2/METRO/VIVIMUSIC_1), progressive blur, share/share-as-image. **Already aligns `v1` left / `v2` right / `v1000` center and shrinks+italicizes `{bg}` lines.** |
| `app/.../ui/component/MetroLyrics.kt`, `ViviMusicLyrics.kt`, `LyricsV2.kt` | Alternate per-line renderers (same agent alignment logic). |
| `app/.../ui/menu/LyricsMenu.kt` + `AppearanceSettings.kt` | Lyrics UX settings (text position, animation style, glow, blur…). |
| `app/.../widget/MetrolistWidgetManager.kt` | Widget reuses `LyricsUtils.parseLyrics`. |

**Data flow:** provider → LRC text → `LyricsEntity` (Room) → `PlayerConnection.currentLyrics` →
`LyricsUtils.parseLyrics` → `List<LyricsEntry>` → `Lyrics.kt` renders, driven by
`playerConnection.player.currentPosition` polled at 100 ms and `findCurrentLineIndex`
(binary search, +300 ms look-ahead).

## 2. Current Limitations

1. **Parser** — `{agent:v1}` is captured as an opaque string; the *meaning* of `v1`
   (which human) is thrown away. TTML parser reads per-line `ttm:agent` but **discards the
   `ttm:agent` registry in `<head><metadata>`** that maps ids → performer names.
2. **Data model** — no singer identity: no name, no type (person vs. group), no color slot,
   no "shared vocals" concept (`v1000` is treated only as a centering hint).
3. **UI** — alignment already differentiates sides, but nothing tells you *who* sings:
   no label, no per-singer color; every line uses the single `expressiveAccent`.
4. **Playback sync** — line-level singer switching is implicitly possible (agent is per
   line) but the UI derives nothing from it; no animated transition on singer change.
5. **Data source** — LRC (LRCLIB, KuGou, YouTube captions, Musixmatch subtitles) carries
   **no singer metadata at all**; only TTML-based sources (Better Lyrics API, PaxSenix
   TTML) and Musixmatch *richsync* (not fetched by the current client) carry voice ids.
6. **Storage format** — the normalized LRC dialect persisted in Room has no place to keep
   singer names, so names can't survive the provider→DB→UI trip.

## 3. Apple Music Research

Apple delivers lyrics as **TTML with private extensions** (documented in Apple's
*Video and Audio Asset Guide*):

* Performers are declared in `<head><metadata>` as
  `<ttm:agent type="person|group|other" xml:id="v1"><ttm:name type="full">Ryan Gosling</ttm:name></ttm:agent>`.
* Each line (`<p>`) or word/phrase (`<span>`) references a performer via `ttm:agent="v1"`.
* `type="group"` (conventionally `v1000`) marks **shared vocals**; Apple's Duet feature
  then "shows lyrics on opposite sides of the screen".
* Word-by-word (beat-by-beat) `<span begin end>` timing drives the karaoke fill;
  `ttm:role="x-bg"` marks background vocals, `x-translation`/`x-roman` are sidecars.
* Animations (per-word glow/fill, blur of inactive lines, spring scale) are client-side.

**Proprietary?** The carrier (TTML) and the `ttm:*` attributes are W3C-standard namespaced
TTML; Apple's *usage pattern* (`itunes:song-part`, `x-bg`, agent registry) is an Apple
convention, not a closed binary format. Conclusion: **the full Apple behavior can be
recreated from open formats** — TTML when available, and an LRC extension for storage —
which is what this implementation does.

## 4. Other Projects

| Project | Format | Singer metadata | Rendering | Sync |
|---|---|---|---|---|
| **Convx (upstream)** | LRC + custom word sidecar | none | active-line highlight | polling |
| **SimpMusic** | Musixmatch-derived JSON/TTML | per-line voice (`line.bg`, opposite turns) | alignment | polling |
| **RiMusic** | LRC (LRCLIB etc.) | none | active highlight | polling |
| **Harmony Music** | LRC | none | active highlight | polling |
| **Spotify** | proprietary line-synced JSON | provider-side "Name:" prefixes baked into text | color-per-state fill | event stream |
| **Apple Music** | TTML + `ttm:agent` | full (ids + names + group type) | opposite sides, word fill, blur | word timings |
| **Musixmatch** | richsync JSON (`{agent:vN}` per line) | voice ids only (no names) | per-app | word timings |
| **BetterLyrics (boidu API)** | Apple TTML served over JSON | inherits TTML `ttm:agent` | per-app | word timings |
| **LRCLIB** | plain + enhanced LRC | **none** (format has no field) | per-app | line/word |
| **YouTube Music** | captions / timed JSON | none | per-app | line |

## 5. Lyrics Format Comparison

| Format | Singer attribution | Precision | Styling | Extensibility | Parser cost |
|---|---|---|---|---|---|
| LRC | none | 10 ms–1 s (centisecond typical) | none | tags ignorable by old parsers | trivial |
| Enhanced LRC (A2) | none | word-level | none | same | small |
| **Convxy LRC dialect (this repo)** | `{agent:vN}` + `{bg}` ids | word-level sidecar | alignment flags | header tags skipped by old parsers | small |
| TTML (W3C) | `ttm:agent` ids | full (ms, spans) | rich | namespaces | DOM, medium |
| Apple TTML | ids **+ names + group type** | full + per-word agents | rich | namespaced | DOM, medium |
| WebVTT | CSS classes/voice tags | ms | CSS | poor for music | medium |
| JSON (per-app) | arbitrary | arbitrary | arbitrary | no interop | per-app |
| Musixmatch richsync | voice ids | word-level | none | closed | medium |

**Recommendation for Convxy:** keep the normalized LRC dialect as the *storage* format and
extend it with a **singer registry header**:

```
[singers:v1=Artist A|v2=Artist B|v1000=]
[00:01.00]{agent:v1}I remember all the things that happened...
```

Rationale: (a) every existing parser already ignores unknown bracket tags → zero
migration, zero breakage for old builds/other consumers; (b) TTML stays the *interchange*
format — names are extracted from `ttm:agent` metadata and flattened into the header;
(c) anonymous sources (LRCLIB, PaxSenix, Musixmatch subtitle) keep working, with a
UI-level fallback that infers names from the track's artist list. Also accepted: the
`v2:` line-prefix duet dialect used by some converters.

## 6. Architecture Proposal (implemented)

```
TTML provider ──┐  ttm:agent registry (ids+names+type)
                ├─► TTMLParser ──► LRC + [singers:…] header
LRC providers ──┘                     │
                                      ▼
                        LyricsEntity (Room, unchanged schema)
                                      │  currentLyrics flow (unchanged)
                                      ▼
        LyricsUtils.parseLyricsWithSingers() ──► ParsedLyrics(entries, singers)
                                      │                    (parseLyrics() now delegates)
                                      ▼
        Lyrics.kt ── singer color map (SingerPalette, memoized once per song)
                  ── per-line accent color (falls back to expressiveAccent)
                  ── SingerBadge ("🎤 Artist A") on section starts, animated
                  ── existing alignment/animation code paths unchanged
```

* **Models** — new `SingerInfo(id, name, isGroup)` + `ParsedLyrics(entries, singers)`.
  `LyricsEntry` itself is untouched (it already has `agent`/`isBackground`), so every
  existing consumer keeps compiling and behaving identically.
* **Parser** — `parseLyricsWithSingers()` reuses the same unescape/decode/rich-sync
  pipeline; adds `[singers:…]` header parsing, `vN:` prefix recognition, and shared-vocal
  agent forms (`v1+v2`, `v1,v2`, `v1000`). `parseLyrics()` delegates → single code path.
* **TTML** — `TTMLParser.parseAgents()` reads the `ttm:agent` registry (id, type,
  `ttm:name` child/attribute); `toLRC()` emits the header when names exist.
* **Repository/ViewModel** — untouched (the feature rides the existing text pipeline;
  no Room migration because singer data lives inside the lyrics text).
* **UI** — `SingerBadge.kt` (glass pill: color dot + name, Material 3 shapes, animated via
  `AnimatedVisibility`), `SingerPalette` (deterministic iOS-system color wheel ordered by
  first appearance), per-line accent passed down to every animation style including
  `MetroLyricsLine`/`ViviMusicLyricsLine` (call-site only change). Two new Appearance
  toggles: *Singer labels*, *Singer colors*.
* **Sync** — singer changes are derived from the already-computed current line index at
  line boundaries (no extra polling); badge/color transitions are 200–400 ms tweens,
  matching the existing line-change animation so nothing flickers.
* **Performance** — color map + singer map `remember`-ed per lyrics; badge only composes
  for the active item; no extra allocations per frame; large files unaffected.

### Trade-offs

1. *Names inside the LRC text vs. a new DB column* — text wins: no schema migration,
   works with the existing provider/cache/share pipeline, and old lyrics simply lack the
   header. Cost: header must be re-parsed (trivial, one regex over ≤ a few lines… we scan
   all lines but short-circuit on first match).
2. *Per-singer fixed palette vs. album-art-derived colors* — fixed palette wins: derived
   colors can be illegible on dynamic backgrounds and are unstable across palette
   re-extractions; the chosen iOS-system hues harmonize with the Apple-Music aesthetic.
   Solo songs (single agent) always keep the exact current accent → zero visual regression
   for the common case.
3. *Badge on every line vs. section starts* — section starts (first line of a singer's
   turn) match the requested example, reduce clutter, and avoid re-animating a label on
   every line.
4. *Full TTML storage vs. LRC header* — full TTML would preserve more (per-word agents,
   song parts) but would require touching every provider, the DB consumers, and the
   editor UX; deferred (see Future Work).

---

## 7. Implementation Summary

### Storage format extension

```
[singers:v1=Artist A|v2=Artist B|v1000=]          ← singer registry (optional)
[00:01.00]{agent:v1}I remember all the things that happened...   ← existing dialect
[00:12.00]v2: Nothing stays forever                ← accepted duet-prefix dialect
[00:20.00]{agent:v1+v2}We'll sing together         ← composite = shared vocals
```

* Named registry entries come from Apple TTML `ttm:agent`/`ttm:name` metadata
  (Better Lyrics & PaxSenix TTML sources) and are flattened into the header by
  `TTMLParser.toLRC()`.
* Anonymous agents (`v1`, `v2` from PaxSenix turns / Musixmatch-style sources)
  still work: the UI infers names from the track's artist list
  (`v1` → 1st artist, `v2` → 2nd artist) and hides the badge when it can't.
* `v1000`, `v1+v2`, `v1,v2`, `v1&v2` all normalize to shared vocals
  (`primaryAgentId`), rendered centered with a localized "Both" label.

### Modified / added files

| File | Change |
|---|---|
| `app/.../lyrics/SingerInfo.kt` **(new)** | `SingerInfo`, `ParsedLyrics`, `primaryAgentId`, `isSharedVocals`, `GROUP_AGENT_ID`, `isSingerSectionStart`. |
| `app/.../lyrics/LyricsUtils.kt` | `parseLyricsWithSingers()` (+`[singers:…]` header parsing, `vN:` duet prefix in both LRC and rich-sync paths); `parseLyrics()` now delegates — identical behavior for old documents. |
| `betterlyrics/.../TTMLParser.kt` | `TtmlAgent` + `parseAgents()` (Apple `ttm:agent` registry: id/type/name as element or attribute); `toLRC(lines, agents)` emits the header. |
| `betterlyrics/.../BetterLyrics.kt`, `paxsenixlyrics/.../Paxsenix.kt` | Pass the parsed agent registry into `toLRC`. |
| `app/.../ui/component/SingerBadge.kt` **(new)** | `SingerPalette` (deterministic iOS-system colors by first appearance), `resolveSingerDisplay` (registry → artist inference), `SingerBadge` + `AnimatedSingerBadge` composables. |
| `app/.../ui/component/Lyrics.kt` | Parses via `parseLyricsWithSingers` (single parse, memoized); per-line `lineAccent` replaces the global accent inside all nine animation styles (badge + tint for lead singers); badge on section starts incl. the Metro/Vivi renderers; two new preferences. |
| `app/.../constants/PreferenceKeys.kt` | `ShowSingerLabelsKey`, `SingerColorsKey` (both default **on**). |
| `app/.../ui/screens/settings/AppearanceSettings.kt` | "Singer labels" / "Singer colors" toggles in the Lyrics group. |
| `app/src/main/res/values/strings.xml` | `shared_vocals`, `singer_labels(_desc)`, `singer_colors(_desc)`. |
| Tests (new) | `app/src/test/.../LyricsSingerParsingTest.kt`, `app/src/test/.../SingerPaletteTest.kt`, `betterlyrics/src/test/.../TTMLParserSingerTest.kt` (+ JUnit dep for the module). |
| `README.md` | Feature bullet linking here. |

### Migration notes

* **No Room migration** — singer data lives inside the lyrics text; the
  `lyrics` table schema is untouched (still v37).
* **No user action** — old cached lyrics simply have no singer metadata and
  render exactly as before (`hasMultipleSingers == false` short-circuits every
  code path). Re-fetching lyrics from a TTML provider upgrades a song to named
  singers automatically.
* **Forward compatible** — older Convxy builds reading a header-bearing
  document skip the unknown tag, so downgrades are safe too.

### Testing

* Parser: header parsing, agent markers, `vN:` prefix, composite/group agents,
  malformed headers, coexistence with `[ti:]`/`[ar:]` tags and the
  `<word|start|end>` sidecar, three-singer rapid transitions, rich-sync agents,
  `findCurrentLineIndex` across singer switches, backward compatibility.
* Palette/resolver: first-appearance ordering, single-voice passthrough,
  background exclusion, 10-singer cycling, registry-name precedence,
  artist-list inference, group handling.
* TTML: agent registry with child-element and attribute names, unnamed groups,
  header emission/suppression, malformed input.
* Run: `./gradlew :app:testDebugUnitTest :betterlyrics:testDebugUnitTest`

### Future enhancements

1. Store and render **per-word `ttm:agent`** inside a line (partial-line duets)
   — requires extending the word sidecar format.
2. `itunes:song-part` metadata for verse/chorus section headers.
3. Album-art-derived singer colors as an alternative palette.
4. Persisting a `[singers:…]`-annotated copy when the user edits lyrics
   (lyrics editor awareness).
5. LRCv2 `<s:…>` import support if the spec gains traction.

## 8. End-to-end verification & follow-up fixes

### Headless pipeline verification (external harness)

A faithful Python port of the entire pipeline (TTMLParser → LyricsUtils
parsing → SingerInfo → SingerPalette/resolveSingerDisplay → Lyrics.kt panel
decisions incl. alignment, badges and the +300 ms `findCurrentLineIndex`
lookahead) was run against real-world data:

| Input | Result |
|---|---|
| Real named 8-singer TTML (qq-lyrics 704996514) | 8-color palette by first appearance, real-name badges at section starts, "Both" on `v1000` group lines |
| Real anonymous-agent TTML (am-lyrics 1770285047) | Single-voice excerpt → correctly falls back to legacy rendering (file has no name registry and only `v2`) |
| Real LRCLIB LRC for *NOKIA* (Drake/PARTYNEXTDOOR) | No singer metadata → legacy rendering, byte-identical to upstream behavior |
| Apple-structure TTML reconstruction of *NOKIA* | `multi_singer=True`: Drake left/#FF375F, PARTYNEXTDOOR right/#0A84FF, shared hook centered with "Both" badge, `{bg}` ad-libs centered |

Key port-fidelity finding: real Apple TTMLs separate words with literal
whitespace text nodes between spans (Kotlin `hasTrailingSpace` semantics);
naive span iteration drops spaces and merges words. The Kotlin implementation
already handles this correctly.

### Fixes made after verification

1. **Provider preference (`PreferSingerLyricsKey`, default on).** The default
   provider (#1 YouLyPlus) never carries singer metadata, so duets never
   surfaced. `LyricsHelper.getLyrics` now keeps searching past the first
   successful provider until one returns lyrics with
   `LyricsUtils.hasSingerMetadata(...)`, falling back to the first result when
   none do. Toggle: *Appearance → Lyrics → Prefer multi-singer lyrics*.
2. **Unified duet alignment (`singerLineAlignment`/`singerTextAlign`).** The
   previous `when (agent)` only special-cased `v1`/`v2`/`v1000` literals in
   the standard panel; shared composites (`v1+v2`) didn't center in
   Vivi/Metro and `v3`+ vocalists ignored alternation. One shared helper now
   drives all three renderers (standard, Vivi Music, Metro): background →
   center, group/shared → center, `vN` odd → left / even → right, no agent →
   user's text-position preference.
3. **Render-performance scoping in `Lyrics.kt`.** The 10 Hz
   `currentPlaybackPosition` state read moved from the `LazyColumn` scope into
   item lambdas, computed only for branches that animate with it
   (word-timed Vivi items, Metro's word canvas within ±3 lines of the active
   line, LYRICS_V2 active ±1; `isPast` now derives from the displayed line's
   timestamp). Plain LRC lists no longer recompose every item ten times per
   second, which removes the jank most visible with long synced lyrics.
