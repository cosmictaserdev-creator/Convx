package com.composables.sfsymbols.monochrome

import androidx.compose.ui.graphics.vector.ImageVector
import com.composables.sfsymbols.SfSymbols
import com.composables.sfsymbols.addSfPath
import com.composables.sfsymbols.sfIcon

/**
 * SF Symbol: SFChevronUpRight2 (monochrome)
 * Viewport: 24.1992 x 23.9941
 */
public val SfSymbols.Monochrome.SFChevronUpRight2: ImageVector
    get() {
        if (_sFChevronUpRight2 != null) {
            return _sFChevronUpRight2!!
        }
        _sFChevronUpRight2 = sfIcon(
            name = "Monochrome.SFChevronUpRight2",
            viewportWidth = 24.1992f,
            viewportHeight = 23.9941f
        ) {
            addSfPath("M8.90625 1.97266L22.7734 1.97266L21.875 1.07422L21.875 14.9902C21.875 15.5566 22.2852 15.9766 22.8613 15.9766C23.3984 15.9766 23.8379 15.5566 23.8379 14.9902L23.8379 1.08398C23.8379 0.46875 23.3691 0 22.7539 0L8.90625 0C8.33008 0 7.91992 0.419922 7.91992 0.986328C7.91992 1.52344 8.33008 1.97266 8.90625 1.97266Z", fillAlpha = 0.85f)
            addSfPath("M0.986328 9.9707L14.8535 9.9707L13.9551 9.07227L13.9551 22.9883C13.9551 23.5645 14.3652 23.9746 14.9414 23.9746C15.4785 23.9746 15.9082 23.5645 15.9082 22.9883L15.9082 9.08203C15.9082 8.4668 15.4492 8.00781 14.834 8.00781L0.986328 8.00781C0.410156 8.00781 0 8.41797 0 8.99414C0 9.53125 0.410156 9.9707 0.986328 9.9707Z", fillAlpha = 0.85f)
        }
        return _sFChevronUpRight2!!
    }

private var _sFChevronUpRight2: ImageVector? = null
