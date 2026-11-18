package com.composables.sfsymbols.dualtone

import androidx.compose.ui.graphics.vector.ImageVector
import com.composables.sfsymbols.SfSymbols
import com.composables.sfsymbols.addSfPath
import com.composables.sfsymbols.sfIcon

/**
 * SF Symbol: SFAirplayVideo (dualtone)
 * Viewport: 29.4434 x 26.7676
 */
public val SfSymbols.Dualtone.SFAirplayVideo: ImageVector
    get() {
        if (_sFAirplayVideo != null) {
            return _sFAirplayVideo!!
        }
        _sFAirplayVideo = sfIcon(
            name = "Dualtone.SFAirplayVideo",
            viewportWidth = 29.4434f,
            viewportHeight = 26.7676f
        ) {
            addSfPath("M29.082 4.59961L29.082 17.3828C29.082 19.2871 27.9297 20.4395 26.0254 20.4395L21.2975 20.4395L19.7622 18.7012L25.9863 18.7012C26.8457 18.7012 27.3535 18.2031 27.3535 17.3535L27.3535 4.62891C27.3535 3.76953 26.8457 3.27148 25.9863 3.27148L3.08594 3.27148C2.23633 3.27148 1.72852 3.76953 1.72852 4.62891L1.72852 17.3535C1.72852 18.2031 2.23633 18.7012 3.08594 18.7012L9.32498 18.7012L7.78772 20.4395L3.05664 20.4395C1.15234 20.4395 0 19.2871 0 17.3828L0 4.59961C0 2.69531 1.15234 1.54297 3.05664 1.54297L26.0254 1.54297C27.9297 1.54297 29.082 2.69531 29.082 4.59961Z", fillAlpha = 0.85f)
            addSfPath("M27.3535 4.62891L27.3535 17.3535C27.3535 18.2031 26.8457 18.7012 25.9863 18.7012L19.7622 18.7012L16.1914 14.6582C15.3223 13.6719 13.7695 13.6719 12.9004 14.6582L9.32498 18.7012L3.08594 18.7012C2.23633 18.7012 1.72852 18.2031 1.72852 17.3535L1.72852 4.62891C1.72852 3.76953 2.23633 3.27148 3.08594 3.27148L25.9863 3.27148C26.8457 3.27148 27.3535 3.76953 27.3535 4.62891Z", fillAlpha = 0.2125f)
            addSfPath("M22.2461 25.2051C22.7637 25.2051 22.959 24.668 22.6367 24.2871L15.0293 15.6836C14.7754 15.4004 14.3164 15.4004 14.0625 15.6836L6.45508 24.2871C6.13281 24.668 6.32812 25.2051 6.83594 25.2051Z", fillAlpha = 0.85f)
        }
        return _sFAirplayVideo!!
    }

private var _sFAirplayVideo: ImageVector? = null
