package com.composables.sfsymbols.dualtone

import androidx.compose.ui.graphics.vector.ImageVector
import com.composables.sfsymbols.SfSymbols
import com.composables.sfsymbols.addSfPath
import com.composables.sfsymbols.sfIcon

/**
 * SF Symbol: SFCapsuleOnCapsuleFill (dualtone)
 * Viewport: 35.1172 x 28.3496
 */
public val SfSymbols.Dualtone.SFCapsuleOnCapsuleFill: ImageVector
    get() {
        if (_sFCapsuleOnCapsuleFill != null) {
            return _sFCapsuleOnCapsuleFill!!
        }
        _sFCapsuleOnCapsuleFill = sfIcon(
            name = "Dualtone.SFCapsuleOnCapsuleFill",
            viewportWidth = 35.1172f,
            viewportHeight = 28.3496f
        ) {
            addSfPath("M26.0145 5.83253C25.1973 5.68168 24.3357 5.60547 23.4375 5.60547L17.3828 5.60547C10.4492 5.60547 5.70312 10.1465 5.70312 16.9727C5.70312 18.0347 5.81746 19.0414 6.04934 19.9765C3.0073 18.4107 1.19141 15.3611 1.19141 11.3574C1.19141 5.41016 5.22461 1.54297 11.3184 1.54297L17.373 1.54297C21.2669 1.54297 24.3247 3.12707 26.0145 5.83253Z", fillAlpha = 0.425f)
            addSfPath("M7.25586 16.9727C7.25586 22.9395 11.2891 26.7871 17.3828 26.7871L23.4375 26.7871C29.5215 26.7871 33.5547 22.9395 33.5547 16.9727C33.5547 11.0254 29.5215 7.1582 23.4375 7.1582L17.3828 7.1582C11.2891 7.1582 7.25586 11.0254 7.25586 16.9727Z", fillAlpha = 0.85f)
        }
        return _sFCapsuleOnCapsuleFill!!
    }

private var _sFCapsuleOnCapsuleFill: ImageVector? = null
