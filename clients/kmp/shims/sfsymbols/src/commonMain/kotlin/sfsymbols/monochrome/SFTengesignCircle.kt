package com.composables.sfsymbols.monochrome

import androidx.compose.ui.graphics.vector.ImageVector
import com.composables.sfsymbols.SfSymbols
import com.composables.sfsymbols.addSfPath
import com.composables.sfsymbols.sfIcon

/**
 * SF Symbol: SFTengesignCircle (monochrome)
 * Viewport: 25.8008 x 25.459
 */
public val SfSymbols.Monochrome.SFTengesignCircle: ImageVector
    get() {
        if (_sFTengesignCircle != null) {
            return _sFTengesignCircle!!
        }
        _sFTengesignCircle = sfIcon(
            name = "Monochrome.SFTengesignCircle",
            viewportWidth = 25.8008f,
            viewportHeight = 25.459f
        ) {
            addSfPath("M12.7148 25.4395C19.7363 25.4395 25.4395 19.7461 25.4395 12.7246C25.4395 5.70312 19.7363 0 12.7148 0C5.69336 0 0 5.70312 0 12.7246C0 19.7461 5.69336 25.4395 12.7148 25.4395ZM12.7148 23.623C6.68945 23.623 1.81641 18.75 1.81641 12.7246C1.81641 6.69922 6.68945 1.82617 12.7148 1.82617C18.7402 1.82617 23.6133 6.69922 23.6133 12.7246C23.6133 18.75 18.7402 23.623 12.7148 23.623Z", fillAlpha = 0.85f)
            addSfPath("M12.7246 18.5352C13.0957 18.5352 13.3691 18.252 13.3691 17.8711L13.3691 10.4199L16.4551 10.4199C16.748 10.4199 16.9727 10.1953 16.9727 9.90234C16.9727 9.61914 16.748 9.39453 16.4551 9.39453L8.96484 9.39453C8.67188 9.39453 8.44727 9.61914 8.44727 9.90234C8.44727 10.1953 8.67188 10.4199 8.96484 10.4199L12.0703 10.4199L12.0703 17.8711C12.0703 18.252 12.3535 18.5352 12.7246 18.5352ZM8.41797 7.53906C8.41797 7.76367 8.58398 7.91992 8.79883 7.91992L16.582 7.91992C16.7969 7.91992 16.9727 7.74414 16.9727 7.53906C16.9727 7.33398 16.7969 7.1582 16.582 7.1582L8.79883 7.1582C8.58398 7.1582 8.41797 7.33398 8.41797 7.53906Z", fillAlpha = 0.85f)
        }
        return _sFTengesignCircle!!
    }

private var _sFTengesignCircle: ImageVector? = null
