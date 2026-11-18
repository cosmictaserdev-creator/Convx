package com.composables.sfsymbols.monochrome

import androidx.compose.ui.graphics.vector.ImageVector
import com.composables.sfsymbols.SfSymbols
import com.composables.sfsymbols.addSfPath
import com.composables.sfsymbols.sfIcon

/**
 * SF Symbol: SFEurosign (monochrome)
 * Viewport: 18.5938 x 22.3535
 */
public val SfSymbols.Monochrome.SFEurosign: ImageVector
    get() {
        if (_sFEurosign != null) {
            return _sFEurosign!!
        }
        _sFEurosign = sfIcon(
            name = "Monochrome.SFEurosign",
            viewportWidth = 18.5938f,
            viewportHeight = 22.3535f
        ) {
            addSfPath("M15.0195 22.334C15.7227 22.334 16.6016 22.3047 17.2363 22.1582C17.832 22.0215 18.2324 21.748 18.2324 21.1816C18.2324 20.5371 17.8125 20.2734 17.2266 20.2734C16.5918 20.2734 16.2598 20.4004 15.0977 20.4004C9.51172 20.4004 5.88867 17.0508 5.88867 11.25L5.88867 11.084C5.88867 5.37109 9.38477 1.96289 15.0391 1.96289C15.8887 1.96289 16.9238 2.09961 17.2266 2.09961C17.8516 2.09961 18.2324 1.77734 18.2324 1.18164C18.2324 0.595703 17.8125 0.3125 17.2363 0.166016C16.5625 0.00976562 15.6836 0 15.0391 0C8.35938 0 3.70117 3.99414 3.70117 11.1426L3.70117 11.1914C3.70117 18.1152 8.06641 22.334 15.0195 22.334ZM0 8.96484C0 9.38477 0.292969 9.67773 0.703125 9.67773L14.5605 9.67773C15 9.67773 15.2832 9.39453 15.2832 8.96484C15.2832 8.52539 15 8.23242 14.5605 8.23242L0.703125 8.23242C0.292969 8.23242 0 8.53516 0 8.96484ZM0 13.418C0 13.8477 0.292969 14.1406 0.703125 14.1406L14.5605 14.1406C15 14.1406 15.2832 13.8574 15.2832 13.4277C15.2832 12.9883 15 12.6953 14.5605 12.6953L0.703125 12.6855C0.292969 12.6855 0 12.9883 0 13.418Z", fillAlpha = 0.85f)
        }
        return _sFEurosign!!
    }

private var _sFEurosign: ImageVector? = null
