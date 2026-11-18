package com.composables.sfsymbols.dualtone

import androidx.compose.ui.graphics.vector.ImageVector
import com.composables.sfsymbols.SfSymbols
import com.composables.sfsymbols.addSfPath
import com.composables.sfsymbols.sfIcon

/**
 * SF Symbol: SFPaperplaneCircleFill (dualtone)
 * Viewport: 25.8008 x 25.459
 */
public val SfSymbols.Dualtone.SFPaperplaneCircleFill: ImageVector
    get() {
        if (_sFPaperplaneCircleFill != null) {
            return _sFPaperplaneCircleFill!!
        }
        _sFPaperplaneCircleFill = sfIcon(
            name = "Dualtone.SFPaperplaneCircleFill",
            viewportWidth = 25.8008f,
            viewportHeight = 25.459f
        ) {
            addSfPath("M12.7148 25.4395C19.7266 25.4395 25.4395 19.7266 25.4395 12.7246C25.4395 5.71289 19.7266 0 12.7148 0C5.71289 0 0 5.71289 0 12.7246C0 19.7266 5.71289 25.4395 12.7148 25.4395Z", fillAlpha = 0.2125f)
            addSfPath("M13.0273 20.9277C12.4219 20.9277 12.2266 20.459 12.0117 19.7656L10.752 15.6445C10.6348 15.2148 10.6738 14.9805 10.9277 14.7168L18.3887 6.74805C18.4668 6.65039 18.4863 6.54297 18.3984 6.48438C18.3301 6.42578 18.2324 6.41602 18.1348 6.49414L10.1953 13.9844C9.92188 14.2383 9.67773 14.2578 9.26758 14.1309L5.05859 12.8613C4.39453 12.6562 3.95508 12.4414 3.95508 11.8555C3.95508 11.3672 4.36523 11.0254 4.93164 10.8203L17.666 5.94727C17.959 5.83984 18.2031 5.77148 18.4277 5.77148C18.8477 5.77148 19.1113 6.03516 19.1113 6.45508C19.1113 6.67969 19.043 6.92383 18.9355 7.2168L14.0918 19.9023C13.8477 20.5371 13.5059 20.9277 13.0273 20.9277Z", fillAlpha = 0.85f)
        }
        return _sFPaperplaneCircleFill!!
    }

private var _sFPaperplaneCircleFill: ImageVector? = null
