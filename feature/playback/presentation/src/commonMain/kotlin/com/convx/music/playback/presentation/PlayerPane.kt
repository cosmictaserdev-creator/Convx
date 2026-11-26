package com.convx.music.playback.presentation

/** One auxiliary pane is active at a time; platforms animate toward this shared target. */
enum class PlayerPane {
    COVER, LYRICS, QUEUE;

    fun toggle(target: PlayerPane): PlayerPane = if (this == target) COVER else target
    fun back(): PlayerPane = COVER
}
