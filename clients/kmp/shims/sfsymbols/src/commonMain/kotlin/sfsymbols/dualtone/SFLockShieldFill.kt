package com.composables.sfsymbols.dualtone

import androidx.compose.ui.graphics.vector.ImageVector
import com.composables.sfsymbols.SfSymbols
import com.composables.sfsymbols.addSfPath
import com.composables.sfsymbols.sfIcon

/**
 * SF Symbol: SFLockShieldFill (dualtone)
 * Viewport: 20.7324 x 25.3418
 */
public val SfSymbols.Dualtone.SFLockShieldFill: ImageVector
    get() {
        if (_sFLockShieldFill != null) {
            return _sFLockShieldFill!!
        }
        _sFLockShieldFill = sfIcon(
            name = "Dualtone.SFLockShieldFill",
            viewportWidth = 20.7324f,
            viewportHeight = 25.3418f
        ) {
            addSfPath("M10.1855 25.3418C10.4102 25.3418 10.7129 25.2441 11.0156 25.0879C18.1152 21.2402 20.3711 19.502 20.3711 15.1367L20.3711 5.63477C20.3711 4.31641 19.8535 3.88672 18.7402 3.42773C17.0703 2.76367 12.8613 1.20117 11.1914 0.634766C10.8594 0.527344 10.5273 0.449219 10.1855 0.449219C9.84375 0.449219 9.51172 0.537109 9.17969 0.634766C7.50977 1.20117 3.30078 2.77344 1.63086 3.42773C0.517578 3.86719 0 4.31641 0 5.63477L0 15.1367C0 19.502 2.37305 21.0449 9.35547 25.0879C9.64844 25.2539 9.96094 25.3418 10.1855 25.3418Z", fillAlpha = 0.2125f)
            addSfPath("M5.51758 17.5293L5.51758 12.334C5.51758 11.3867 5.91797 10.9375 6.71875 10.8984L6.71875 9.29688C6.71875 6.97266 8.11523 5.42969 10.1953 5.42969C12.2656 5.42969 13.6719 6.97266 13.6719 9.29688L13.6719 10.8984C14.4727 10.9375 14.8633 11.3867 14.8633 12.334L14.8633 17.5293C14.8633 18.5156 14.4336 18.9746 13.5254 18.9746L6.86523 18.9746C5.95703 18.9746 5.51758 18.5156 5.51758 17.5293ZM7.91992 10.8887L12.4609 10.8887L12.4609 9.16992C12.4609 7.62695 11.5527 6.5918 10.1953 6.5918C8.82812 6.5918 7.91992 7.62695 7.91992 9.16992Z", fillAlpha = 0.85f)
        }
        return _sFLockShieldFill!!
    }

private var _sFLockShieldFill: ImageVector? = null
