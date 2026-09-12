# NexaMusic Sync — Listen Together server

Cloudflare Worker + Durable Objects. One Durable Object per room code; that
object owns membership, host, playback position and the queue, so there is
never a second server with a different opinion about the same room.

## About

Listen Together is a shared listening feature for NexaMusic. By default, the app connects to community-hosted servers (see `ListenTogetherServers.kt`). If you want a dedicated server for your use, you can host your own using this code.

## Deploy

```bash
npm install
npx wrangler login      # browser auth, no card asked
npx wrangler deploy
```

You get `https://nexamusic-sync.<subdomain>.workers.dev`. Put
`wss://nexamusic-sync.<subdomain>.workers.dev` into the app under
**Settings → Integrations → Listen Together → custom server URL**.

Check it is alive:

```bash
curl https://nexamusic-sync.<subdomain>.workers.dev/health   # {"ok":true}
```

Watch live logs while testing:

```bash
npx wrangler tail
```

## Billing

Free plan needs no card. If you stay on it there is no payment method on file,
so exceeding the daily request allowance rejects requests — it never produces a
bill. That is the main reason to prefer this over a VM.

**Verify before relying on it:** whether SQLite-backed Durable Objects are still
included on the Workers Free plan (dashboard → Workers & Pages → Plans). The
`wrangler.toml` here uses `new_sqlite_classes` for that reason. If DO has moved
to paid-only, the same design ports to Deno Deploy + Deno KV with no card.

## Config

`wrangler.toml` `[vars]`:

| Var | Default | Meaning |
|---|---|---|
| `ROOM_TTL_HOURS` | 6 | Room lifetime before it closes and deletes itself |
| `MAX_EXTENSIONS` | 2 | Times the owner may extend |
| `MAX_MEMBERS` | 20 | Per-room cap |

## Endpoints

- `POST /api/rooms` → `{ "room_code": "ABC123" }` — allocates a room
- `GET /room/<CODE>` with `Upgrade: websocket` → joins that room's DO
- `GET /health`

The client must call `POST /api/rooms` before opening the socket for a new
room, because the Worker has to know the code to pick a Durable Object and at
upgrade time it would not know it yet.

## Protocol notes

- **JSON only.** The Kotlin client starts in JSON uncompressed and permanently
  flips to protobuf + gzip if the server's first message is protobuf
  (`ListenTogetherClient.kt:783-785`). Never emit protobuf.
- Field names are snake_case to match the `@SerialName` annotations in
  `Protocol.kt`. A renamed field decodes as missing, not as an error.
- v2 adds `control_mode` (`owner` | `everyone`), `expires_at` and
  `extensions_used` to `RoomState`, plus `set_control_mode` / `extend_session`
  inbound and `control_mode_changed` / `room_expiring` / `room_closed` outbound.
  The Kotlin `Json` is configured with `ignoreUnknownKeys`, so older clients
  ignore the new fields instead of failing.

## Design constraints worth not breaking

**Hibernation.** Sockets are accepted with `state.acceptWebSocket()`, which lets
Cloudflare evict the object from memory while listeners are idle and keep the
connections open. That is what makes a quiet room free and removes the cold
start that makes Render/HF free tiers unusable for live sessions. The cost is
that class fields do not survive, so:

- room state lives in `storage`, reloaded in `blockConcurrencyWhile` on wake
- per-socket identity lives in `ws.serializeAttachment()`, not a `Map`
- deadlines use storage alarms, never `setTimeout`

Break any of those and it works in testing, then breaks the first time a room
goes quiet for a minute.

**Single alarm slot.** Room expiry, expiry warning, host grace and buffer
timeout all share one alarm; `rescheduleAlarm()` always arms the earliest.

## Edge cases handled

| Case | Behaviour |
|---|---|
| Host's screen locks | 90s grace before anyone else is promoted |
| Host leaves for good | Longest-connected member promoted, `host_changed` broadcast |
| Transfer host to someone who left | Rejected, room keeps a real host |
| Kicked user reconnects | Session token deleted on kick, so it fails |
| Duplicate socket after reconnect | Older socket closed, no double broadcasts |
| Room code collision | `/claim` returns 409, Worker retries a new code |
| One user never buffers | 10s timeout, room starts without them |
| Out-of-order playback messages | Anything older than `last_update` dropped |
| Clock skew | Server stamps `server_time` on every broadcast |
| Last member leaves | `storage.deleteAll()`, no leak |
| Abandoned room | TTL closes it, warning at T-10min |
| Room full | `join_rejected` with reason |

## No domain needed

`*.workers.dev` is free and comes with TLS, so `wss://` works with nothing
bought. The only thing a domain would add here is zone-level features — WAF
rate-limiting rules, firewall rules, per-zone analytics. Rate limiting is
handled in code instead (`src/guard.ts`), so none of that is required.

Two minor tradeoffs of staying on `workers.dev`: the hostname is long, and a
few corporate/ISP filters block the whole `workers.dev` domain because it has
been abused for phishing. Neither matters much for a music app. If you add a
domain later you can serve both hostnames at once, so existing users' saved
server URLs keep working.

## Not done yet

- Server-side blocklist. `blockedUsernames` is still client-only, so a blocked
  user currently rejoins fine from the server's point of view.
- Client changes: HTTP-then-socket connect flow, control-mode toggle, expiry
  countdown UI, and reconnect jitter (`ListenTogetherClient.kt:434` has none,
  so a redeploy makes every client retry in lockstep).
