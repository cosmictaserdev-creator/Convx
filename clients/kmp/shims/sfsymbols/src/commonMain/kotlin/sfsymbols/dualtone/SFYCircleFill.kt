package com.composables.sfsymbols.dualtone

import androidx.compose.ui.graphics.vector.ImageVector
import com.composables.sfsymbols.SfSymbols
import com.composables.sfsymbols.addSfPath
import com.composables.sfsymbols.sfIcon

/**
 * SF Symbol: SFYCircleFill (dualtone)
 * Viewport: 25.8008 x 25.459
 */
public val SfSymbols.Dualtone.SFYCircleFill: ImageVector
    get() {
        if (_sFYCircleFill != null) {
            return _sFYCircleFill!!
        }
        _sFYCircleFill = sfIcon(
            name = "Dualtone.SFYCircleFill",
            viewportWidth = 25.8008f,
            viewportHeight = 25.459f
        ) {
            addSfPath("M12.7148 25.4395C19.7266 25.4395 25.4395 19.7266 25.4395 12.7246C25.4395 5.71289 19.7266 0 12.7148 0C5.71289 0 0 5.71289 0 12.7246C0 19.7266 5.71289 25.4395 12.7148 25.4395Z", fillAlpha = 0.2125f)
            addSfPath("M12.7051 18.6133C12.1387 18.6133 11.8262 18.2129 11.8262 17.6172L11.8262 13.9746L8.08594 7.95898C7.97852 7.7832 7.91992 7.60742 7.91992 7.43164C7.91992 7.00195 8.28125 6.65039 8.75 6.65039C9.15039 6.65039 9.36523 6.78711 9.59961 7.20703L12.6855 12.2656L12.7637 12.2656L15.8594 7.20703C16.084 6.79688 16.2988 6.65039 16.6797 6.65039C17.1582 6.65039 17.4902 6.99219 17.4902 7.45117C17.4902 7.61719 17.4414 7.80273 17.334 7.95898L13.6133 13.9551L13.6133 17.6172C13.6133 18.2422 13.291 18.6133 12.7051 18.6133Z", fillAlpha = 0.85f)
        }
        return _sFYCircleFill!!
    }

private var _sFYCircleFill: ImageVector? = null
