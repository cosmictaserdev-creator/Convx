# Security Policy

## Supported Versions

Only the latest release (and the current nightly/beta build) are supported with security fixes.
Older versions won't receive backports — please update before reporting an issue.

## Reporting a Vulnerability

Please **do not** open a public GitHub issue for security vulnerabilities.

Report it privately via
[GitHub Security Advisories](https://github.com/MeYashverma/Convxy/security/advisories/new) for
this repo, or reach out on [Discord](https://discord.gg/GquSGfs2u) to arrange a private disclosure.

Include:

- A description of the vulnerability and its impact.
- Steps to reproduce (a minimal repro is enough).
- The app version/build you tested on.

We'll acknowledge reports within a few days and aim to ship a fix before any public disclosure.

## Scope

This covers the Convxy app and the code in this repository, including the Listen Together room
server under `listen-together-server/`. It does not cover the third-party services the app talks to
(YouTube Music, YouTube, Discord, Last.fm, Spotify and the lyrics providers) — please report issues
in those to their respective owners.
