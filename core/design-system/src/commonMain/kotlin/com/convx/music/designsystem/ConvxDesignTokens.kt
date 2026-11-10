package com.convx.music.designsystem

import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp

/** Brand, layout, type, and glass defaults shared by Convx clients. */
object ConvxDesignTokens {
    object Colors {
        val AccentRed = Color(0xFFFA2D48)
        val Background = Color(0xFF121212)
        val BackgroundElevated = Color(0xFF1A1A1A)
        val Card = Color(0xFF1C1C1E)
        val CardSecondary = Color(0xFF2C2C2E)
        val SecondaryLabel = Color(0xFFA1A1AA)
    }

    object Metrics {
        val SidebarWidth = 320.dp
        val SidebarCollapsedWidth = 72.dp
        val SidebarInset = 30.dp
        val SidebarContentGap = 20.dp
        val SidebarRowHeight = 44.dp
        val SidebarLeadingSlot = 24.dp
        val SidebarIconSize = 22.dp
        val SidebarCorner = 20.dp
        val TabletWidthThreshold = 840.dp
        val MiniPlayerHeight = 64.dp
        val MiniPlayerArtworkSize = 38.dp
        val MiniPlayerControlSize = 36.dp
        val MiniPlayerIconSize = 18.dp
        val MiniPlayerPlayIconSize = 22.dp
        const val MiniPlayerWidthFraction = 0.8f
        val ContentGutter = 20.dp
        val ListRowInset = 16.dp
        val ItemGap = 16.dp
        val SectionGap = 24.dp
        val TextGap = 2.dp
        val ArtworkCorner = 8.dp
        val CardCorner = 14.dp
        val LargeCardCorner = 18.dp
    }

    object Type {
        val ScreenTitle = 34.sp
        val ScreenTitleLineHeight = 41.sp
        val SectionHeader = 22.sp
        val SectionHeaderLineHeight = 28.sp
        val ItemTitle = 15.sp
        val ItemTitleLineHeight = 20.sp
        val ItemSubtitle = 13.sp
        val ItemSubtitleLineHeight = 18.sp
        val Caption = 12.sp
        val CaptionLineHeight = 16.sp
        val SidebarLabel = 17.sp
        val SidebarLabelLineHeight = 22.sp
        val SidebarLabelTracking = (-0.43).sp
        val MiniPlayerTitle = 15.sp
        val MiniPlayerTitleLineHeight = 20.sp
        val MiniPlayerArtist = 11.sp
        val MiniPlayerArtistLineHeight = 13.sp
    }

    object Glass {
        const val MaxLensDp = 48f
        const val SidePanelBlurDp = 2f
        const val SidePanelSaturation = 1.2f
        const val SidePanelLensHeight = 0.4f
        const val SidePanelLensAmount = 0.6f
        const val SidePanelTintOpacity = 0.75f
        const val MiniPlayerBlurDp = 3f
        const val MiniPlayerSaturation = 2f
        const val MiniPlayerLensHeight = 0.6f
        const val MiniPlayerLensAmount = 0.69f
        const val DarkSurfaceLift = 0.15f
        const val DarkSelectedPuckOpacity = 0.8f
        const val SidePanelHighlightOpacity = 0.3f
        const val MiniPlayerHighlightOpacity = 0.3f
    }
}
