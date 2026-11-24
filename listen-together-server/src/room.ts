/**
 * One Durable Object per room code. Owns everything that has to be consistent:
 * membership, who is host, what is playing, the queue.
 *
 * HIBERNATION IS THE WHOLE DESIGN CONSTRAINT. Sockets are accepted with
 * state.acceptWebSocket(), which lets Cloudflare evict this object from memory
 * while listeners are idle and keep the connections open — that is what makes
 * a quiet room cost nothing and removes the cold start that makes free tiers
 * on Render/HF unusable for live sessions.
 *
 * The price is that every class field is gone after a hibernation. So:
 *   - room state lives in storage, reloaded in blockConcurrencyWhile on wake
 *   - per-socket identity lives in ws.serializeAttachment(), not a Map
 *   - deadlines use storage alarms, never setTimeout
 * Break any of those three and it works in testing, then breaks the moment a
 * room goes quiet for a minute.
 */

import { C2S, S2C, encode, type ControlMode, type RoomState, type TrackInfo, type UserInfo } from './protocol';
import type { Env } from './index';

interface Member {
  user_id: string;
  username: string;
  session_token: string;
  connected: boolean;
  joined_at: number;
}

interface RoomData {
  room_code: string;
  host_id: string | null;
  current_track: TrackInfo | null;
  is_playing: boolean;
  position: number;
  last_update: number;
  volume: number;
  queue: TrackInfo[];
  control_mode: ControlMode;
  created_at: number;
  expires_at: number;
  extensions_used: number;
  warned: boolean;
}

interface PendingJoin {
  user_id: string;
  username: string;
  session_token: string;
}

interface Suggestion {
  suggestion_id: string;
  from_user_id: string;
  from_username: string;
  track_info: TrackInfo;
}

interface BufferWait {
  track_id: string;
  waiting_for: string[];
  deadline: number;
}

/** How long a dropped host keeps the role before someone else is promoted.
 *  Short enough that a room is not headless for long, long enough to survive a
 *  tunnel or a screen lock. A host who returns after this is a normal member —
 *  their session token still works, they just do not get the crown back. */
const HOST_GRACE_MS = 90_000;

/** One straggler on bad signal must not be able to hold the whole room. */
const BUFFER_TIMEOUT_MS = 10_000;

const EXPIRY_WARNING_MS = 10 * 60_000;

export class Room {
  private state: DurableObjectState;
  private env: Env;
  private room: RoomData | null = null;
  private members: Record<string, Member> = {};
  private tokens: Record<string, string> = {};
  private pending: Record<string, PendingJoin> = {};
  private suggestions: Record<string, Suggestion> = {};
  private buffer: BufferWait | null = null;
  private hostGraceDeadline = 0;

  constructor(state: DurableObjectState, env: Env) {
    this.state = state;
    this.env = env;
    // Runs on every wake, including after hibernation. This is what replaces
    // the in-memory state the eviction threw away.
    this.state.blockConcurrencyWhile(async () => {
      const s = this.state.storage;
      this.room = (await s.get<RoomData>('room')) ?? null;
      this.members = (await s.get<Record<string, Member>>('members')) ?? {};
      this.tokens = (await s.get<Record<string, string>>('tokens')) ?? {};
      this.pending = (await s.get<Record<string, PendingJoin>>('pending')) ?? {};
      this.suggestions = (await s.get<Record<string, Suggestion>>('suggestions')) ?? {};
      this.buffer = (await s.get<BufferWait>('buffer')) ?? null;
      this.hostGraceDeadline = (await s.get<number>('hostGrace')) ?? 0;
    });
  }

  private async persist() {
    const s = this.state.storage;
    await s.put({
      room: this.room,
      members: this.members,
      tokens: this.tokens,
      pending: this.pending,
      suggestions: this.suggestions,
      buffer: this.buffer,
      hostGrace: this.hostGraceDeadline,
    });
  }

  async fetch(request: Request): Promise<Response> {
    const url = new URL(request.url);

    // Called by the Worker while allocating a code. Fails if this room is
    // already live, which is what stops a generated code from silently
    // dropping the creator into a stranger's room.
    if (url.pathname === '/claim') {
      if (this.room && this.room.expires_at > Date.now()) {
        return new Response('taken', { status: 409 });
      }
      const code = url.searchParams.get('code') ?? '';
      const ttl = Number(this.env.ROOM_TTL_HOURS ?? '6') * 3600_000;
      const now = Date.now();
      this.room = {
        room_code: code,
        host_id: null,
        current_track: null,
        is_playing: false,
        position: 0,
        last_update: now,
        volume: 1,
        queue: [],
        control_mode: 'owner',
        created_at: now,
        expires_at: now + ttl,
        extensions_used: 0,
        warned: false,
      };
      this.members = {};
      this.tokens = {};
      this.pending = {};
      this.suggestions = {};
      this.buffer = null;
      this.hostGraceDeadline = 0;
      await this.persist();
      await this.rescheduleAlarm();
      return new Response('ok');
    }

    if (!this.room || this.room.expires_at <= Date.now()) {
      return new Response('room not found', { status: 404 });
    }

    const pair = new WebSocketPair();
    const [client, server] = Object.values(pair);
    // acceptWebSocket, NOT server.accept() — the latter pins this object in
    // memory for the life of every connection and forfeits hibernation.
    this.state.acceptWebSocket(server);
    server.serializeAttachment({ user_id: null });
    return new Response(null, { status: 101, webSocket: client });
  }

  // Socket plumbing.

  private send(ws: WebSocket, type: string, payload?: unknown) {
    try {
      ws.send(encode(type, payload));
    } catch {
      /* socket already gone; close handler will reconcile */
    }
  }

  private socketsFor(userId: string): WebSocket[] {
    return this.state.getWebSockets().filter((ws) => {
      const att = ws.deserializeAttachment() as { user_id: string | null } | null;
      return att?.user_id === userId;
    });
  }

  private broadcast(type: string, payload?: unknown, exceptUserId?: string) {
    const data = encode(type, payload);
    for (const ws of this.state.getWebSockets()) {
      const att = ws.deserializeAttachment() as { user_id: string | null } | null;
      if (!att?.user_id) continue;
      if (exceptUserId && att.user_id === exceptUserId) continue;
      try {
        ws.send(data);
      } catch {
        /* ignore */
      }
    }
  }

  private snapshot(): RoomState {
    const r = this.room!;
    const users: UserInfo[] = Object.values(this.members).map((m) => ({
      user_id: m.user_id,
      username: m.username,
      is_host: m.user_id === r.host_id,
      is_connected: m.connected,
    }));
    return {
      room_code: r.room_code,
      host_id: r.host_id ?? '',
      users,
      current_track: r.current_track,
      is_playing: r.is_playing,
      position: r.position,
      last_update: r.last_update,
      volume: r.volume,
      queue: r.queue,
      control_mode: r.control_mode,
      expires_at: r.expires_at,
      extensions_used: r.extensions_used,
    };
  }

  private isHost(userId: string): boolean {
    return !!this.room && this.room.host_id === userId;
  }

  private canControl(userId: string): boolean {
    if (!this.room) return false;
    return this.room.control_mode === 'everyone' || this.room.host_id === userId;
  }

  private err(ws: WebSocket, code: string, message: string) {
    this.send(ws, S2C.ERROR, { code, message });
  }

  // Message handling.

  async webSocketMessage(ws: WebSocket, raw: string | ArrayBuffer) {
    if (!this.room) return;
    let msg: { type: string; payload?: any };
    try {
      msg = JSON.parse(typeof raw === 'string' ? raw : new TextDecoder().decode(raw));
    } catch {
      return this.err(ws, 'bad_message', 'malformed json');
    }

    const att = (ws.deserializeAttachment() ?? {}) as { user_id: string | null };
    const p = msg.payload ?? {};

    // Unauthenticated verbs
    switch (msg.type) {
      case C2S.PING:
        // The clock lets a client measure its offset before any playback message
        // arrives, so a guest joining a playing room lands where the room is.
        return this.send(ws, S2C.PONG, { server_time: Date.now() });
      case C2S.CREATE_ROOM:
        return this.handleCreate(ws, p.username);
      case C2S.JOIN_ROOM:
        return this.handleJoin(ws, p.username);
      case C2S.RECONNECT:
        return this.handleReconnect(ws, p.session_token);
    }

    if (!att.user_id || !this.members[att.user_id]) {
      return this.err(ws, 'not_in_room', 'join first');
    }
    const me = att.user_id;

    switch (msg.type) {
      case C2S.LEAVE_ROOM:
        return this.removeMember(me, 'left');

      case C2S.APPROVE_JOIN:
        if (!this.isHost(me)) return this.err(ws, 'forbidden', 'not host');
        return this.approveJoin(p.user_id);

      case C2S.REJECT_JOIN:
        if (!this.isHost(me)) return this.err(ws, 'forbidden', 'not host');
        return this.rejectJoin(p.user_id, p.reason ?? 'Rejected by host');

      case C2S.PLAYBACK_ACTION:
        if (!this.canControl(me)) return this.err(ws, 'forbidden', 'control is owner-only');
        return this.playback(me, p);

      case C2S.SET_CONTROL_MODE: {
        if (!this.isHost(me)) return this.err(ws, 'forbidden', 'not host');
        const mode: ControlMode = p.control_mode === 'everyone' ? 'everyone' : 'owner';
        this.room.control_mode = mode;
        await this.persist();
        return this.broadcast(S2C.CONTROL_MODE_CHANGED, { control_mode: mode });
      }

      case C2S.EXTEND_SESSION: {
        if (!this.isHost(me)) return this.err(ws, 'forbidden', 'not host');
        const max = Number(this.env.MAX_EXTENSIONS ?? '2');
        if (this.room.extensions_used >= max) {
          return this.err(ws, 'extend_limit', 'no extensions left');
        }
        const ttl = Number(this.env.ROOM_TTL_HOURS ?? '6') * 3600_000;
        this.room.extensions_used += 1;
        this.room.expires_at += ttl;
        this.room.warned = false;
        await this.persist();
        await this.rescheduleAlarm();
        return this.broadcast(S2C.SYNC_STATE, this.syncPayload());
      }

      case C2S.KICK_USER: {
        if (!this.isHost(me)) return this.err(ws, 'forbidden', 'not host');
        return this.kick(p.user_id, p.reason ?? 'Removed by host');
      }

      case C2S.TRANSFER_HOST: {
        if (!this.isHost(me)) return this.err(ws, 'forbidden', 'not host');
        const target = this.members[p.new_host_id];
        // Transferring to someone who already left leaves the room with a
        // phantom host and nobody able to control it.
        if (!target || !target.connected) return this.err(ws, 'bad_target', 'user not connected');
        this.room.host_id = target.user_id;
        this.hostGraceDeadline = 0;
        await this.persist();
        return this.broadcast(S2C.HOST_CHANGED, {
          new_host_id: target.user_id,
          new_host_name: target.username,
        });
      }

      case C2S.CHAT:
        // Echoed back under the same "chat" type the client sends — the
        // Kotlin side has no separate server->client constant for it.
        // Cap length to prevent room spam/flood.
        return this.broadcast(C2S.CHAT, {
          user_id: me,
          username: this.members[me].username,
          message: String(p.message ?? '').slice(0, 500),
          timestamp: Date.now(),
          reply_to: typeof p.reply_to === 'string' ? p.reply_to.slice(0, 100) : null,
        });

      case C2S.REQUEST_SYNC:
        return this.send(ws, S2C.SYNC_STATE, this.syncPayload());

      case C2S.BUFFER_READY:
        return this.bufferReady(me, p.track_id);

      case C2S.SUGGEST_TRACK:
        return this.suggest(me, p.track_info);

      case C2S.APPROVE_SUGGESTION:
        if (!this.isHost(me)) return this.err(ws, 'forbidden', 'not host');
        return this.resolveSuggestion(p.suggestion_id, true, null);

      case C2S.REJECT_SUGGESTION:
        if (!this.isHost(me)) return this.err(ws, 'forbidden', 'not host');
        return this.resolveSuggestion(p.suggestion_id, false, p.reason ?? null);

      default:
        return this.err(ws, 'unknown_type', msg.type);
    }
  }

  private syncPayload() {
    const r = this.room!;
    return {
      current_track: r.current_track,
      is_playing: r.is_playing,
      position: r.position,
      last_update: r.last_update,
      queue: r.queue,
      volume: r.volume,
      control_mode: r.control_mode,
      expires_at: r.expires_at,
    };
  }

  private async handleCreate(ws: WebSocket, username: string) {
    if (!username?.trim()) return this.err(ws, 'bad_username', 'username required');
    if (this.room!.host_id) return this.err(ws, 'room_taken', 'room already has a host');
    const user_id = crypto.randomUUID();
    const session_token = crypto.randomUUID();
    this.members[user_id] = {
      user_id,
      username: username.trim(),
      session_token,
      connected: true,
      joined_at: Date.now(),
    };
    this.tokens[session_token] = user_id;
    this.room!.host_id = user_id;
    ws.serializeAttachment({ user_id });
    await this.persist();
    this.send(ws, S2C.ROOM_CREATED, {
      room_code: this.room!.room_code,
      user_id,
      session_token,
      // The creator needs the real room state too. Without it the client had to
      // invent one locally, which meant the host never learned when the room
      // expires or what the control mode is — the countdown simply never
      // appeared for the one person who could act on it.
      state: this.snapshot(),
    });
  }

  private async handleJoin(ws: WebSocket, username: string) {
    if (!username?.trim()) return this.err(ws, 'bad_username', 'username required');
    const max = Number(this.env.MAX_MEMBERS ?? '20');
    if (Object.keys(this.members).length >= max) {
      return this.send(ws, S2C.JOIN_REJECTED, { reason: 'Room is full' });
    }
    const user_id = crypto.randomUUID();
    const session_token = crypto.randomUUID();
    this.pending[user_id] = { user_id, username: username.trim(), session_token };
    ws.serializeAttachment({ user_id: null, pending_id: user_id });
    await this.persist();

    // Approval is deliberately kept: room codes are six characters and
    // therefore guessable, so an open door is an abuse vector.
    const host = this.room!.host_id;
    if (host) {
      for (const hostWs of this.socketsFor(host)) {
        this.send(hostWs, S2C.JOIN_REQUEST, { user_id, username: username.trim() });
      }
    }
  }

  private async approveJoin(userId: string) {
    const req = this.pending[userId];
    if (!req) return;
    delete this.pending[userId];
    this.members[userId] = {
      user_id: userId,
      username: req.username,
      session_token: req.session_token,
      connected: true,
      joined_at: Date.now(),
    };
    this.tokens[req.session_token] = userId;
    await this.persist();

    for (const ws of this.state.getWebSockets()) {
      const att = ws.deserializeAttachment() as { user_id: string | null; pending_id?: string } | null;
      if (att?.pending_id === userId) {
        ws.serializeAttachment({ user_id: userId });
        this.send(ws, S2C.JOIN_APPROVED, {
          room_code: this.room!.room_code,
          user_id: userId,
          session_token: req.session_token,
          state: this.snapshot(),
        });
      }
    }
    this.broadcast(S2C.USER_JOINED, { user_id: userId, username: req.username }, userId);
  }

  private async rejectJoin(userId: string, reason: string) {
    const req = this.pending[userId];
    if (!req) return;
    delete this.pending[userId];
    await this.persist();
    for (const ws of this.state.getWebSockets()) {
      const att = ws.deserializeAttachment() as { pending_id?: string } | null;
      if (att?.pending_id === userId) this.send(ws, S2C.JOIN_REJECTED, { reason });
    }
  }

  private async handleReconnect(ws: WebSocket, token: string) {
    const userId = token ? this.tokens[token] : undefined;
    const member = userId ? this.members[userId] : undefined;
    // A kicked user's token is deleted, so this is also what makes a kick
    // outlast the app staying open.
    if (!userId || !member) {
      return this.send(ws, S2C.JOIN_REJECTED, { reason: 'Session expired' });
    }
    // Close any socket still holding this identity, or the user receives
    // every broadcast twice for the rest of the session.
    for (const old of this.socketsFor(userId)) {
      if (old !== ws) {
        try {
          old.close(1000, 'replaced');
        } catch {
          /* ignore */
        }
      }
    }
    member.connected = true;
    ws.serializeAttachment({ user_id: userId });
    if (this.room!.host_id === userId) this.hostGraceDeadline = 0;
    await this.persist();
    this.send(ws, S2C.RECONNECTED, {
      room_code: this.room!.room_code,
      user_id: userId,
      state: this.snapshot(),
      is_host: this.room!.host_id === userId,
    });
    this.broadcast(S2C.USER_RECONNECTED, { user_id: userId, username: member.username }, userId);
  }

  private async playback(fromUserId: string, p: any) {
    const r = this.room!;
    const now = Date.now();
    // No client-clock ordering here, deliberately. This used to drop actions
    // whose p.server_time (the CLIENT's clock) was older than r.last_update
    // (the SERVER's clock) — two unrelated clocks. Any device running slightly
    // behind Cloudflare had its commands silently discarded whenever somebody
    // else had just acted, which looks exactly like "the button does nothing".
    // A Durable Object handles one message at a time, so arrival order here is
    // already the authoritative order; there is nothing to reorder, and no
    // untrusted timestamp that could do it better.

    // Set by the track-changing actions below; drives the buffer window opened
    // after the broadcast.
    let startedTrackId: string | null = null;

    switch (p.action) {
      case 'play':
        r.is_playing = true;
        break;
      case 'pause':
        r.is_playing = false;
        break;
      case 'seek':
        if (typeof p.position === 'number') r.position = p.position;
        break;
      case 'set_volume':
        if (typeof p.volume === 'number') r.volume = p.volume;
        break;
      case 'change_track':
      case 'skip_next':
      case 'skip_prev':
        if (p.track_info) r.current_track = p.track_info;
        r.position = typeof p.position === 'number' ? p.position : 0;
        startedTrackId = r.current_track?.id ?? null;
        break;
      case 'queue_add':
        if (p.track_info) r.queue = [...r.queue, p.track_info];
        break;
      case 'queue_remove':
        if (p.track_id) r.queue = r.queue.filter((t) => t.id !== p.track_id);
        break;
      case 'queue_clear':
        r.queue = [];
        break;
      case 'sync_queue':
        if (Array.isArray(p.queue)) r.queue = p.queue;
        break;
    }
    if (typeof p.position === 'number') r.position = p.position;
    r.last_update = now;
    await this.persist();

    // server_time is stamped here so clients can derive their clock offset;
    // without it every guest drifts by however wrong its device clock is.
    this.broadcast(S2C.SYNC_PLAYBACK, { ...p, server_time: now, from_user_id: fromUserId });

    if (startedTrackId) await this.openBufferWindow(startedTrackId, fromUserId);
  }

  /**
   * A guest that receives a track change pauses, loads the track, sends
   * BUFFER_READY and then waits for BUFFER_COMPLETE before it plays. Nothing
   * used to open this window, so `bufferReady` saw a null buffer, dropped every
   * report, and no BUFFER_COMPLETE was ever broadcast — the track changed on
   * every device and none of them ever started playing.
   *
   * Everyone except the actor is waited on; the actor is already on the track.
   * The alarm deadline releases the room if a straggler never reports.
   */
  private async openBufferWindow(trackId: string, actorId: string) {
    const waiting = Object.values(this.members)
      .filter((m) => m.connected && m.user_id !== actorId)
      .map((m) => m.user_id);

    if (waiting.length === 0) {
      // Nobody to wait for: release immediately rather than leaving guests that
      // join mid-track with no completion to wait on.
      this.buffer = null;
      await this.persist();
      this.broadcast(S2C.BUFFER_COMPLETE, { track_id: trackId });
      return;
    }

    this.buffer = {
      track_id: trackId,
      waiting_for: waiting,
      deadline: Date.now() + BUFFER_TIMEOUT_MS,
    };
    await this.persist();
    this.broadcast(S2C.BUFFER_WAIT, { track_id: trackId, waiting_for: waiting });
    await this.rescheduleAlarm();
  }

  private async suggest(fromUserId: string, track: TrackInfo) {
    if (!track || typeof track !== 'object') return;
    const cleanId = typeof (track as any).id === 'string' ? (track as any).id.slice(0, 128) : '';
    const cleanTitle = typeof (track as any).title === 'string' ? (track as any).title.slice(0, 200) : '';
    if (!cleanId) return;
    // Per-user cap to prevent suggestion spam.
    const userCount = Object.values(this.suggestions).filter(
      (s) => s.from_user_id === fromUserId,
    ).length;
    if (userCount >= 20) return;
    const suggestion_id = crypto.randomUUID();
    const from = this.members[fromUserId];
    this.suggestions[suggestion_id] = {
      suggestion_id,
      from_user_id: fromUserId,
      from_username: from.username,
      track_info: { ...track, id: cleanId, title: cleanTitle, suggested_by: from.username },
    };
    await this.persist();
    const host = this.room!.host_id;
    if (host) {
      for (const ws of this.socketsFor(host)) {
        this.send(ws, S2C.SUGGESTION_RECEIVED, this.suggestions[suggestion_id]);
      }
    }
  }

  private async resolveSuggestion(id: string, approved: boolean, reason: string | null) {
    const s = this.suggestions[id];
    if (!s) return;
    delete this.suggestions[id];
    if (approved) {
      this.room!.queue = [...this.room!.queue, s.track_info];
      await this.persist();
      this.broadcast(S2C.SUGGESTION_APPROVED, { suggestion_id: id, track_info: s.track_info });
    } else {
      await this.persist();
      for (const ws of this.socketsFor(s.from_user_id)) {
        this.send(ws, S2C.SUGGESTION_REJECTED, { suggestion_id: id, reason });
      }
    }
  }

  private async bufferReady(userId: string, trackId: string) {
    if (!this.buffer || this.buffer.track_id !== trackId) return;
    this.buffer.waiting_for = this.buffer.waiting_for.filter((u) => u !== userId);
    if (this.buffer.waiting_for.length === 0) {
      const t = this.buffer.track_id;
      this.buffer = null;
      await this.persist();
      this.broadcast(S2C.BUFFER_COMPLETE, { track_id: t });
    } else {
      await this.persist();
    }
    await this.rescheduleAlarm();
  }

  private async kick(userId: string, reason: string) {
    const m = this.members[userId];
    if (!m) return;
    for (const ws of this.socketsFor(userId)) {
      this.send(ws, S2C.KICKED, { reason });
      try {
        ws.close(1000, 'kicked');
      } catch {
        /* ignore */
      }
    }
    // Token dies with the membership, otherwise the kick lasts only until the
    // client's next automatic reconnect.
    delete this.tokens[m.session_token];
    await this.removeMember(userId, 'kicked');
  }

  private async removeMember(userId: string, why: 'left' | 'kicked') {
    const m = this.members[userId];
    if (!m) return;
    delete this.members[userId];
    if (why === 'left') delete this.tokens[m.session_token];
    this.broadcast(S2C.USER_LEFT, { user_id: userId, username: m.username });

    if (this.room!.host_id === userId) {
      await this.promoteHost();
    }
    if (Object.keys(this.members).length === 0) {
      // Nobody left: drop the room rather than leak its storage forever.
      await this.state.storage.deleteAll();
      this.room = null;
      return;
    }
    await this.persist();
  }

  private async promoteHost() {
    const candidates = Object.values(this.members)
      .filter((m) => m.connected)
      .sort((a, b) => a.joined_at - b.joined_at);
    const next = candidates[0];
    this.room!.host_id = next ? next.user_id : null;
    this.hostGraceDeadline = 0;
    await this.persist();
    if (next) {
      this.broadcast(S2C.HOST_CHANGED, { new_host_id: next.user_id, new_host_name: next.username });
    }
  }

  // Lifecycle.

  async webSocketClose(ws: WebSocket) {
    await this.onSocketGone(ws);
  }

  async webSocketError(ws: WebSocket) {
    await this.onSocketGone(ws);
  }

  private async onSocketGone(ws: WebSocket) {
    if (!this.room) return;
    const att = ws.deserializeAttachment() as { user_id: string | null } | null;
    const userId = att?.user_id;
    if (!userId) return;
    // Another socket may already hold this identity after a reconnect race.
    if (this.socketsFor(userId).some((s) => s !== ws)) return;

    const m = this.members[userId];
    if (!m) return;
    m.connected = false;
    this.broadcast(S2C.USER_DISCONNECTED, { user_id: userId, username: m.username });

    // A host whose screen locked has not abdicated. Give them a grace window
    // before handing the room to someone else.
    if (this.room.host_id === userId) {
      this.hostGraceDeadline = Date.now() + HOST_GRACE_MS;
    }
    await this.persist();
    await this.rescheduleAlarm();
  }

  /** One alarm slot, several deadlines — always arm the earliest. */
  private async rescheduleAlarm() {
    if (!this.room) return;
    const times: number[] = [this.room.expires_at];
    if (!this.room.warned) times.push(this.room.expires_at - EXPIRY_WARNING_MS);
    if (this.hostGraceDeadline > 0) times.push(this.hostGraceDeadline);
    if (this.buffer) times.push(this.buffer.deadline);
    const next = Math.min(...times.filter((t) => t > Date.now()));
    if (Number.isFinite(next)) await this.state.storage.setAlarm(next);
  }

  async alarm() {
    if (!this.room) return;
    const now = Date.now();

    if (now >= this.room.expires_at) {
      this.broadcast(S2C.ROOM_CLOSED, { reason: 'Session ended' });
      for (const ws of this.state.getWebSockets()) {
        try {
          ws.close(1000, 'room expired');
        } catch {
          /* ignore */
        }
      }
      await this.state.storage.deleteAll();
      this.room = null;
      return;
    }

    if (!this.room.warned && now >= this.room.expires_at - EXPIRY_WARNING_MS) {
      this.room.warned = true;
      this.broadcast(S2C.ROOM_EXPIRING, {
        expires_at: this.room.expires_at,
        extensions_left: Number(this.env.MAX_EXTENSIONS ?? '2') - this.room.extensions_used,
      });
    }

    if (this.hostGraceDeadline > 0 && now >= this.hostGraceDeadline) {
      await this.promoteHost();
    }

    if (this.buffer && now >= this.buffer.deadline) {
      const t = this.buffer.track_id;
      this.buffer = null;
      // Start without the straggler; they resync on their own. Waiting
      // forever is how one bad connection freezes the whole room.
      this.broadcast(S2C.BUFFER_COMPLETE, { track_id: t });
    }

    await this.persist();
    await this.rescheduleAlarm();
  }
}
