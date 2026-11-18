package com.composables.sfsymbols.monochrome

import androidx.compose.ui.graphics.vector.ImageVector
import com.composables.sfsymbols.SfSymbols
import com.composables.sfsymbols.addSfPath
import com.composables.sfsymbols.sfIcon

/**
 * SF Symbol: SFExclamationmarkLockFill (monochrome)
 * Viewport: 17.5 x 25.2246
 */
public val SfSymbols.Monochrome.SFExclamationmarkLockFill: ImageVector
    get() {
        if (_sFExclamationmarkLockFill != null) {
            return _sFExclamationmarkLockFill!!
        }
        _sFExclamationmarkLockFill = sfIcon(
            name = "Monochrome.SFExclamationmarkLockFill",
            viewportWidth = 17.5f,
            viewportHeight = 25.2246f
        ) {
            addSfPath("M14.9121 6.93359L14.9121 9.92672C16.3429 10.1082 17.1387 11.0981 17.1387 12.7734L17.1387 21.6602C17.1387 23.5352 16.1523 24.5508 14.4043 24.5508L2.73438 24.5508C0.976562 24.5508 0 23.5352 0 21.6602L0 12.7734C0 11.0934 0.792259 10.1026 2.23633 9.92529L2.23633 6.93359C2.23633 2.38281 5.19531 0 8.56445 0C11.9434 0 14.9121 2.38281 14.9121 6.93359ZM7.55859 20.625C7.55859 21.1621 8.01758 21.6016 8.55469 21.6016C9.12109 21.6016 9.58008 21.1621 9.58008 20.625C9.58008 20.0684 9.12109 19.6289 8.55469 19.6289C8.00781 19.6289 7.55859 20.0781 7.55859 20.625ZM7.76367 13.3789L7.88086 17.9297C7.89062 18.3887 8.13477 18.6523 8.56445 18.6523C8.99414 18.6523 9.23828 18.3887 9.24805 17.9297L9.375 13.3887C9.38477 12.9395 9.0332 12.5977 8.56445 12.5977C8.0957 12.5977 7.75391 12.9297 7.76367 13.3789ZM3.95508 6.72852L3.95508 9.89258L13.1934 9.89258L13.1934 6.72852C13.1934 3.47656 11.1133 1.63086 8.56445 1.63086C6.01562 1.63086 3.95508 3.47656 3.95508 6.72852Z", fillAlpha = 0.85f)
        }
        return _sFExclamationmarkLockFill!!
    }

private var _sFExclamationmarkLockFill: ImageVector? = null
