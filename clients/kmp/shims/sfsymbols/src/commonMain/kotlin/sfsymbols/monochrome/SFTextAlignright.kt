package com.composables.sfsymbols.monochrome

import androidx.compose.ui.graphics.vector.ImageVector
import com.composables.sfsymbols.SfSymbols
import com.composables.sfsymbols.addSfPath
import com.composables.sfsymbols.sfIcon

/**
 * SF Symbol: SFTextAlignright (monochrome)
 * Viewport: 25.4785 x 21.5332
 */
public val SfSymbols.Monochrome.SFTextAlignright: ImageVector
    get() {
        if (_sFTextAlignright != null) {
            return _sFTextAlignright!!
        }
        _sFTextAlignright = sfIcon(
            name = "Monochrome.SFTextAlignright",
            viewportWidth = 25.4785f,
            viewportHeight = 21.5332f
        ) {
            addSfPath("M24.248 21.5137C24.7363 21.5137 25.1172 21.1426 25.1172 20.6543C25.1172 20.1562 24.7363 19.7852 24.248 19.7852L9.90234 19.7852C9.41406 19.7852 9.0332 20.1562 9.0332 20.6543C9.0332 21.1426 9.41406 21.5137 9.90234 21.5137Z", fillAlpha = 0.85f)
            addSfPath("M24.248 14.9219C24.7363 14.9219 25.1172 14.541 25.1172 14.0527C25.1172 13.5645 24.7363 13.1934 24.248 13.1934L0.878906 13.1934C0.380859 13.1934 0 13.5645 0 14.0527C0 14.541 0.380859 14.9219 0.878906 14.9219Z", fillAlpha = 0.85f)
            addSfPath("M24.248 8.33008C24.7363 8.33008 25.1172 7.94922 25.1172 7.46094C25.1172 6.97266 24.7363 6.5918 24.248 6.5918L9.90234 6.5918C9.41406 6.5918 9.0332 6.97266 9.0332 7.46094C9.0332 7.94922 9.41406 8.33008 9.90234 8.33008Z", fillAlpha = 0.85f)
            addSfPath("M24.248 1.72852C24.7363 1.72852 25.1172 1.35742 25.1172 0.869141C25.1172 0.380859 24.7363 0 24.248 0L0.878906 0C0.380859 0 0 0.380859 0 0.869141C0 1.35742 0.380859 1.72852 0.878906 1.72852Z", fillAlpha = 0.85f)
        }
        return _sFTextAlignright!!
    }

private var _sFTextAlignright: ImageVector? = null
