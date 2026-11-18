package com.composables.sfsymbols.monochrome

import androidx.compose.ui.graphics.vector.ImageVector
import com.composables.sfsymbols.SfSymbols
import com.composables.sfsymbols.addSfPath
import com.composables.sfsymbols.sfIcon

/**
 * SF Symbol: SFNCircle (monochrome)
 * Viewport: 25.8008 x 25.459
 */
public val SfSymbols.Monochrome.SFNCircle: ImageVector
    get() {
        if (_sFNCircle != null) {
            return _sFNCircle!!
        }
        _sFNCircle = sfIcon(
            name = "Monochrome.SFNCircle",
            viewportWidth = 25.8008f,
            viewportHeight = 25.459f
        ) {
            addSfPath("M12.7148 25.4395C19.7363 25.4395 25.4395 19.7461 25.4395 12.7246C25.4395 5.70312 19.7363 0 12.7148 0C5.69336 0 0 5.70312 0 12.7246C0 19.7461 5.69336 25.4395 12.7148 25.4395ZM12.7148 23.623C6.68945 23.623 1.81641 18.75 1.81641 12.7246C1.81641 6.69922 6.68945 1.82617 12.7148 1.82617C18.7402 1.82617 23.6133 6.69922 23.6133 12.7246C23.6133 18.75 18.7402 23.623 12.7148 23.623Z", fillAlpha = 0.85f)
            addSfPath("M8.90625 18.4668C9.42383 18.4668 9.72656 18.1445 9.72656 17.6074L9.72656 9.7168L9.79492 9.7168L15.5078 17.9102C15.8105 18.3398 16.0352 18.4668 16.416 18.4668C16.9629 18.4668 17.3047 18.125 17.3047 17.5391L17.3047 7.66602C17.3047 7.11914 17.002 6.80664 16.4844 6.80664C15.9766 6.80664 15.6836 7.13867 15.6836 7.66602L15.6836 15.6543L15.6055 15.6543L9.90234 7.35352C9.60938 6.93359 9.35547 6.80664 8.97461 6.80664C8.42773 6.80664 8.10547 7.1582 8.10547 7.75391L8.10547 17.6074C8.10547 18.125 8.39844 18.4668 8.90625 18.4668Z", fillAlpha = 0.85f)
        }
        return _sFNCircle!!
    }

private var _sFNCircle: ImageVector? = null
