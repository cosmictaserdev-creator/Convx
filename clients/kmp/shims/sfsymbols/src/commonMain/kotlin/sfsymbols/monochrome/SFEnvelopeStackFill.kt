package com.composables.sfsymbols.monochrome

import androidx.compose.ui.graphics.vector.ImageVector
import com.composables.sfsymbols.SfSymbols
import com.composables.sfsymbols.addSfPath
import com.composables.sfsymbols.sfIcon

/**
 * SF Symbol: SFEnvelopeStackFill (monochrome)
 * Viewport: 33.3789 x 31.5723
 */
public val SfSymbols.Monochrome.SFEnvelopeStackFill: ImageVector
    get() {
        if (_sFEnvelopeStackFill != null) {
            return _sFEnvelopeStackFill!!
        }
        _sFEnvelopeStackFill = sfIcon(
            name = "Monochrome.SFEnvelopeStackFill",
            viewportWidth = 33.3789f,
            viewportHeight = 31.5723f
        ) {
            addSfPath("M24.8574 3.51563L8.15054 3.51563C8.21027 2.62376 8.79422 2.09961 9.73633 2.09961L23.2812 2.09961C24.2234 2.09961 24.7986 2.62376 24.8574 3.51563Z", fillAlpha = 0.85f)
            addSfPath("M27.3784 6.85004C27.3425 6.84596 27.3041 6.8457 27.2656 6.8457L5.63058 6.8457C5.79081 5.86487 6.49872 5.3125 7.61719 5.3125L25.4004 5.3125C26.5205 5.3125 27.2214 5.86651 27.3784 6.85004Z", fillAlpha = 0.85f)
            addSfPath("M16.5039 20.6641C16.9141 20.6641 17.3145 20.498 17.6953 20.1465L29.6582 9.56055C29.0332 9.0918 28.2715 8.94531 27.2656 8.94531L5.74219 8.94531C4.74609 8.94531 3.98438 9.0918 3.34961 9.56055L15.3125 20.1465C15.7031 20.498 16.1035 20.6641 16.5039 20.6641ZM2.49023 27.6953L11.4453 18.7402L2.45117 10.7812C2.27539 11.0742 2.11914 11.709 2.11914 12.5391L2.11914 25.8594C2.11914 26.6504 2.25586 27.2266 2.49023 27.6953ZM5.74219 29.4531L27.2656 29.4531C28.4277 29.4531 29.0918 29.1895 29.5605 28.877L20.4199 19.7461L18.623 21.3379C17.9395 21.9336 17.2363 22.2363 16.5039 22.2363C15.7715 22.2363 15.0781 21.9336 14.3848 21.3379L12.5977 19.7461L3.45703 28.877C3.91602 29.1895 4.58008 29.4531 5.74219 29.4531ZM30.5273 27.6953C30.752 27.2266 30.8887 26.6504 30.8887 25.8594L30.8887 12.5391C30.8887 11.709 30.7422 11.0742 30.5566 10.7812L21.5625 18.7402Z", fillAlpha = 0.85f)
        }
        return _sFEnvelopeStackFill!!
    }

private var _sFEnvelopeStackFill: ImageVector? = null
