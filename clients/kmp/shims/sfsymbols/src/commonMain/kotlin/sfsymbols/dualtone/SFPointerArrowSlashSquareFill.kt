package com.composables.sfsymbols.dualtone

import androidx.compose.ui.graphics.vector.ImageVector
import com.composables.sfsymbols.SfSymbols
import com.composables.sfsymbols.addSfPath
import com.composables.sfsymbols.sfIcon

/**
 * SF Symbol: SFPointerArrowSlashSquareFill (dualtone)
 * Viewport: 23.3203 x 22.959
 */
public val SfSymbols.Dualtone.SFPointerArrowSlashSquareFill: ImageVector
    get() {
        if (_sFPointerArrowSlashSquareFill != null) {
            return _sFPointerArrowSlashSquareFill!!
        }
        _sFPointerArrowSlashSquareFill = sfIcon(
            name = "Dualtone.SFPointerArrowSlashSquareFill",
            viewportWidth = 23.3203f,
            viewportHeight = 22.959f
        ) {
            addSfPath("M3.79883 22.959L19.1504 22.959C21.6797 22.959 22.959 21.6797 22.959 19.1992L22.959 3.76953C22.959 1.2793 21.6797 0 19.1504 0L3.79883 0C1.2793 0 0 1.26953 0 3.76953L0 19.1992C0 21.6992 1.2793 22.959 3.79883 22.959Z", fillAlpha = 0.2125f)
            addSfPath("M14.0349 15.9783L14.6484 17.4023C14.873 17.9297 14.6289 18.5352 14.0918 18.75C13.5547 18.9648 12.9395 18.7109 12.7246 18.2031L10.6836 13.1543L8.68164 15.166C8.37891 15.459 7.8418 15.3125 7.85156 14.8535L7.87538 9.81874ZM5.81055 5.0293L17.9297 17.1582C18.1348 17.3633 18.1348 17.6855 17.9297 17.9102C17.7246 18.1152 17.373 18.1055 17.1777 17.9102L5.04883 5.78125C4.85352 5.58594 4.84375 5.24414 5.04883 5.0293C5.26367 4.82422 5.60547 4.82422 5.81055 5.0293ZM8.68164 4.21875L15.7324 11.6016C16.0547 11.9336 15.8398 12.373 15.3809 12.3926L14.408 12.4158L7.89391 5.90172L7.90039 4.53125C7.91016 4.08203 8.37891 3.90625 8.68164 4.21875Z", fillAlpha = 0.85f)
        }
        return _sFPointerArrowSlashSquareFill!!
    }

private var _sFPointerArrowSlashSquareFill: ImageVector? = null
