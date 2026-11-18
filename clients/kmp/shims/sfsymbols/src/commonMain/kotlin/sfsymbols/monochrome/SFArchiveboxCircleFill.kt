package com.composables.sfsymbols.monochrome

import androidx.compose.ui.graphics.vector.ImageVector
import com.composables.sfsymbols.SfSymbols
import com.composables.sfsymbols.addSfPath
import com.composables.sfsymbols.sfIcon

/**
 * SF Symbol: SFArchiveboxCircleFill (monochrome)
 * Viewport: 25.8008 x 25.459
 */
public val SfSymbols.Monochrome.SFArchiveboxCircleFill: ImageVector
    get() {
        if (_sFArchiveboxCircleFill != null) {
            return _sFArchiveboxCircleFill!!
        }
        _sFArchiveboxCircleFill = sfIcon(
            name = "Monochrome.SFArchiveboxCircleFill",
            viewportWidth = 25.8008f,
            viewportHeight = 25.459f
        ) {
            addSfPath("M25.4395 12.7344C25.4395 19.7461 19.7266 25.459 12.7148 25.459C5.71289 25.459 0 19.7461 0 12.7344C0 5.73242 5.71289 0.0195312 12.7148 0.0195312C19.7266 0.0195312 25.4395 5.73242 25.4395 12.7344ZM6.85547 10.5859L6.85547 17.207C6.85547 18.5449 7.56836 19.2188 8.88672 19.2188L16.5527 19.2188C17.8711 19.2188 18.584 18.5352 18.584 17.207L18.584 10.5859ZM15.498 12.334L15.498 12.5488C15.498 12.832 15.3223 13.0176 15.0293 13.0176L10.4004 13.0176C10.127 13.0176 9.93164 12.832 9.93164 12.5488L9.93164 12.334C9.93164 12.0508 10.127 11.875 10.4004 11.875L15.0293 11.875C15.3223 11.875 15.498 12.0508 15.498 12.334ZM6.96289 6.57227C6.25977 6.57227 5.83984 7.02148 5.83984 7.76367L5.83984 8.44727C5.83984 9.17969 6.2207 9.62891 6.96289 9.62891L18.4668 9.62891C19.2188 9.62891 19.5996 9.17969 19.5996 8.44727L19.5996 7.76367C19.5996 7.02148 19.2188 6.57227 18.4668 6.57227Z", fillAlpha = 0.85f)
        }
        return _sFArchiveboxCircleFill!!
    }

private var _sFArchiveboxCircleFill: ImageVector? = null
