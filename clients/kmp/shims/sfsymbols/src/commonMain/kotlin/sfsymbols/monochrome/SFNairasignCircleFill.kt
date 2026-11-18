package com.composables.sfsymbols.monochrome

import androidx.compose.ui.graphics.vector.ImageVector
import com.composables.sfsymbols.SfSymbols
import com.composables.sfsymbols.addSfPath
import com.composables.sfsymbols.sfIcon

/**
 * SF Symbol: SFNairasignCircleFill (monochrome)
 * Viewport: 25.8008 x 25.459
 */
public val SfSymbols.Monochrome.SFNairasignCircleFill: ImageVector
    get() {
        if (_sFNairasignCircleFill != null) {
            return _sFNairasignCircleFill!!
        }
        _sFNairasignCircleFill = sfIcon(
            name = "Monochrome.SFNairasignCircleFill",
            viewportWidth = 25.8008f,
            viewportHeight = 25.459f
        ) {
            addSfPath("M25.4395 12.7246C25.4395 19.7266 19.7266 25.4395 12.7148 25.4395C5.71289 25.4395 0 19.7266 0 12.7246C0 5.71289 5.71289 0 12.7148 0C19.7266 0 25.4395 5.71289 25.4395 12.7246ZM15.1562 7.5293L15.1562 12.3535L13.0957 12.3535L10.6641 7.53906C10.4688 7.11914 10.1562 6.86523 9.6875 6.86523C9.14062 6.86523 8.78906 7.2168 8.78906 7.73438L8.78906 12.3535L8.11523 12.3535C7.89062 12.3535 7.70508 12.5293 7.70508 12.7539C7.70508 12.9785 7.89062 13.1543 8.11523 13.1543L8.78906 13.1543L8.78906 17.9297C8.78906 18.3301 9.0625 18.6035 9.44336 18.6035C9.82422 18.6035 10.0977 18.3301 10.0977 17.9297L10.0977 13.1543L12.168 13.1543L14.5801 17.9297C14.7852 18.3398 15.0879 18.6035 15.5664 18.6035C16.1914 18.6035 16.4551 18.1641 16.4551 17.7246L16.4551 13.1543L17.1387 13.1543C17.3633 13.1543 17.5488 12.9785 17.5488 12.7539C17.5488 12.5293 17.3633 12.3535 17.1387 12.3535L16.4551 12.3535L16.4551 7.5293C16.4551 7.12891 16.2012 6.86523 15.8008 6.86523C15.4102 6.86523 15.1562 7.12891 15.1562 7.5293ZM15.1562 16.25L15.0684 16.25L13.4961 13.1543L15.1562 13.1543ZM11.7676 12.3535L10.0977 12.3535L10.0977 9.24805L10.1758 9.24805Z", fillAlpha = 0.85f)
        }
        return _sFNairasignCircleFill!!
    }

private var _sFNairasignCircleFill: ImageVector? = null
