package com.composables.sfsymbols.monochrome

import androidx.compose.ui.graphics.vector.ImageVector
import com.composables.sfsymbols.SfSymbols
import com.composables.sfsymbols.addSfPath
import com.composables.sfsymbols.sfIcon

/**
 * SF Symbol: SFPersonBadgeClockFill (monochrome)
 * Viewport: 36.8164 x 34.7754
 */
public val SfSymbols.Monochrome.SFPersonBadgeClockFill: ImageVector
    get() {
        if (_sFPersonBadgeClockFill != null) {
            return _sFPersonBadgeClockFill!!
        }
        _sFPersonBadgeClockFill = sfIcon(
            name = "Monochrome.SFPersonBadgeClockFill",
            viewportWidth = 36.8164f,
            viewportHeight = 34.7754f
        ) {
            addSfPath("M24.0914 20.7281C22.2518 22.1695 21.0645 24.4097 21.0645 26.9043C21.0645 27.5021 21.1318 28.0847 21.2654 28.6426L9.7168 28.6426C8.4375 28.6426 7.65625 28.0273 7.65625 27.0312C7.65625 23.7891 11.709 19.3164 18.2227 19.3164C20.5074 19.3164 22.4893 19.8667 24.0914 20.7281ZM23.2422 11.5137C23.2422 14.6387 20.957 17.0605 18.2324 17.0605C15.5176 17.0605 13.2324 14.6387 13.2324 11.5332C13.2324 8.49609 15.5371 6.12305 18.2324 6.12305C20.9375 6.12305 23.2422 8.45703 23.2422 11.5137Z", fillAlpha = 0.85f)
            addSfPath("M35.2539 26.9043C35.2539 30.3613 32.373 33.2227 28.9355 33.2227C25.4785 33.2227 22.627 30.3809 22.627 26.9043C22.627 23.4473 25.4785 20.5859 28.9355 20.5859C32.4023 20.5859 35.2539 23.4375 35.2539 26.9043ZM28.2812 23.125L28.2812 26.4355L25.918 26.4355C25.5273 26.4355 25.1953 26.7578 25.1953 27.1387C25.1953 27.5391 25.5176 27.8613 25.918 27.8613L28.9941 27.8613C29.3945 27.8613 29.707 27.5391 29.707 27.1387L29.707 23.125C29.707 22.7344 29.3945 22.4316 28.9941 22.4316C28.6035 22.4316 28.2812 22.7344 28.2812 23.125Z", fillAlpha = 0.85f)
        }
        return _sFPersonBadgeClockFill!!
    }

private var _sFPersonBadgeClockFill: ImageVector? = null
