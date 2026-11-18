package com.composables.sfsymbols.monochrome

import androidx.compose.ui.graphics.vector.ImageVector
import com.composables.sfsymbols.SfSymbols
import com.composables.sfsymbols.addSfPath
import com.composables.sfsymbols.sfIcon

/**
 * SF Symbol: SFLockOpen (monochrome)
 * Viewport: 26.4648 x 25.1855
 */
public val SfSymbols.Monochrome.SFLockOpen: ImageVector
    get() {
        if (_sFLockOpen != null) {
            return _sFLockOpen!!
        }
        _sFLockOpen = sfIcon(
            name = "Monochrome.SFLockOpen",
            viewportWidth = 26.4648f,
            viewportHeight = 25.1855f
        ) {
            addSfPath("M2.73438 24.5312L14.4043 24.5312C16.1523 24.5312 17.1387 23.5254 17.1387 21.6504L17.1387 12.7637C17.1387 10.8887 16.1523 9.88281 14.4043 9.88281L2.73438 9.88281C0.976562 9.88281 0 10.8887 0 12.7637L0 21.6504C0 23.5254 0.976562 24.5312 2.73438 24.5312ZM2.77344 22.9004C2.13867 22.9004 1.73828 22.4707 1.73828 21.7578L1.73828 12.6465C1.73828 11.9336 2.13867 11.5137 2.77344 11.5137L14.375 11.5137C15.0098 11.5137 15.3906 11.9336 15.3906 12.6465L15.3906 21.7578C15.3906 22.4707 15.0098 22.9004 14.375 22.9004ZM13.4375 10.7129L15.1465 10.7129L15.1465 6.72852C15.1465 3.47656 17.2168 1.63086 19.7656 1.63086C22.3242 1.63086 24.3945 3.47656 24.3945 6.72852L24.3945 9.51172C24.3945 10.1367 24.7754 10.459 25.2539 10.459C25.7129 10.459 26.1035 10.166 26.1035 9.51172L26.1035 6.92383C26.1035 2.38281 23.1445 0 19.7656 0C16.3965 0 13.4375 2.38281 13.4375 6.92383Z", fillAlpha = 0.85f)
        }
        return _sFLockOpen!!
    }

private var _sFLockOpen: ImageVector? = null
