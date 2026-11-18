package com.composables.sfsymbols.monochrome

import androidx.compose.ui.graphics.vector.ImageVector
import com.composables.sfsymbols.SfSymbols
import com.composables.sfsymbols.addSfPath
import com.composables.sfsymbols.sfIcon

/**
 * SF Symbol: SFPowerplugFill (monochrome)
 * Viewport: 31.1426 x 18.7012
 */
public val SfSymbols.Monochrome.SFPowerplugFill: ImageVector
    get() {
        if (_sFPowerplugFill != null) {
            return _sFPowerplugFill!!
        }
        _sFPowerplugFill = sfIcon(
            name = "Monochrome.SFPowerplugFill",
            viewportWidth = 31.1426f,
            viewportHeight = 18.7012f
        ) {
            addSfPath("M0 7.51953L0 11.0645C0 12.4414 0.751953 13.1836 2.13867 13.1836L7.31445 13.1836C8.87695 13.1836 9.73633 13.916 10.7324 15.2539C12.334 17.4316 14.8438 18.584 17.4805 18.584L21.5234 18.584C22.8906 18.584 23.6328 17.8613 23.6328 16.4941L23.6328 15.0586L29.1699 15.0586C30.0586 15.0586 30.7812 14.3555 30.7812 13.4668C30.7812 12.5586 30.0586 11.8457 29.1699 11.8457L23.6328 11.8457L23.6328 6.73828L29.1699 6.73828C30.0586 6.73828 30.7812 6.01562 30.7812 5.13672C30.7812 4.23828 30.0586 3.51562 29.1699 3.51562L23.6328 3.51562L23.6328 2.05078C23.6328 0.693359 22.8906 0 21.5234 0L17.4805 0C14.8438 0 12.334 1.13281 10.7324 3.33984C9.73633 4.6875 8.87695 5.41016 7.31445 5.41016L2.13867 5.41016C0.751953 5.41016 0 6.12305 0 7.51953Z", fillAlpha = 0.85f)
        }
        return _sFPowerplugFill!!
    }

private var _sFPowerplugFill: ImageVector? = null
