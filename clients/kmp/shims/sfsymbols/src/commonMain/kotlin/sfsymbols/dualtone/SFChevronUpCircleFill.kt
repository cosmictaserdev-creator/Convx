package com.composables.sfsymbols.dualtone

import androidx.compose.ui.graphics.vector.ImageVector
import com.composables.sfsymbols.SfSymbols
import com.composables.sfsymbols.addSfPath
import com.composables.sfsymbols.sfIcon

/**
 * SF Symbol: SFChevronUpCircleFill (dualtone)
 * Viewport: 25.8008 x 25.459
 */
public val SfSymbols.Dualtone.SFChevronUpCircleFill: ImageVector
    get() {
        if (_sFChevronUpCircleFill != null) {
            return _sFChevronUpCircleFill!!
        }
        _sFChevronUpCircleFill = sfIcon(
            name = "Dualtone.SFChevronUpCircleFill",
            viewportWidth = 25.8008f,
            viewportHeight = 25.459f
        ) {
            addSfPath("M12.7148 25.4395C19.7266 25.4395 25.4395 19.7266 25.4395 12.7246C25.4395 5.71289 19.7266 0 12.7148 0C5.71289 0 0 5.71289 0 12.7246C0 19.7266 5.71289 25.4395 12.7148 25.4395Z", fillAlpha = 0.2125f)
            addSfPath("M6.35742 15.6934C6.04492 15.3906 6.04492 14.8633 6.39648 14.5117L11.6309 9.00391C12.3242 8.27148 13.1152 8.27148 13.8184 9.00391L19.0527 14.5117C19.3945 14.8633 19.4043 15.3906 19.0918 15.6934C18.75 16.0449 18.2031 16.0449 17.8906 15.7031L12.7246 10.2832L7.55859 15.7031C7.23633 16.0449 6.69922 16.0449 6.35742 15.6934Z", fillAlpha = 0.85f)
        }
        return _sFChevronUpCircleFill!!
    }

private var _sFChevronUpCircleFill: ImageVector? = null
