package com.composables.sfsymbols.monochrome

import androidx.compose.ui.graphics.vector.ImageVector
import com.composables.sfsymbols.SfSymbols
import com.composables.sfsymbols.addSfPath
import com.composables.sfsymbols.sfIcon

/**
 * SF Symbol: SFEyeSquareFill (monochrome)
 * Viewport: 23.3203 x 22.959
 */
public val SfSymbols.Monochrome.SFEyeSquareFill: ImageVector
    get() {
        if (_sFEyeSquareFill != null) {
            return _sFEyeSquareFill!!
        }
        _sFEyeSquareFill = sfIcon(
            name = "Monochrome.SFEyeSquareFill",
            viewportWidth = 23.3203f,
            viewportHeight = 22.959f
        ) {
            addSfPath("M22.959 3.76953L22.959 19.1992C22.959 21.6797 21.6797 22.959 19.1504 22.959L3.79883 22.959C1.2793 22.959 0 21.6992 0 19.1992L0 3.76953C0 1.26953 1.2793 0 3.79883 0L19.1504 0C21.6797 0 22.959 1.2793 22.959 3.76953ZM2.94922 11.4746C2.94922 12.7441 6.51367 16.8555 11.4844 16.8555C16.4941 16.8555 20.0195 12.7441 20.0195 11.4746C20.0195 10.2051 16.4746 6.09375 11.4844 6.09375C6.46484 6.09375 2.94922 10.2051 2.94922 11.4746ZM15.0195 11.4746C15.0195 13.3887 13.4277 15 11.4844 15C9.52148 15 7.94922 13.3887 7.93945 11.4746C7.93945 9.51172 9.52148 7.94922 11.4844 7.94922C13.4277 7.94922 15.0195 9.51172 15.0195 11.4746ZM9.99023 11.4746C9.99023 12.2656 10.6836 12.959 11.5039 12.959C12.2949 12.959 12.9785 12.2656 12.9785 11.4746C12.9785 10.6738 12.2949 9.99023 11.5039 9.99023C10.6836 9.99023 9.99023 10.6738 9.99023 11.4746Z", fillAlpha = 0.85f)
        }
        return _sFEyeSquareFill!!
    }

private var _sFEyeSquareFill: ImageVector? = null
