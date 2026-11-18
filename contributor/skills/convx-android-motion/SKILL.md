---
name: convx-android-motion
description: Compose motion and micro-interaction standards for Convx. Use when adding or changing interactive Android UI, transitions, menus, sheets, loading feedback, gestures, or animated glass surfaces.
---

# Convx Android Motion

Use this skill for Android UI motion in Convx. Follow the repository's HIG and existing Compose components. Motion is part of the interaction design and must remain useful, restrained, and accessible.

## Start with the reason

Before adding an animation, identify the user event it explains: press, selection, progress, state change, arrival, dismissal, or gesture response. Add a short micro-interaction when it makes that event clearer or gives timely feedback. Keep static content still when movement adds no information. Do not add looping decoration or animate every property by default.

## Use the native stack

- Use Jetpack Compose animation APIs already present in the project, such as `animate*AsState`, `AnimatedVisibility`, `updateTransition`, and `Animatable`, when appropriate.
- Prefer an existing Convx component or motion helper over a one-off animation implementation.
- Keep business state in the existing ViewModel/state owner. Use `remember` only for composition-owned state such as animation controllers and gesture state.
- Keep animated values out of frequently read composition when practical. Use layer/draw-phase reads for per-frame alpha, scale, rotation, and translation where that avoids repeated composition or layout work.
- Never start unmanaged animation coroutines from a composable. Use Compose animation APIs and lifecycle-aware effects for effects that genuinely belong in composition.
- Preserve gesture input while an animation is running. A user should be able to interrupt, reverse, or dismiss an interactive transition without waiting for it to finish.

## Timing and feel

- Make press feedback immediate and subtle. Do not delay action processing until an animation completes.
- Use springs for gesture-driven or interruptible movement. Use a short tween for simple fades or color changes when a spring has no benefit.
- Start with a calm, low-bounce response. Add overshoot only when a physical gesture or momentum makes it understandable.
- Keep enter and exit motion spatially consistent. Menus and popovers should feel connected to their trigger; sheets should follow the same direction and surface behavior on entry and dismissal.
- Avoid animating large areas in ways that distract from album art, lyrics, or playback controls. Keep text readable during motion.

## Glass, menus, and sheets

- Use the correct Convx backdrop and glass component. Follow existing `Modifier.liquidGlass(...)` usage and the nearest backdrop ownership pattern.
- Prefer the existing Cupertino dropdown and modal sheet components. Their motion, insets, shape, scrim, and dismissal behavior are part of the design system.
- Do not use Material Design surfaces, dialogs, menus, or bottom sheets for new UI.
- Avoid stacking glass layers. Make sure text and controls remain legible as the sampled background changes.

## Accessibility and edge cases

- Respect Android's system animator duration scale. Keep state changes understandable when animations are disabled or reduced.
- Preserve touch targets, semantics, focus behavior, and gesture cancellation. A drag that returns to its origin should not trigger the completed action.
- Check rapid retargeting, repeated taps, interruption, dismissal during loading, screen rotation, lifecycle pause/resume, and content changes during transitions.
- Do not encode meaning with motion alone. Pair it with labels, state, or other feedback.
- Avoid high-frequency flashing, large unexpected movement, and motion that blocks playback or navigation.

## Review checklist

- [ ] The motion has a clear user-facing purpose and is brief.
- [ ] Press, selection, loading, and dismissal states respond at the correct time.
- [ ] Gestures remain interruptible and do not create conflicting input states.
- [ ] Per-frame work avoids unnecessary recomposition and layout.
- [ ] Menus, sheets, and glass use the established Convx primitives and correct backdrop.
- [ ] Disabled animations, accessibility, light/dark themes, and changing content remain understandable.
- [ ] Motion was reviewed on an emulator or device when the change affects user-visible transitions.
