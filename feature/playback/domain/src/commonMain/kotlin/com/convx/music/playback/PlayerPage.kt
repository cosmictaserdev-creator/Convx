package com.convx.music.playback

/** Player destinations shared by the Android V2 player and desktop. */
enum class PlayerPage {
    COVER, LYRICS, QUEUE;

    /** Selecting the active auxiliary tab returns to the artwork. */
    fun toggle(page: PlayerPage): PlayerPage = if (this == page) COVER else page
}
