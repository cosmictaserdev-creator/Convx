---
name: convx-grill-me
description: Automatically clarify substantial Convx feature, UX, and architecture requests before implementation. Use when product behavior, edge cases, architecture boundaries, data ownership, privacy, legality, or interaction design has unresolved choices that materially affect the solution.
---

# Convx Grill Me

Use this skill before implementing a substantial feature or design change when a consequential requirement remains unclear. It helps agents ask the smallest useful question set and reach shared understanding without making the user answer questions the repository can answer.

## Workflow

1. Read the applicable instructions, inspect the relevant modules and neighboring implementation, and search the repository for existing decisions or patterns before asking anything.
2. Write down the unresolved choices that could change user-visible behavior, architecture, data handling, privacy, licensing, compatibility, or scope.
3. Ask one concise question at a time. Include a recommended answer and briefly say what it changes. Wait for the answer before doing work that depends on it.
4. Continue independent discovery or implementation while waiting when it does not assume the unresolved choice.
5. Repeat only while a material decision remains open. Summarize the agreed behavior before implementing a broad feature.

## When to ask

Ask when multiple plausible choices produce meaningfully different behavior or architecture, or when requirements for failure handling, destructive actions, permissions, privacy, offline behavior, accessibility, or data retention are missing.

Do not ask questions that can be answered by reading code, configuration, tests, existing screens, or repository guidance. Do not grill a clear bug fix, a small low-risk change, or a request with explicit acceptance criteria. Do not ask for approval on reversible work that the user already authorized. Avoid speculative questions about implementation details that do not change the result.

## Convx-specific checks

For UI work, clarify the interaction and relevant edge cases only if they cannot be inferred from the current screen and Convx's HIG. Follow the existing Cupertino-style and Liquid Glass language, and use the correct backdrop for popups and sheets. New UI should use purposeful Compose micro-interactions without forcing decorative motion.

For architecture work, inspect current Gradle modules and dependency direction first. Convx uses Hilt and an existing multi-module structure. Do not assume a generic skill's Koin, Compose Navigation, or feature-module architecture applies here.

For persistence, network, playback, and integrations, consider offline behavior, retries, cancellation, duplicate actions, lifecycle recovery, and compatibility before asking the user about any unresolved product policy.

Use [grill-me](../grill-me/SKILL.md) and [grilling](../grilling/SKILL.md) for the interview style. Use [domain-modeling](../domain-modeling/SKILL.md) and [decision-mapping](../decision-mapping/SKILL.md) when modeling a complex feature or tracing dependent product decisions. Use [grill-with-docs](../grill-with-docs/SKILL.md) only when the user wants decision documents; do not recreate the deleted `docs/` directory unless asked.
