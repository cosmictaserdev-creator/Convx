#!/usr/bin/env python3
"""One-shot Convx -> Convxy identity rename.

Kept in the repo (rather than run and thrown away) so the exact set of textual
substitutions the rebrand made is reviewable, and so anyone with a stale branch
can re-apply it. Every rule below is a *whole-token* style replacement with an
explicit allow/deny list: identifiers that must NOT move (live third-party
endpoints, user-data file formats, upstream attribution) are never touched.

Usage:  python3 scripts/rename_convxy.py [--check]
  --check   report remaining occurrences and exit non-zero if any rule still applies
"""

from __future__ import annotations

import os
import re
import subprocess
import sys

ROOT = os.path.dirname(os.path.dirname(os.path.abspath(__file__)))

# Binary / generated / vendored paths we never rewrite.
SKIP_SUFFIXES = (
    ".png", ".jpg", ".jpeg", ".webp", ".gif", ".svg", ".ico", ".dm", ".keystore",
    ".jks", ".jar", ".zip", ".apk", ".aab", ".ttf", ".otf", ".woff", ".woff2",
    ".csv", ".xlsx", ".pdf", ".so", ".bin",
)

# Exact repo-relative paths deliberately left alone.
SKIP_PATHS = {
    # Upstream Listen Together sync deployment: the *live* worker name and its
    # URL. Renaming it would point the app's default server list at a worker
    # that does not exist.
    "listen-together-server/package.json",
    "listen-together-server/package-lock.json",
    "listen-together-server/wrangler.toml",
    "listen-together-server/README.md",
    # A local analytics export, not part of the build.
    ".gitignore",
}

# NOTE: ListenTogetherServers.kt is deliberately *not* skipped — its package
# declaration has to move with everything else. The strings in it that must stay
# put ("Convx Sync", the convx-sync worker URL, operator "Convx") are live
# third-party deployment identifiers, and no rule below matches them.

# (regex, replacement) applied in order. Rules are narrow on purpose.
RULES: list[tuple[str, str]] = [
    # --- Android application identity -------------------------------------
    # namespace, applicationId, package declarations/imports, fully-qualified
    # references, widget/listen-together intent actions, Cast options provider
    # class, shortcuts targetClass/targetPackage, ProGuard/R8 keeps, Compose
    # stability config, library-module namespaces.
    (r"\bcom\.convx\b", "com.convxy"),
    (r"\bcom\.convx\.", "com.convxy."),
    # Path form used in docs / comments.
    (r"\bcom/convx\b", "com/convxy"),
    (r"\bcom/convx/", "com/convxy/"),

    # --- Gradle root project ----------------------------------------------
    (r'rootProject\.name = "convx"', 'rootProject.name = "convxy"'),

    # --- Theme styles ------------------------------------------------------
    (r"\bTheme\.Widget\.Convx\b", "Theme.Widget.Convxy"),
    (r"\bTheme\.Convx\b", "Theme.Convxy"),

    # --- Brand drawables ---------------------------------------------------
    (r"\bconvx_logo\b", "convxy_logo"),
    (r"\bconvx_notification\b", "convxy_notification"),

    # --- CI build artifacts + the in-app updater URLs that consume them ----
    (r"\bconvx-gms-nightly\b", "convxy-gms-nightly"),
    (r"\bconvx-\$\{TAG\}\.apk\b", "convxy-${TAG}.apk"),
    (r"\bconvx-\$versionName\.apk\b", "convxy-$versionName.apk"),

    # --- Public-facing product name ---------------------------------------
    (r'"Convx Debug"', '"Convxy Debug"'),
    (r'"Convx playback error"', '"Convxy playback error"'),
    (r'"convx_crash_', '"convxy_crash_'),
    (r'"Convx Crash Report"', '"Convxy Crash Report"'),
    (r'"Convx Debug Log"', '"Convxy Debug Log"'),
    (r'"Convx client probe"', '"Convxy client probe"'),
    (r'\\"Convx Music\\"', '\\"Convxy Music\\"'),
    (r'"ConvxMusic/1\.5"', '"ConvxyMusic/1.5"'),
    (r'"Visit Convx"', '"Visit Convxy"'),
    (r"found Convx recently", "found Convxy recently"),
    (r'PLAYLIST_NAME = "Convx \$YEAR"', 'PLAYLIST_NAME = "Convxy $YEAR"'),
    (r'DIRECTORY_PICTURES \+ "/Convx"', 'DIRECTORY_PICTURES + "/Convxy"'),
    (r'replaced with "Convx"', 'replaced with "Convxy"'),
    (r'"Convx \$TAG - \$TYPE"', '"Convxy $TAG - $TYPE"'),
    (r'"Convx Music - Open Source"', '"Convxy Music - Open Source"'),
    (r"<!-- Convx monogram", "<!-- Convxy monogram"),
]

COMPILED = [(re.compile(p), r) for p, r in RULES]


def tracked_files() -> list[str]:
    out = subprocess.run(
        ["git", "ls-files", "-z"], cwd=ROOT, capture_output=True, check=True
    ).stdout
    return [p for p in out.decode("utf-8").split("\0") if p]


def should_skip(path: str) -> bool:
    if path in SKIP_PATHS:
        return True
    if path.endswith(SKIP_SUFFIXES):
        return True
    if path.startswith("scripts/rename_convxy.py"):
        return True
    return False


def main() -> int:
    check_only = "--check" in sys.argv
    changed = 0
    for rel in tracked_files():
        abs_path = os.path.join(ROOT, rel)
        if not os.path.isfile(abs_path) or should_skip(rel):
            continue
        try:
            with open(abs_path, "r", encoding="utf-8") as fh:
                original = fh.read()
        except (UnicodeDecodeError, OSError):
            continue

        text = original
        for pattern, repl in COMPILED:
            text = pattern.sub(repl, text)
        if text == original:
            continue
        changed += 1
        if not check_only:
            with open(abs_path, "w", encoding="utf-8") as fh:
                fh.write(text)
        else:
            print(f"would rewrite {rel}")

    print(f"{'files needing rewrite' if check_only else 'files rewritten'}: {changed}")
    return 1 if (check_only and changed) else 0


if __name__ == "__main__":
    sys.exit(main())
