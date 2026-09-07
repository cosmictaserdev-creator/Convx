# Ambient Mode

A full-screen landscape view that turns the phone into a music display: album artwork on the left,
synced lyrics on the right, and an artwork-sampled glow — or the track's own animated canvas —
behind both. There is no control row: play/pause is a tap, previous/next is a swipe, progress is a
ring you scrub from the screen edge, and the back button hides itself after three seconds of
inactivity.

Open it from the player's menu; **Settings → Ambient Mode** is where it is configured.

## Settings

| Setting | Default | Range |
| --- | --- | --- |
| Video Canvas | off | uses the current track's canvas instead of the glow |
| Canvas source | Auto | Auto, Echo Music, Apple Music, ViviMusic, Tidal |
| Canvas blur | 12 dp | 0–24 |
| Canvas dim | 42% | 0–75% |
| Lyrics size | the global lyrics size (30 sp) | 16–56 sp |
| Progress ring and ring seeking | on | also gates seek time and seek haptics |
| Track information lower-third | on | title and artist over the artwork, briefly, on track change |
| Tap to play or pause, swipe navigation, slide transitions, auto-hide back button | on | |

The canvas rows and the whole Position & Fit block are disabled while Video Canvas is off, rather
than sitting there silently doing nothing.

## Canvas Position & Fit

By default the video canvas covers the whole 16:9 frame behind everything else, which means a
portrait canvas — 3:4 or 9:16 — is cropped hard at the top and bottom to make it bleed to the
edges. **Canvas Position & Fit** is the alternative: the portrait canvas keeps its own aspect
ratio, is fitted inside the frame at its full height, and is anchored to one side of the layout.
The gradient gets heavier on that side and lighter on the side the lyrics sit on.

All of it is adjustable in the same screen:

| Setting | Default | Notes |
| --- | --- | --- |
| Side width | 48% | 28–72%, in 4% steps. How much of the frame the canvas panel may claim. |
| Canvas-side gradient | 35% | 0–80%. Added on top of the normal Canvas dim strength on the canvas side. |
| Gradient spread | 60% | 20–100%. How far that side reaches toward the lyrics. |
| Lyrics-side gradient | 10% | 0–60%. The lighter veil left on the opposite side. |
| Edge feather | 15% | 0–40%, in ~6.7% steps. Blends the panel edge into the glow instead of a hard line. |
| Canvas side | Auto | Auto keeps the canvas on the artwork side, opposite the lyrics (so it follows layout direction); Left / Right are explicit. |
| Canvas fit | Fit | Fit keeps the whole frame visible; Fill side zooms until the panel is covered and crops; Stretch ignores the aspect ratio. |

Two behaviours are worth knowing about:

* The panel can never be wider than the canvas allows. With **Fit**, the width is clamped to
  `min(side width, frame aspect ÷ canvas aspect)`, so a narrow 9:16 canvas at a wide slider
  setting simply stops growing instead of being cropped. **Fill side** and **Stretch** take the
  slider literally.
* Canvases at or above 1.15 aspect (everything landscape) keep the normal full-width background
  regardless of the setting — the option is about portrait media. The **Side width** row shows
  "Not used for this canvas" in that case.

The same numbers drive a live mockup at the top of the block, with 9:16, 3:4 and 16:9 chips, so the
sliders can be watched instead of guessed. It is not a stylised drawing: it calls
`ambientCanvasPanelFraction`, `ambientCanvasVeilAlphas` and the same `AmbientCanvasVeil`
composable the screen draws through, so what it shows is the geometry the screen will use. It also keeps drawing its own canvas when Video
Canvas is off, which is the point — the layout can be judged on a song with no video attached.

## Where it lives in the code

| File | Responsibility |
| --- | --- |
| `ui/screens/ambient/AmbientModeScreen.kt` | The screen: glow or video canvas behind the artwork/lyrics split, the progress ring, the gesture arena, the lower-third and the auto-hiding back button. |
| `ui/screens/ambient/AmbientVideoCanvas.kt` | `PlayerView` wrapper; applies the resolved resize mode for the current fit setting. |
| `ui/screens/ambient/AmbientCanvasPositionFit.kt` | The maths, with no Compose state in it: `ambientCanvasPanelFraction`, `ambientCanvasUsesSidePanel`, `ambientCanvasAnchoredRight`, `ambientCanvasVeilAlphas`, `ambientCanvasVeilStops`, `AmbientCanvasVeil`, `Modifier.ambientCanvasEdgeFeather`, and `AmbientCanvasFitDefaults` (the slider defaults and ranges). |
| `ui/screens/ambient/AmbientCanvasFitPreview.kt` | The settings mockup, including its own `ambientCanvasPreviewContentSize`, which mirrors how `AspectRatioFrameLayout` fits a source into a box. |
| `ui/screens/settings/AmbientModeSettings.kt` | The settings UI, and where each default is applied. |
| `constants/PreferenceKeys.kt` | The eight `ambientCanvas*` keys plus `AmbientCanvasAnchorSide` and `AmbientCanvasFitMode`. |

The gradient is not painted over the canvas: a linear-gradient veil is drawn on top, from
`canvasDim + sideGradient` at the anchored edge down to `farVeil` at the other. Separately, the
trailing edge of the panel — the one facing the lyrics — is erased with a `DstIn` mask, so it
dissolves into the glow instead of ending on a straight line. The canvas itself is fitted by the
`AspectRatioFrameLayout` in `AmbientVideoCanvas`, which is why **Fill side** maps to
`RESIZE_MODE_ZOOM` and **Stretch** to `RESIZE_MODE_FILL`.

Unit tests: `app/src/test/kotlin/com/convxy/music/ui/screens/ambient/AmbientCanvasPositionFitTest.kt`
(12 cases) pins the clamp and aspect rules, the anchor fallback, the veil arithmetic and the preview
geometry, so a slider range or a default that moves without the maths changing shows up as a
failure.
