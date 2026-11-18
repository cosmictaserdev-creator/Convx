package com.composables.sfsymbols.monochrome

import androidx.compose.ui.graphics.vector.ImageVector
import com.composables.sfsymbols.SfSymbols
import com.composables.sfsymbols.addSfPath
import com.composables.sfsymbols.sfIcon

/**
 * SF Symbol: SFMagnifyingglassCircleFill (monochrome)
 * Viewport: 25.8008 x 25.459
 */
public val SfSymbols.Monochrome.SFMagnifyingglassCircleFill: ImageVector
    get() {
        if (_sFMagnifyingglassCircleFill != null) {
            return _sFMagnifyingglassCircleFill!!
        }
        _sFMagnifyingglassCircleFill = sfIcon(
            name = "Monochrome.SFMagnifyingglassCircleFill",
            viewportWidth = 25.8008f,
            viewportHeight = 25.459f
        ) {
            addSfPath("M25.4395 12.7246C25.4395 19.7266 19.7266 25.4395 12.7148 25.4395C5.71289 25.4395 0 19.7266 0 12.7246C0 5.71289 5.71289 0 12.7148 0C19.7266 0 25.4395 5.71289 25.4395 12.7246ZM5.97656 11.2305C5.97656 14.1504 8.37891 16.543 11.2988 16.543C12.373 16.543 13.3789 16.2012 14.2188 15.6445L17.6758 19.1016C17.9199 19.3555 18.1543 19.4629 18.4961 19.4629C19.043 19.4629 19.4238 19.0527 19.4238 18.4863C19.4238 18.2227 19.2969 17.9785 19.1016 17.7832L15.625 14.2871C16.2402 13.4277 16.6113 12.373 16.6113 11.2305C16.6113 8.29102 14.2285 5.89844 11.2988 5.89844C8.37891 5.89844 5.97656 8.30078 5.97656 11.2305ZM15.0781 11.2207C15.0781 13.291 13.3789 15.0098 11.2988 15.0098C9.21875 15.0098 7.50977 13.291 7.50977 11.2207C7.50977 9.15039 9.21875 7.43164 11.2988 7.43164C13.3789 7.43164 15.0781 9.14062 15.0781 11.2207Z", fillAlpha = 0.85f)
        }
        return _sFMagnifyingglassCircleFill!!
    }

private var _sFMagnifyingglassCircleFill: ImageVector? = null
