package com.composables.sfsymbols.dualtone

import androidx.compose.ui.graphics.vector.ImageVector
import com.composables.sfsymbols.SfSymbols
import com.composables.sfsymbols.addSfPath
import com.composables.sfsymbols.sfIcon

/**
 * SF Symbol: SFMagnifyingglassCircleFill (dualtone)
 * Viewport: 25.8008 x 25.459
 */
public val SfSymbols.Dualtone.SFMagnifyingglassCircleFill: ImageVector
    get() {
        if (_sFMagnifyingglassCircleFill != null) {
            return _sFMagnifyingglassCircleFill!!
        }
        _sFMagnifyingglassCircleFill = sfIcon(
            name = "Dualtone.SFMagnifyingglassCircleFill",
            viewportWidth = 25.8008f,
            viewportHeight = 25.459f
        ) {
            addSfPath("M12.7148 25.4395C19.7266 25.4395 25.4395 19.7266 25.4395 12.7246C25.4395 5.71289 19.7266 0 12.7148 0C5.71289 0 0 5.71289 0 12.7246C0 19.7266 5.71289 25.4395 12.7148 25.4395Z", fillAlpha = 0.2125f)
            addSfPath("M11.2988 16.543C8.37891 16.543 5.97656 14.1504 5.97656 11.2305C5.97656 8.30078 8.37891 5.89844 11.2988 5.89844C14.2285 5.89844 16.6113 8.29102 16.6113 11.2305C16.6113 12.373 16.2402 13.4277 15.625 14.2871L19.1016 17.7832C19.2969 17.9785 19.4238 18.2227 19.4238 18.4863C19.4238 19.0527 19.043 19.4629 18.4961 19.4629C18.1543 19.4629 17.9199 19.3555 17.6758 19.1016L14.2188 15.6445C13.3789 16.2012 12.373 16.543 11.2988 16.543ZM11.2988 15.0098C13.3789 15.0098 15.0781 13.291 15.0781 11.2207C15.0781 9.14062 13.3789 7.43164 11.2988 7.43164C9.21875 7.43164 7.50977 9.15039 7.50977 11.2207C7.50977 13.291 9.21875 15.0098 11.2988 15.0098Z", fillAlpha = 0.85f)
        }
        return _sFMagnifyingglassCircleFill!!
    }

private var _sFMagnifyingglassCircleFill: ImageVector? = null
