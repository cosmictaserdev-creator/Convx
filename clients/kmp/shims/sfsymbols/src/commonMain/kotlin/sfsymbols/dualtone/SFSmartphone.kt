package com.composables.sfsymbols.dualtone

import androidx.compose.ui.graphics.vector.ImageVector
import com.composables.sfsymbols.SfSymbols
import com.composables.sfsymbols.addSfPath
import com.composables.sfsymbols.sfIcon

/**
 * SF Symbol: SFSmartphone (dualtone)
 * Viewport: 16.0254 x 26.2402
 */
public val SfSymbols.Dualtone.SFSmartphone: ImageVector
    get() {
        if (_sFSmartphone != null) {
            return _sFSmartphone!!
        }
        _sFSmartphone = sfIcon(
            name = "Dualtone.SFSmartphone",
            viewportWidth = 16.0254f,
            viewportHeight = 26.2402f
        ) {
            addSfPath("M2.26562 22.8809C1.96289 22.8809 1.72852 22.6465 1.72852 22.3438L1.72852 2.27539C1.72852 1.95312 1.94336 1.72852 2.26562 1.72852L13.3887 1.72852C13.7109 1.72852 13.9258 1.95312 13.9258 2.27539L13.9258 22.3438C13.9258 22.6465 13.7012 22.8809 13.3887 22.8809Z", fillAlpha = 0.2125f)
            addSfPath("M1.52344 26.2207L14.1309 26.2207C15.0195 26.2207 15.6641 25.5762 15.6641 24.6875L15.6641 1.5332C15.6641 0.634766 15.0293 0 14.1309 0L1.52344 0C0.625 0 0 0.634766 0 1.5332L0 24.6875C0 25.5762 0.644531 26.2207 1.52344 26.2207ZM2.26562 22.8809C1.96289 22.8809 1.72852 22.6465 1.72852 22.3438L1.72852 2.27539C1.72852 1.95312 1.94336 1.72852 2.26562 1.72852L13.3887 1.72852C13.7109 1.72852 13.9258 1.95312 13.9258 2.27539L13.9258 22.3438C13.9258 22.6465 13.7012 22.8809 13.3887 22.8809Z", fillAlpha = 0.85f)
        }
        return _sFSmartphone!!
    }

private var _sFSmartphone: ImageVector? = null
