package com.composables.sfsymbols.dualtone

import androidx.compose.ui.graphics.vector.ImageVector
import com.composables.sfsymbols.SfSymbols
import com.composables.sfsymbols.addSfPath
import com.composables.sfsymbols.sfIcon

/**
 * SF Symbol: SFSelectionPinInOut (dualtone)
 * Viewport: 20.8398 x 33.4082
 */
public val SfSymbols.Dualtone.SFSelectionPinInOut: ImageVector
    get() {
        if (_sFSelectionPinInOut != null) {
            return _sFSelectionPinInOut!!
        }
        _sFSelectionPinInOut = sfIcon(
            name = "Dualtone.SFSelectionPinInOut",
            viewportWidth = 20.8398f,
            viewportHeight = 33.4082f
        ) {
            addSfPath("M4.64844 25.7812L17.998 25.7812C18.3887 25.7812 18.7402 25.3418 18.7402 24.9609C18.7402 24.5801 18.3887 24.1504 17.998 24.1504L4.62891 24.1504C3.55469 24.1504 3.01758 23.5645 3.01758 22.5195L3.01758 3.1543L1.2793 3.1543L1.2793 22.4609C1.2793 24.668 2.41211 25.7812 4.64844 25.7812ZM17.5098 31.2402L19.2383 31.2402L19.2383 11.3965C19.2383 9.23828 18.1348 8.16406 15.9766 8.16406L2.51953 8.16406C2.13867 8.16406 1.77734 8.59375 1.77734 8.97461C1.77734 9.35547 2.13867 9.79492 2.51953 9.79492L15.8887 9.79492C16.9629 9.79492 17.5098 10.3711 17.5098 11.4258ZM18.3691 33.4082C19.541 33.4082 20.4785 32.4609 20.4785 31.2891C20.4785 30.127 19.541 29.1699 18.3691 29.1699C17.207 29.1699 16.25 30.127 16.25 31.2891C16.25 32.4609 17.207 33.4082 18.3691 33.4082ZM2.11914 4.77539C3.29102 4.77539 4.23828 3.82812 4.23828 2.65625C4.23828 1.49414 3.29102 0.537109 2.11914 0.537109C0.957031 0.537109 0 1.49414 0 2.65625C0 3.82812 0.957031 4.77539 2.11914 4.77539Z", fillAlpha = 0.85f)
        }
        return _sFSelectionPinInOut!!
    }

private var _sFSelectionPinInOut: ImageVector? = null
