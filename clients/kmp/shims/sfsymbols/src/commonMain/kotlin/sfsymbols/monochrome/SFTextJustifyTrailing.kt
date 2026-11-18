package com.composables.sfsymbols.monochrome

import androidx.compose.ui.graphics.vector.ImageVector
import com.composables.sfsymbols.SfSymbols
import com.composables.sfsymbols.addSfPath
import com.composables.sfsymbols.sfIcon

/**
 * SF Symbol: SFTextJustifyTrailing (monochrome)
 * Viewport: 25.4785 x 21.5332
 */
public val SfSymbols.Monochrome.SFTextJustifyTrailing: ImageVector
    get() {
        if (_sFTextJustifyTrailing != null) {
            return _sFTextJustifyTrailing!!
        }
        _sFTextJustifyTrailing = sfIcon(
            name = "Monochrome.SFTextJustifyTrailing",
            viewportWidth = 25.4785f,
            viewportHeight = 21.5332f
        ) {
            addSfPath("M11.1328 21.5137L24.2383 21.5137C24.7266 21.5137 25.1172 21.1426 25.1172 20.6543C25.1172 20.1562 24.7266 19.7852 24.2383 19.7852L11.1328 19.7852C10.6445 19.7852 10.2734 20.1562 10.2734 20.6543C10.2734 21.1426 10.6445 21.5137 11.1328 21.5137Z", fillAlpha = 0.85f)
            addSfPath("M0.869141 14.9219L24.2383 14.9219C24.7266 14.9219 25.1172 14.541 25.1172 14.0527C25.1172 13.5645 24.7266 13.1934 24.2383 13.1934L0.869141 13.1934C0.371094 13.1934 0 13.5645 0 14.0527C0 14.541 0.371094 14.9219 0.869141 14.9219Z", fillAlpha = 0.85f)
            addSfPath("M0.869141 8.33008L24.2383 8.33008C24.7266 8.33008 25.1172 7.94922 25.1172 7.46094C25.1172 6.97266 24.7266 6.5918 24.2383 6.5918L0.869141 6.5918C0.371094 6.5918 0 6.97266 0 7.46094C0 7.94922 0.371094 8.33008 0.869141 8.33008Z", fillAlpha = 0.85f)
            addSfPath("M0.869141 1.72852L24.2383 1.72852C24.7266 1.72852 25.1172 1.35742 25.1172 0.869141C25.1172 0.380859 24.7266 0 24.2383 0L0.869141 0C0.371094 0 0 0.380859 0 0.869141C0 1.35742 0.371094 1.72852 0.869141 1.72852Z", fillAlpha = 0.85f)
        }
        return _sFTextJustifyTrailing!!
    }

private var _sFTextJustifyTrailing: ImageVector? = null
