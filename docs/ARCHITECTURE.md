# Convx architecture

Convx is one repository with one Android app and three KMP clients (iOS, desktop, watchOS) that share Kotlin Multiplatform modules.

```text
app/                    Production Android app (Media3, Room schema 40, Koin)
clients/kmp/            Separate Gradle build for the KMP hosts
  shared/               Compose Multiplatform client shell + shared Koin wiring
  androidHost/          Small Android host that exercises the shared client
  desktopApp/           Compose Desktop host (JavaFX/FFmpeg playback, packaging)
  iosApp/  watchApp/    SwiftUI hosts (Xcode)

core/                   Shared by every feature
  innertube/            YouTube Music requests and response mapping (:innertube)
  design-system/        Tokens, fonts (:design-system)
  hig/                  Convx controls, symbols, glass components (:hig)

feature/<name>/         One folder per feature, split by layer
  domain/               Models and contracts, pure commonMain (:<name>-core)
  data/                 Implementations: network, Room, files (:<name>-data)
  presentation/         State holders and Compose UI (:<name>-presentation)

canvas/ spine/ network-storage/ spotify/ integrations/ artwork/
                        Android-only libraries the app still uses
```

Features today: account, canvas, catalog, history, library, listen-together, lyrics, modules, network-storage, playback, search.

## Rules

- Dependencies point `presentation -> domain <- data`. `domain` never imports Android, Media3, Room, Java networking or NewPipe.
- Platform work (stream resolution, playback engine, secure storage) sits behind a `domain` interface and is implemented per host.
- Each host owns its Koin composition root. The Android app's list is `app/.../di/AppModules.kt`; `KoinGraphTest` fails the build when a binding is missing.
- A fix in `core/` or `feature/*` reaches every client that depends on that module. A fix in `app/` reaches Android only.

## Gradle names vs folders

Gradle project names did not change when the folders moved, so `project(":playback-core")` still works. The mapping lives in `settings.gradle.kts` and `clients/kmp/settings.gradle.kts`.

## Not done yet

- `clients/kmp` is still a second Gradle build with its own version catalog. Merge it into the root build once iOS CI is back as the safety net.
- The Android-only libraries at the root (`canvas/`, `spine/`, ...) should fold into their `feature/` folders as they become multiplatform.
