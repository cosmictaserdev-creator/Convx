package com.composables.sfsymbols.monochrome

import androidx.compose.ui.graphics.vector.ImageVector
import com.composables.sfsymbols.SfSymbols
import com.composables.sfsymbols.addSfPath
import com.composables.sfsymbols.sfIcon

/**
 * SF Symbol: SFDegreesignFahrenheit (monochrome)
 * Viewport: 24.1699 x 22.6855
 */
public val SfSymbols.Monochrome.SFDegreesignFahrenheit: ImageVector
    get() {
        if (_sFDegreesignFahrenheit != null) {
            return _sFDegreesignFahrenheit!!
        }
        _sFDegreesignFahrenheit = sfIcon(
            name = "Monochrome.SFDegreesignFahrenheit",
            viewportWidth = 24.1699f,
            viewportHeight = 22.6855f
        ) {
            addSfPath("M11.7969 22.6465C12.4121 22.6465 12.8516 22.1875 12.8516 21.5625L12.8516 11.9141L21.9141 11.9141C22.4805 11.9141 22.8809 11.5137 22.8809 10.9668C22.8809 10.4199 22.4805 10.0293 21.9141 10.0293L12.8516 10.0293L12.8516 1.91406L22.832 1.91406C23.3984 1.91406 23.8086 1.52344 23.8086 0.966797C23.8086 0.400391 23.3984 0 22.832 0L11.7969 0C11.1426 0 10.7227 0.449219 10.7227 1.10352L10.7227 21.5625C10.7227 22.1777 11.1426 22.6465 11.7969 22.6465ZM3.68164 8.66211C5.74219 8.66211 7.36328 7.04102 7.36328 4.9707C7.36328 2.91016 5.74219 1.28906 3.68164 1.28906C1.62109 1.28906 0 2.91016 0 4.9707C0 7.04102 1.62109 8.66211 3.68164 8.66211ZM3.68164 7.12891C2.48047 7.12891 1.5332 6.18164 1.5332 4.9707C1.5332 3.76953 2.48047 2.82227 3.68164 2.82227C4.88281 2.82227 5.83008 3.76953 5.83008 4.9707C5.83008 6.18164 4.88281 7.12891 3.68164 7.12891Z", fillAlpha = 0.85f)
        }
        return _sFDegreesignFahrenheit!!
    }

private var _sFDegreesignFahrenheit: ImageVector? = null
