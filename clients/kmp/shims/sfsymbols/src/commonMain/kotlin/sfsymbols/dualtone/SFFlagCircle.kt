package com.composables.sfsymbols.dualtone

import androidx.compose.ui.graphics.vector.ImageVector
import com.composables.sfsymbols.SfSymbols
import com.composables.sfsymbols.addSfPath
import com.composables.sfsymbols.sfIcon

/**
 * SF Symbol: SFFlagCircle (dualtone)
 * Viewport: 25.8008 x 25.459
 */
public val SfSymbols.Dualtone.SFFlagCircle: ImageVector
    get() {
        if (_sFFlagCircle != null) {
            return _sFFlagCircle!!
        }
        _sFFlagCircle = sfIcon(
            name = "Dualtone.SFFlagCircle",
            viewportWidth = 25.8008f,
            viewportHeight = 25.459f
        ) {
            addSfPath("M12.7148 25.4395C19.7363 25.4395 25.4395 19.7461 25.4395 12.7246C25.4395 5.70312 19.7363 0 12.7148 0C5.69336 0 0 5.70312 0 12.7246C0 19.7461 5.69336 25.4395 12.7148 25.4395ZM12.7148 23.623C6.68945 23.623 1.81641 18.75 1.81641 12.7246C1.81641 6.69922 6.68945 1.82617 12.7148 1.82617C18.7402 1.82617 23.6133 6.69922 23.6133 12.7246C23.6133 18.75 18.7402 23.623 12.7148 23.623Z", fillAlpha = 0.425f)
            addSfPath("M7.88086 19.4629C8.19336 19.4629 8.39844 19.2285 8.39844 18.9453L8.39844 15.1465C8.64258 15.0488 9.16016 14.8828 9.9707 14.8828C12.2363 14.8828 13.6328 16.0059 15.8496 16.0059C16.7676 16.0059 17.1875 15.9082 17.666 15.6836C18.1738 15.4492 18.3984 15.1172 18.3984 14.6094L18.3984 8.36914C18.3984 8.01758 18.2227 7.7832 17.7539 7.7832C17.4023 7.7832 16.8359 8.05664 15.7812 8.05664C13.6133 8.05664 12.1973 6.92383 9.90234 6.92383C8.95508 6.92383 8.53516 7.04102 8.0957 7.23633C7.59766 7.45117 7.35352 7.82227 7.35352 8.31055L7.35352 18.9453C7.35352 19.2188 7.59766 19.4629 7.88086 19.4629Z", fillAlpha = 0.85f)
        }
        return _sFFlagCircle!!
    }

private var _sFFlagCircle: ImageVector? = null
