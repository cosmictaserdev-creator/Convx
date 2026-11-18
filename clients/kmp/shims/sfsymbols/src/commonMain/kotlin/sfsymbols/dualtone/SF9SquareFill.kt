package com.composables.sfsymbols.dualtone

import androidx.compose.ui.graphics.vector.ImageVector
import com.composables.sfsymbols.SfSymbols
import com.composables.sfsymbols.addSfPath
import com.composables.sfsymbols.sfIcon

/**
 * SF Symbol: SF9SquareFill (dualtone)
 * Viewport: 23.3203 x 22.959
 */
public val SfSymbols.Dualtone.SF9SquareFill: ImageVector
    get() {
        if (_sF9SquareFill != null) {
            return _sF9SquareFill!!
        }
        _sF9SquareFill = sfIcon(
            name = "Dualtone.SF9SquareFill",
            viewportWidth = 23.3203f,
            viewportHeight = 22.959f
        ) {
            addSfPath("M3.79883 22.959L19.1504 22.959C21.6797 22.959 22.959 21.6797 22.959 19.1992L22.959 3.76953C22.959 1.2793 21.6797 0 19.1504 0L3.79883 0C1.2793 0 0 1.26953 0 3.76953L0 19.1992C0 21.6992 1.2793 22.959 3.79883 22.959Z", fillAlpha = 0.2125f)
            addSfPath("M11.1719 17.5586C9.11133 17.5586 7.86133 16.4648 7.57812 15.5859C7.5293 15.459 7.5 15.332 7.5 15.1953C7.5 14.7656 7.8125 14.4629 8.24219 14.4629C8.60352 14.4629 8.81836 14.6484 9.02344 14.9316C9.53125 15.6934 10.2246 16.1133 11.1621 16.1133C13.0371 16.1133 14.2676 14.3652 14.248 11.3281L14.1602 11.3281C13.7109 12.3828 12.6367 13.2227 10.9277 13.2227C8.89648 13.2227 7.11914 11.7871 7.11914 9.43359C7.11914 6.99219 8.82812 5.39062 11.3965 5.39062C14.1895 5.39062 15.8691 7.32422 15.8691 10.9863C15.8691 15.3516 14.1992 17.5586 11.1719 17.5586ZM11.4551 11.9238C12.9785 11.9238 14.082 10.8301 14.082 9.375C14.082 7.86133 12.9492 6.73828 11.4648 6.73828C9.91211 6.73828 8.83789 7.79297 8.83789 9.35547C8.83789 10.8398 9.91211 11.9238 11.4551 11.9238Z", fillAlpha = 0.85f)
        }
        return _sF9SquareFill!!
    }

private var _sF9SquareFill: ImageVector? = null
