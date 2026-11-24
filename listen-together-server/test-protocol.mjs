/**
 * End-to-end protocol smoke test against a deployed Worker.
 *
 *   node test-protocol.mjs https://convx-sync.<sub>.workers.dev
 *
 * Drives the real create -> join -> approve -> playback path with two sockets,
 * which is the part that cannot be checked with curl.
 */

const base = process.argv[2];
if (!base) {
  console.error('usage: node test-protocol.mjs <https-base-url>');
  process.exit(1);
}
const wsBase = base.replace(/^http/, 'ws');

let failures = 0;
const check = (ok, label) => {
  console.log(`${ok ? 'PASS' : 'FAIL'}  ${label}`);
  if (!ok) failures++;
};

function open(url) {
  return new Promise((resolve, reject) => {
    const ws = new WebSocket(url);
    ws.inbox = [];
    ws.addEventListener('message', (e) => ws.inbox.push(JSON.parse(e.data)));
    ws.addEventListener('open', () => resolve(ws));
    ws.addEventListener('error', reject);
  });
}

const send = (ws, type, payload) => ws.send(JSON.stringify(payload === undefined ? { type } : { type, payload }));

async function waitFor(ws, type, ms = 6000) {
  const deadline = Date.now() + ms;
  while (Date.now() < deadline) {
    const hit = ws.inbox.find((m) => m.type === type);
    if (hit) return hit;
    await new Promise((r) => setTimeout(r, 60));
  }
  return null;
}

const room = await (await fetch(`${base}/api/rooms`, { method: 'POST' })).json();
check(!!room.room_code, `allocate room (${room.room_code})`);

const host = await open(`${wsBase}/room/${room.room_code}`);
send(host, 'create_room', { username: 'host-user' });
const created = await waitFor(host, 'room_created');
check(!!created?.payload?.session_token, 'room_created returns session token');
check(created?.payload?.room_code === room.room_code, 'room code matches allocation');

send(host, 'ping');
const pong = await waitFor(host, 'pong');
check(!!pong, 'ping -> pong');
check(Math.abs((pong?.payload?.server_time ?? 0) - Date.now()) < 60_000, 'pong carries server_time');

const guest = await open(`${wsBase}/room/${room.room_code}`);
send(guest, 'join_room', { username: 'guest-user' });
const req = await waitFor(host, 'join_request');
check(req?.payload?.username === 'guest-user', 'host receives join_request');

send(host, 'approve_join', { user_id: req?.payload?.user_id });
const approved = await waitFor(guest, 'join_approved');
check(!!approved?.payload?.state, 'guest receives join_approved with state');
check(approved?.payload?.state?.control_mode === 'owner', 'control_mode defaults to owner');
check(typeof approved?.payload?.state?.expires_at === 'number', 'expires_at present');
check(!!(await waitFor(host, 'user_joined')), 'host sees user_joined');

// Guest must NOT be able to control while mode is owner-only.
guest.inbox.length = 0;
send(guest, 'playback_action', { action: 'pause' });
const denied = await waitFor(guest, 'error', 2500);
check(denied?.payload?.code === 'forbidden', 'guest playback blocked in owner mode');

// Flip to everyone and retry.
send(host, 'set_control_mode', { control_mode: 'everyone' });
check(!!(await waitFor(guest, 'control_mode_changed')), 'control_mode_changed broadcast');
guest.inbox.length = 0;
host.inbox.length = 0;
send(guest, 'playback_action', { action: 'pause' });
const synced = await waitFor(host, 'sync_playback');
check(!!synced, 'guest playback allowed in everyone mode');
check(typeof synced?.payload?.server_time === 'number', 'server stamps server_time');

// Host-only verbs stay host-only regardless of control mode.
guest.inbox.length = 0;
send(guest, 'set_control_mode', { control_mode: 'owner' });
const stillForbidden = await waitFor(guest, 'error', 2500);
check(stillForbidden?.payload?.code === 'forbidden', 'guest cannot change control mode');

// Bits the Android fix depends on.
// The host must be able to tell a member's action from its own echo. Without a
// correct from_user_id the client either ignores real commands or feeds back
// on itself, which is exactly the "controls do nothing" symptom.
const guestId = approved?.payload?.user_id;
const hostId = created?.payload?.user_id;
check(!!guestId && !!hostId && guestId !== hostId, 'host and member have distinct ids');

host.inbox.length = 0;
send(guest, 'playback_action', { action: 'pause', position: 12345, server_time: Date.now() });
const onHost = await waitFor(host, 'sync_playback');
check(onHost?.payload?.from_user_id === guestId, 'host sees from_user_id = member (not itself)');
check(onHost?.payload?.action === 'pause', 'action survives the relay');
check(onHost?.payload?.position === 12345, 'position survives the relay');

// And the reverse: a host action must reach the member stamped as the host.
guest.inbox.length = 0;
send(host, 'playback_action', { action: 'play', position: 999, server_time: Date.now() });
const onGuest = await waitFor(guest, 'sync_playback');
check(onGuest?.payload?.from_user_id === hostId, 'member sees from_user_id = host');

// Track changes carry the metadata the player needs to actually load a song.
guest.inbox.length = 0;
send(host, 'playback_action', {
  action: 'change_track',
  track_id: 'yt-abc123',
  position: 0,
  server_time: Date.now(),
  track_info: { id: 'yt-abc123', title: 'Test Song', artist: 'Test Artist', duration: 180000 },
});
const track = await waitFor(guest, 'sync_playback');
check(track?.payload?.track_info?.id === 'yt-abc123', 'change_track relays track_info');
check(track?.payload?.track_info?.title === 'Test Song', 'track title survives');

// The server must also hold it in room state, or a late joiner gets silence.
guest.inbox.length = 0;
send(guest, 'request_sync');
const state = await waitFor(guest, 'sync_state');
check(state?.payload?.current_track?.id === 'yt-abc123', 'current_track persisted in room state');
check(state?.payload?.is_playing === true, 'is_playing persisted in room state');

host.close();
guest.close();
console.log(failures === 0 ? '\nAll checks passed.' : `\n${failures} check(s) failed.`);
process.exit(failures === 0 ? 0 : 1);
