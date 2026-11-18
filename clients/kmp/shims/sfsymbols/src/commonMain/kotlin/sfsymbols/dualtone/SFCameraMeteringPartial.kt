package com.composables.sfsymbols.dualtone

import androidx.compose.ui.graphics.vector.ImageVector
import com.composables.sfsymbols.SfSymbols
import com.composables.sfsymbols.addSfPath
import com.composables.sfsymbols.sfIcon

/**
 * SF Symbol: SFCameraMeteringPartial (dualtone)
 * Viewport: 29.9512 x 22.959
 */
public val SfSymbols.Dualtone.SFCameraMeteringPartial: ImageVector
    get() {
        if (_sFCameraMeteringPartial != null) {
            return _sFCameraMeteringPartial!!
        }
        _sFCameraMeteringPartial = sfIcon(
            name = "Dualtone.SFCameraMeteringPartial",
            viewportWidth = 29.9512f,
            viewportHeight = 22.959f
        ) {
            addSfPath("M3.79883 22.959L14.0332 22.959L14.0332 21.2305L3.83789 21.2305C2.4707 21.2305 1.72852 20.5078 1.72852 19.1016L1.72852 12.2559L0 12.2559L0 19.1992C0 21.6992 1.2793 22.959 3.79883 22.959ZM15.5859 22.959L25.7812 22.959C28.3105 22.959 29.5898 21.6797 29.5898 19.1992L29.5898 12.2559L27.8516 12.2559L27.8516 19.1016C27.8516 20.5078 27.0898 21.2305 25.752 21.2305L15.5859 21.2305ZM27.8516 10.7031L29.5898 10.7031L29.5898 3.76953C29.5898 1.2793 28.3105 0 25.7812 0L15.5859 0L15.5859 1.72852L25.752 1.72852C27.0898 1.72852 27.8516 2.46094 27.8516 3.85742ZM0 10.7031L1.72852 10.7031L1.72852 3.85742C1.72852 2.46094 2.4707 1.72852 3.83789 1.72852L14.0332 1.72852L14.0332 0L3.79883 0C1.2793 0 0 1.26953 0 3.76953Z", fillAlpha = 0.425f)
            addSfPath("M21.6602 12.2754L20.2246 12.2754C19.834 14.9219 17.5488 16.9727 14.8145 16.9727C12.0605 16.9727 9.76562 14.9316 9.36523 12.2754L7.93945 12.2754C8.33984 15.7129 11.2793 18.3887 14.8145 18.3887C18.3203 18.3887 21.25 15.7031 21.6602 12.2754ZM20.2344 10.7129L21.6602 10.7129C21.2793 7.30469 18.3301 4.59961 14.8145 4.59961C11.2793 4.59961 8.31055 7.30469 7.93945 10.7129L9.35547 10.7129C9.73633 8.08594 12.0605 6.01562 14.8145 6.01562C17.5586 6.01562 19.8633 8.08594 20.2344 10.7129Z", fillAlpha = 0.85f)
        }
        return _sFCameraMeteringPartial!!
    }

private var _sFCameraMeteringPartial: ImageVector? = null
