package com.composables.sfsymbols.dualtone

import androidx.compose.ui.graphics.vector.ImageVector
import com.composables.sfsymbols.SfSymbols
import com.composables.sfsymbols.addSfPath
import com.composables.sfsymbols.sfIcon

/**
 * SF Symbol: SFPersonSlashFill (dualtone)
 * Viewport: 29.2287 x 28.4499
 */
public val SfSymbols.Dualtone.SFPersonSlashFill: ImageVector
    get() {
        if (_sFPersonSlashFill != null) {
            return _sFPersonSlashFill!!
        }
        _sFPersonSlashFill = sfIcon(
            name = "Dualtone.SFPersonSlashFill",
            viewportWidth = 29.2287f,
            viewportHeight = 28.4499f
        ) {
            addSfPath("M23.2524 25.4656C23.1542 25.4756 23.0511 25.4799 22.9444 25.4799L5.92293 25.4799C4.64363 25.4799 3.86238 24.8646 3.86238 23.8685C3.86238 20.706 7.72761 16.3728 13.9606 16.1737ZM19.4483 8.35095C19.4483 10.1804 18.6651 11.7689 17.4678 12.7669L10.2111 5.51018C11.0997 3.96632 12.6871 2.96032 14.4483 2.96032C17.1436 2.96032 19.4483 5.29431 19.4483 8.35095Z", fillAlpha = 0.425f)
            addSfPath("M26.1964 26.1342C26.5186 26.4662 27.0557 26.4564 27.378 26.1244C27.7003 25.7924 27.7003 25.2748 27.378 24.9525L4.2823 1.85681C3.96004 1.53454 3.43269 1.52478 3.10066 1.85681C2.7784 2.16931 2.7784 2.71618 3.10066 3.02868Z", fillAlpha = 0.85f)
        }
        return _sFPersonSlashFill!!
    }

private var _sFPersonSlashFill: ImageVector? = null
