package com.composables.sfsymbols.dualtone

import androidx.compose.ui.graphics.vector.ImageVector
import com.composables.sfsymbols.SfSymbols
import com.composables.sfsymbols.addSfPath
import com.composables.sfsymbols.sfIcon

/**
 * SF Symbol: SFSpeakerSquareFill (dualtone)
 * Viewport: 23.3203 x 22.959
 */
public val SfSymbols.Dualtone.SFSpeakerSquareFill: ImageVector
    get() {
        if (_sFSpeakerSquareFill != null) {
            return _sFSpeakerSquareFill!!
        }
        _sFSpeakerSquareFill = sfIcon(
            name = "Dualtone.SFSpeakerSquareFill",
            viewportWidth = 23.3203f,
            viewportHeight = 22.959f
        ) {
            addSfPath("M3.79883 22.959L19.1504 22.959C21.6797 22.959 22.959 21.6797 22.959 19.1992L22.959 3.76953C22.959 1.2793 21.6797 0 19.1504 0L3.79883 0C1.2793 0 0 1.26953 0 3.76953L0 19.1992C0 21.6992 1.2793 22.959 3.79883 22.959Z", fillAlpha = 0.2125f)
            addSfPath("M13.9258 17.6562C13.6133 17.6562 13.3594 17.5391 13.0762 17.2656L10.0195 14.3457C9.9707 14.3066 9.87305 14.2773 9.79492 14.2773L7.71484 14.2773C6.82617 14.2773 6.32812 13.7598 6.32812 12.832L6.32812 10.166C6.32812 9.22852 6.82617 8.7207 7.71484 8.7207L9.79492 8.7207C9.87305 8.7207 9.9707 8.70117 10.0195 8.64258L13.0762 5.73242C13.3984 5.42969 13.6035 5.3125 13.916 5.3125C14.3359 5.3125 14.6289 5.64453 14.6289 6.05469L14.6289 16.9336C14.6289 17.3535 14.3359 17.6562 13.9258 17.6562Z", fillAlpha = 0.85f)
        }
        return _sFSpeakerSquareFill!!
    }

private var _sFSpeakerSquareFill: ImageVector? = null
