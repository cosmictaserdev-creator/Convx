package com.convx.music.canvas

import kotlinx.serialization.Serializable

@Serializable
data class AppleArtist(
    val id: String,
    val name: String,
    /** What iOS shows as the artist hero: motion preview frame > vipSquare > profile artwork. */
    val hero: AppleArtwork?,
    /** Artist's uploaded wordmark, white variant preferred. Transparent PNG. */
    val logo: AppleArtwork?,
    /** Artist's chosen button colour (info/play/favourite); null means white. */
    val keyColor: Int?,
    /** Square motion artwork (HLS) that plays in the hero, when the artist has one. */
    val motionVideoUrl: String?,
)
