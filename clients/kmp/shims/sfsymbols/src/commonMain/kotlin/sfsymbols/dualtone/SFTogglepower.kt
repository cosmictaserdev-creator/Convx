package com.composables.sfsymbols.dualtone

import androidx.compose.ui.graphics.vector.ImageVector
import com.composables.sfsymbols.SfSymbols
import com.composables.sfsymbols.addSfPath
import com.composables.sfsymbols.sfIcon

/**
 * SF Symbol: SFTogglepower (dualtone)
 * Viewport: 25.8008 x 25.459
 */
public val SfSymbols.Dualtone.SFTogglepower: ImageVector
    get() {
        if (_sFTogglepower != null) {
            return _sFTogglepower!!
        }
        _sFTogglepower = sfIcon(
            name = "Dualtone.SFTogglepower",
            viewportWidth = 25.8008f,
            viewportHeight = 25.459f
        ) {
            addSfPath("M12.7148 25.4395C19.7363 25.4395 25.4395 19.7461 25.4395 12.7246C25.4395 5.70312 19.7363 0 12.7148 0C5.69336 0 0 5.70312 0 12.7246C0 19.7461 5.69336 25.4395 12.7148 25.4395ZM12.7148 23.623C6.68945 23.623 1.81641 18.75 1.81641 12.7246C1.81641 6.69922 6.68945 1.82617 12.7148 1.82617C18.7402 1.82617 23.6133 6.69922 23.6133 12.7246C23.6133 18.75 18.7402 23.623 12.7148 23.623Z", fillAlpha = 0.425f)
            addSfPath("M12.7148 21.0449C13.2324 21.0449 13.584 20.6738 13.584 20.1465L13.584 5.2832C13.584 4.73633 13.2324 4.36523 12.7148 4.36523C12.207 4.36523 11.8555 4.73633 11.8555 5.2832L11.8555 20.1465C11.8555 20.6738 12.207 21.0449 12.7148 21.0449Z", fillAlpha = 0.85f)
        }
        return _sFTogglepower!!
    }

private var _sFTogglepower: ImageVector? = null
