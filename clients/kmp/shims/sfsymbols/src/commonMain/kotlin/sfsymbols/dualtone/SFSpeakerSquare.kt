package com.composables.sfsymbols.dualtone

import androidx.compose.ui.graphics.vector.ImageVector
import com.composables.sfsymbols.SfSymbols
import com.composables.sfsymbols.addSfPath
import com.composables.sfsymbols.sfIcon

/**
 * SF Symbol: SFSpeakerSquare (dualtone)
 * Viewport: 23.3203 x 22.959
 */
public val SfSymbols.Dualtone.SFSpeakerSquare: ImageVector
    get() {
        if (_sFSpeakerSquare != null) {
            return _sFSpeakerSquare!!
        }
        _sFSpeakerSquare = sfIcon(
            name = "Dualtone.SFSpeakerSquare",
            viewportWidth = 23.3203f,
            viewportHeight = 22.959f
        ) {
            addSfPath("M3.79883 22.959L19.1504 22.959C21.6797 22.959 22.959 21.6797 22.959 19.1992L22.959 3.76953C22.959 1.2793 21.6797 0 19.1504 0L3.79883 0C1.2793 0 0 1.26953 0 3.76953L0 19.1992C0 21.6992 1.2793 22.959 3.79883 22.959ZM3.83789 21.2305C2.4707 21.2305 1.72852 20.5078 1.72852 19.1016L1.72852 3.85742C1.72852 2.46094 2.4707 1.72852 3.83789 1.72852L19.1211 1.72852C20.459 1.72852 21.2305 2.46094 21.2305 3.85742L21.2305 19.1016C21.2305 20.5078 20.459 21.2305 19.1211 21.2305Z", fillAlpha = 0.425f)
            addSfPath("M13.8379 17.4902C14.248 17.4902 14.5312 17.207 14.5312 16.7969L14.5312 6.17188C14.5312 5.77148 14.248 5.43945 13.8281 5.43945C13.5352 5.43945 13.3301 5.56641 13.0176 5.85938L10.0293 8.70117C9.98047 8.75 9.89258 8.7793 9.81445 8.7793L7.77344 8.7793C6.91406 8.7793 6.42578 9.27734 6.42578 10.1855L6.42578 12.7832C6.42578 13.6914 6.91406 14.1895 7.77344 14.1895L9.81445 14.1895C9.89258 14.1895 9.98047 14.2188 10.0293 14.2676L13.0176 17.1094C13.3008 17.3828 13.5449 17.4902 13.8379 17.4902Z", fillAlpha = 0.85f)
        }
        return _sFSpeakerSquare!!
    }

private var _sFSpeakerSquare: ImageVector? = null
