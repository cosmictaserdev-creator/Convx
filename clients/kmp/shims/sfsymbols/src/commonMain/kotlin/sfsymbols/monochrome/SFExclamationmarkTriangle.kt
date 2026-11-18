package com.composables.sfsymbols.monochrome

import androidx.compose.ui.graphics.vector.ImageVector
import com.composables.sfsymbols.SfSymbols
import com.composables.sfsymbols.addSfPath
import com.composables.sfsymbols.sfIcon

/**
 * SF Symbol: SFExclamationmarkTriangle (monochrome)
 * Viewport: 26.6504 x 24.0723
 */
public val SfSymbols.Monochrome.SFExclamationmarkTriangle: ImageVector
    get() {
        if (_sFExclamationmarkTriangle != null) {
            return _sFExclamationmarkTriangle!!
        }
        _sFExclamationmarkTriangle = sfIcon(
            name = "Monochrome.SFExclamationmarkTriangle",
            viewportWidth = 26.6504f,
            viewportHeight = 24.0723f
        ) {
            addSfPath("M3.26172 23.8672L23.0176 23.8672C25.0586 23.8672 26.2891 22.4414 26.2891 20.6348C26.2891 20.0488 26.123 19.4434 25.8008 18.8867L15.9277 1.62109C15.3125 0.537109 14.2285 0 13.1445 0C12.0508 0 10.9766 0.537109 10.3613 1.62109L0.488281 18.8867C0.15625 19.4531 0 20.0488 0 20.6348C0 22.4414 1.23047 23.8672 3.26172 23.8672ZM3.28125 22.1777C2.31445 22.1777 1.73828 21.4453 1.73828 20.625C1.73828 20.332 1.81641 20 1.98242 19.6875L11.8359 2.44141C12.1289 1.92383 12.6465 1.71875 13.1445 1.71875C13.6426 1.71875 14.1504 1.92383 14.4336 2.44141L24.2969 19.6973C24.4629 20.0098 24.541 20.332 24.541 20.625C24.541 21.4453 23.9551 22.1777 22.9883 22.1777Z", fillAlpha = 0.85f)
            addSfPath("M13.1348 15.4395C13.6523 15.4395 13.9648 15.1367 13.9746 14.5605L14.1309 7.85156C14.1406 7.28516 13.7109 6.86523 13.125 6.86523C12.5293 6.86523 12.1191 7.27539 12.1289 7.8418L12.2754 14.5605C12.2852 15.127 12.5879 15.4395 13.1348 15.4395ZM13.1348 19.4141C13.7988 19.4141 14.375 18.8867 14.375 18.2227C14.375 17.5488 13.8086 17.0215 13.1348 17.0215C12.4609 17.0215 11.8945 17.5586 11.8945 18.2227C11.8945 18.877 12.4707 19.4141 13.1348 19.4141Z", fillAlpha = 0.85f)
        }
        return _sFExclamationmarkTriangle!!
    }

private var _sFExclamationmarkTriangle: ImageVector? = null
