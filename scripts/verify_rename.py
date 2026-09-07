#!/usr/bin/env python3
"""Static consistency checks for the Convx -> Convxy rename.

There is no JDK/Android SDK in this environment, so `./gradlew assemble*` cannot
run here. These checks cover, statically, the things a package rename actually
breaks:

  1. package declaration <-> source directory agreement
  2. every internal `com.convxy.*` import resolves to a real declaration
  3. manifest `android:name` components resolve to real classes
  4. `@style/` and `@drawable/` references resolve to real resources
  5. no `com.convx` / `com/convx` identifier survives anywhere
  6. Gradle namespaces agree with the source tree they own

Run:  python3 scripts/verify_rename.py
"""

from __future__ import annotations

import os
import re
import subprocess
import sys
from collections import defaultdict

ROOT = os.path.dirname(os.path.dirname(os.path.abspath(__file__)))
FAILURES: list[str] = []
WARNINGS: list[str] = []


def tracked() -> list[str]:
    """Every file that is part of the working tree, tracked or not.

    `--others --exclude-standard` matters: a rename (or any feature branch) adds files, and checking
    only the index would silently skip exactly the code that is newest and least proven. Ignored
    paths are still excluded, so build outputs cannot swamp the scan.
    """
    out = subprocess.run(
        ["git", "ls-files", "-z", "--cached", "--others", "--exclude-standard"],
        cwd=ROOT, capture_output=True, check=True,
    ).stdout
    return [p for p in out.decode().split("\0") if p]


def read(path: str) -> str:
    with open(os.path.join(ROOT, path), "r", encoding="utf-8", errors="replace") as fh:
        return fh.read()


FILES = [p for p in tracked() if os.path.isfile(os.path.join(ROOT, p))]
KT = [p for p in FILES if p.endswith(".kt")]

# ---------------------------------------------------------------------------
# Collect declarations: fully-qualified top-level names available in the repo.
# ---------------------------------------------------------------------------
PACKAGE_RE = re.compile(r"^\s*package\s+([\w.]+)", re.MULTILINE)
DECL_RE = re.compile(
    r"^\s*(?:@\w+(?:\([^)]*\))?\s+)*(?:public\s+|internal\s+|private\s+|abstract\s+|open\s+|sealed\s+|data\s+|value\s+|enum\s+|annotation\s+|inner\s+|companion\s+|suspend\s+|inline\s+|expect\s+|actual\s+|)*"
    r"(?:class|interface|object|fun|val|var|typealias)\s+([A-Za-z_]\w*)",
    re.MULTILINE,
)

declared: set[str] = set()          # com.convxy.x.y.Name
declared_packages: set[str] = set() # com.convxy.x.y
file_packages: dict[str, str] = {}

for path in KT:
    text = read(path)
    m = PACKAGE_RE.search(text)
    if not m:
        continue
    pkg = m.group(1)
    file_packages[path] = pkg
    declared_packages.add(pkg)
    for d in DECL_RE.findall(text):
        declared.add(f"{pkg}.{d}")

# Java-style: enum entries / nested types are covered well enough by the above
# for import resolution purposes.

# ---------------------------------------------------------------------------
# 1. package declaration <-> directory
# ---------------------------------------------------------------------------
for path, pkg in file_packages.items():
    expected_tail = pkg.replace(".", "/")
    norm = path.replace("\\", "/")
    # source root is everything before the package path
    idx = norm.find(expected_tail + "/")
    if idx == -1:
        # Kotlin (unlike Java) does not require the directory to mirror the
        # package, and two files in this repo already rely on that. Report it,
        # don't fail on it.
        WARNINGS.append(f"[pkg/dir] {path}: declares `{pkg}` but is not under .../{expected_tail}/")

# ---------------------------------------------------------------------------
# 2. internal imports resolve
# ---------------------------------------------------------------------------
IMPORT_RE = re.compile(r"^\s*import\s+([\w.]+?)(?:\s+as\s+\w+)?\s*$", re.MULTILINE)
unresolved = defaultdict(list)
for path in KT:
    for imp in IMPORT_RE.findall(read(path)):
        if not imp.startswith("com.convxy."):
            continue
        if imp in declared or imp in declared_packages:
            continue
        # Member imports (`Foo.Companion.BAR`, `Foo.CONST`, enum entries) resolve
        # against an owner *type*, not a package — walk up the segments until one
        # matches a known declaration or package.
        parts = imp.split(".")
        if any(".".join(parts[:i]) in declared or ".".join(parts[:i]) in declared_packages
               for i in range(len(parts), 1, -1)):
            continue
        unresolved[imp].append(path)

for imp, paths in sorted(unresolved.items()):
    FAILURES.append(f"[unresolved import] {imp}  (used by {len(paths)} file(s), e.g. {paths[0]})")

# ---------------------------------------------------------------------------
# 3. manifest components resolve
# ---------------------------------------------------------------------------
NAMESPACES = {}
for path in [p for p in FILES if p.endswith("build.gradle.kts")]:
    m = re.search(r'namespace\s*=\s*"([^"]+)"', read(path))
    if m:
        NAMESPACES[os.path.dirname(path)] = m.group(1)

MANIFEST_RE = re.compile(
    r'<(?:activity|service|receiver|provider|application)\b[^>]*?android:name="(\.?[\w.$]+)"',
    re.DOTALL,
)
for path in [p for p in FILES if p.endswith("AndroidManifest.xml")]:
    module = path.split("/src/")[0]
    ns = NAMESPACES.get(module, "com.convxy.music")
    text = read(path)
    # activity-alias entries are launcher identities, not classes; skip them.
    alias_names = set(re.findall(r'<activity-alias\b[^>]*?android:name="([^"]+)"', text, re.DOTALL))
    for name in MANIFEST_RE.findall(text):
        if name in alias_names:
            continue
        fq = (ns + name) if name.startswith(".") else name
        if not fq.startswith("com.convxy"):
            continue  # androidx.*, com.google.*, com.dpi.*, com.yalantis.* ...
        if fq in declared or fq in declared_packages:
            continue
        FAILURES.append(f"[manifest] {path}: `{name}` -> `{fq}` has no Kotlin declaration")

# ---------------------------------------------------------------------------
# 4. resource references
# ---------------------------------------------------------------------------
res_files = [p for p in FILES if "/res/" in p]
styles = set()
drawables = set()
xmls = set()
for p in res_files:
    base = os.path.basename(p)
    stem, ext = os.path.splitext(base)
    if ext == ".xml" and "/values" in p:
        for m in re.finditer(r'<style\s+name="([^"]+)"', read(p)):
            styles.add(m.group(1))
    elif "/drawable" in p or "/mipmap" in p:
        drawables.add(stem)
    elif "/xml/" in p:
        xmls.add(stem)

# Only unqualified `@style/Foo` refs point at resources *we* must define.
# `@android:style/...` and friends are framework/library resources.
# Styles contributed by AAR dependencies rather than by this repo's res/.
EXTERNAL_STYLES = {
    "Theme.AppCompat.Light.NoActionBar",
}

REF_RE = re.compile(r'@(?!(?:\w+):)(style|drawable|mipmap|xml)/([\w.]+)')
for p in res_files + [x for x in FILES if x.endswith("AndroidManifest.xml")]:
    for kind, name in REF_RE.findall(read(p)):
        pool = {"style": styles, "drawable": drawables, "mipmap": drawables, "xml": xmls}[kind]
        if name in EXTERNAL_STYLES:
            continue
        if name not in pool:
            FAILURES.append(f"[missing {kind}] {p}: @{kind}/{name}")

# R.drawable.* / R.style.* in Kotlin
R_REF_RE = re.compile(r"(?<![.\w])R\.(drawable|style|xml|mipmap)\.(\w+)")
for p in KT:
    for kind, name in R_REF_RE.findall(read(p)):
        pool = {"drawable": drawables, "mipmap": drawables, "style": styles, "xml": xmls}[kind]
        if name not in pool:
            # could belong to a library module's R; only flag app-module refs
            if p.startswith("app/"):
                FAILURES.append(f"[missing R.{kind}] {p}: R.{kind}.{name}")

# ---------------------------------------------------------------------------
# 5. no old identifier survives
# ---------------------------------------------------------------------------
OLD_RE = re.compile(r"com[./]convx\b")
# The rename tooling itself has to spell the old identifier in order to rewrite it, and this
# verifier has to spell it in order to search for it. Skipping them is not a hole in the check: they
# are the only two files in the repo whose *purpose* is to contain the string.
SELF_REFERENTIAL = {"scripts/rename_convxy.py", "scripts/verify_rename.py"}
for p in FILES:
    if p.endswith((".png", ".jpg", ".webp", ".dm", ".svg", ".csv", ".xlsx")):
        continue
    if p.replace("\\", "/") in SELF_REFERENTIAL:
        continue
    for i, line in enumerate(read(p).splitlines(), 1):
        if OLD_RE.search(line):
            FAILURES.append(f"[old identifier] {p}:{i}: {line.strip()[:100]}")

# ---------------------------------------------------------------------------
# 6. namespaces vs source tree
# ---------------------------------------------------------------------------
for module, ns in sorted(NAMESPACES.items()):
    if not ns.startswith("com.convxy"):
        continue
    tail = ns.replace(".", "/")
    src_root = os.path.join(module, "src/main/kotlin")
    if os.path.isdir(os.path.join(ROOT, src_root)) and not \
            os.path.isdir(os.path.join(ROOT, src_root, tail)):
        FAILURES.append(f"[namespace/dir] {module}: namespace `{ns}` but no {src_root}/{tail}")

# ---------------------------------------------------------------------------
print(f"kotlin files scanned : {len(KT)}")
print(f"internal packages    : {len([p for p in declared_packages if p.startswith('com.convxy')])}")
print(f"gradle namespaces    : {len(NAMESPACES)}")
print()
for w in WARNINGS:
    print("WARN " + w)
print()
if FAILURES:
    for f in FAILURES:
        print("FAIL " + f)
    print(f"\n{len(FAILURES)} failure(s)")
    sys.exit(1)
print("OK — rename is internally consistent")
