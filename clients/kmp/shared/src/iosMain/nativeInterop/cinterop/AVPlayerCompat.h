#import <AVFoundation/AVFoundation.h>
#import <CoreMedia/CoreMedia.h>

// Kotlin/Native's bundled AVFoundation metadata omits these AVPlayer selectors.
static inline void convx_av_player_play(id player) {
    [(AVPlayer *)player play];
}

static inline void convx_av_player_play_at_rate(id player, double rate) {
    [(AVPlayer *)player playImmediatelyAtRate:(float)rate];
}

static inline void convx_av_player_set_rate(id player, double rate) {
    [(AVPlayer *)player setRate:(float)rate];
}

static inline void convx_av_player_pause(id player) {
    [(AVPlayer *)player pause];
}

static inline void convx_av_player_set_volume(id player, double volume) {
    [(AVPlayer *)player setVolume:(float)volume];
}

static inline void convx_av_player_replace_item(id player, id item) {
    [(AVPlayer *)player replaceCurrentItemWithPlayerItem:(AVPlayerItem *)item];
}

static inline void convx_av_player_seek_to_seconds(id player, double seconds) {
    [(AVPlayer *)player seekToTime:CMTimeMakeWithSeconds(seconds, 600)];
}

static inline double convx_av_player_current_time_seconds(id player) {
    return CMTimeGetSeconds([(AVPlayer *)player currentTime]);
}

static inline double convx_av_player_duration_seconds(id player) {
    AVPlayerItem *item = [(AVPlayer *)player currentItem];
    return item == nil ? 0.0 : CMTimeGetSeconds(item.duration);
}

static inline long long convx_av_player_item_status(id player) {
    AVPlayerItem *item = [(AVPlayer *)player currentItem];
    return item == nil ? -1 : (long long)item.status;
}
