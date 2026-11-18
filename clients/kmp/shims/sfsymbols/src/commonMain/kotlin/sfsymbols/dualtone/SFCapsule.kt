package com.composables.sfsymbols.dualtone

import androidx.compose.ui.graphics.vector.ImageVector
import com.composables.sfsymbols.SfSymbols
import com.composables.sfsymbols.addSfPath
import com.composables.sfsymbols.sfIcon

/**
 * SF Symbol: SFCapsule (dualtone)
 * Viewport: 32.3828 x 23.8184
 */
public val SfSymbols.Dualtone.SFCapsule: ImageVector
    get() {
        if (_sFCapsule != null) {
            return _sFCapsule!!
        }
        _sFCapsule = sfIcon(
            name = "Dualtone.SFCapsule",
            viewportWidth = 32.3828f,
            viewportHeight = 23.8184f
        ) {
            addSfPath("M0 11.9043C0 19.1211 4.89258 23.8086 12.3047 23.8086L19.7168 23.8086C27.1289 23.8086 32.0215 19.1211 32.0215 11.9043C32.0215 4.67773 27.1289 0 19.7168 0L12.3047 0C4.89258 0 0 4.67773 0 11.9043ZM1.72852 11.9043C1.72852 5.75195 5.95703 1.72852 12.3047 1.72852L19.7168 1.72852C26.0645 1.72852 30.293 5.75195 30.293 11.9043C30.293 18.0469 26.0645 22.0703 19.7168 22.0703L12.3047 22.0703C5.95703 22.0703 1.72852 18.0469 1.72852 11.9043Z", fillAlpha = 0.85f)
        }
        return _sFCapsule!!
    }

private var _sFCapsule: ImageVector? = null
