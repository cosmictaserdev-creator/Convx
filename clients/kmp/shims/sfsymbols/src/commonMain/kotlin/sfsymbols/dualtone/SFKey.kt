package com.composables.sfsymbols.dualtone

import androidx.compose.ui.graphics.vector.ImageVector
import com.composables.sfsymbols.SfSymbols
import com.composables.sfsymbols.addSfPath
import com.composables.sfsymbols.sfIcon

/**
 * SF Symbol: SFKey (dualtone)
 * Viewport: 16.8359 x 31.1816
 */
public val SfSymbols.Dualtone.SFKey: ImageVector
    get() {
        if (_sFKey != null) {
            return _sFKey!!
        }
        _sFKey = sfIcon(
            name = "Dualtone.SFKey",
            viewportWidth = 16.8359f,
            viewportHeight = 31.1816f
        ) {
            addSfPath("M7.5 30.7227C7.93945 31.1035 8.54492 31.1523 8.97461 30.7324L12.8613 26.8457C13.291 26.416 13.2715 25.7715 12.8516 25.3613L10.7715 23.291L13.75 20.3125C14.1602 19.9121 14.1504 19.2578 13.7305 18.8281L10.9082 15.9863C14.4629 14.375 16.4746 11.5625 16.4746 8.21289C16.4746 3.66211 12.793 0 8.23242 0C3.66211 0 0 3.65234 0 8.21289C0 11.5527 1.94336 14.5215 5 15.8105L5 27.7832C5 28.1543 5.11719 28.5742 5.44922 28.8672ZM8.23242 29.0137L6.73828 27.5195L6.73828 14.5605C3.76953 13.8477 1.71875 11.2695 1.71875 8.21289C1.71875 4.62891 4.62891 1.73828 8.23242 1.73828C11.8359 1.73828 14.7363 4.61914 14.7363 8.21289C14.7363 11.2402 12.6758 13.8477 9.32617 14.6973L9.32617 16.8945L12.0215 19.5898L9.18945 22.3828L9.18945 24.1602L11.1328 26.084ZM8.23242 7.75391C9.3457 7.75391 10.2441 6.85547 10.2441 5.75195C10.2441 4.63867 9.3457 3.75 8.23242 3.75C7.11914 3.75 6.24023 4.63867 6.24023 5.75195C6.24023 6.85547 7.12891 7.75391 8.23242 7.75391Z", fillAlpha = 0.85f)
        }
        return _sFKey!!
    }

private var _sFKey: ImageVector? = null
