package com.composables.sfsymbols.monochrome

import androidx.compose.ui.graphics.vector.ImageVector
import com.composables.sfsymbols.SfSymbols
import com.composables.sfsymbols.addSfPath
import com.composables.sfsymbols.sfIcon

/**
 * SF Symbol: SFPaperplaneCircleFill (monochrome)
 * Viewport: 25.8008 x 25.459
 */
public val SfSymbols.Monochrome.SFPaperplaneCircleFill: ImageVector
    get() {
        if (_sFPaperplaneCircleFill != null) {
            return _sFPaperplaneCircleFill!!
        }
        _sFPaperplaneCircleFill = sfIcon(
            name = "Monochrome.SFPaperplaneCircleFill",
            viewportWidth = 25.8008f,
            viewportHeight = 25.459f
        ) {
            addSfPath("M25.4395 12.7246C25.4395 19.7266 19.7266 25.4395 12.7148 25.4395C5.71289 25.4395 0 19.7266 0 12.7246C0 5.71289 5.71289 0 12.7148 0C19.7266 0 25.4395 5.71289 25.4395 12.7246ZM17.666 5.94727L4.93164 10.8203C4.36523 11.0254 3.95508 11.3672 3.95508 11.8555C3.95508 12.4414 4.39453 12.6562 5.05859 12.8613L9.26758 14.1309C9.67773 14.2578 9.92188 14.2383 10.1953 13.9844L18.1348 6.49414C18.2324 6.41602 18.3301 6.42578 18.3984 6.48438C18.4863 6.54297 18.4668 6.65039 18.3887 6.74805L10.9277 14.7168C10.6738 14.9805 10.6348 15.2148 10.752 15.6445L12.0117 19.7656C12.2266 20.459 12.4219 20.9277 13.0273 20.9277C13.5059 20.9277 13.8477 20.5371 14.0918 19.9023L18.9355 7.2168C19.043 6.92383 19.1113 6.67969 19.1113 6.45508C19.1113 6.03516 18.8477 5.77148 18.4277 5.77148C18.2031 5.77148 17.959 5.83984 17.666 5.94727Z", fillAlpha = 0.85f)
        }
        return _sFPaperplaneCircleFill!!
    }

private var _sFPaperplaneCircleFill: ImageVector? = null
