#!/usr/bin/env python3
"""Build deterministic updater metadata for APKs attached to one GitHub release."""
import argparse
import hashlib
import json
import re
from pathlib import Path


def main():
    parser = argparse.ArgumentParser()
    parser.add_argument("--version", required=True)
    parser.add_argument("--version-code", required=True, type=int)
    parser.add_argument("--output", default="updater.json")
    parser.add_argument("--image")
    parser.add_argument("--release-notes")
    parser.add_argument("--nightly-run", type=int, default=0)
    parser.add_argument("--display-version")
    parser.add_argument("apks", nargs="+")
    args = parser.parse_args()
    if args.version_code <= 0 or args.nightly_run < 0 or args.nightly_run > 9_998:
        parser.error("version code must be positive and nightly run number must be between 0 and 9998")

    notes = Path(args.release_notes).read_text(encoding="utf-8") if args.release_notes and Path(args.release_notes).is_file() else ""
    changelog = []
    title = "What’s new"
    lines = []
    for line in notes.splitlines():
        value = line.strip()
        if value.startswith("#"):
            if lines:
                changelog.append({"title": title, "items": lines})
            title = value.lstrip("# ").strip() or "What’s new"
            lines = []
        elif re.match(r"(?:[-*+] |\d+[.)] )", value):
            lines.append(re.sub(r"^(?:[-*+] |\d+[.)] )", "", value))
        elif value and not value.startswith(("<!--", "![", "[")):
            lines.append(value)
    if lines:
        changelog.append({"title": title, "items": lines})
    if not changelog:
        changelog = [{"title": "What’s new", "items": ["Bug fixes and improvements"]}]

    assets = []
    for value in args.apks:
        path = Path(value)
        name = path.name
        match = re.fullmatch(r"convx-(universal|arm32)-(gms|foss)\.apk", name)
        if not match or not path.is_file() or path.stat().st_size == 0:
            parser.error(f"unexpected or empty APK: {path}")
        abi, variant = match.groups()
        digest = hashlib.sha256()
        with path.open("rb") as apk:
            for block in iter(lambda: apk.read(1024 * 1024), b""):
                digest.update(block)
        assets.append({"name": name, "size": path.stat().st_size, "sha256": digest.hexdigest(),
                       "versionCode": args.version_code + args.nightly_run,
                       "variant": variant, "abis": ["arm64-v8a", "x86_64"] if abi == "universal" else ["armeabi-v7a"],
                       "minSdk": 26})
    if not assets:
        parser.error("at least one APK is required")

    summary_parts = []
    for line in notes.splitlines():
        value = line.strip()
        if not value:
            if summary_parts:
                break
            continue
        if value.startswith(("#", "- ", "* ", "+ ", "![", "[")):
            continue
        value = re.sub(r"\[([^\]]+)\]\([^)]+\)", r"\1", value)
        value = re.sub(r"[`*_~]", "", value)
        summary_parts.append(value)
    summary = " ".join(summary_parts)[:800]
    result = {"schemaVersion": 1, "packageName": "com.convx.music", "version": args.display_version or args.version,
              "channel": "nightly" if args.nightly_run else "stable",
              "versionCode": args.version_code + args.nightly_run,
              "description": summary or f"Convx {args.version}", "changelog": changelog,
              "assets": sorted(assets, key=lambda item: (item["variant"], item["name"]))}
    if args.image:
        if not args.image.startswith("https://") or len(args.image) > 2048:
            parser.error("image must be an HTTPS URL")
        result["image"] = args.image
    Path(args.output).write_text(json.dumps(result, ensure_ascii=False, indent=2) + "\n", encoding="utf-8")


if __name__ == "__main__":
    main()
