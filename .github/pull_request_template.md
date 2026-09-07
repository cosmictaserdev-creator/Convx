## What does this change?

<!-- One paragraph. What a reviewer would notice, not which files you touched. -->

Fixes #

## How was it tested?

<!-- Device/emulator + Android version, and the path you actually took. For playback or Ambient
changes say whether you tested with the video canvas on and off. -->

## Screenshots or recording

Required for anything UI-visible. A short recording is better than three stills if the change moves.

## Checklist

- [ ] Ran the change on a device or emulator, not just a compile
- [ ] `./gradlew :app:testUniversalGmsDebugUnitTest` passes (add a test if the change is logic, e.g. fitting, parsing, queue math)
- [ ] New UI strings live in `vivi_strings.xml` / `strings.xml`, not hard-coded
- [ ] Settings-backed behaviour keeps its old default for people who never touch the new switch
- [ ] Updated the relevant doc in `docs/` when the change is user-facing
- [ ] No unrelated reformatting in the diff

## Notes for review

<!-- Anything you'd want to know before reading the diff: a workaround, a thing you tried that
didn't work, an API that might change under you. -->
