package com.composables.sfsymbols.dualtone

import androidx.compose.ui.graphics.vector.ImageVector
import com.composables.sfsymbols.SfSymbols
import com.composables.sfsymbols.addSfPath
import com.composables.sfsymbols.sfIcon

/**
 * SF Symbol: SFCone (dualtone)
 * Viewport: 25.4395 x 27.3535
 */
public val SfSymbols.Dualtone.SFCone: ImageVector
    get() {
        if (_sFCone != null) {
            return _sFCone!!
        }
        _sFCone = sfIcon(
            name = "Dualtone.SFCone",
            viewportWidth = 25.4395f,
            viewportHeight = 27.3535f
        ) {
            addSfPath("M0 22.3047C0 25.1855 5.2832 27.3535 12.5391 27.3535C19.7949 27.3535 25.0781 25.1855 25.0781 22.3047C25.0781 21.9141 24.9121 21.377 24.6484 20.8887L13.6719 1.03516C13.3594 0.478516 12.9492 0.341797 12.5391 0.341797C12.1387 0.341797 11.7188 0.478516 11.416 1.03516L0.429688 20.8887C0.166016 21.377 0 21.9141 0 22.3047ZM1.67969 22.3047C1.67969 22.1387 1.82617 21.8359 1.98242 21.5527L12.2754 2.97852C12.3438 2.87109 12.4316 2.8125 12.5391 2.8125C12.6562 2.8125 12.7441 2.87109 12.8027 2.97852L23.1055 21.5527C23.2617 21.8359 23.3984 22.1387 23.3984 22.3047C23.3984 23.9355 18.3496 25.6641 12.5391 25.6641C6.72852 25.6641 1.67969 23.9355 1.67969 22.3047Z", fillAlpha = 0.85f)
        }
        return _sFCone!!
    }

private var _sFCone: ImageVector? = null
