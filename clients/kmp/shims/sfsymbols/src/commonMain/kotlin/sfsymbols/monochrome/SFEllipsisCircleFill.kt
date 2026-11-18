package com.composables.sfsymbols.monochrome

import androidx.compose.ui.graphics.vector.ImageVector
import com.composables.sfsymbols.SfSymbols
import com.composables.sfsymbols.addSfPath
import com.composables.sfsymbols.sfIcon

/**
 * SF Symbol: SFEllipsisCircleFill (monochrome)
 * Viewport: 25.8008 x 25.459
 */
public val SfSymbols.Monochrome.SFEllipsisCircleFill: ImageVector
    get() {
        if (_sFEllipsisCircleFill != null) {
            return _sFEllipsisCircleFill!!
        }
        _sFEllipsisCircleFill = sfIcon(
            name = "Monochrome.SFEllipsisCircleFill",
            viewportWidth = 25.8008f,
            viewportHeight = 25.459f
        ) {
            addSfPath("M25.4395 12.7246C25.4395 19.7266 19.7266 25.4395 12.7148 25.4395C5.71289 25.4395 0 19.7266 0 12.7246C0 5.71289 5.71289 0 12.7148 0C19.7266 0 25.4395 5.71289 25.4395 12.7246ZM16.9043 12.7051C16.9043 13.7012 17.7051 14.502 18.7012 14.502C19.6875 14.502 20.498 13.7012 20.498 12.7051C20.498 11.7188 19.6875 10.918 18.7012 10.918C17.7051 10.918 16.9043 11.7188 16.9043 12.7051ZM10.9277 12.7051C10.9277 13.7012 11.7285 14.502 12.7148 14.502C13.7109 14.502 14.5117 13.7012 14.5117 12.7051C14.5117 11.7188 13.7109 10.918 12.7148 10.918C11.7285 10.918 10.9277 11.7188 10.9277 12.7051ZM4.94141 12.7051C4.94141 13.7012 5.75195 14.502 6.72852 14.502C7.72461 14.502 8.52539 13.7012 8.52539 12.7051C8.52539 11.7188 7.71484 10.918 6.72852 10.918C5.75195 10.918 4.94141 11.7188 4.94141 12.7051Z", fillAlpha = 0.85f)
        }
        return _sFEllipsisCircleFill!!
    }

private var _sFEllipsisCircleFill: ImageVector? = null
