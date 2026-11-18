package com.composables.sfsymbols.monochrome

import androidx.compose.ui.graphics.vector.ImageVector
import com.composables.sfsymbols.SfSymbols
import com.composables.sfsymbols.addSfPath
import com.composables.sfsymbols.sfIcon

/**
 * SF Symbol: SFLJoystick (monochrome)
 * Viewport: 28.8086 x 24.1504
 */
public val SfSymbols.Monochrome.SFLJoystick: ImageVector
    get() {
        if (_sFLJoystick != null) {
            return _sFLJoystick!!
        }
        _sFLJoystick = sfIcon(
            name = "Monochrome.SFLJoystick",
            viewportWidth = 28.8086f,
            viewportHeight = 24.1504f
        ) {
            addSfPath("M14.2285 20.3516C15.9647 20.3516 17.5834 20.1863 19.0527 19.8669L19.0527 20.4297C19.0527 22.7734 17.2559 24.1504 14.2285 24.1504C11.1914 24.1504 9.39453 22.7734 9.39453 20.4297L9.39453 19.8657C10.8664 20.186 12.4884 20.3516 14.2285 20.3516Z", fillAlpha = 0.85f)
            addSfPath("M14.2285 18.9844C22.0703 18.9844 27.0898 15.5078 27.0898 10.6348C27.0898 5.77148 22.0605 2.28516 14.209 2.28516C6.38672 2.28516 1.35742 5.77148 1.35742 10.6348C1.35742 15.5078 6.38672 18.9844 14.2285 18.9844ZM14.2285 17.1484C7.69531 17.1484 3.29102 14.502 3.29102 10.6348C3.29102 6.77734 7.69531 4.13086 14.209 4.13086C20.7422 4.13086 25.166 6.77734 25.166 10.6348C25.166 14.502 20.752 17.1484 14.2285 17.1484Z", fillAlpha = 0.85f)
            addSfPath("M12.4023 14.541L16.3477 14.541C16.7676 14.541 17.0605 14.248 17.0605 13.8379C17.0605 13.418 16.7676 13.1348 16.3477 13.1348L13.2031 13.1348L13.2031 7.17773C13.2031 6.65039 12.8809 6.31836 12.4023 6.31836C11.9238 6.31836 11.6016 6.66016 11.6016 7.17773L11.6016 13.6816C11.6016 14.1992 11.9141 14.541 12.4023 14.541Z", fillAlpha = 0.85f)
        }
        return _sFLJoystick!!
    }

private var _sFLJoystick: ImageVector? = null
