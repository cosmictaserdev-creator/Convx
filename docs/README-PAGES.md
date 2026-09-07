# GitHub Pages for the Convxy website

`docs/index.html` is the project website. It is one static HTML file with inline CSS and a
short script — no build step, no dependencies, nothing to install. It lives in `docs/` so GitHub
Pages can serve it straight from the branch.

The favicon and header mark are `docs/icon.png`, generated from `app_icons/Convxy.png`.

## One-time enable (repo owner)

GitHub Pages must be switched on once in the repo settings:

1. Open **Settings → Pages** on `MeYashverma/Convxy`.
2. Under **Build and deployment → Source**, choose **Deploy from a branch**.
3. Branch: `main`, folder: `/docs`, then **Save**.
4. Wait ~1 minute; the site goes live at:

```
https://meyashverma.github.io/Convxy/
```

Every later push to `main` that touches `docs/` redeploys automatically.

## What the page is made of

- Text and links only, plus screenshots from `docs/screenshots/`. If you fix a typo in the copy,
  you can do it in the GitHub web editor.
- One network call: `GET api.github.com/repos/MeYashverma/Convxy/releases/latest`, which fills in
  the version line and points the download button at the release asset ending in `.apk`. It is
  wrapped in a `catch`, so when the API is rate-limited or offline the page still reads correctly
  and the button just goes to the releases page.
- Nothing else is fetched. No star counters, no commit feeds, no analytics, no webfonts.

## Editing

The layout is a single `44rem` column of sections; `<h2>` headings carry the `id`s that the nav
links point at. The palette is five CSS custom properties at the top (`--paper`, `--ink`,
`--muted`, `--rule`, `--accent`) and a `prefers-color-scheme: dark` override of the same five —
change those and the whole page follows.

Screenshots are the same files the README links, so one new capture serves both. Portrait shots
are 1245×2359 and the tablet ones 2226×1590; `width`/`height` attributes are set on each `<img>`
so the grid doesn't jump while images load.

## Local preview

```bash
cd docs && python3 -m http.server 8000
# open http://localhost:8000
```
