package com.composables.sfsymbols.dualtone

import androidx.compose.ui.graphics.vector.ImageVector
import com.composables.sfsymbols.SfSymbols
import com.composables.sfsymbols.addSfPath
import com.composables.sfsymbols.sfIcon

/**
 * SF Symbol: SFCupAndSaucerFill (dualtone)
 * Viewport: 32.4219 x 23.9355
 */
public val SfSymbols.Dualtone.SFCupAndSaucerFill: ImageVector
    get() {
        if (_sFCupAndSaucerFill != null) {
            return _sFCupAndSaucerFill!!
        }
        _sFCupAndSaucerFill = sfIcon(
            name = "Dualtone.SFCupAndSaucerFill",
            viewportWidth = 32.4219f,
            viewportHeight = 23.9355f
        ) {
            addSfPath("M15.5664 18.1348L16.5918 18.1348C22.9492 18.1348 27.041 13.9062 27.041 7.45117L27.041 4.94141C27.041 2.06055 22.5879 0.0488281 16.0742 0.0488281C9.56055 0.0488281 5.10742 2.06055 5.10742 4.94141L5.10742 7.45117C5.10742 13.7305 9.19922 18.1348 15.5664 18.1348ZM16.0742 8.16406C10.7031 8.16406 6.78711 6.75781 6.78711 4.94141C6.78711 3.13477 10.7031 1.72852 16.0742 1.72852C21.4551 1.72852 25.3711 3.13477 25.3711 4.94141C25.3711 6.75781 21.4551 8.16406 16.0742 8.16406ZM25.752 12.5586L26.7578 12.5586C29.4238 12.5586 31.0645 11.1133 31.0645 8.7793C31.0645 6.44531 29.4141 5.00977 26.7578 5.00977L26.1035 5.00977L26.1035 6.66992L26.7578 6.66992C28.3984 6.66992 29.3945 7.4707 29.3945 8.7793C29.3945 10.0781 28.3984 10.8887 26.7578 10.8887L25.752 10.8887ZM16.0254 23.9355C25.3809 23.9355 32.0605 21.2891 32.0605 17.4609C32.0605 15.9375 30.9473 14.5703 28.9844 13.5059C28.3301 13.7598 27.6367 13.8965 26.8359 13.916C24.8828 17.4414 21.2402 19.502 16.5918 19.502L15.5664 19.502C10.4492 19.502 6.51367 16.875 4.74609 12.7051C1.72852 13.8574 0 15.5176 0 17.4609C0 21.2891 6.68945 23.9355 16.0254 23.9355Z", fillAlpha = 0.85f)
        }
        return _sFCupAndSaucerFill!!
    }

private var _sFCupAndSaucerFill: ImageVector? = null
