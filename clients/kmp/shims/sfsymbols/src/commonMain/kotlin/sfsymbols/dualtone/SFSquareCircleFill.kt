package com.composables.sfsymbols.dualtone

import androidx.compose.ui.graphics.vector.ImageVector
import com.composables.sfsymbols.SfSymbols
import com.composables.sfsymbols.addSfPath
import com.composables.sfsymbols.sfIcon

/**
 * SF Symbol: SFSquareCircleFill (dualtone)
 * Viewport: 25.8008 x 25.5566
 */
public val SfSymbols.Dualtone.SFSquareCircleFill: ImageVector
    get() {
        if (_sFSquareCircleFill != null) {
            return _sFSquareCircleFill!!
        }
        _sFSquareCircleFill = sfIcon(
            name = "Dualtone.SFSquareCircleFill",
            viewportWidth = 25.8008f,
            viewportHeight = 25.5566f
        ) {
            addSfPath("M12.7148 25.5566C19.7266 25.5566 25.4395 19.8438 25.4395 12.832C25.4395 5.82031 19.7266 0.117188 12.7148 0.117188C5.71289 0.117188 0 5.82031 0 12.832C0 19.8438 5.71289 25.5566 12.7148 25.5566Z", fillAlpha = 0.2125f)
            addSfPath("M7.96875 19.3262C6.93359 19.3262 6.24023 18.5547 6.24023 17.4121L6.24023 8.05664C6.24023 7.04102 6.93359 6.34766 7.96875 6.34766L17.4805 6.34766C18.6523 6.34766 19.2188 6.91406 19.2188 8.05664L19.2188 17.4121C19.2188 18.6719 18.6523 19.3262 17.4805 19.3262ZM8.61328 17.6855L16.8359 17.6855C17.2754 17.6855 17.5781 17.3926 17.5781 16.9434L17.5781 8.73047C17.5781 8.28125 17.2754 7.98828 16.8359 7.98828L8.61328 7.98828C8.17383 7.98828 7.88086 8.28125 7.88086 8.73047L7.88086 16.9434C7.88086 17.3926 8.17383 17.6855 8.61328 17.6855Z", fillAlpha = 0.85f)
        }
        return _sFSquareCircleFill!!
    }

private var _sFSquareCircleFill: ImageVector? = null
