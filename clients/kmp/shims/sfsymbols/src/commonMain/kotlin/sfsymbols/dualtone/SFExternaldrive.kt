package com.composables.sfsymbols.dualtone

import androidx.compose.ui.graphics.vector.ImageVector
import com.composables.sfsymbols.SfSymbols
import com.composables.sfsymbols.addSfPath
import com.composables.sfsymbols.sfIcon

/**
 * SF Symbol: SFExternaldrive (dualtone)
 * Viewport: 29.7559 x 21.2012
 */
public val SfSymbols.Dualtone.SFExternaldrive: ImageVector
    get() {
        if (_sFExternaldrive != null) {
            return _sFExternaldrive!!
        }
        _sFExternaldrive = sfIcon(
            name = "Dualtone.SFExternaldrive",
            viewportWidth = 29.7559f,
            viewportHeight = 21.2012f
        ) {
            addSfPath("M0 15.7324C0 18.7988 2.39258 21.2012 5.70312 21.2012L23.7012 21.2012C27.002 21.2012 29.3945 18.7988 29.3945 15.7324C29.3945 14.8242 29.1211 13.9941 28.8086 13.2227L24.3945 2.91992C23.5938 1.01562 21.9922 0.0292969 19.873 0.0292969L9.52148 0.0292969C7.41211 0.0292969 5.81055 1.01562 5.00977 2.91992L0.585938 13.2227C0.273438 13.9844 0 14.8242 0 15.7324ZM1.72852 15.7324C1.72852 13.623 3.34961 12.002 5.70312 12.002L23.7012 12.002C26.0449 12.002 27.666 13.623 27.666 15.7324C27.666 18.0371 26.0449 19.4629 23.7012 19.4629L5.70312 19.4629C3.34961 19.4629 1.72852 17.8418 1.72852 15.7324ZM3.62305 10.7031L6.66992 3.37891C7.17773 2.16797 8.18359 1.55273 9.58008 1.55273L19.8242 1.55273C21.2109 1.55273 22.2266 2.16797 22.7246 3.37891L25.7617 10.6934C25.1465 10.4297 24.4434 10.2637 23.7012 10.2637L5.70312 10.2637C4.95117 10.2637 4.24805 10.4297 3.62305 10.7031ZM22.1191 15.7324C22.1191 16.6016 22.832 17.2656 23.6816 17.2656C24.502 17.2656 25.2051 16.582 25.2051 15.7324C25.2051 14.9121 24.502 14.1992 23.6816 14.1992C22.832 14.1992 22.1191 14.9121 22.1191 15.7324Z", fillAlpha = 0.85f)
        }
        return _sFExternaldrive!!
    }

private var _sFExternaldrive: ImageVector? = null
