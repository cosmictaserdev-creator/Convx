package com.composables.sfsymbols.dualtone

import androidx.compose.ui.graphics.vector.ImageVector
import com.composables.sfsymbols.SfSymbols
import com.composables.sfsymbols.addSfPath
import com.composables.sfsymbols.sfIcon

/**
 * SF Symbol: SFDegreesignCelsius (dualtone)
 * Viewport: 27.9785 x 22.7832
 */
public val SfSymbols.Dualtone.SFDegreesignCelsius: ImageVector
    get() {
        if (_sFDegreesignCelsius != null) {
            return _sFDegreesignCelsius!!
        }
        _sFDegreesignCelsius = sfIcon(
            name = "Dualtone.SFDegreesignCelsius",
            viewportWidth = 27.9785f,
            viewportHeight = 22.7832f
        ) {
            addSfPath("M19.2773 22.7832C23.3789 22.7832 26.3379 20.7031 27.4023 17.5977C27.5293 17.2168 27.5977 16.8359 27.6172 16.4551C27.6172 15.8984 27.2656 15.498 26.6797 15.498C26.123 15.498 25.8398 15.8203 25.6641 16.377C25.0293 19.0918 22.5293 20.9277 19.2871 20.9277C14.7656 20.9277 11.8945 17.207 11.8945 11.3184C11.8945 5.53711 14.7656 1.91406 19.2773 1.91406C22.3828 1.91406 24.8828 3.89648 25.5371 6.5918C25.7227 7.20703 25.9961 7.46094 26.543 7.46094C27.1191 7.46094 27.4902 7.07031 27.4805 6.49414C27.4609 6.13281 27.4023 5.79102 27.2852 5.45898C26.1719 2.16797 23.0371 0.0488281 19.2773 0.0488281C13.4863 0.0488281 9.77539 4.41406 9.77539 11.3184C9.77539 18.3301 13.4766 22.7832 19.2773 22.7832ZM3.68164 8.71094C5.74219 8.71094 7.36328 7.08984 7.36328 5.01953C7.36328 2.95898 5.74219 1.33789 3.68164 1.33789C1.62109 1.33789 0 2.95898 0 5.01953C0 7.08984 1.62109 8.71094 3.68164 8.71094ZM3.68164 7.17773C2.48047 7.17773 1.5332 6.23047 1.5332 5.01953C1.5332 3.81836 2.48047 2.87109 3.68164 2.87109C4.88281 2.87109 5.83008 3.81836 5.83008 5.01953C5.83008 6.23047 4.88281 7.17773 3.68164 7.17773Z", fillAlpha = 0.85f)
        }
        return _sFDegreesignCelsius!!
    }

private var _sFDegreesignCelsius: ImageVector? = null
