package com.composables.sfsymbols.dualtone

import androidx.compose.ui.graphics.vector.ImageVector
import com.composables.sfsymbols.SfSymbols
import com.composables.sfsymbols.addSfPath
import com.composables.sfsymbols.sfIcon

/**
 * SF Symbol: SFCubeCircleFill (dualtone)
 * Viewport: 25.8008 x 25.459
 */
public val SfSymbols.Dualtone.SFCubeCircleFill: ImageVector
    get() {
        if (_sFCubeCircleFill != null) {
            return _sFCubeCircleFill!!
        }
        _sFCubeCircleFill = sfIcon(
            name = "Dualtone.SFCubeCircleFill",
            viewportWidth = 25.8008f,
            viewportHeight = 25.459f
        ) {
            addSfPath("M12.7148 25.4395C19.7266 25.4395 25.4395 19.7266 25.4395 12.7246C25.4395 5.71289 19.7266 0 12.7148 0C5.71289 0 0 5.71289 0 12.7246C0 19.7266 5.71289 25.4395 12.7148 25.4395Z", fillAlpha = 0.2125f)
            addSfPath("M12.7148 12.3828L6.2793 8.75C6.37695 8.62305 6.49414 8.51562 6.65039 8.4375L11.9922 5.35156C12.4512 5.07812 12.9883 5.07812 13.4473 5.35156L18.7793 8.4375C18.9355 8.51562 19.0625 8.62305 19.1699 8.74023ZM12.207 13.252L12.207 20.3223C12.1289 20.293 12.0703 20.2539 11.9922 20.2148L6.65039 17.1289C6.19141 16.8555 5.91797 16.3965 5.91797 15.8691L5.91797 9.82422C5.91797 9.78516 5.91797 9.73633 5.9375 9.7168ZM13.2227 13.252L19.5117 9.69727C19.5117 9.73633 19.5117 9.78516 19.5117 9.81445L19.5117 15.8691C19.5117 16.3965 19.2383 16.8555 18.7793 17.1289L13.4473 20.2148C13.3691 20.2637 13.291 20.293 13.2227 20.3223Z", fillAlpha = 0.85f)
        }
        return _sFCubeCircleFill!!
    }

private var _sFCubeCircleFill: ImageVector? = null
