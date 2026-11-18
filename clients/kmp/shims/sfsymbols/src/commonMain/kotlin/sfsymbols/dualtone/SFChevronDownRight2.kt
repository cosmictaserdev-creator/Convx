package com.composables.sfsymbols.dualtone

import androidx.compose.ui.graphics.vector.ImageVector
import com.composables.sfsymbols.SfSymbols
import com.composables.sfsymbols.addSfPath
import com.composables.sfsymbols.sfIcon

/**
 * SF Symbol: SFChevronDownRight2 (dualtone)
 * Viewport: 24.1992 x 23.9941
 */
public val SfSymbols.Dualtone.SFChevronDownRight2: ImageVector
    get() {
        if (_sFChevronDownRight2 != null) {
            return _sFChevronDownRight2!!
        }
        _sFChevronDownRight2 = sfIcon(
            name = "Dualtone.SFChevronDownRight2",
            viewportWidth = 24.1992f,
            viewportHeight = 23.9941f
        ) {
            addSfPath("M8.90625 22.002C8.33008 22.002 7.91992 22.4512 7.91992 22.9883C7.91992 23.5645 8.33008 23.9746 8.90625 23.9746L22.7539 23.9746C23.3691 23.9746 23.8379 23.5156 23.8379 22.9004L23.8379 8.99414C23.8379 8.41797 23.3984 8.00781 22.8613 8.00781C22.2852 8.00781 21.875 8.41797 21.875 8.99414L21.875 22.9102L22.7734 22.002Z", fillAlpha = 0.85f)
            addSfPath("M0.986328 14.0039C0.410156 14.0039 0 14.4531 0 14.9902C0 15.5566 0.410156 15.9766 0.986328 15.9766L14.834 15.9766C15.4492 15.9766 15.9082 15.5078 15.9082 14.8926L15.9082 0.986328C15.9082 0.419922 15.4785 0 14.9414 0C14.3652 0 13.9551 0.419922 13.9551 0.986328L13.9551 14.9023L14.8535 14.0039Z", fillAlpha = 0.85f)
        }
        return _sFChevronDownRight2!!
    }

private var _sFChevronDownRight2: ImageVector? = null
