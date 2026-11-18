package com.composables.sfsymbols.monochrome

import androidx.compose.ui.graphics.vector.ImageVector
import com.composables.sfsymbols.SfSymbols
import com.composables.sfsymbols.addSfPath
import com.composables.sfsymbols.sfIcon

/**
 * SF Symbol: SFInfoCircleFill (monochrome)
 * Viewport: 25.8008 x 25.459
 */
public val SfSymbols.Monochrome.SFInfoCircleFill: ImageVector
    get() {
        if (_sFInfoCircleFill != null) {
            return _sFInfoCircleFill!!
        }
        _sFInfoCircleFill = sfIcon(
            name = "Monochrome.SFInfoCircleFill",
            viewportWidth = 25.8008f,
            viewportHeight = 25.459f
        ) {
            addSfPath("M25.4395 12.7246C25.4395 19.7266 19.7266 25.4395 12.7148 25.4395C5.71289 25.4395 0 19.7266 0 12.7246C0 5.71289 5.71289 0 12.7148 0C19.7266 0 25.4395 5.71289 25.4395 12.7246ZM10.6152 10.3418C10.1562 10.3418 9.79492 10.6934 9.79492 11.1328C9.79492 11.5918 10.1562 11.9336 10.6152 11.9336L12.1777 11.9336L12.1777 18.3105L10.4297 18.3105C9.96094 18.3105 9.60938 18.6621 9.60938 19.1016C9.60938 19.5605 9.96094 19.9023 10.4297 19.9023L15.6934 19.9023C16.1621 19.9023 16.5234 19.5605 16.5234 19.1016C16.5234 18.6621 16.1621 18.3105 15.6934 18.3105L13.9453 18.3105L13.9453 11.3379C13.9453 10.752 13.6426 10.3418 13.0762 10.3418ZM11.1035 6.54297C11.1035 7.39258 11.7773 8.07617 12.627 8.07617C13.4766 8.07617 14.1406 7.39258 14.1406 6.54297C14.1406 5.69336 13.4766 5.00977 12.627 5.00977C11.7773 5.00977 11.1035 5.69336 11.1035 6.54297Z", fillAlpha = 0.85f)
        }
        return _sFInfoCircleFill!!
    }

private var _sFInfoCircleFill: ImageVector? = null
