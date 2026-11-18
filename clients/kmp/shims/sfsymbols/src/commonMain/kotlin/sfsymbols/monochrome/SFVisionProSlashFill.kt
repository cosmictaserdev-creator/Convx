package com.composables.sfsymbols.monochrome

import androidx.compose.ui.graphics.vector.ImageVector
import com.composables.sfsymbols.SfSymbols
import com.composables.sfsymbols.addSfPath
import com.composables.sfsymbols.sfIcon

/**
 * SF Symbol: SFVisionProSlashFill (monochrome)
 * Viewport: 35.5762 x 29.3921
 */
public val SfSymbols.Monochrome.SFVisionProSlashFill: ImageVector
    get() {
        if (_sFVisionProSlashFill != null) {
            return _sFVisionProSlashFill!!
        }
        _sFVisionProSlashFill = sfIcon(
            name = "Monochrome.SFVisionProSlashFill",
            viewportWidth = 35.5762f,
            viewportHeight = 29.3921f
        ) {
            addSfPath("M22.7308 22.9523C20.638 21.7757 19.1131 20.199 17.6074 20.199C15.2051 20.199 12.7539 24.2127 8.02734 24.2127C3.44727 24.2127 0 20.0525 0 14.574C0 9.80681 2.23058 7.29513 5.79937 6.00097ZM35.2148 14.574C35.2148 18.9773 32.9879 22.529 29.7333 23.7484L10.9109 4.90388C12.972 4.68198 15.2343 4.62281 17.6074 4.62281C27.3438 4.62281 35.2148 5.60914 35.2148 14.574Z", fillAlpha = 0.85f)
            addSfPath("M29.4824 27.7869C29.8047 28.1091 30.332 28.1091 30.6543 27.7869C30.9863 27.4548 30.9961 26.9275 30.6738 26.6052L5.72266 1.63453C5.40039 1.31226 4.87305 1.3025 4.54102 1.63453C4.21875 1.95679 4.21875 2.4939 4.54102 2.81617Z", fillAlpha = 0.85f)
        }
        return _sFVisionProSlashFill!!
    }

private var _sFVisionProSlashFill: ImageVector? = null
