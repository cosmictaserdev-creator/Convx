package com.composables.sfsymbols.monochrome

import androidx.compose.ui.graphics.vector.ImageVector
import com.composables.sfsymbols.SfSymbols
import com.composables.sfsymbols.addSfPath
import com.composables.sfsymbols.sfIcon

/**
 * SF Symbol: SFPersonFillBadgeMinus (monochrome)
 * Viewport: 36.8164 x 34.7754
 */
public val SfSymbols.Monochrome.SFPersonFillBadgeMinus: ImageVector
    get() {
        if (_sFPersonFillBadgeMinus != null) {
            return _sFPersonFillBadgeMinus!!
        }
        _sFPersonFillBadgeMinus = sfIcon(
            name = "Monochrome.SFPersonFillBadgeMinus",
            viewportWidth = 36.8164f,
            viewportHeight = 34.7754f
        ) {
            addSfPath("M24.0914 20.7281C22.2518 22.1695 21.0645 24.4097 21.0645 26.9043C21.0645 27.5021 21.1318 28.0847 21.2654 28.6426L9.7168 28.6426C8.4375 28.6426 7.65625 28.0273 7.65625 27.0312C7.65625 23.7891 11.709 19.3164 18.2227 19.3164C20.5074 19.3164 22.4893 19.8667 24.0914 20.7281ZM23.2422 11.5137C23.2422 14.6387 20.957 17.0605 18.2324 17.0605C15.5176 17.0605 13.2324 14.6387 13.2324 11.5332C13.2324 8.49609 15.5371 6.12305 18.2324 6.12305C20.9375 6.12305 23.2422 8.45703 23.2422 11.5137Z", fillAlpha = 0.85f)
            addSfPath("M35.2539 26.9043C35.2539 30.3613 32.373 33.2227 28.9355 33.2227C25.4785 33.2227 22.627 30.3809 22.627 26.9043C22.627 23.4473 25.4785 20.5859 28.9355 20.5859C32.4023 20.5859 35.2539 23.4375 35.2539 26.9043ZM25.6543 26.1914C25.2734 26.1914 24.9414 26.5332 24.9414 26.9043C24.9414 27.2852 25.2734 27.6172 25.6543 27.6172L32.2168 27.6172C32.6074 27.6172 32.9297 27.2852 32.9297 26.9043C32.9297 26.5332 32.6074 26.1914 32.2168 26.1914Z", fillAlpha = 0.85f)
        }
        return _sFPersonFillBadgeMinus!!
    }

private var _sFPersonFillBadgeMinus: ImageVector? = null
