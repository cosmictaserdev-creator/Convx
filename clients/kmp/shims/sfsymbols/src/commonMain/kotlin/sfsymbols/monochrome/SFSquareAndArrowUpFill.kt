package com.composables.sfsymbols.monochrome

import androidx.compose.ui.graphics.vector.ImageVector
import com.composables.sfsymbols.SfSymbols
import com.composables.sfsymbols.addSfPath
import com.composables.sfsymbols.sfIcon

/**
 * SF Symbol: SFSquareAndArrowUpFill (monochrome)
 * Viewport: 22.4512 x 30.1465
 */
public val SfSymbols.Monochrome.SFSquareAndArrowUpFill: ImageVector
    get() {
        if (_sFSquareAndArrowUpFill != null) {
            return _sFSquareAndArrowUpFill!!
        }
        _sFSquareAndArrowUpFill = sfIcon(
            name = "Monochrome.SFSquareAndArrowUpFill",
            viewportWidth = 22.4512f,
            viewportHeight = 30.1465f
        ) {
            addSfPath("M6.75781 5.67383C6.30859 5.67383 5.98633 5.35156 5.98633 4.91211C5.98633 4.6875 6.07422 4.51172 6.24023 4.3457L10.4199 0.283203C10.6348 0.0683594 10.8301 0 11.0449 0C11.2598 0 11.4551 0.0683594 11.6699 0.283203L15.8496 4.3457C16.0059 4.51172 16.1035 4.6875 16.1035 4.91211C16.1035 5.35156 15.7617 5.67383 15.3125 5.67383C15.1074 5.67383 14.8828 5.58594 14.7266 5.41016L12.6074 3.18359L11.0449 1.54297L9.48242 3.18359L7.35352 5.41016C7.19727 5.58594 6.96289 5.67383 6.75781 5.67383ZM11.0449 18.1738C10.5859 18.1738 10.1855 17.793 10.1855 17.3438L10.1855 4.23828L10.3125 1.11328C10.332 0.712891 10.6445 0.380859 11.0449 0.380859C11.4453 0.380859 11.7578 0.712891 11.7773 1.11328L11.9043 4.23828L11.9043 17.3438C11.9043 17.793 11.5039 18.1738 11.0449 18.1738ZM5.20508 27.9297L16.8848 27.9297C20.2344 27.9297 22.0898 26.0645 22.0898 22.7246L22.0898 13.0859C22.0898 9.74609 20.2344 7.89062 16.8848 7.89062L5.20508 7.89062C1.85547 7.89062 0 9.74609 0 13.0859L0 22.7246C0 26.0645 1.85547 27.9297 5.20508 27.9297Z", fillAlpha = 0.85f)
        }
        return _sFSquareAndArrowUpFill!!
    }

private var _sFSquareAndArrowUpFill: ImageVector? = null
