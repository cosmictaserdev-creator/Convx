package com.composables.sfsymbols.dualtone

import androidx.compose.ui.graphics.vector.ImageVector
import com.composables.sfsymbols.SfSymbols
import com.composables.sfsymbols.addSfPath
import com.composables.sfsymbols.sfIcon

/**
 * SF Symbol: SFMapCircleFill (dualtone)
 * Viewport: 25.8008 x 25.459
 */
public val SfSymbols.Dualtone.SFMapCircleFill: ImageVector
    get() {
        if (_sFMapCircleFill != null) {
            return _sFMapCircleFill!!
        }
        _sFMapCircleFill = sfIcon(
            name = "Dualtone.SFMapCircleFill",
            viewportWidth = 25.8008f,
            viewportHeight = 25.459f
        ) {
            addSfPath("M12.7148 25.4395C19.7266 25.4395 25.4395 19.7266 25.4395 12.7246C25.4395 5.71289 19.7266 0 12.7148 0C5.71289 0 0 5.71289 0 12.7246C0 19.7266 5.71289 25.4395 12.7148 25.4395Z", fillAlpha = 0.2125f)
            addSfPath("M5.75195 18.3594L5.75195 9.18945C5.75195 8.76953 5.91797 8.48633 6.28906 8.27148L9.54102 6.38672C9.67773 6.31836 9.81445 6.25 9.93164 6.19141L9.93164 17.3047L6.96289 18.916C6.77734 19.0137 6.61133 19.082 6.45508 19.082C6.01562 19.082 5.75195 18.8184 5.75195 18.3594ZM10.7715 17.207L10.7715 6.11328C10.918 6.13281 11.0449 6.19141 11.1719 6.25977L14.5117 8.30078L14.5117 19.3066C14.4141 19.2773 14.2969 19.2383 14.1895 19.1797ZM15.3418 19.2773L15.3418 8.20312L18.418 6.51367C18.6035 6.40625 18.7793 6.35742 18.9355 6.35742C19.3652 6.35742 19.6289 6.60156 19.6289 7.07031L19.6289 16.25C19.6289 16.6602 19.4629 16.9531 19.0918 17.168L15.5762 19.1797C15.498 19.2285 15.4199 19.2578 15.3418 19.2773Z", fillAlpha = 0.85f)
        }
        return _sFMapCircleFill!!
    }

private var _sFMapCircleFill: ImageVector? = null
