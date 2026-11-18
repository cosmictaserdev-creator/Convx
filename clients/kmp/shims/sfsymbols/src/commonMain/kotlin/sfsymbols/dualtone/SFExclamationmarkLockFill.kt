package com.composables.sfsymbols.dualtone

import androidx.compose.ui.graphics.vector.ImageVector
import com.composables.sfsymbols.SfSymbols
import com.composables.sfsymbols.addSfPath
import com.composables.sfsymbols.sfIcon

/**
 * SF Symbol: SFExclamationmarkLockFill (dualtone)
 * Viewport: 17.5 x 25.2246
 */
public val SfSymbols.Dualtone.SFExclamationmarkLockFill: ImageVector
    get() {
        if (_sFExclamationmarkLockFill != null) {
            return _sFExclamationmarkLockFill!!
        }
        _sFExclamationmarkLockFill = sfIcon(
            name = "Dualtone.SFExclamationmarkLockFill",
            viewportWidth = 17.5f,
            viewportHeight = 25.2246f
        ) {
            addSfPath("M2.73438 24.5508L14.4043 24.5508C16.1523 24.5508 17.1387 23.5352 17.1387 21.6602L17.1387 12.7734C17.1387 10.9082 16.1523 9.89258 14.4043 9.89258L2.73438 9.89258C0.976562 9.89258 0 10.9082 0 12.7734L0 21.6602C0 23.5352 0.976562 24.5508 2.73438 24.5508ZM2.23633 10.7227L3.95508 10.7227L3.95508 6.72852C3.95508 3.47656 6.01562 1.63086 8.56445 1.63086C11.1133 1.63086 13.1934 3.47656 13.1934 6.72852L13.1934 10.7227L14.9121 10.7227L14.9121 6.93359C14.9121 2.38281 11.9434 0 8.56445 0C5.19531 0 2.23633 2.38281 2.23633 6.93359Z", fillAlpha = 0.2125f)
            addSfPath("M8.56445 18.6523C8.13477 18.6523 7.89062 18.3887 7.88086 17.9297L7.76367 13.3789C7.75391 12.9297 8.0957 12.5977 8.56445 12.5977C9.0332 12.5977 9.38477 12.9395 9.375 13.3887L9.24805 17.9297C9.23828 18.3887 8.99414 18.6523 8.56445 18.6523ZM8.55469 21.6016C8.01758 21.6016 7.55859 21.1621 7.55859 20.625C7.55859 20.0781 8.00781 19.6289 8.55469 19.6289C9.12109 19.6289 9.58008 20.0684 9.58008 20.625C9.58008 21.1621 9.12109 21.6016 8.55469 21.6016Z", fillAlpha = 0.85f)
        }
        return _sFExclamationmarkLockFill!!
    }

private var _sFExclamationmarkLockFill: ImageVector? = null
