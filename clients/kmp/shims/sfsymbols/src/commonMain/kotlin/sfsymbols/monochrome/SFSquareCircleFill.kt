package com.composables.sfsymbols.monochrome

import androidx.compose.ui.graphics.vector.ImageVector
import com.composables.sfsymbols.SfSymbols
import com.composables.sfsymbols.addSfPath
import com.composables.sfsymbols.sfIcon

/**
 * SF Symbol: SFSquareCircleFill (monochrome)
 * Viewport: 25.8008 x 25.5566
 */
public val SfSymbols.Monochrome.SFSquareCircleFill: ImageVector
    get() {
        if (_sFSquareCircleFill != null) {
            return _sFSquareCircleFill!!
        }
        _sFSquareCircleFill = sfIcon(
            name = "Monochrome.SFSquareCircleFill",
            viewportWidth = 25.8008f,
            viewportHeight = 25.5566f
        ) {
            addSfPath("M25.4395 12.832C25.4395 19.8438 19.7266 25.5566 12.7148 25.5566C5.71289 25.5566 0 19.8438 0 12.832C0 5.82031 5.71289 0.117188 12.7148 0.117188C19.7266 0.117188 25.4395 5.82031 25.4395 12.832ZM7.96875 6.34766C6.93359 6.34766 6.24023 7.04102 6.24023 8.05664L6.24023 17.4121C6.24023 18.5547 6.93359 19.3262 7.96875 19.3262L17.4805 19.3262C18.6523 19.3262 19.2188 18.6719 19.2188 17.4121L19.2188 8.05664C19.2188 6.91406 18.6523 6.34766 17.4805 6.34766ZM17.5781 8.73047L17.5781 16.9434C17.5781 17.3926 17.2754 17.6855 16.8359 17.6855L8.61328 17.6855C8.17383 17.6855 7.88086 17.3926 7.88086 16.9434L7.88086 8.73047C7.88086 8.28125 8.17383 7.98828 8.61328 7.98828L16.8359 7.98828C17.2754 7.98828 17.5781 8.28125 17.5781 8.73047Z", fillAlpha = 0.85f)
        }
        return _sFSquareCircleFill!!
    }

private var _sFSquareCircleFill: ImageVector? = null
