package com.composables.sfsymbols.dualtone

import androidx.compose.ui.graphics.vector.ImageVector
import com.composables.sfsymbols.SfSymbols
import com.composables.sfsymbols.addSfPath
import com.composables.sfsymbols.sfIcon

/**
 * SF Symbol: SFEyeSquareFill (dualtone)
 * Viewport: 23.3203 x 22.959
 */
public val SfSymbols.Dualtone.SFEyeSquareFill: ImageVector
    get() {
        if (_sFEyeSquareFill != null) {
            return _sFEyeSquareFill!!
        }
        _sFEyeSquareFill = sfIcon(
            name = "Dualtone.SFEyeSquareFill",
            viewportWidth = 23.3203f,
            viewportHeight = 22.959f
        ) {
            addSfPath("M3.79883 22.959L19.1504 22.959C21.6797 22.959 22.959 21.6797 22.959 19.1992L22.959 3.76953C22.959 1.2793 21.6797 0 19.1504 0L3.79883 0C1.2793 0 0 1.26953 0 3.76953L0 19.1992C0 21.6992 1.2793 22.959 3.79883 22.959Z", fillAlpha = 0.2125f)
            addSfPath("M11.4844 16.8555C6.51367 16.8555 2.94922 12.7441 2.94922 11.4746C2.94922 10.2051 6.46484 6.09375 11.4844 6.09375C16.4746 6.09375 20.0195 10.2051 20.0195 11.4746C20.0195 12.7441 16.4941 16.8555 11.4844 16.8555ZM11.4844 15C13.4277 15 15.0195 13.3887 15.0195 11.4746C15.0195 9.51172 13.4277 7.94922 11.4844 7.94922C9.52148 7.94922 7.93945 9.51172 7.93945 11.4746C7.94922 13.3887 9.52148 15 11.4844 15ZM11.5039 12.959C10.6836 12.959 9.99023 12.2656 9.99023 11.4746C9.99023 10.6738 10.6836 9.99023 11.5039 9.99023C12.2949 9.99023 12.9785 10.6738 12.9785 11.4746C12.9785 12.2656 12.2949 12.959 11.5039 12.959Z", fillAlpha = 0.85f)
        }
        return _sFEyeSquareFill!!
    }

private var _sFEyeSquareFill: ImageVector? = null
