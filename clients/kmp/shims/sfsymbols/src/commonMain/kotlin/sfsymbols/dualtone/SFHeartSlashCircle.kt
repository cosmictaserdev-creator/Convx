package com.composables.sfsymbols.dualtone

import androidx.compose.ui.graphics.vector.ImageVector
import com.composables.sfsymbols.SfSymbols
import com.composables.sfsymbols.addSfPath
import com.composables.sfsymbols.sfIcon

/**
 * SF Symbol: SFHeartSlashCircle (dualtone)
 * Viewport: 25.8008 x 25.459
 */
public val SfSymbols.Dualtone.SFHeartSlashCircle: ImageVector
    get() {
        if (_sFHeartSlashCircle != null) {
            return _sFHeartSlashCircle!!
        }
        _sFHeartSlashCircle = sfIcon(
            name = "Dualtone.SFHeartSlashCircle",
            viewportWidth = 25.8008f,
            viewportHeight = 25.459f
        ) {
            addSfPath("M25.4395 12.7246C25.4395 19.7461 19.7363 25.4395 12.7148 25.4395C5.69336 25.4395 0 19.7461 0 12.7246C0 5.70312 5.69336 0 12.7148 0C19.7363 0 25.4395 5.70312 25.4395 12.7246ZM1.81641 12.7246C1.81641 18.75 6.68945 23.623 12.7148 23.623C18.7402 23.623 23.6133 18.75 23.6133 12.7246C23.6133 6.69922 18.7402 1.82617 12.7148 1.82617C6.68945 1.82617 1.81641 6.69922 1.81641 12.7246Z", fillAlpha = 0.425f)
            addSfPath("M15.4538 17.3679C14.7078 17.997 13.954 18.5415 13.291 18.9844C13.1152 19.1113 12.8809 19.2578 12.7344 19.2578C12.5879 19.2578 12.3438 19.1113 12.1387 18.9844C9.6582 17.4219 6.03516 14.3457 6.03516 10.9473C6.03516 10.0595 6.28542 9.26745 6.72748 8.64154ZM19.4043 10.9473C19.4043 12.5849 18.5516 14.1319 17.3916 15.4757L9.10869 7.18596C9.29762 7.15325 9.49475 7.13867 9.69727 7.13867C11.0352 7.13867 12.0996 7.92969 12.7148 9.05273C13.3301 7.92969 14.4141 7.13867 15.7422 7.13867C17.8613 7.13867 19.4043 8.75 19.4043 10.9473Z", fillAlpha = 0.85f)
            addSfPath("M18.2812 18.9941C18.4766 19.1992 18.8086 19.1992 19.0137 18.9941C19.2188 18.7891 19.2188 18.4766 19.0137 18.2617L7.1875 6.43555C6.98242 6.23047 6.65039 6.23047 6.44531 6.43555C6.24023 6.64062 6.25 6.97266 6.44531 7.16797Z", fillAlpha = 0.85f)
        }
        return _sFHeartSlashCircle!!
    }

private var _sFHeartSlashCircle: ImageVector? = null
