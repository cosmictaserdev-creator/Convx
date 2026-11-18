package com.composables.sfsymbols.dualtone

import androidx.compose.ui.graphics.vector.ImageVector
import com.composables.sfsymbols.SfSymbols
import com.composables.sfsymbols.addSfPath
import com.composables.sfsymbols.sfIcon

/**
 * SF Symbol: SFTextAligncenter (dualtone)
 * Viewport: 25.4785 x 21.5332
 */
public val SfSymbols.Dualtone.SFTextAligncenter: ImageVector
    get() {
        if (_sFTextAligncenter != null) {
            return _sFTextAligncenter!!
        }
        _sFTextAligncenter = sfIcon(
            name = "Dualtone.SFTextAligncenter",
            viewportWidth = 25.4785f,
            viewportHeight = 21.5332f
        ) {
            addSfPath("M5.69336 21.5137L19.4141 21.5137C19.9023 21.5137 20.293 21.1426 20.293 20.6543C20.293 20.1562 19.9023 19.7852 19.4141 19.7852L5.69336 19.7852C5.20508 19.7852 4.82422 20.1562 4.82422 20.6543C4.82422 21.1426 5.20508 21.5137 5.69336 21.5137Z", fillAlpha = 0.85f)
            addSfPath("M0.869141 14.9219L24.2383 14.9219C24.7266 14.9219 25.1172 14.541 25.1172 14.0527C25.1172 13.5645 24.7266 13.1934 24.2383 13.1934L0.869141 13.1934C0.371094 13.1934 0 13.5645 0 14.0527C0 14.541 0.371094 14.9219 0.869141 14.9219Z", fillAlpha = 0.85f)
            addSfPath("M5.69336 8.33008L19.4141 8.33008C19.9023 8.33008 20.293 7.94922 20.293 7.46094C20.293 6.97266 19.9023 6.5918 19.4141 6.5918L5.69336 6.5918C5.20508 6.5918 4.82422 6.97266 4.82422 7.46094C4.82422 7.94922 5.20508 8.33008 5.69336 8.33008Z", fillAlpha = 0.85f)
            addSfPath("M0.869141 1.72852L24.2383 1.72852C24.7266 1.72852 25.1172 1.35742 25.1172 0.869141C25.1172 0.380859 24.7266 0 24.2383 0L0.869141 0C0.371094 0 0 0.380859 0 0.869141C0 1.35742 0.371094 1.72852 0.869141 1.72852Z", fillAlpha = 0.85f)
        }
        return _sFTextAligncenter!!
    }

private var _sFTextAligncenter: ImageVector? = null
