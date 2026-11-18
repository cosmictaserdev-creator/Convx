package com.composables.sfsymbols.dualtone

import androidx.compose.ui.graphics.vector.ImageVector
import com.composables.sfsymbols.SfSymbols
import com.composables.sfsymbols.addSfPath
import com.composables.sfsymbols.sfIcon

/**
 * SF Symbol: SF4AltCircleFill (dualtone)
 * Viewport: 25.8008 x 25.459
 */
public val SfSymbols.Dualtone.SF4AltCircleFill: ImageVector
    get() {
        if (_sF4AltCircleFill != null) {
            return _sF4AltCircleFill!!
        }
        _sF4AltCircleFill = sfIcon(
            name = "Dualtone.SF4AltCircleFill",
            viewportWidth = 25.8008f,
            viewportHeight = 25.459f
        ) {
            addSfPath("M12.7148 25.4395C19.7266 25.4395 25.4395 19.7266 25.4395 12.7246C25.4395 5.71289 19.7266 0 12.7148 0C5.71289 0 0 5.71289 0 12.7246C0 19.7266 5.71289 25.4395 12.7148 25.4395Z", fillAlpha = 0.2125f)
            addSfPath("M7.98828 15.127C7.98828 14.8828 8.04688 14.6484 8.18359 14.4043C9.53125 12.002 10.918 9.57031 12.2559 7.24609C12.4609 6.875 12.7051 6.69922 13.0566 6.69922C13.5742 6.69922 13.8477 7.08984 13.8477 7.51953C13.8477 7.70508 13.7891 7.86133 13.7012 8.04688C12.5098 10.2539 11.2305 12.4121 10.0879 14.5508L13.9062 14.5508L13.9062 11.6211C13.9062 11.0254 14.209 10.6836 14.7168 10.6836C15.2441 10.6836 15.5566 11.0156 15.5566 11.6211L15.5566 14.5508L16.5527 14.5508C17.0508 14.5508 17.3633 14.8438 17.3633 15.2734C17.3633 15.7422 17.0508 16.0449 16.5527 16.0449L15.5566 16.0449L15.5566 17.6367C15.5566 18.252 15.2441 18.584 14.7168 18.584C14.209 18.584 13.9062 18.2422 13.9062 17.6367L13.9062 16.0449L8.98438 16.0449C8.36914 16.0449 7.98828 15.6836 7.98828 15.127Z", fillAlpha = 0.85f)
        }
        return _sF4AltCircleFill!!
    }

private var _sF4AltCircleFill: ImageVector? = null
