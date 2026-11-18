package com.composables.sfsymbols.dualtone

import androidx.compose.ui.graphics.vector.ImageVector
import com.composables.sfsymbols.SfSymbols
import com.composables.sfsymbols.addSfPath
import com.composables.sfsymbols.sfIcon

/**
 * SF Symbol: SFTextAlignleft (dualtone)
 * Viewport: 25.4785 x 21.5332
 */
public val SfSymbols.Dualtone.SFTextAlignleft: ImageVector
    get() {
        if (_sFTextAlignleft != null) {
            return _sFTextAlignleft!!
        }
        _sFTextAlignleft = sfIcon(
            name = "Dualtone.SFTextAlignleft",
            viewportWidth = 25.4785f,
            viewportHeight = 21.5332f
        ) {
            addSfPath("M0.869141 21.5137L15.2051 21.5137C15.7031 21.5137 16.084 21.1426 16.084 20.6543C16.084 20.1562 15.7031 19.7852 15.2051 19.7852L0.869141 19.7852C0.371094 19.7852 0 20.1562 0 20.6543C0 21.1426 0.371094 21.5137 0.869141 21.5137Z", fillAlpha = 0.85f)
            addSfPath("M0.869141 14.9219L24.2383 14.9219C24.7266 14.9219 25.1172 14.541 25.1172 14.0527C25.1172 13.5645 24.7266 13.1934 24.2383 13.1934L0.869141 13.1934C0.371094 13.1934 0 13.5645 0 14.0527C0 14.541 0.371094 14.9219 0.869141 14.9219Z", fillAlpha = 0.85f)
            addSfPath("M0.869141 8.33008L15.2051 8.33008C15.7031 8.33008 16.084 7.94922 16.084 7.46094C16.084 6.97266 15.7031 6.5918 15.2051 6.5918L0.869141 6.5918C0.371094 6.5918 0 6.97266 0 7.46094C0 7.94922 0.371094 8.33008 0.869141 8.33008Z", fillAlpha = 0.85f)
            addSfPath("M0.869141 1.72852L24.2383 1.72852C24.7266 1.72852 25.1172 1.35742 25.1172 0.869141C25.1172 0.380859 24.7266 0 24.2383 0L0.869141 0C0.371094 0 0 0.380859 0 0.869141C0 1.35742 0.371094 1.72852 0.869141 1.72852Z", fillAlpha = 0.85f)
        }
        return _sFTextAlignleft!!
    }

private var _sFTextAlignleft: ImageVector? = null
