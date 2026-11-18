package com.composables.sfsymbols.monochrome

import androidx.compose.ui.graphics.vector.ImageVector
import com.composables.sfsymbols.SfSymbols
import com.composables.sfsymbols.addSfPath
import com.composables.sfsymbols.sfIcon

/**
 * SF Symbol: SFCubeCircleFill (monochrome)
 * Viewport: 25.8008 x 25.459
 */
public val SfSymbols.Monochrome.SFCubeCircleFill: ImageVector
    get() {
        if (_sFCubeCircleFill != null) {
            return _sFCubeCircleFill!!
        }
        _sFCubeCircleFill = sfIcon(
            name = "Monochrome.SFCubeCircleFill",
            viewportWidth = 25.8008f,
            viewportHeight = 25.459f
        ) {
            addSfPath("M25.4395 12.7246C25.4395 19.7266 19.7266 25.4395 12.7148 25.4395C5.71289 25.4395 0 19.7266 0 12.7246C0 5.71289 5.71289 0 12.7148 0C19.7266 0 25.4395 5.71289 25.4395 12.7246ZM5.91797 9.82422L5.91797 15.8691C5.91797 16.3965 6.19141 16.8555 6.65039 17.1289L11.9922 20.2148C12.0703 20.2539 12.1289 20.293 12.207 20.3223L12.207 13.252L5.9375 9.7168C5.91797 9.73633 5.91797 9.78516 5.91797 9.82422ZM13.2227 13.252L13.2227 20.3223C13.291 20.293 13.3691 20.2637 13.4473 20.2148L18.7793 17.1289C19.2383 16.8555 19.5117 16.3965 19.5117 15.8691L19.5117 9.81445C19.5117 9.78516 19.5117 9.73633 19.5117 9.69727ZM11.9922 5.35156L6.65039 8.4375C6.49414 8.51562 6.37695 8.62305 6.2793 8.75L12.7148 12.3828L19.1699 8.74023C19.0625 8.62305 18.9355 8.51562 18.7793 8.4375L13.4473 5.35156C12.9883 5.07812 12.4512 5.07812 11.9922 5.35156Z", fillAlpha = 0.85f)
        }
        return _sFCubeCircleFill!!
    }

private var _sFCubeCircleFill: ImageVector? = null
