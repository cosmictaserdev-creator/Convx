package com.composables.sfsymbols.dualtone

import androidx.compose.ui.graphics.vector.ImageVector
import com.composables.sfsymbols.SfSymbols
import com.composables.sfsymbols.addSfPath
import com.composables.sfsymbols.sfIcon

/**
 * SF Symbol: SFXmarkRectangle (dualtone)
 * Viewport: 29.9512 x 22.959
 */
public val SfSymbols.Dualtone.SFXmarkRectangle: ImageVector
    get() {
        if (_sFXmarkRectangle != null) {
            return _sFXmarkRectangle!!
        }
        _sFXmarkRectangle = sfIcon(
            name = "Dualtone.SFXmarkRectangle",
            viewportWidth = 29.9512f,
            viewportHeight = 22.959f
        ) {
            addSfPath("M3.79883 22.959L25.7812 22.959C28.3105 22.959 29.5898 21.6797 29.5898 19.1992L29.5898 3.76953C29.5898 1.2793 28.3105 0 25.7812 0L3.79883 0C1.2793 0 0 1.26953 0 3.76953L0 19.1992C0 21.6992 1.2793 22.959 3.79883 22.959ZM3.83789 21.2305C2.4707 21.2305 1.72852 20.5078 1.72852 19.1016L1.72852 3.85742C1.72852 2.46094 2.4707 1.72852 3.83789 1.72852L25.752 1.72852C27.0898 1.72852 27.8516 2.46094 27.8516 3.85742L27.8516 19.1016C27.8516 20.5078 27.0898 21.2305 25.752 21.2305Z", fillAlpha = 0.425f)
            addSfPath("M11.0742 16.4453L19.7754 7.74414C19.9512 7.57812 20.0391 7.36328 20.0391 7.12891C20.0391 6.64062 19.6484 6.26953 19.1699 6.26953C18.9258 6.26953 18.7305 6.35742 18.5547 6.5332L9.83398 15.2246C9.6582 15.4004 9.57031 15.5957 9.57031 15.8496C9.57031 16.3281 9.96094 16.7188 10.4395 16.7188C10.7031 16.7188 10.8984 16.6211 11.0742 16.4453ZM18.5352 16.4453C18.7109 16.6211 18.9062 16.7188 19.1699 16.7188C19.6484 16.7188 20.0391 16.3281 20.0391 15.8496C20.0391 15.5957 19.9512 15.4004 19.7754 15.2246L11.0547 6.5332C10.8789 6.35742 10.6836 6.26953 10.4395 6.26953C9.96094 6.26953 9.57031 6.64062 9.57031 7.12891C9.57031 7.36328 9.6582 7.57812 9.83398 7.74414Z", fillAlpha = 0.85f)
        }
        return _sFXmarkRectangle!!
    }

private var _sFXmarkRectangle: ImageVector? = null
