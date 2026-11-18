package com.composables.sfsymbols.dualtone

import androidx.compose.ui.graphics.vector.ImageVector
import com.composables.sfsymbols.SfSymbols
import com.composables.sfsymbols.addSfPath
import com.composables.sfsymbols.sfIcon

/**
 * SF Symbol: SFVisionProSlash (dualtone)
 * Viewport: 35.5762 x 29.3921
 */
public val SfSymbols.Dualtone.SFVisionProSlash: ImageVector
    get() {
        if (_sFVisionProSlash != null) {
            return _sFVisionProSlash!!
        }
        _sFVisionProSlash = sfIcon(
            name = "Dualtone.SFVisionProSlash",
            viewportWidth = 35.5762f,
            viewportHeight = 29.3921f
        ) {
            addSfPath("M7.11079 7.31393C3.70174 8.33526 1.72852 10.4132 1.72852 14.574C1.72852 19.408 4.24805 22.4841 8.02734 22.4841C12.3828 22.4841 14.1699 18.4705 17.6074 18.4705C17.8525 18.4705 18.0892 18.4909 18.3172 18.5335L22.7308 22.9523C20.638 21.7757 19.1131 20.199 17.6074 20.199C15.2051 20.199 12.7539 24.2127 8.02734 24.2127C3.44727 24.2127 0 20.0525 0 14.574C0 9.80681 2.23058 7.29513 5.79937 6.00097ZM35.2148 14.574C35.2148 18.9773 32.9879 22.529 29.7333 23.7484L28.3589 22.3724C31.4866 21.8033 33.4863 18.8889 33.4863 14.574C33.4863 7.11304 27.1582 6.35132 17.6074 6.35132C15.7728 6.35132 14.0571 6.37979 12.4854 6.48021L10.9109 4.90388C12.972 4.68198 15.2343 4.62281 17.6074 4.62281C27.3438 4.62281 35.2148 5.60914 35.2148 14.574Z", fillAlpha = 0.425f)
            addSfPath("M29.4824 27.7869C29.8047 28.1091 30.332 28.1091 30.6543 27.7869C30.9863 27.4548 30.9961 26.9275 30.6738 26.6052L5.72266 1.63453C5.40039 1.31226 4.87305 1.3025 4.54102 1.63453C4.21875 1.95679 4.21875 2.4939 4.54102 2.81617Z", fillAlpha = 0.85f)
        }
        return _sFVisionProSlash!!
    }

private var _sFVisionProSlash: ImageVector? = null
