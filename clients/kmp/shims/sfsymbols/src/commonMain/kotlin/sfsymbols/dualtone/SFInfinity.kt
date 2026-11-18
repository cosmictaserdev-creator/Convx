package com.composables.sfsymbols.dualtone

import androidx.compose.ui.graphics.vector.ImageVector
import com.composables.sfsymbols.SfSymbols
import com.composables.sfsymbols.addSfPath
import com.composables.sfsymbols.sfIcon

/**
 * SF Symbol: SFInfinity (dualtone)
 * Viewport: 32.4902 x 14.4629
 */
public val SfSymbols.Dualtone.SFInfinity: ImageVector
    get() {
        if (_sFInfinity != null) {
            return _sFInfinity!!
        }
        _sFInfinity = sfIcon(
            name = "Dualtone.SFInfinity",
            viewportWidth = 32.4902f,
            viewportHeight = 14.4629f
        ) {
            addSfPath("M0 7.22656C0 11.6016 2.79297 14.4434 6.82617 14.4434C9.07227 14.4434 10.8984 13.4668 12.959 11.4941L16.0742 8.51562L19.1699 11.4941C21.2402 13.4668 23.0664 14.4434 25.3027 14.4434C29.3457 14.4434 32.1289 11.6016 32.1289 7.22656C32.1289 2.8418 29.3457 0 25.3027 0C23.0664 0 21.2402 0.986328 19.1699 2.95898L16.0742 5.92773L12.959 2.95898C10.8984 0.986328 9.07227 0 6.82617 0C2.79297 0 0 2.8418 0 7.22656ZM1.92383 7.22656C1.92383 4.01367 3.95508 1.92383 6.82617 1.92383C8.50586 1.92383 9.92188 2.75391 11.5918 4.31641L14.7168 7.22656L11.5918 10.127C9.91211 11.6895 8.49609 12.5195 6.82617 12.5195C3.95508 12.5195 1.92383 10.4395 1.92383 7.22656ZM17.4121 7.22656L20.5371 4.31641C22.2168 2.75391 23.6328 1.92383 25.3027 1.92383C28.1836 1.92383 30.2051 4.01367 30.2051 7.22656C30.2051 10.4395 28.1836 12.5195 25.3027 12.5195C23.6328 12.5195 22.2168 11.6895 20.5469 10.127Z", fillAlpha = 0.85f)
        }
        return _sFInfinity!!
    }

private var _sFInfinity: ImageVector? = null
