package com.composables.sfsymbols.dualtone

import androidx.compose.ui.graphics.vector.ImageVector
import com.composables.sfsymbols.SfSymbols
import com.composables.sfsymbols.addSfPath
import com.composables.sfsymbols.sfIcon

/**
 * SF Symbol: SFXmarkRectangleFill (dualtone)
 * Viewport: 29.9512 x 22.959
 */
public val SfSymbols.Dualtone.SFXmarkRectangleFill: ImageVector
    get() {
        if (_sFXmarkRectangleFill != null) {
            return _sFXmarkRectangleFill!!
        }
        _sFXmarkRectangleFill = sfIcon(
            name = "Dualtone.SFXmarkRectangleFill",
            viewportWidth = 29.9512f,
            viewportHeight = 22.959f
        ) {
            addSfPath("M3.79883 22.959L25.7812 22.959C28.3105 22.959 29.5898 21.6797 29.5898 19.1992L29.5898 3.76953C29.5898 1.2793 28.3105 0 25.7812 0L3.79883 0C1.2793 0 0 1.26953 0 3.76953L0 19.1992C0 21.6992 1.2793 22.959 3.79883 22.959Z", fillAlpha = 0.2125f)
            addSfPath("M10.9961 16.582L19.9023 7.67578C20.0781 7.5 20.166 7.27539 20.166 7.02148C20.166 6.5332 19.7754 6.15234 19.2773 6.15234C19.0234 6.15234 18.8184 6.24023 18.6426 6.41602L9.70703 15.3125C9.53125 15.498 9.44336 15.7129 9.44336 15.957C9.44336 16.4551 9.84375 16.8555 10.3418 16.8555C10.6055 16.8555 10.8105 16.7578 10.9961 16.582ZM18.6133 16.582C18.7988 16.7578 19.0039 16.8555 19.2773 16.8555C19.7754 16.8555 20.166 16.4551 20.166 15.957C20.166 15.7129 20.0781 15.498 19.9023 15.3125L10.9766 6.41602C10.791 6.24023 10.5859 6.15234 10.3418 6.15234C9.84375 6.15234 9.44336 6.5332 9.44336 7.02148C9.44336 7.27539 9.53125 7.5 9.70703 7.67578Z", fillAlpha = 0.85f)
        }
        return _sFXmarkRectangleFill!!
    }

private var _sFXmarkRectangleFill: ImageVector? = null
