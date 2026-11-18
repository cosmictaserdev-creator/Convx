package com.composables.sfsymbols.dualtone

import androidx.compose.ui.graphics.vector.ImageVector
import com.composables.sfsymbols.SfSymbols
import com.composables.sfsymbols.addSfPath
import com.composables.sfsymbols.sfIcon

/**
 * SF Symbol: SFCSquareFill (dualtone)
 * Viewport: 23.3203 x 22.959
 */
public val SfSymbols.Dualtone.SFCSquareFill: ImageVector
    get() {
        if (_sFCSquareFill != null) {
            return _sFCSquareFill!!
        }
        _sFCSquareFill = sfIcon(
            name = "Dualtone.SFCSquareFill",
            viewportWidth = 23.3203f,
            viewportHeight = 22.959f
        ) {
            addSfPath("M3.79883 22.959L19.1504 22.959C21.6797 22.959 22.959 21.6797 22.959 19.1992L22.959 3.76953C22.959 1.2793 21.6797 0 19.1504 0L3.79883 0C1.2793 0 0 1.26953 0 3.76953L0 19.1992C0 21.6992 1.2793 22.959 3.79883 22.959Z", fillAlpha = 0.2125f)
            addSfPath("M11.5332 17.4902C8.25195 17.4902 6.14258 15.0879 6.14258 11.3867C6.14258 7.68555 8.26172 5.27344 11.5332 5.27344C13.7793 5.27344 15.6152 6.42578 16.1816 8.1543C16.25 8.38867 16.2695 8.51562 16.2695 8.70117C16.2695 9.18945 15.9766 9.48242 15.498 9.48242C15.1465 9.48242 14.9219 9.30664 14.7461 8.90625C14.2871 7.55859 13.1152 6.76758 11.5527 6.76758C9.4043 6.76758 7.94922 8.64258 7.94922 11.3867C7.94922 14.1406 9.4043 15.9961 11.5527 15.9961C13.1152 15.9961 14.2773 15.2148 14.7461 13.8574C14.9219 13.457 15.1465 13.2812 15.498 13.2812C15.9766 13.2812 16.2695 13.5742 16.2695 14.0625C16.2695 14.2285 16.25 14.375 16.1816 14.6094C15.625 16.3574 13.7891 17.4902 11.5332 17.4902Z", fillAlpha = 0.85f)
        }
        return _sFCSquareFill!!
    }

private var _sFCSquareFill: ImageVector? = null
