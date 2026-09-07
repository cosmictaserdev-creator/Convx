/**
 * Wire protocol for Convx Listen Together.
 *
 * Mirrors app/src/main/kotlin/com/convxy/music/listentogether/Protocol.kt.
 * Field names are snake_case because the Kotlin side declares @SerialName on
 * every multi-word field — changing a name here silently breaks decoding on
 * the client, which fails as a missing field rather than an error.
 *
 * The client starts in JSON, uncompressed, and only flips to protobuf+gzip if
 * the SERVER speaks protobuf first (ListenTogetherClient.kt:783-785). This
 * server is JSON-only and must stay that way, or every client permanently
 * switches format mid-session.
 */

export const C2S = {
  CREATE_ROOM: 'create_room',
  JOIN_ROOM: 'join_room',
  LEAVE_ROOM: 'leave_room',
  APPROVE_JOIN: 'approve_join',
  REJECT_JOIN: 'reject_join',
  PLAYBACK_ACTION: 'playback_action',
  BUFFER_READY: 'buffer_ready',
  KICK_USER: 'kick_user',
  TRANSFER_HOST: 'transfer_host',
  PING: 'ping',
  CHAT: 'chat',
  REQUEST_SYNC: 'request_sync',
  RECONNECT: 'reconnect',
  SUGGEST_TRACK: 'suggest_track',
  APPROVE_SUGGESTION: 'approve_suggestion',
  REJECT_SUGGESTION: 'reject_suggestion',
  // v2
  SET_CONTROL_MODE: 'set_control_mode',
  EXTEND_SESSION: 'extend_session',
} as const;

export const S2C = {
  ROOM_CREATED: 'room_created',
  JOIN_REQUEST: 'join_request',
  JOIN_APPROVED: 'join_approved',
  JOIN_REJECTED: 'join_rejected',
  USER_JOINED: 'user_joined',
  USER_LEFT: 'user_left',
  SYNC_PLAYBACK: 'sync_playback',
  BUFFER_WAIT: 'buffer_wait',
  BUFFER_COMPLETE: 'buffer_complete',
  ERROR: 'error',
  PONG: 'pong',
  HOST_CHANGED: 'host_changed',
  KICKED: 'kicked',
  SYNC_STATE: 'sync_state',
  RECONNECTED: 'reconnected',
  USER_RECONNECTED: 'user_reconnected',
  USER_DISCONNECTED: 'user_disconnected',
  SUGGESTION_RECEIVED: 'suggestion_received',
  SUGGESTION_APPROVED: 'suggestion_approved',
  SUGGESTION_REJECTED: 'suggestion_rejected',
  // v2
  CONTROL_MODE_CHANGED: 'control_mode_changed',
  ROOM_EXPIRING: 'room_expiring',
  ROOM_CLOSED: 'room_closed',
} as const;

export const PlaybackActions = {
  PLAY: 'play',
  PAUSE: 'pause',
  SEEK: 'seek',
  SKIP_NEXT: 'skip_next',
  SKIP_PREV: 'skip_prev',
  CHANGE_TRACK: 'change_track',
  QUEUE_ADD: 'queue_add',
  QUEUE_REMOVE: 'queue_remove',
  QUEUE_CLEAR: 'queue_clear',
  SYNC_QUEUE: 'sync_queue',
  SET_VOLUME: 'set_volume',
} as const;

/** Who may send playback actions. Owner-settable, enforced server-side. */
export type ControlMode = 'owner' | 'everyone';

export interface TrackInfo {
  id: string;
  title: string;
  artist: string;
  album?: string | null;
  duration: number;
  thumbnail?: string | null;
  suggested_by?: string | null;
}

export interface UserInfo {
  user_id: string;
  username: string;
  is_host: boolean;
  is_connected: boolean;
}

export interface RoomState {
  room_code: string;
  host_id: string;
  users: UserInfo[];
  current_track: TrackInfo | null;
  is_playing: boolean;
  position: number;
  last_update: number;
  volume: number;
  queue: TrackInfo[];
  // v2 additions. Older clients ignore unknown fields (the Kotlin Json is
  // configured with ignoreUnknownKeys), so adding these is backward safe.
  control_mode: ControlMode;
  expires_at: number;
  extensions_used: number;
}

export interface Message {
  type: string;
  payload?: unknown;
}

export function encode(type: string, payload?: unknown): string {
  return JSON.stringify(payload === undefined ? { type } : { type, payload });
}
