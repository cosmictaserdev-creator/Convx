package com.composables.sfsymbols.monochrome

import androidx.compose.ui.graphics.vector.ImageVector
import com.composables.sfsymbols.SfSymbols
import com.composables.sfsymbols.addSfPath
import com.composables.sfsymbols.sfIcon

/**
 * SF Symbol: SFEllipsisCircle (monochrome)
 * Viewport: 25.8008 x 25.459
 */
public val SfSymbols.Monochrome.SFEllipsisCircle: ImageVector
    get() {
        if (_sFEllipsisCircle != null) {
            return _sFEllipsisCircle!!
        }
        _sFEllipsisCircle = sfIcon(
            name = "Monochrome.SFEllipsisCircle",
            viewportWidth = 25.8008f,
            viewportHeight = 25.459f
        ) {
            addSfPath("M12.7148 25.4395C19.7363 25.4395 25.4395 19.7461 25.4395 12.7246C25.4395 5.70312 19.7363 0 12.7148 0C5.69336 0 0 5.70312 0 12.7246C0 19.7461 5.69336 25.4395 12.7148 25.4395ZM12.7148 23.623C6.68945 23.623 1.81641 18.75 1.81641 12.7246C1.81641 6.69922 6.68945 1.82617 12.7148 1.82617C18.7402 1.82617 23.6133 6.69922 23.6133 12.7246C23.6133 18.75 18.7402 23.623 12.7148 23.623Z", fillAlpha = 0.85f)
            addSfPath("M18.5449 14.4531C19.502 14.4531 20.2832 13.6719 20.2832 12.7051C20.2832 11.748 19.502 10.9668 18.5449 10.9668C17.5781 10.9668 16.7969 11.748 16.7969 12.7051C16.7969 13.6719 17.5781 14.4531 18.5449 14.4531Z", fillAlpha = 0.85f)
            addSfPath("M12.7051 14.4531C13.6719 14.4531 14.4531 13.6719 14.4531 12.7051C14.4531 11.748 13.6719 10.9668 12.7051 10.9668C11.748 10.9668 10.9668 11.748 10.9668 12.7051C10.9668 13.6719 11.748 14.4531 12.7051 14.4531Z", fillAlpha = 0.85f)
            addSfPath("M6.875 14.4531C7.83203 14.4531 8.61328 13.6719 8.61328 12.7051C8.61328 11.748 7.82227 10.9668 6.875 10.9668C5.9082 10.9668 5.12695 11.748 5.12695 12.7051C5.12695 13.6719 5.9082 14.4531 6.875 14.4531Z", fillAlpha = 0.85f)
        }
        return _sFEllipsisCircle!!
    }

private var _sFEllipsisCircle: ImageVector? = null
