package com.composables.sfsymbols.dualtone

import androidx.compose.ui.graphics.vector.ImageVector
import com.composables.sfsymbols.SfSymbols
import com.composables.sfsymbols.addSfPath
import com.composables.sfsymbols.sfIcon

/**
 * SF Symbol: SFRectangleOnRectangleCircleFill (dualtone)
 * Viewport: 25.8008 x 25.459
 */
public val SfSymbols.Dualtone.SFRectangleOnRectangleCircleFill: ImageVector
    get() {
        if (_sFRectangleOnRectangleCircleFill != null) {
            return _sFRectangleOnRectangleCircleFill!!
        }
        _sFRectangleOnRectangleCircleFill = sfIcon(
            name = "Dualtone.SFRectangleOnRectangleCircleFill",
            viewportWidth = 25.8008f,
            viewportHeight = 25.459f
        ) {
            addSfPath("M12.7148 25.4395C19.7266 25.4395 25.4395 19.7266 25.4395 12.7246C25.4395 5.71289 19.7266 0 12.7148 0C5.71289 0 0 5.71289 0 12.7246C0 19.7266 5.71289 25.4395 12.7148 25.4395Z", fillAlpha = 0.2125f)
            addSfPath("M20.2051 10.8789L20.2051 16.9824C20.2051 18.125 19.6289 18.7012 18.4863 18.7012L9.70703 18.7012C8.56445 18.7012 7.99805 18.125 7.99805 16.9824L7.99805 10.8789C7.99805 9.76562 8.56445 9.17969 9.70703 9.17969L18.4863 9.17969C19.6289 9.17969 20.2051 9.76562 20.2051 10.8789ZM17.4023 8.24219L17.4023 8.42773L9.70703 8.42773C8.14453 8.42773 7.23633 9.3457 7.23633 10.8789L7.23633 16.0547L6.9043 16.0547C5.75195 16.0547 5.18555 15.498 5.18555 14.3555L5.18555 8.24219C5.18555 7.12891 5.75195 6.54297 6.9043 6.54297L15.6836 6.54297C16.8262 6.54297 17.4023 7.12891 17.4023 8.24219Z", fillAlpha = 0.85f)
        }
        return _sFRectangleOnRectangleCircleFill!!
    }

private var _sFRectangleOnRectangleCircleFill: ImageVector? = null
