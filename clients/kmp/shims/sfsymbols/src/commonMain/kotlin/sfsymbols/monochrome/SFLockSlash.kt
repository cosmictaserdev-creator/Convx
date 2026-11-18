package com.composables.sfsymbols.monochrome

import androidx.compose.ui.graphics.vector.ImageVector
import com.composables.sfsymbols.SfSymbols
import com.composables.sfsymbols.addSfPath
import com.composables.sfsymbols.sfIcon

/**
 * SF Symbol: SFLockSlash (monochrome)
 * Viewport: 26.1719 x 28.3252
 */
public val SfSymbols.Monochrome.SFLockSlash: ImageVector
    get() {
        if (_sFLockSlash != null) {
            return _sFLockSlash!!
        }
        _sFLockSlash = sfIcon(
            name = "Monochrome.SFLockSlash",
            viewportWidth = 26.1719f,
            viewportHeight = 28.3252f
        ) {
            addSfPath("M7.52578 13.0835L5.49805 13.0835C4.87305 13.0835 4.47266 13.5034 4.47266 14.2163L4.47266 23.3277C4.47266 24.0405 4.87305 24.4702 5.49805 24.4702L17.0996 24.4702C17.6339 24.4702 17.9953 24.1658 18.0952 23.6433L19.4409 24.9878C18.9987 25.7198 18.2149 26.1011 17.1387 26.1011L5.45898 26.1011C3.71094 26.1011 2.72461 25.0952 2.72461 23.2202L2.72461 14.3335C2.72461 12.6418 3.52755 11.6576 4.9707 11.4842L4.9707 10.5308ZM17.6367 8.48391L17.6367 11.4854C19.072 11.6622 19.8633 12.6457 19.8633 14.3335L19.8633 19.2017L18.125 17.465L18.125 14.2163C18.125 13.5034 17.7344 13.0835 17.0996 13.0835L13.7395 13.0835L12.1072 11.4527L15.9277 11.4527L15.9277 8.28859C15.9277 5.03664 13.8477 3.19094 11.2988 3.19094C9.31259 3.19094 7.62649 4.30324 6.97643 6.32655L5.68341 5.0347C6.75116 2.74275 8.91836 1.56008 11.2988 1.56008C14.668 1.56008 17.6367 3.94289 17.6367 8.48391Z", fillAlpha = 0.85f)
            addSfPath("M23.1055 26.7261C23.4277 27.0484 23.9648 27.0484 24.2773 26.7261C24.5996 26.3843 24.6094 25.8667 24.2773 25.5445L2.79297 4.06008C2.4707 3.74758 1.93359 3.72805 1.60156 4.06008C1.28906 4.38234 1.28906 4.92922 1.60156 5.24172Z", fillAlpha = 0.85f)
        }
        return _sFLockSlash!!
    }

private var _sFLockSlash: ImageVector? = null
