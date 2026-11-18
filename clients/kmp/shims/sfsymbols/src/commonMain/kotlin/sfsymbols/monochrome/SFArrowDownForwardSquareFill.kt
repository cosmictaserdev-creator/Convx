package com.composables.sfsymbols.monochrome

import androidx.compose.ui.graphics.vector.ImageVector
import com.composables.sfsymbols.SfSymbols
import com.composables.sfsymbols.addSfPath
import com.composables.sfsymbols.sfIcon

/**
 * SF Symbol: SFArrowDownForwardSquareFill (monochrome)
 * Viewport: 23.3203 x 22.959
 */
public val SfSymbols.Monochrome.SFArrowDownForwardSquareFill: ImageVector
    get() {
        if (_sFArrowDownForwardSquareFill != null) {
            return _sFArrowDownForwardSquareFill!!
        }
        _sFArrowDownForwardSquareFill = sfIcon(
            name = "Monochrome.SFArrowDownForwardSquareFill",
            viewportWidth = 23.3203f,
            viewportHeight = 22.959f
        ) {
            addSfPath("M22.959 3.76953L22.959 19.1992C22.959 21.6797 21.6797 22.959 19.1504 22.959L3.79883 22.959C1.2793 22.959 0 21.6992 0 19.1992L0 3.76953C0 1.26953 1.2793 0 3.79883 0L19.1504 0C21.6797 0 22.959 1.2793 22.959 3.76953ZM6.5332 7.37305C6.5332 7.59766 6.64062 7.8125 6.80664 7.97852L12.0605 13.2227L13.9417 14.9937L12.0215 14.8047L9.20898 14.8047C8.67188 14.8047 8.33008 15.127 8.33008 15.625C8.33008 16.1133 8.66211 16.4355 9.18945 16.4355L15.5273 16.4355C16.084 16.4355 16.4453 16.1621 16.4453 15.5176L16.4453 9.21875C16.4453 8.69141 16.1133 8.33008 15.6348 8.33008C15.1367 8.33008 14.8047 8.66211 14.8047 9.19922L14.8047 11.7969L14.9927 13.921L13.2324 12.0508L7.99805 6.79688C7.83203 6.64062 7.62695 6.54297 7.34375 6.54297C6.85547 6.54297 6.5332 6.86523 6.5332 7.37305Z", fillAlpha = 0.85f)
        }
        return _sFArrowDownForwardSquareFill!!
    }

private var _sFArrowDownForwardSquareFill: ImageVector? = null
