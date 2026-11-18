package com.composables.sfsymbols.monochrome

import androidx.compose.ui.graphics.vector.ImageVector
import com.composables.sfsymbols.SfSymbols
import com.composables.sfsymbols.addSfPath
import com.composables.sfsymbols.sfIcon

/**
 * SF Symbol: SFMapCircleFill (monochrome)
 * Viewport: 25.8008 x 25.459
 */
public val SfSymbols.Monochrome.SFMapCircleFill: ImageVector
    get() {
        if (_sFMapCircleFill != null) {
            return _sFMapCircleFill!!
        }
        _sFMapCircleFill = sfIcon(
            name = "Monochrome.SFMapCircleFill",
            viewportWidth = 25.8008f,
            viewportHeight = 25.459f
        ) {
            addSfPath("M25.4395 12.7246C25.4395 19.7266 19.7266 25.4395 12.7148 25.4395C5.71289 25.4395 0 19.7266 0 12.7246C0 5.71289 5.71289 0 12.7148 0C19.7266 0 25.4395 5.71289 25.4395 12.7246ZM10.7715 17.207L14.1895 19.1797C14.2969 19.2383 14.4141 19.2773 14.5117 19.3066L14.5117 8.30078L11.1719 6.25977C11.0449 6.19141 10.918 6.13281 10.7715 6.11328ZM18.418 6.51367L15.3418 8.20312L15.3418 19.2773C15.4199 19.2578 15.498 19.2285 15.5762 19.1797L19.0918 17.168C19.4629 16.9531 19.6289 16.6602 19.6289 16.25L19.6289 7.07031C19.6289 6.60156 19.3652 6.35742 18.9355 6.35742C18.7793 6.35742 18.6035 6.40625 18.418 6.51367ZM9.54102 6.38672L6.28906 8.27148C5.91797 8.48633 5.75195 8.76953 5.75195 9.18945L5.75195 18.3594C5.75195 18.8184 6.01562 19.082 6.45508 19.082C6.61133 19.082 6.77734 19.0137 6.96289 18.916L9.93164 17.3047L9.93164 6.19141C9.81445 6.25 9.67773 6.31836 9.54102 6.38672Z", fillAlpha = 0.85f)
        }
        return _sFMapCircleFill!!
    }

private var _sFMapCircleFill: ImageVector? = null
