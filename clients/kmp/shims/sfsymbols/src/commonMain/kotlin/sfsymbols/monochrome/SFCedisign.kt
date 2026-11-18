package com.composables.sfsymbols.monochrome

import androidx.compose.ui.graphics.vector.ImageVector
import com.composables.sfsymbols.SfSymbols
import com.composables.sfsymbols.addSfPath
import com.composables.sfsymbols.sfIcon

/**
 * SF Symbol: SFCedisign (monochrome)
 * Viewport: 18.4668 x 28.4473
 */
public val SfSymbols.Monochrome.SFCedisign: ImageVector
    get() {
        if (_sFCedisign != null) {
            return _sFCedisign!!
        }
        _sFCedisign = sfIcon(
            name = "Monochrome.SFCedisign",
            viewportWidth = 18.4668f,
            viewportHeight = 28.4473f
        ) {
            addSfPath("M9.64844 25.3906C13.3203 25.3906 16.6992 23.7891 17.8906 20.3223C18.0273 19.9902 18.1055 19.4922 18.1055 19.1797C18.1055 18.5254 17.6855 18.1934 17.1387 18.1934C16.5527 18.1934 16.2598 18.5254 16.084 19.082C15.4004 21.8652 12.7637 23.457 9.6582 23.457C4.66797 23.457 2.23633 19.2969 2.23633 14.1504C2.23633 9.17969 4.58984 4.9707 9.64844 4.9707C12.5684 4.9707 15.2441 6.67969 15.9473 9.47266C16.123 10.0391 16.3965 10.3516 16.9922 10.3516C17.5879 10.3516 17.9688 9.96094 17.9688 9.375C17.9688 9.0332 17.8906 8.62305 17.7637 8.26172C16.5918 4.96094 13.2617 3.03711 9.64844 3.03711C3.39844 3.03711 0 7.79297 0 14.1504C0 20.5957 3.36914 25.3906 9.64844 25.3906ZM9.44336 28.4473C9.87305 28.4473 10.1562 28.1641 10.1562 27.7246L10.1562 0.859375C10.1562 0.361328 9.79492 0.126953 9.44336 0.126953C9.10156 0.126953 8.74023 0.361328 8.74023 0.859375L8.74023 27.7246C8.74023 28.2031 9.07227 28.4473 9.44336 28.4473Z", fillAlpha = 0.85f)
        }
        return _sFCedisign!!
    }

private var _sFCedisign: ImageVector? = null
