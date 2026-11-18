package com.composables.sfsymbols.monochrome

import androidx.compose.ui.graphics.vector.ImageVector
import com.composables.sfsymbols.SfSymbols
import com.composables.sfsymbols.addSfPath
import com.composables.sfsymbols.sfIcon

/**
 * SF Symbol: SFArrowDownBackwardAndArrowUpForward (monochrome)
 * Viewport: 24.2383 x 23.8965
 */
public val SfSymbols.Monochrome.SFArrowDownBackwardAndArrowUpForward: ImageVector
    get() {
        if (_sFArrowDownBackwardAndArrowUpForward != null) {
            return _sFArrowDownBackwardAndArrowUpForward!!
        }
        _sFArrowDownBackwardAndArrowUpForward = sfIcon(
            name = "Monochrome.SFArrowDownBackwardAndArrowUpForward",
            viewportWidth = 24.2383f,
            viewportHeight = 23.8965f
        ) {
            addSfPath("M22.8906 10.0293C23.4668 10.0293 23.877 9.58984 23.877 9.0332L23.877 1.71875C23.877 0.634766 23.2422 0 22.1484 0L14.834 0C14.2578 0 13.8281 0.400391 13.8281 0.976562C13.8281 1.55273 14.2676 1.94336 14.834 1.94336L16.543 1.94336L20.8691 1.71875L17.0996 5.33203L13.0762 9.36523C12.8809 9.56055 12.7832 9.81445 12.7832 10.0879C12.7832 10.6934 13.2227 11.0938 13.8281 11.0938C14.082 11.0938 14.3262 10.9961 14.5117 10.8105L18.5059 6.76758L22.1484 3.00781L21.9238 7.31445L21.9238 9.0332C21.9238 9.60938 22.3242 10.0293 22.8906 10.0293ZM9.04297 23.8867C9.61914 23.8867 10.0488 23.4863 10.0488 22.9102C10.0488 22.334 9.60938 21.9434 9.04297 21.9434L7.32422 21.9434L3.00781 22.168L6.77734 18.5547L10.8008 14.5215C10.9961 14.3262 11.0938 14.0723 11.0938 13.7988C11.0938 13.1934 10.6543 12.793 10.0488 12.793C9.78516 12.793 9.55078 12.8906 9.36523 13.0762L5.36133 17.1191L1.72852 20.8789L1.95312 16.5723L1.95312 14.8535C1.95312 14.2773 1.55273 13.8574 0.986328 13.8574C0.410156 13.8574 0 14.2969 0 14.8535L0 22.168C0 23.252 0.634766 23.8867 1.71875 23.8867Z", fillAlpha = 0.85f)
        }
        return _sFArrowDownBackwardAndArrowUpForward!!
    }

private var _sFArrowDownBackwardAndArrowUpForward: ImageVector? = null
