package com.convx.music.ui.component

import com.composables.sfsymbols.SfSymbol

/**
 * Symbols that pop in beside a sung word, Verci-style. Matched on the word itself (case and
 * trailing punctuation ignored, simple plurals folded), so "Hearts," finds "heart". Missing
 * symbols resolve to null, never an error: an icon is decoration, not something to crash on.
 */
object LyricStickers {
    fun symbolFor(word: String): SfSymbol? {
        val key = word.lowercase().trim { !it.isLetterOrDigit() && it != '\'' }
        val name = WORDS[key] ?: WORDS[key.removeSuffix("s")] ?: return null
        return cache[name]
    }


    private val WORDS = mapOf(
        "love" to "heart.fill", "heart" to "heart.fill", "kiss" to "mouth.fill",
        "see" to "eye.fill", "eye" to "eye.fill", "look" to "eye.fill", "watch" to "eye.fill",
        "you" to "person.fill", "me" to "person.fill", "we" to "person.2.fill", "us" to "person.2.fill",
        "call" to "phone.fill", "phone" to "phone.fill", "talk" to "bubble.left.fill", "telling" to "bubble.left.fill",
        "say" to "bubble.left.fill", "tell" to "bubble.left.fill",
        "car" to "car.fill", "drive" to "car.fill", "ride" to "car.fill",
        "start" to "play.fill", "play" to "play.fill", "stop" to "stop.fill",
        "wait" to "hourglass", "minute" to "clock.fill", "time" to "clock.fill", "hour" to "clock.fill",
        "night" to "moon.fill", "moon" to "moon.fill", "sun" to "sun.max.fill", "day" to "sun.max.fill",
        "star" to "star.fill", "sky" to "cloud.fill", "rain" to "cloud.rain.fill", "storm" to "cloud.bolt.fill",
        "fire" to "flame.fill", "burn" to "flame.fill", "hot" to "flame.fill", "cold" to "snowflake",
        "money" to "dollarsign.circle.fill", "cash" to "dollarsign.circle.fill", "rich" to "dollarsign.circle.fill",
        "home" to "house.fill", "house" to "house.fill", "world" to "globe", "fly" to "airplane",
        "music" to "music.note", "song" to "music.note", "sing" to "music.mic", "dance" to "figure.dance",
        "name" to "signature", "write" to "pencil", "blew" to "wind", "wind" to "wind", "breathe" to "wind",
        "chance" to "dice.fill", "luck" to "dice.fill", "ball" to "basketball.fill", "ballin" to "basketball.fill",
        "swish" to "hand.point.up.left.fill", "touch" to "hand.point.up.left.fill", "hand" to "hand.raised.fill",
        "king" to "crown.fill", "queen" to "crown.fill", "crown" to "crown.fill", "gold" to "crown.fill",
        "tears" to "drop.fill", "cry" to "drop.fill", "water" to "drop.fill", "ocean" to "water.waves",
        "sleep" to "bed.double.fill", "dream" to "sparkles", "magic" to "sparkles", "light" to "lightbulb.fill",
        "key" to "key.fill", "lock" to "lock.fill", "door" to "door.left.hand.open", "road" to "road.lanes",
        "gun" to "scope", "shot" to "scope", "party" to "party.popper.fill", "drink" to "wineglass.fill",
        "button" to "button.programmable", "switch" to "switch.2", "game" to "gamecontroller.fill",
        "camera" to "camera.fill", "picture" to "photo.fill", "photo" to "photo.fill",
        "book" to "book.fill", "letter" to "envelope.fill", "message" to "message.fill", "text" to "message.fill",
        "shame" to "eye.slash.fill", "hide" to "eye.slash.fill", "broken" to "heart.slash.fill",
    )
    private val cache = WORDS.values.distinct().associateWith(SfSymbol::named)
}
