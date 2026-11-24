# Convx Agent Contribution Guide

**Project owner:** Cosmic Taser (`@cosmictaserdev-creator`)

This guide is the contribution contract for AI agents working in Convx. Read it before exploring or changing code. Use the bundled skills under [`contributor/skills/`](contributor/skills/) when their topic applies. The Android Compose and Convx motion skills are required for UI work.

## Project overview
 

Convx is an open-source Android music player built with Kotlin, Jetpack Compose, Media3, and a custom Liquid Glass visual language. It streams and manages music, provides lyrics and integrations, and supports local and network storage. The UI should feel calm, responsive, accessible, and native to Convx.

The repository is a Gradle multi-module Android project. Current modules are:

| Module | Responsibility |
| --- | --- |
| `app/` | Android application, app navigation, screens, ViewModels, playback service, persistence, settings, and feature wiring. |
| `hig/` | Shared Cupertino-style controls, theme primitives, accessibility helpers, and glass/backdrop utilities. |
| `innertube/` | YouTube Music / InnerTube client and related models. |
| `lyrics/` | Lyrics providers, parsing, and presentation support. |
| `canvas/` | Animated canvas and catalog-related functionality. |
| `artwork/` | Artwork loading, caching, and presentation support. |
| `spotify/` | Spotify authentication and import integration. |
| `integrations/` | External integrations such as Discord and music recognition. |
| `network-storage/` | Network music storage, including SMB and WebDAV support. |
| `spine/` | Add-on module hosting and lifecycle. |

`listen-together-server/` is a separate server component. Keep Android changes out of it unless the task requires coordinated server behavior.

Before placing code, inspect the current module and neighboring implementation. The codebase is the source of truth when a generic bundled skill suggests a different setup. The KMP migration has selected Koin for dependency injection across Android and the new clients. Move the existing Hilt graph incrementally with each feature migration; do not add Hilt bindings for features already migrated to Koin. Do not migrate a feature to a new architecture as part of an unrelated change.

## Contribution owner and scope

The repository owner is Cosmic Taser. Agents contribute changes for review; do not claim ownership of the project or impersonate a human contributor. Keep the diff focused on the requested feature or fix. Preserve unrelated user changes and avoid drive-by reformatting.

Do not mention another application or project as the source of an implementation in product copy, comments, documentation, or commit content. Describe Convx's behavior on its own terms. Follow licenses and attribution requirements for all dependencies and assets.

## Code quality and design

- Write Kotlin that is direct, readable, and easy to change. Prefer clear names and small functions with one responsibility over clever abstractions or compressed expressions.
- Use low-level design deliberately: make responsibilities explicit, keep dependencies pointed toward stable contracts, encapsulate mutable state, and expose the smallest useful API.
- Reuse existing project abstractions before introducing a new one. Avoid duplicate state, parallel implementations, speculative frameworks, and abstractions used only once without a clear boundary.
- Keep UI rendering, state coordination, domain rules, persistence, and remote access in their existing responsibilities. Composables render state and report actions. ViewModels coordinate screen state. Data access stays behind the existing data/service boundaries.
- Use Koin for new shared/KMP code and Android features. The Android app uses Koin as its only dependency injection container. Keep one composition root per app, constructors explicit, and shared code free of platform-specific dependencies.
- Preserve coroutine cancellation. Do not swallow `CancellationException`; handle expected failures explicitly and present useful recoverable states.
- Prefer immutable state and stable item keys for lazy lists. Avoid work, allocations, or state reads that cause avoidable recompositions, especially inside frequently recomposed or scrolling content.
- Use current stable Android/Kotlin/Compose APIs already supported by this repository. Check the version catalog and existing patterns before adding or upgrading a dependency. Do not add experimental APIs without a concrete need and suitable opt-in.
- Keep comments minimal. Add a comment only when it explains a non-obvious reason, invariant, platform workaround, or safety constraint. Remove stale or misleading comments; never leave commented-out code.

### Naming and file placement

Follow neighboring code first. Use these conventions when there is no established local pattern:

- Kotlin types and Compose functions use `PascalCase`: `PlaylistViewModel`, `GlassMenu`, `PlaybackState`.
- Functions, properties, and packages use `camelCase`: `loadPlaylist`, `isPlaying`, `playback`.
- Constants use the local convention; prefer descriptive `UPPER_SNAKE_CASE` for true compile-time constants.
- Name files after their primary public type or feature responsibility: `PlaylistViewModel.kt`, `GlassMenu.kt`, `PlaylistRepository.kt`. Avoid generic names such as `Utils.kt`, `Common.kt`, or `Manager.kt` unless the existing package has a clear convention.
- Keep related private helpers beside their single caller. Split a file when it has multiple independent responsibilities or becomes difficult to navigate; do not split every small declaration into its own file.
- Use resource names that describe meaning and context, and put user-facing text in Android string resources so it can be translated.
- Name functions by observable behavior (`loadQueue`, `formatDuration`, `onTrackSelected`). Avoid vague names (`handle`, `process`, `doStuff`) and redundant suffixes.

### When to create a module

Keep work in the current module by default. Create a Gradle module only when at least one durable boundary justifies the added build and dependency cost:

1. The code is a cohesive capability with a clear API and independent lifecycle or platform boundary.
2. It is reused by multiple modules and extracting it reduces coupling rather than just moving files.
3. It needs meaningful dependency isolation, such as keeping platform or vendor libraries away from the app UI.
4. It is large enough to own its implementation, tests, and dependencies independently.

Do not create a module for a handful of files, a single screen, or organization alone. Before creating one, inspect the closest existing module, define what belongs inside and what its public API is, and confirm the dependency direction. Use a concise lowercase module name with hyphens when appropriate (for example, `network-storage`). Add it to `settings.gradle.kts`, give it a focused Gradle build file, and avoid dependencies from a lower-level module back into `app/`. Explain the boundary in the change summary.

## UI, HIG, and glass surfaces

- Follow Convx's HIG and existing design language. Look at `hig/`, nearby screens, themes, and existing shared components before designing a control.
- Do not build new UI with Material Design components or Material theming. Use the project’s Cupertino-style and custom Convx components. Existing non-visual AndroidX/Compose infrastructure is fine where the project already uses it.
- For a popup menu, inspect `CupertinoDropdownMenu` and current glass menu patterns. For a bottom sheet, inspect `CupertinoModalBottomSheet` and existing Convx sheet implementations. Use the glass surface that samples the correct nearby backdrop; do not approximate it with a plain translucent fill when an established glass component applies.
- Use the correct backdrop source and surface scope. A glass surface should sample the content behind its intended visual region, remain legible over changing artwork, and avoid stacking translucent glass surfaces in a way that destroys contrast.
- Match the existing corner radii, spacing, typography, tonal hierarchy, and light/dark behavior. Use theme values rather than unexplained hard-coded colors.
- Preserve accessibility: meaningful labels for controls, decorative imagery marked as decorative, adequate touch targets, keyboard/focus behavior where relevant, and layouts that tolerate larger font scales and localization.

## Motion and micro-interactions

Use [`contributor/skills/convx-android-motion/SKILL.md`](contributor/skills/convx-android-motion/SKILL.md) for every UI feature that changes or adds interactive behavior. New interactive controls should provide concise micro-feedback when it helps users understand press, selection, progress, success, or dismissal. Motion must serve feedback or spatial understanding; do not animate every element just to add movement.

Use Compose animation APIs and the project's existing motion primitives. Keep transitions interruptible, brief, and consistent with the gesture. Prefer animating draw/layer properties where that avoids per-frame recomposition. Respect Android's animator duration scale and avoid motion that impairs readability, playback controls, or accessibility. Test motion on a real device or emulator when possible, including reduced/disabled animation settings.

## Edge cases and reliability

For each feature, identify edge cases before coding and cover those that apply. For user-facing flows, consider at minimum:

- Empty, single-item, very large, duplicate, stale, malformed, and partially missing data.
- Rapid repeated taps, double submission, competing requests, cancellation, retries, timeouts, and out-of-order responses.
- Offline mode, server errors, authentication expiry, unavailable providers, corrupted cache, and recovery after reconnection.
- App background/foreground, process recreation, configuration change, navigation away during work, and playback service disconnect/reconnect.
- Permission denied, permission revoked, low storage, unavailable media, and inaccessible network shares where relevant.
- Small screens, tablets/wide layouts, landscape, system insets, RTL, long translated strings, large font scale, light/dark theme, and accessibility services.
- Invalid route arguments, missing database records, migration paths, and backward compatibility for persisted data.

Do not add defensive branches for impossible states without reason. Make invalid states unrepresentable where practical, and provide a recoverable UI state when an external system can fail.

## Legal, privacy, and assets

Do not add illegal, unauthorized, or copyrighted material. Do not copy code, artwork, audio, fonts, icons, branding, or text from another project or service unless its license explicitly allows the intended use and required notices are included. Prefer existing licensed assets and the project's icon set. Keep dependency licenses and notices intact. Do not add secrets, private keys, tokens, personal data, or tracking without an approved product requirement. Respect upstream service terms and Convx's privacy commitments.

## Tests and verification

Test the feature, not just the happy path. Add or update tests for meaningful business logic, state transitions, persistence, parsing, and edge cases. UI changes need visual and interaction review on a device/emulator when available. Verify light/dark appearance, font scaling, and wide layouts when relevant. Do not claim a test passed unless it was run successfully.

Use the repo's current build setup and choose the narrowest useful commands, then widen verification when the change crosses module boundaries. Common commands from the contributor guide include:

```bash
./gradlew :app:assembleUniversalFossDebug
./gradlew :app:testUniversalFossDebugUnitTest
```

For changes in a library module, also build or test that module's relevant variant. For native, server, or integration changes, inspect that component's own build and test instructions. If required verification cannot run, state exactly what was attempted and why it could not complete.

Before reporting completion:

1. Review the final diff for accidental files, generated output, secrets, and unrelated edits.
2. Check names, architecture boundaries, accessibility, failure states, and the relevant edge cases.
3. Run the relevant tests and builds requested or required by the change.
4. Summarize what changed and list the verification that actually ran.

## Requirement discovery before implementation

For a substantial feature, broad redesign, or ambiguous UX/architecture request, automatically use [Convx Grill Me](contributor/skills/convx-grill-me/SKILL.md) before making dependent implementation choices. Inspect the repo first and ask one focused question at a time only when the answer materially changes behavior, architecture, safety, privacy, licensing, or scope. Include a recommended answer. Do not ask questions the codebase can answer, and do not slow down clear low-risk work with unnecessary grilling. If an answer is required, pause only the dependent work and continue independent discovery.

## Bundled skills

The complete skill folders are checked into [`contributor/skills/`](contributor/skills/). They work offline and include their companion references and recipes. Use the Convx-specific skills as the primary rules; generic skills provide additional techniques.

### Clarifying requirements and modeling decisions

- [Convx Grill Me](contributor/skills/convx-grill-me/SKILL.md) — automatically clarify consequential unknowns before substantial implementations.
- [Grill Me](contributor/skills/grill-me/SKILL.md) and [Grilling](contributor/skills/grilling/SKILL.md) — interview approach; ask one question at a time and recommend an answer.
- [Grill with Docs](contributor/skills/grill-with-docs/SKILL.md) — optional decision docs when requested; do not recreate `docs/` unless asked.
- [Domain modeling](contributor/skills/domain-modeling/SKILL.md) and [Decision mapping](contributor/skills/decision-mapping/SKILL.md) — model complex domains and dependencies between product choices.

### Android implementation

- [Android Compose UI](contributor/skills/android-compose-ui/SKILL.md) — Compose state, effects, recomposition, animation, previews, and accessibility.
- [Android data layer](contributor/skills/android-data-layer/SKILL.md) — data sources, repositories, mapping, persistence, and networking.
- [Android error handling](contributor/skills/android-error-handling/SKILL.md) — typed failures and coroutine/network errors.
- [Android module structure](contributor/skills/android-module-structure/SKILL.md) — module boundaries and Gradle organization.
- [Android navigation](contributor/skills/android-navigation/SKILL.md) — navigation patterns; use Convx's current navigation setup.
- [Android performance](contributor/skills/android-performance-skill/SKILL.md) — profiling and performance guidance.
- [Android presentation / MVI](contributor/skills/android-presentation-mvi/SKILL.md) — state and ViewModel patterns; follow local code where it differs.
- [Android testing](contributor/skills/android-testing/SKILL.md) — unit, UI, and integration test guidance.
- [Android dependency injection](contributor/skills/android-di-koin/SKILL.md) — Koin module definitions, app assembly, and Compose ViewModel injection for migrated Android/KMP features.

### Animation and interaction design

- [Convx Android motion](contributor/skills/convx-android-motion/SKILL.md) — required motion rules for Compose UI in this project.
- [Animate](contributor/skills/animate/SKILL.md) — web animation implementation; use its reasoning only, not its web code, for Android.
- [Animate Expo](contributor/skills/animate-expo/SKILL.md) — React Native and Expo only; not an implementation guide for Compose.
- [Animation vocabulary](contributor/skills/animation-vocabulary/SKILL.md) — motion terminology.
- [Find animation opportunities](contributor/skills/find-animation-opportunities/SKILL.md) — identify purposeful places for motion.
- [Improve animations](contributor/skills/improve-animations/SKILL.md) and [Review animations](contributor/skills/review-animations/SKILL.md) — audit or critique existing motion when requested.
- [Apple design](contributor/skills/apple-design/SKILL.md) and [Emil design engineering](contributor/skills/emil-design-eng/SKILL.md) — interaction and craft principles; translate concepts into native Compose and Convx HIG components.

### Code quality and design workflow

- [Anti-slop code](contributor/skills/antislop-code/SKILL.md) and [Anti-slop UI](contributor/skills/antislop-ui/SKILL.md) — keep implementation and interfaces specific, clear, and restrained.
- [Code review](contributor/skills/code-review-skill/SKILL.md) — review changes for correctness, maintainability, and edge cases.
- [Prototype](contributor/skills/prototype/SKILL.md) — use for interactive design exploration when a prototype is requested; do not treat its web tooling as app implementation.
- [Pick UI library](contributor/skills/pick-ui-library/SKILL.md) — reference for UI technology choices; follow Convx's existing Compose stack and avoid Material Design components.

The bundled skills are references, not instructions to migrate the codebase. If a skill conflicts with this guide or established Convx architecture, follow this guide and the project implementation.
