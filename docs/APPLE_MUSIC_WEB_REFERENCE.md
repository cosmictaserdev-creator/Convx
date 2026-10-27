# Apple Music Web layout reference

This is a signed-out page-template inventory of public Apple Music Web. The initial pass captured 1280 × 720 screenshots of New, Radio, Search, Top Charts, Top Songs, and Top Albums, then inspected route/content text for the remaining templates. The screenshots show a clean browser session with no track playing; the site also showed its location prompt for India. This records distinct page templates and states, not every album, playlist, artist, or station instance. Personal library and account pages require sign-in and were not available in the public session.

## Shared application frame

- At the captured desktop width, a fixed left navigation rail is about 260 px wide. It groups Search, Home, New, and Radio, with “Open in Music” and Sign In below.
- The main area changes its content hierarchy by page. Artwork and section headers align to a consistent content gutter; wide desktop layouts use shelves and tables rather than stretching phone-sized lists.
- The idle mini-player is a translucent rounded capsule about 670 × 56 px, floating over the lower center of the content. In this signed-out idle capture it showed Apple Music branding plus queue and output controls; playing-track details were not observed.
- A dark location prompt can cover roughly the bottom 74 px, with a country selector, Continue action, and close control. It is separate from the page canvas.
- Pages favor artwork-led sections, quiet neutral surfaces, strong type hierarchy, and compact separators. Detail screens use denser metadata and track rows than discovery screens.

## Screenshot-verified page differences

- **New:** editorial cards form a horizontal hero shelf, followed by compact song rows and more shelves. At 1280 px, two hero cards are fully visible and the next is clipped at the right edge.
- **Radio:** six station tiles lead the page; episode/program rows and interview shelves follow.
- **Search, empty:** a centered search field sits above a four-column category grid. Category artwork uses distinct tinted treatments.
- **Top Charts:** chart families are shelves, with city-chart cards below. Opening a family changes the page template.
- **Top Songs:** a dense numbered table shows artwork, title, artist, album, and duration in columns.
- **Top Albums:** a five-column artwork grid carries rank, album title, and artist beneath each cover.

These screenshots establish the signed-out desktop hierarchy and the difference between shelves, grids, and track tables. They do not establish responsive breakpoints, hover/focus states, or the player while audio is active.

## Reviewed page templates

| Page | Main structure | Distinctive content and actions |
| --- | --- | --- |
| Home / signed-out access | The `/us/home` screenshot shows a blank main feed beneath the country selector | This is not the personalized Home feed; the signed-in state remains unreviewed. |
| New | Editorial hero followed by horizontally browsable album and playlist shelves | New releases, featured picks, and section-level browse actions. |
| Radio | Live station artwork row, genre collections, and episode/program shelves | Station selection and curated radio entry points. |
| Search, empty query | Search field and browsable category grid | Category tiles provide a visual starting point before a query is entered. |
| Search results (Drake query, screenshot-verified) | Dynamic populated-query state | Top Results cards lead into artist and album result shelves; the populated state was captured in the expanded audit below. |
| Album detail | Large artwork and metadata header above a track table | Album title, artist/year details, the preview action, and ordered track rows. |
| Artist detail | Artist hero followed by sectioned discography and related content | Prominent play/follow controls, then albums, songs, and similar artists. |
| Playlist detail | Artwork and playlist metadata above a dense track table | Curator and description details, the preview action, and playlist tracks. |
| Top Charts | Chart-family shelves followed by regional and city chart links | Opening a family leads to its own ranked songs, albums, playlists, or videos page. |
| Radio station | Station artwork and title above station-specific content | A direct play action and associated programming. |
| Editorial room | Editorial heading followed by episode and program card shelves | Curator context, episode artwork, titles, and short descriptions lead the page. |

## Expanded route audit (October 25, 2026)

I re-opened the signed-out web player and followed the public navigation and representative content links. This audit covers distinct page templates and states; Apple Music has too many individual catalogue items to inspect each album, artist, song, or playlist instance separately.

| Template / state | Representative route | What is present |
| --- | --- | --- |
| New / Browse | [`/us/new`](https://music.apple.com/us/new) | Editorial feature cards followed by shelves for new songs, weekly releases, albums, playlists, trending tracks, charts, and Apple Music exclusives. |
| Radio index | [`/us/radio`](https://music.apple.com/us/radio) | Live station entry points, radio episodes and interviews, then local and international broadcaster groups. |
| Search landing | [`/us/search`](https://music.apple.com/us/search) | Search input and a category grid spanning genres, charts, moods, music videos, DJ mixes, and editorial topics. |
| Search results | Search for Drake | Top Results cards lead into separate artist and album shelves; this populated state is screenshot-verified. |
| Category / curator | [`Apple Music Pop`](https://music.apple.com/us/curator/apple-music-pop/976439548) | A category-specific editorial hero and a long mix of playlist shelves, new releases, songs, and exclusive features. |
| Playlist detail | [`A-List Pop`](https://music.apple.com/us/playlist/a-list-pop/pl.5ee8333dbe944d9f9151e97d92d1ead9) | Artwork, curator, update date, description, preview, song count and total duration, featured artists, and an ordered track sequence. |
| Artist detail | [`Megan Thee Stallion`](https://music.apple.com/us/artist/megan-thee-stallion/1258989914) | Latest release, top songs, albums, singles and EPs, music videos, and other artist-related shelves. Upcoming releases are another state of this template. |
| Album detail | [`Good News`](https://music.apple.com/us/album/good-news/1540031620) | Artwork and album metadata, editorial description, release/runtime information, versions, videos, featured-on links, and related albums. |
| Song selected from an album | [`WAP`](https://music.apple.com/us/album/wap-feat-megan-thee-stallion/1821526317?i=1821526567) | The public URL keeps the album route and adds a selected-song identifier. The text view resolves the album context; a distinct full-screen player state requires playback interaction. |
| Top Charts | [`Top Charts`](https://music.apple.com/us/new/top-charts) | Ranked song, album, playlist, and video sections, plus city chart collections. A chart-family link opens its own ranking route. |
| Editorial room | [`Subscribe to Play Episodes`](https://music.apple.com/us/room/6743553488) | A named editorial collection made from episode, interview, and story cards; it is distinct from an individual artist or playlist page. |
| Signed-out Home | [`/us/home`](https://music.apple.com/us/home) | The current text capture surfaced the country/region selector instead of the underlying personalized Home feed. Do not infer the signed-in feed from this result. |
| Personal library / account | Account-gated | Library and personalized listening states need an authenticated session and were not part of the signed-out route pass. |

The text capture alone does not show the live player's transient states. The Up Next drawer was later screenshot-verified in the expanded audit below. Lyrics, output/device menus, expanded Now Playing, and active playback overlays remain unverified.

### Additional route variants opened

| Template / state | Representative route | Observed variation |
| --- | --- | --- |
| Top Songs chart | [`/us/new/top-charts/songs`](https://music.apple.com/us/new/top-charts/songs) | Ranked song rows with genre and region selectors. |
| Top Albums chart | [`/us/new/top-charts/albums`](https://music.apple.com/us/new/top-charts/albums) | Ranked album artwork and metadata, with genre and region selectors. |
| Top Playlists chart | [`/us/new/top-charts/playlists`](https://music.apple.com/us/new/top-charts/playlists) | Ranked playlist artwork and curator metadata. |
| Top Videos chart | [`/us/new/top-charts/music-videos`](https://music.apple.com/us/new/top-charts/music-videos) | Ranked video rows with video titles and artists. |
| Live station detail | [`Apple Music 1`](https://music.apple.com/us/station/apple-music-1/ra.978194965) | Station artwork/title, “Live Station” label, and direct Play action. |
| Music videos shelf | [`/us/new`](https://music.apple.com/us/new) and [`Apple Music Pop`](https://music.apple.com/us/curator/apple-music-pop/976439548) | New Music Videos is a distinct shelf on discovery and curator pages. A direct video detail route was not captured in this text-only pass; see the visual route captured below. |
| Search result state | Search for Drake | The populated query and grouped result layout were visually captured in the expanded audit below. |
| Selected song on album | [`WAP`](https://music.apple.com/us/album/wap-feat-megan-thee-stallion/1821526317?i=1821526567) | The selected-song link resolves to album context in the text capture; the Now Playing state was not opened. |

The public signed-out routes inspected cover the distinct discovery, chart, curator, catalog-detail, station, and editorial-room templates listed above. “Every page” here means each distinct public page template/state, not every catalog record or every personalized/account state. Populated search and music-video detail were later visually captured in the expanded audit below. Active Now Playing and playback overlays remain unverified. Signed-in library and personalized Home remain account-gated.

## Interaction and implementation notes

- Keep the navigation rail and mini-player stable while changing the central page. Detail navigation should preserve playback state.
- Use a horizontal shelf for curated collections and a table/list for chart, album, playlist, and editorial track sequences.
- Search should have distinct empty-query and populated-result states; the category grid is not the results page.
- Do not treat the signed-out landing as equivalent to a personalized Home/Listen Now page. Authenticated library and recommendation layouts still need a signed-in reference session.
- These observations describe hierarchy and visible controls, not exact pixel measurements or behavior behind account gates.
## Expanded live visual audit (October 25, 2026)

This update supersedes the earlier route-level gaps for populated search and music-video detail. The listed public templates now have screenshot coverage at a 1280x720 desktop viewport in a fresh, signed-out browser session. Screenshots show the captured viewport, not full-page scroll states. The audit covers distinct templates and representative routes, not every catalog item or every region.

### Additional page templates captured

| Template / state | Representative page | Visual structure observed |
| --- | --- | --- |
| Populated search | [Search for Drake](https://music.apple.com/us/search?term=Drake) | Top Results cards, followed by artist and album result shelves. The query state is distinct from the empty category grid. |
| Curator | [Apple Music Pop](https://music.apple.com/us/curator/apple-music-pop/976439548) | Wide editorial feature cards, followed by playlist and new-release artwork shelves. |
| Playlist detail | [A-List Pop](https://music.apple.com/us/playlist/a-list-pop/pl.5ee8333dbe944d9f9151e97d92d1ead9) | Artwork and curator header over a saturated red page tint, then a dense multi-column track table. |
| Artist detail | [Megan Thee Stallion](https://music.apple.com/us/artist/megan-thee-stallion/1258989914) | Large dark hero with centered artist name and play/follow controls; release and song sections start below. |
| Album detail | [Good News](https://music.apple.com/us/album/good-news/1540031620) | Cover art and metadata share a wide header; a compact numbered track list fills the page below. |
| Selected song | [WAP on its album](https://music.apple.com/us/album/wap-feat-megan-thee-stallion/1821526317?i=1821526567) | The selected track opens in album context and is brought into view and highlighted in the ordered list. |
| Top Playlists chart | [Top Playlists](https://music.apple.com/us/new/top-charts/playlists) | Five-column ranked cover grid with curator labels. |
| Top Videos chart | [Top Videos](https://music.apple.com/us/new/top-charts/music-videos) | Ranked video-thumbnail grid with title and artist labels. |
| Music video detail | [Slap The City](https://music.apple.com/us/music-video/slap-the-city/6818209994) | Large playable video preview beside title, artists, and year, with a horizontal More By Drake shelf below. |
| Radio station detail | [Apple Music 1](https://music.apple.com/us/station/apple-music-1/ra.978194965) | Sparse centered station artwork and title with a prominent Play action. |
| Editorial room | [Subscribe to Play Episodes](https://music.apple.com/us/room/6743553488) | Five-column episode cards with title and episode description, followed by further shelves. |
| Signed-out Home | [/us/home](https://music.apple.com/us/home) | Navigation remains, but the main feed is blank while the location prompt is open. This is not evidence for personalized Home. |

The previously captured New, Radio, empty Search, Top Charts, Top Songs, and Top Albums pages complete the public route-template screenshot set above.

### Additional interaction states

- Submitting the query `Drake` shows grouped search results for artists, albums, and songs.
- Selecting `Up Next` opens a translucent right-side drawer titled `Up Next`; the empty guest state says there are no upcoming songs.
- `Sign In` opens a centered `Continue with Email` dialog. The email form was inspected without entering credentials.
- The idle mini-player stays centered near the bottom edge. In the signed-out session, transport controls were disabled; pressing an album Preview control did not establish active playback. A subscription prompt appeared.

### Remaining visual limits

- Personal Library and personalized Home require an authenticated account, which was not used.
- Active Now Playing, lyrics, output/device selection, and a populated playback queue were not verified. The observed queue was empty, and preview interaction did not start audio.
- These screenshots cover one desktop viewport. Responsive breakpoints, full-page scroll captures, and all hover/focus states remain outside this pass.
- Screenshots were inspected in the live capture session; this document stores route links and visual notes, not screenshot image files.

## Additional live routes checked (October 25, 2026)

These routes were opened in the live public session after the screenshot audit. They add one distinct template and confirm two variants of already-covered templates.

| Route / state | Structure observed | Coverage |
| --- | --- | --- |
| [Concerts](https://music.apple.com/us/concerts) | Dedicated discovery page with location, date, and genre filters, followed by city-grouped event cards showing artist, venue, date, and time. | Live route/content verified; no saved screenshot. |
| [Apple Music Live](https://music.apple.com/us/curator/apple-music-live/1649426593) | Curator page with exclusive performance hero cards and shelves for upcoming and past performances. | Live route/content verified; same curator template family. |
| [Watch Interviews](https://music.apple.com/us/room/6743553487) | Editorial room with interview cards and durations. | Live route/content verified; same room template family. |
| Selected song URL | A song selected with `?i=` keeps the album-detail page and track list, with the song selected in context; it does not introduce a separate catalog-detail layout. | Live route/content verified; prior visual capture covers the selected state. |

This completes the public route-template inventory beyond the captured representative music pages. It still does not include every catalog record, every location/genre filter result, signed-in pages, or full-page scroll screenshots.

## Live visual route pass (October 25, 2026)

This pass used a clean, signed-out Chromium session at 1280 x 720. I captured and visually inspected Search empty and populated, New, Radio, Top Charts and its Songs/Albums/Playlists/Videos pages, playlist, album, artist, music video, radio station, curator, and Up Next. Concerts and editorial room routes were also opened and their content structure checked, but were not included in the screenshot set. The catalog contains many individual records; each record uses one of these page templates with different content.

- The fixed left rail remains about 260 px wide. Search, Home, New, and Radio stay at the top; Open in Music and the red Sign In action sit at the bottom.
- The mini-player floats centered along the lower edge as a translucent capsule. Up Next opens a right-side drawer over the page; the drawer has a blurred, glass-like background, and the empty signed-out queue reads No upcoming songs.
- New and curator pages use wide editorial cards above horizontal artwork shelves. Search starts with a centered field and a four-column category grid; a populated query adds Top Results cards, circular artist results, and additional result shelves.
- Radio starts with six branded station tiles, then episode cards and broadcaster collections. Concerts adds location, date, and genre filters above city-grouped event cards.
- Top Charts is a multi-shelf overview with ranked song summaries and regional city chart cards. Top Songs uses a dense table; Albums and Playlists use ranked cover grids; Videos uses a five-column video grid.
- Playlist detail uses a strong playlist-color page treatment with a wide artwork/metadata header and a multi-column song table. Album detail uses a cover and metadata header over a numbered track list. Artist detail uses a dark full-width hero with play and follow controls, then releases and song shelves.
- Music video detail places a wide video preview beside the title and artists, followed by a More By shelf. A live-station detail is sparse, with station artwork, title, and a direct Play action. Editorial rooms use card grids for episodes and interviews.
- In the clean signed-out session, opening Sign In showed a white loading panel with a spinner at the sampled moment. No credentials were entered. Personal Library and personalized Home remain unobserved because they require an authenticated account.
- Opening /us/home in the live session displayed the country/region chooser. It covered the bottom of the page and obscured the underlying Home content; the personalized feed could not be inferred.

These are viewport captures and interaction observations, not full-page scroll exports. The captured screenshots were inspected in the browser session but are not stored as image files in this repository. Responsive layouts, all hover states, every catalog record, and signed-in content remain outside the visual pass.
