package com.composables.sfsymbols.dualtone

import androidx.compose.ui.graphics.vector.ImageVector
import com.composables.sfsymbols.SfSymbols
import com.composables.sfsymbols.addSfPath
import com.composables.sfsymbols.sfIcon

/**
 * SF Symbol: SFCapsuleOnCapsule (dualtone)
 * Viewport: 32.7246 x 25.2637
 */
public val SfSymbols.Dualtone.SFCapsuleOnCapsule: ImageVector
    get() {
        if (_sFCapsuleOnCapsule != null) {
            return _sFCapsuleOnCapsule!!
        }
        _sFCapsuleOnCapsule = sfIcon(
            name = "Dualtone.SFCapsuleOnCapsule",
            viewportWidth = 32.7246f,
            viewportHeight = 25.2637f
        ) {
            addSfPath("M25.6809 6.09038C25.0317 5.89207 24.3376 5.7579 23.6036 5.69141C22.2727 3.19966 19.6323 1.72852 16.1816 1.72852L10.127 1.72852C5.03906 1.72852 1.72852 4.90234 1.72852 9.81445C1.72852 13.3157 3.39712 15.921 6.19166 17.1304C6.28115 17.8516 6.44554 18.5281 6.67924 19.1559C2.55634 17.972 0 14.5648 0 9.81445C0 3.86719 4.0332 0 10.127 0L16.1816 0C20.8751 0 24.348 2.30143 25.6809 6.09038Z", fillAlpha = 0.425f)
            addSfPath("M6.06445 15.4297C6.06445 21.3965 10.0879 25.2441 16.1816 25.2441L22.2363 25.2441C28.3203 25.2441 32.3633 21.3965 32.3633 15.4297C32.3633 9.48242 28.3203 5.61523 22.2363 5.61523L16.1816 5.61523C10.0879 5.61523 6.06445 9.48242 6.06445 15.4297ZM7.79297 15.4297C7.79297 10.5176 11.1035 7.34375 16.1816 7.34375L22.2363 7.34375C27.3145 7.34375 30.6348 10.5176 30.6348 15.4297C30.6348 20.3613 27.3145 23.5156 22.2363 23.5156L16.1816 23.5156C11.1035 23.5156 7.79297 20.3613 7.79297 15.4297Z", fillAlpha = 0.85f)
        }
        return _sFCapsuleOnCapsule!!
    }

private var _sFCapsuleOnCapsule: ImageVector? = null
