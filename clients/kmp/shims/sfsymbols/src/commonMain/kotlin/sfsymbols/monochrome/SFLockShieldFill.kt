package com.composables.sfsymbols.monochrome

import androidx.compose.ui.graphics.vector.ImageVector
import com.composables.sfsymbols.SfSymbols
import com.composables.sfsymbols.addSfPath
import com.composables.sfsymbols.sfIcon

/**
 * SF Symbol: SFLockShieldFill (monochrome)
 * Viewport: 20.7324 x 25.3418
 */
public val SfSymbols.Monochrome.SFLockShieldFill: ImageVector
    get() {
        if (_sFLockShieldFill != null) {
            return _sFLockShieldFill!!
        }
        _sFLockShieldFill = sfIcon(
            name = "Monochrome.SFLockShieldFill",
            viewportWidth = 20.7324f,
            viewportHeight = 25.3418f
        ) {
            addSfPath("M11.1914 0.634766C12.8613 1.20117 17.0703 2.76367 18.7402 3.42773C19.8535 3.88672 20.3711 4.31641 20.3711 5.63477L20.3711 15.1367C20.3711 19.502 18.1152 21.2402 11.0156 25.0879C10.7129 25.2441 10.4102 25.3418 10.1855 25.3418C9.96094 25.3418 9.64844 25.2539 9.35547 25.0879C2.37305 21.0449 0 19.502 0 15.1367L0 5.63477C0 4.31641 0.517578 3.86719 1.63086 3.42773C3.30078 2.77344 7.50977 1.20117 9.17969 0.634766C9.51172 0.537109 9.84375 0.449219 10.1855 0.449219C10.5273 0.449219 10.8594 0.527344 11.1914 0.634766ZM6.71875 9.29688L6.71875 10.8984C5.91797 10.9375 5.51758 11.3867 5.51758 12.334L5.51758 17.5293C5.51758 18.5156 5.95703 18.9746 6.86523 18.9746L13.5254 18.9746C14.4336 18.9746 14.8633 18.5156 14.8633 17.5293L14.8633 12.334C14.8633 11.3867 14.4727 10.9375 13.6719 10.8984L13.6719 9.29688C13.6719 6.97266 12.2656 5.42969 10.1953 5.42969C8.11523 5.42969 6.71875 6.97266 6.71875 9.29688ZM12.4609 9.16992L12.4609 10.8887L7.91992 10.8887L7.91992 9.16992C7.91992 7.62695 8.82812 6.5918 10.1953 6.5918C11.5527 6.5918 12.4609 7.62695 12.4609 9.16992Z", fillAlpha = 0.85f)
        }
        return _sFLockShieldFill!!
    }

private var _sFLockShieldFill: ImageVector? = null
