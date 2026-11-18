package com.composables.sfsymbols.dualtone

import androidx.compose.ui.graphics.vector.ImageVector
import com.composables.sfsymbols.SfSymbols
import com.composables.sfsymbols.addSfPath
import com.composables.sfsymbols.sfIcon

/**
 * SF Symbol: SFPi (dualtone)
 * Viewport: 20.0293 x 18.0371
 */
public val SfSymbols.Dualtone.SFPi: ImageVector
    get() {
        if (_sFPi != null) {
            return _sFPi!!
        }
        _sFPi = sfIcon(
            name = "Dualtone.SFPi",
            viewportWidth = 20.0293f,
            viewportHeight = 18.0371f
        ) {
            addSfPath("M4.91211 18.0371C5.49805 18.0371 5.88867 17.666 5.88867 17.1094L5.88867 1.9043L13.8184 1.9043L13.8184 14.1113C13.8184 16.6406 14.8633 17.9004 17.168 17.9004C18.5156 17.9004 19.1211 17.5195 19.1211 16.8555C19.1211 16.3477 18.8477 16.0938 18.2324 16.0938C18.0273 16.0938 17.7734 16.1426 17.5098 16.1426C16.3574 16.1426 15.7812 15.459 15.7812 14.0332L15.7812 1.9043L18.8086 1.9043C19.3164 1.9043 19.668 1.55273 19.668 1.01562C19.668 0.478516 19.3164 0.146484 18.8086 0.146484L0.859375 0.146484C0.351562 0.146484 0 0.478516 0 1.01562C0 1.55273 0.351562 1.9043 0.859375 1.9043L3.92578 1.9043L3.92578 17.1094C3.92578 17.666 4.31641 18.0371 4.91211 18.0371Z", fillAlpha = 0.85f)
        }
        return _sFPi!!
    }

private var _sFPi: ImageVector? = null
