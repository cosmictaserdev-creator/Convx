package com.composables.sfsymbols.monochrome

import androidx.compose.ui.graphics.vector.ImageVector
import com.composables.sfsymbols.SfSymbols
import com.composables.sfsymbols.addSfPath
import com.composables.sfsymbols.sfIcon

/**
 * SF Symbol: SFLockSlashFill (monochrome)
 * Viewport: 26.1719 x 28.3252
 */
public val SfSymbols.Monochrome.SFLockSlashFill: ImageVector
    get() {
        if (_sFLockSlashFill != null) {
            return _sFLockSlashFill!!
        }
        _sFLockSlashFill = sfIcon(
            name = "Monochrome.SFLockSlashFill",
            viewportWidth = 26.1719f,
            viewportHeight = 28.3252f
        ) {
            addSfPath("M19.4383 24.9852C18.9958 25.7175 18.2129 26.1011 17.1387 26.1011L5.45898 26.1011C3.71094 26.1011 2.72461 25.0855 2.72461 23.2105L2.72461 14.3238C2.72461 12.6408 3.52755 11.6496 4.9707 11.4747L4.9707 10.5308ZM17.6367 8.48391L17.6367 11.4759C19.072 11.6541 19.8633 12.6447 19.8633 14.3238L19.8633 19.2017L12.0974 11.4429L15.9277 11.4429L15.9277 8.27883C15.9277 5.02688 13.8477 3.18117 11.2988 3.18117C9.31017 3.18117 7.62238 4.29619 6.97409 6.32421L5.68147 5.03276C6.74844 2.73447 8.91686 1.55031 11.2988 1.55031C14.668 1.55031 17.6367 3.93313 17.6367 8.48391Z", fillAlpha = 0.85f)
            addSfPath("M23.1055 26.7261C23.4277 27.0484 23.9648 27.0484 24.2773 26.7261C24.5996 26.3843 24.6094 25.8667 24.2773 25.5445L2.79297 4.06008C2.4707 3.74758 1.93359 3.72805 1.60156 4.06008C1.28906 4.38234 1.28906 4.92922 1.60156 5.24172Z", fillAlpha = 0.85f)
        }
        return _sFLockSlashFill!!
    }

private var _sFLockSlashFill: ImageVector? = null
