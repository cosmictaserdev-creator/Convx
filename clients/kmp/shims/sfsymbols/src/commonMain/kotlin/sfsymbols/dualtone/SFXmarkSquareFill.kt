package com.composables.sfsymbols.dualtone

import androidx.compose.ui.graphics.vector.ImageVector
import com.composables.sfsymbols.SfSymbols
import com.composables.sfsymbols.addSfPath
import com.composables.sfsymbols.sfIcon

/**
 * SF Symbol: SFXmarkSquareFill (dualtone)
 * Viewport: 23.3203 x 22.959
 */
public val SfSymbols.Dualtone.SFXmarkSquareFill: ImageVector
    get() {
        if (_sFXmarkSquareFill != null) {
            return _sFXmarkSquareFill!!
        }
        _sFXmarkSquareFill = sfIcon(
            name = "Dualtone.SFXmarkSquareFill",
            viewportWidth = 23.3203f,
            viewportHeight = 22.959f
        ) {
            addSfPath("M3.79883 22.959L19.1504 22.959C21.6797 22.959 22.959 21.6797 22.959 19.1992L22.959 3.76953C22.959 1.2793 21.6797 0 19.1504 0L3.79883 0C1.2793 0 0 1.26953 0 3.76953L0 19.1992C0 21.6992 1.2793 22.959 3.79883 22.959Z", fillAlpha = 0.2125f)
            addSfPath("M7.68555 16.5723L16.5918 7.66602C16.7578 7.49023 16.8555 7.26562 16.8555 7.01172C16.8555 6.52344 16.4551 6.14258 15.957 6.14258C15.7129 6.14258 15.498 6.23047 15.332 6.40625L6.39648 15.3027C6.2207 15.4883 6.12305 15.7031 6.12305 15.9473C6.12305 16.4453 6.52344 16.8457 7.03125 16.8457C7.28516 16.8457 7.5 16.748 7.68555 16.5723ZM15.3027 16.5723C15.4785 16.748 15.6934 16.8457 15.957 16.8457C16.4551 16.8457 16.8555 16.4453 16.8555 15.9473C16.8555 15.7031 16.7578 15.4883 16.5918 15.3027L7.66602 6.40625C7.48047 6.23047 7.27539 6.14258 7.03125 6.14258C6.52344 6.14258 6.12305 6.52344 6.12305 7.01172C6.12305 7.26562 6.2207 7.49023 6.39648 7.66602Z", fillAlpha = 0.85f)
        }
        return _sFXmarkSquareFill!!
    }

private var _sFXmarkSquareFill: ImageVector? = null
