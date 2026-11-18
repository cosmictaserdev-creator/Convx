package com.composables.sfsymbols.monochrome

import androidx.compose.ui.graphics.vector.ImageVector
import com.composables.sfsymbols.SfSymbols
import com.composables.sfsymbols.addSfPath
import com.composables.sfsymbols.sfIcon

/**
 * SF Symbol: SFExclamationmarkTriangleFill (monochrome)
 * Viewport: 26.6504 x 24.0723
 */
public val SfSymbols.Monochrome.SFExclamationmarkTriangleFill: ImageVector
    get() {
        if (_sFExclamationmarkTriangleFill != null) {
            return _sFExclamationmarkTriangleFill!!
        }
        _sFExclamationmarkTriangleFill = sfIcon(
            name = "Monochrome.SFExclamationmarkTriangleFill",
            viewportWidth = 26.6504f,
            viewportHeight = 24.0723f
        ) {
            addSfPath("M15.9277 1.62109L25.8008 18.8867C26.123 19.4434 26.2891 20.0488 26.2891 20.6348C26.2891 22.4414 25.0586 23.8672 23.0176 23.8672L3.26172 23.8672C1.23047 23.8672 0 22.4414 0 20.6348C0 20.0488 0.15625 19.4531 0.488281 18.8867L10.3613 1.62109C10.9766 0.537109 12.0508 0 13.1445 0C14.2285 0 15.3125 0.537109 15.9277 1.62109ZM11.8652 18.3496C11.8652 19.0234 12.4512 19.5801 13.1445 19.5801C13.8281 19.5801 14.4238 19.0332 14.4238 18.3496C14.4238 17.6562 13.8379 17.1094 13.1445 17.1094C12.4414 17.1094 11.8652 17.666 11.8652 18.3496ZM12.0996 7.71484L12.2559 14.5898C12.2656 15.1758 12.5781 15.5078 13.1445 15.5078C13.6816 15.5078 14.0039 15.1855 14.0137 14.5898L14.1699 7.72461C14.1797 7.13867 13.7402 6.70898 13.1348 6.70898C12.5195 6.70898 12.0898 7.12891 12.0996 7.71484Z", fillAlpha = 0.85f)
        }
        return _sFExclamationmarkTriangleFill!!
    }

private var _sFExclamationmarkTriangleFill: ImageVector? = null
