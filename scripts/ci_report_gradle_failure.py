#!/usr/bin/env python3
"""Emit GitHub Actions error annotations from a Gradle log."""
from __future__ import annotations

import sys
from pathlib import Path

MARKERS = (
    "e: ",
    "error:",
    "ERROR:",
    "What went wrong",
    "FAILURE:",
    "FAILED",
    "Unresolved reference",
    "AAPT",
    "Execution failed",
    "Compilation error",
    "None of the following functions",
    "Type mismatch",
    "No value passed for parameter",
    "Cannot access",
    "Cannot invoke",
)


def annotate(message: str) -> None:
    escaped = (
        message.replace("%", "%25")
        .replace("\r", "%0D")
        .replace("\n", "%0A")
    )
    print(f"::error::{escaped[:6000]}")


def main(argv: list[str]) -> int:
    if len(argv) != 2:
        print("usage: ci_report_gradle_failure.py <gradle-log>", file=sys.stderr)
        return 2
    path = Path(argv[1])
    if not path.exists():
        annotate(f"Gradle log missing: {path}")
        return 1
    lines = path.read_text(errors="replace").splitlines()
    hits: list[str] = []
    for i, line in enumerate(lines):
        if any(m in line for m in MARKERS):
            start = max(0, i - 1)
            end = min(len(lines), i + 2)
            for extra in lines[start:end]:
                if extra not in hits:
                    hits.append(extra)
    kotlin = [
        l
        for l in hits
        if l.startswith("e: ") or "AAPT" in l or "Unresolved" in l or "error:" in l.lower()
    ]
    body = "\n".join(kotlin or hits or lines[-80:])
    chunks = [body[i : i + 3500] for i in range(0, min(len(body), 14000), 3500)]
    for chunk in chunks or ["Gradle failed with no captured output"]:
        annotate(chunk)
    return 0


if __name__ == "__main__":
    raise SystemExit(main(sys.argv))
