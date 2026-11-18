package com.composables.sfsymbols.dualtone

import androidx.compose.ui.graphics.vector.ImageVector
import com.composables.sfsymbols.SfSymbols
import com.composables.sfsymbols.addSfPath
import com.composables.sfsymbols.sfIcon

/**
 * SF Symbol: SFLampFloor (dualtone)
 * Viewport: 18.9786 x 30.3027
 */
public val SfSymbols.Dualtone.SFLampFloor: ImageVector
    get() {
        if (_sFLampFloor != null) {
            return _sFLampFloor!!
        }
        _sFLampFloor = sfIcon(
            name = "Dualtone.SFLampFloor",
            viewportWidth = 18.9786f,
            viewportHeight = 30.3027f
        ) {
            addSfPath("M9.31351 16.748C12.0381 16.748 14.6846 16.5918 16.7647 16.2793C18.4346 16.0254 18.9522 14.8242 18.4248 13.125L15.1631 2.56836C14.6162 0.800781 13.2491 0 11.3545 0L7.26273 0C5.36819 0 4.01077 0.800781 3.45413 2.56836L0.202179 13.125C-0.33493 14.8242 0.192414 16.0254 1.85257 16.2793C3.93265 16.5918 6.57913 16.748 9.31351 16.748ZM9.31351 15.1465C7.01859 15.1465 4.83109 15.0293 2.41898 14.7461C1.69632 14.668 1.5596 14.209 1.78421 13.4375L4.91898 3.16406C5.24124 2.08984 6.10062 1.58203 7.26273 1.58203L11.3545 1.58203C12.5166 1.58203 13.376 2.08984 13.6983 3.16406L16.833 13.4375C17.0576 14.209 16.9209 14.668 16.1983 14.7461C13.7862 15.0293 11.5987 15.1465 9.31351 15.1465ZM8.44437 16.1523L8.44437 29.375L10.1729 29.375L10.1729 16.1523ZM3.84476 30.1855L14.7725 30.1855C15.1826 30.1855 15.4561 29.9023 15.4561 29.4727C15.4561 28.6523 14.792 28.0664 13.7373 28.0664L4.87991 28.0664C3.82523 28.0664 3.16116 28.6523 3.16116 29.4727C3.16116 29.9023 3.4346 30.1855 3.84476 30.1855Z", fillAlpha = 0.85f)
        }
        return _sFLampFloor!!
    }

private var _sFLampFloor: ImageVector? = null
