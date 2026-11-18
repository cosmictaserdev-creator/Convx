package com.composables.sfsymbols.monochrome

import androidx.compose.ui.graphics.vector.ImageVector
import com.composables.sfsymbols.SfSymbols
import com.composables.sfsymbols.addSfPath
import com.composables.sfsymbols.sfIcon

/**
 * SF Symbol: SFSpeakerSquareFill (monochrome)
 * Viewport: 23.3203 x 22.959
 */
public val SfSymbols.Monochrome.SFSpeakerSquareFill: ImageVector
    get() {
        if (_sFSpeakerSquareFill != null) {
            return _sFSpeakerSquareFill!!
        }
        _sFSpeakerSquareFill = sfIcon(
            name = "Monochrome.SFSpeakerSquareFill",
            viewportWidth = 23.3203f,
            viewportHeight = 22.959f
        ) {
            addSfPath("M22.959 3.76953L22.959 19.1992C22.959 21.6797 21.6797 22.959 19.1504 22.959L3.79883 22.959C1.2793 22.959 0 21.6992 0 19.1992L0 3.76953C0 1.26953 1.2793 0 3.79883 0L19.1504 0C21.6797 0 22.959 1.2793 22.959 3.76953ZM13.0762 5.73242L10.0195 8.64258C9.9707 8.70117 9.87305 8.7207 9.79492 8.7207L7.71484 8.7207C6.82617 8.7207 6.32812 9.22852 6.32812 10.166L6.32812 12.832C6.32812 13.7598 6.82617 14.2773 7.71484 14.2773L9.79492 14.2773C9.87305 14.2773 9.9707 14.3066 10.0195 14.3457L13.0762 17.2656C13.3594 17.5391 13.6133 17.6562 13.9258 17.6562C14.3359 17.6562 14.6289 17.3535 14.6289 16.9336L14.6289 6.05469C14.6289 5.64453 14.3359 5.3125 13.916 5.3125C13.6035 5.3125 13.3984 5.42969 13.0762 5.73242Z", fillAlpha = 0.85f)
        }
        return _sFSpeakerSquareFill!!
    }

private var _sFSpeakerSquareFill: ImageVector? = null
