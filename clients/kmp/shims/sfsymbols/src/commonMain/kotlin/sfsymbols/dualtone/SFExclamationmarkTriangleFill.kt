package com.composables.sfsymbols.dualtone

import androidx.compose.ui.graphics.vector.ImageVector
import com.composables.sfsymbols.SfSymbols
import com.composables.sfsymbols.addSfPath
import com.composables.sfsymbols.sfIcon

/**
 * SF Symbol: SFExclamationmarkTriangleFill (dualtone)
 * Viewport: 26.6504 x 24.0723
 */
public val SfSymbols.Dualtone.SFExclamationmarkTriangleFill: ImageVector
    get() {
        if (_sFExclamationmarkTriangleFill != null) {
            return _sFExclamationmarkTriangleFill!!
        }
        _sFExclamationmarkTriangleFill = sfIcon(
            name = "Dualtone.SFExclamationmarkTriangleFill",
            viewportWidth = 26.6504f,
            viewportHeight = 24.0723f
        ) {
            addSfPath("M3.26172 23.8672L23.0176 23.8672C25.0586 23.8672 26.2891 22.4414 26.2891 20.6348C26.2891 20.0488 26.123 19.4434 25.8008 18.8867L15.9277 1.62109C15.3125 0.537109 14.2285 0 13.1445 0C12.0508 0 10.9766 0.537109 10.3613 1.62109L0.488281 18.8867C0.15625 19.4531 0 20.0488 0 20.6348C0 22.4414 1.23047 23.8672 3.26172 23.8672Z", fillAlpha = 0.2125f)
            addSfPath("M13.1445 15.5078C12.5781 15.5078 12.2656 15.1758 12.2559 14.5898L12.0996 7.71484C12.0898 7.12891 12.5195 6.70898 13.1348 6.70898C13.7402 6.70898 14.1797 7.13867 14.1699 7.72461L14.0137 14.5898C14.0039 15.1855 13.6816 15.5078 13.1445 15.5078ZM13.1445 19.5801C12.4512 19.5801 11.8652 19.0234 11.8652 18.3496C11.8652 17.666 12.4414 17.1094 13.1445 17.1094C13.8379 17.1094 14.4238 17.6562 14.4238 18.3496C14.4238 19.0332 13.8281 19.5801 13.1445 19.5801Z", fillAlpha = 0.85f)
        }
        return _sFExclamationmarkTriangleFill!!
    }

private var _sFExclamationmarkTriangleFill: ImageVector? = null
