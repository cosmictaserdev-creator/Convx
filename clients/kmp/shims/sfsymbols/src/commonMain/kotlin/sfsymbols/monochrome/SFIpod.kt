package com.composables.sfsymbols.monochrome

import androidx.compose.ui.graphics.vector.ImageVector
import com.composables.sfsymbols.SfSymbols
import com.composables.sfsymbols.addSfPath
import com.composables.sfsymbols.sfIcon

/**
 * SF Symbol: SFIpod (monochrome)
 * Viewport: 16.9238 x 24.7363
 */
public val SfSymbols.Monochrome.SFIpod: ImageVector
    get() {
        if (_sFIpod != null) {
            return _sFIpod!!
        }
        _sFIpod = sfIcon(
            name = "Monochrome.SFIpod",
            viewportWidth = 16.9238f,
            viewportHeight = 24.7363f
        ) {
            addSfPath("M0 21.5137C0 23.4863 1.28906 24.7363 3.33008 24.7363L13.2324 24.7363C15.2637 24.7363 16.5625 23.4863 16.5625 21.5137L16.5625 3.22266C16.5625 1.25 15.2637 0 13.2324 0L3.33008 0C1.28906 0 0 1.25 0 3.22266ZM1.72852 9.29688L1.72852 3.47656C1.72852 2.33398 2.35352 1.72852 3.54492 1.72852L13.0176 1.72852C14.209 1.72852 14.834 2.33398 14.834 3.47656L14.834 9.29688C14.834 10.4395 14.209 11.0449 13.0176 11.0449L3.54492 11.0449C2.35352 11.0449 1.72852 10.4395 1.72852 9.29688ZM8.29102 22.2949C5.68359 22.2949 3.57422 20.1953 3.57422 17.5977C3.57422 14.9902 5.68359 12.8906 8.29102 12.8906C10.8789 12.8906 12.998 14.9902 12.998 17.5977C12.998 20.1953 10.8789 22.2949 8.29102 22.2949ZM8.29102 20.6934C6.5625 20.6934 5.17578 19.2969 5.17578 17.5977C5.17578 15.8887 6.5625 14.4922 8.29102 14.4922C10 14.4922 11.3867 15.8887 11.3867 17.5977C11.3867 19.2969 10 20.6934 8.29102 20.6934Z", fillAlpha = 0.85f)
        }
        return _sFIpod!!
    }

private var _sFIpod: ImageVector? = null
