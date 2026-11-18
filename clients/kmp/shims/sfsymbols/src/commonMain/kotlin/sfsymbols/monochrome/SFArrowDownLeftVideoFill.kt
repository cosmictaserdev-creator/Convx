package com.composables.sfsymbols.monochrome

import androidx.compose.ui.graphics.vector.ImageVector
import com.composables.sfsymbols.SfSymbols
import com.composables.sfsymbols.addSfPath
import com.composables.sfsymbols.sfIcon

/**
 * SF Symbol: SFArrowDownLeftVideoFill (monochrome)
 * Viewport: 32.3242 x 20.4199
 */
public val SfSymbols.Monochrome.SFArrowDownLeftVideoFill: ImageVector
    get() {
        if (_sFArrowDownLeftVideoFill != null) {
            return _sFArrowDownLeftVideoFill!!
        }
        _sFArrowDownLeftVideoFill = sfIcon(
            name = "Monochrome.SFArrowDownLeftVideoFill",
            viewportWidth = 32.3242f,
            viewportHeight = 20.4199f
        ) {
            addSfPath("M23.3008 4.00391L23.3008 16.3965C23.3008 18.9551 21.8066 20.4102 19.2383 20.4102L5.57617 20.4102C3.00781 20.4102 1.51367 18.9551 1.51367 16.3965L1.51367 4.00391C1.51367 1.45508 3.0957 0 5.57617 0L19.3359 0C21.9043 0 23.3008 1.45508 23.3008 4.00391ZM32.3242 3.66211L32.3242 16.7383C32.3242 17.7441 31.7188 18.4082 30.8008 18.4082C30.332 18.4082 29.8633 18.1738 29.4238 17.8027L24.8535 13.916L24.8535 6.47461L29.4238 2.60742C29.8633 2.23633 30.332 2.00195 30.8008 2.00195C31.7188 2.00195 32.3242 2.66602 32.3242 3.66211ZM16.1816 5.16602L10.6055 10.752L8.58767 12.8959L8.79883 9.23828L8.79883 7.09961C8.79883 6.66992 8.47656 6.30859 8.03711 6.30859C7.57812 6.30859 7.20703 6.65039 7.20703 7.09961L7.20703 14.4141C7.20703 14.9805 7.50977 15.293 8.0957 15.293L15.4492 15.293C15.8984 15.293 16.2402 14.9219 16.2402 14.4629C16.2402 14.0234 15.8887 13.7012 15.4492 13.7012L13.252 13.7012L9.59016 13.913L11.7676 11.8945L17.334 6.31836C17.5293 6.12305 17.6074 5.92773 17.6172 5.69336C17.6074 5.24414 17.2461 4.90234 16.7871 4.91211C16.5234 4.90234 16.3574 5 16.1816 5.16602Z", fillAlpha = 0.85f)
        }
        return _sFArrowDownLeftVideoFill!!
    }

private var _sFArrowDownLeftVideoFill: ImageVector? = null
