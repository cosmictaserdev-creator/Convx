package com.composables.sfsymbols.dualtone

import androidx.compose.ui.graphics.vector.ImageVector
import com.composables.sfsymbols.SfSymbols
import com.composables.sfsymbols.addSfPath
import com.composables.sfsymbols.sfIcon

/**
 * SF Symbol: SFEyeSlashCircleFill (dualtone)
 * Viewport: 25.8008 x 25.459
 */
public val SfSymbols.Dualtone.SFEyeSlashCircleFill: ImageVector
    get() {
        if (_sFEyeSlashCircleFill != null) {
            return _sFEyeSlashCircleFill!!
        }
        _sFEyeSlashCircleFill = sfIcon(
            name = "Dualtone.SFEyeSlashCircleFill",
            viewportWidth = 25.8008f,
            viewportHeight = 25.459f
        ) {
            addSfPath("M12.7148 25.459C19.7266 25.459 25.4395 19.7461 25.4395 12.7344C25.4395 5.73242 19.7266 0.0195312 12.7148 0.0195312C5.71289 0.0195312 0 5.73242 0 12.7344C0 19.7461 5.71289 25.459 12.7148 25.459Z", fillAlpha = 0.2125f)
            addSfPath("M7.04102 6.29883L19.1602 18.418C19.3652 18.623 19.3652 18.9453 19.1602 19.1699C18.9551 19.375 18.6035 19.375 18.4082 19.1699L6.2793 7.05078C6.08398 6.85547 6.07422 6.51367 6.2793 6.29883C6.50391 6.08398 6.83594 6.08398 7.04102 6.29883ZM9.4335 11.4159C9.26686 11.8241 9.17734 12.2726 9.17969 12.7441C9.17969 14.6582 10.752 16.2695 12.7148 16.2695C13.1784 16.2695 13.6219 16.1779 14.0275 16.0099L15.6546 17.637C14.7565 17.9431 13.7685 18.125 12.7148 18.125C7.74414 18.125 4.17969 14.0137 4.17969 12.7441C4.17969 12.0247 5.29145 10.4128 7.1573 9.13973ZM21.25 12.7441C21.25 13.4556 20.1427 15.0597 18.2864 16.3321L15.9946 14.0385C16.1601 13.6366 16.25 13.1989 16.25 12.7441C16.25 10.7812 14.6582 9.21875 12.7148 9.21875C12.2574 9.21875 11.8207 9.3036 11.4215 9.46162L9.80116 7.84C10.6919 7.5407 11.6709 7.36328 12.7148 7.36328C17.7051 7.36328 21.25 11.4648 21.25 12.7441Z", fillAlpha = 0.85f)
        }
        return _sFEyeSlashCircleFill!!
    }

private var _sFEyeSlashCircleFill: ImageVector? = null
