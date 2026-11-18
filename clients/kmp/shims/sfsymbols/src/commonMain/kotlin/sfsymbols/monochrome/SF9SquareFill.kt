package com.composables.sfsymbols.monochrome

import androidx.compose.ui.graphics.vector.ImageVector
import com.composables.sfsymbols.SfSymbols
import com.composables.sfsymbols.addSfPath
import com.composables.sfsymbols.sfIcon

/**
 * SF Symbol: SF9SquareFill (monochrome)
 * Viewport: 23.3203 x 22.959
 */
public val SfSymbols.Monochrome.SF9SquareFill: ImageVector
    get() {
        if (_sF9SquareFill != null) {
            return _sF9SquareFill!!
        }
        _sF9SquareFill = sfIcon(
            name = "Monochrome.SF9SquareFill",
            viewportWidth = 23.3203f,
            viewportHeight = 22.959f
        ) {
            addSfPath("M22.959 3.76953L22.959 19.1992C22.959 21.6797 21.6797 22.959 19.1504 22.959L3.79883 22.959C1.2793 22.959 0 21.6992 0 19.1992L0 3.76953C0 1.26953 1.2793 0 3.79883 0L19.1504 0C21.6797 0 22.959 1.2793 22.959 3.76953ZM7.11914 9.43359C7.11914 11.7871 8.89648 13.2227 10.9277 13.2227C12.6367 13.2227 13.7109 12.3828 14.1602 11.3281L14.248 11.3281C14.2676 14.3652 13.0371 16.1133 11.1621 16.1133C10.2246 16.1133 9.53125 15.6934 9.02344 14.9316C8.81836 14.6484 8.60352 14.4629 8.24219 14.4629C7.8125 14.4629 7.5 14.7656 7.5 15.1953C7.5 15.332 7.5293 15.459 7.57812 15.5859C7.86133 16.4648 9.11133 17.5586 11.1719 17.5586C14.1992 17.5586 15.8691 15.3516 15.8691 10.9863C15.8691 7.32422 14.1895 5.39062 11.3965 5.39062C8.82812 5.39062 7.11914 6.99219 7.11914 9.43359ZM14.082 9.375C14.082 10.8301 12.9785 11.9238 11.4551 11.9238C9.91211 11.9238 8.83789 10.8398 8.83789 9.35547C8.83789 7.79297 9.91211 6.73828 11.4648 6.73828C12.9492 6.73828 14.082 7.86133 14.082 9.375Z", fillAlpha = 0.85f)
        }
        return _sF9SquareFill!!
    }

private var _sF9SquareFill: ImageVector? = null
