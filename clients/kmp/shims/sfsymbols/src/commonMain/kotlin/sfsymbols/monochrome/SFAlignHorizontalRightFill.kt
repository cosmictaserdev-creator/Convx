package com.composables.sfsymbols.monochrome

import androidx.compose.ui.graphics.vector.ImageVector
import com.composables.sfsymbols.SfSymbols
import com.composables.sfsymbols.addSfPath
import com.composables.sfsymbols.sfIcon

/**
 * SF Symbol: SFAlignHorizontalRightFill (monochrome)
 * Viewport: 27.9688 x 28.6621
 */
public val SfSymbols.Monochrome.SFAlignHorizontalRightFill: ImageVector
    get() {
        if (_sFAlignHorizontalRightFill != null) {
            return _sFAlignHorizontalRightFill!!
        }
        _sFAlignHorizontalRightFill = sfIcon(
            name = "Monochrome.SFAlignHorizontalRightFill",
            viewportWidth = 27.9688f,
            viewportHeight = 28.6621f
        ) {
            addSfPath("M21.0254 13.0176C22.7051 13.0176 23.6426 12.0898 23.6426 10.4395L23.6426 5.83008C23.6426 4.17969 22.7051 3.25195 21.0254 3.25195L11.3477 3.25195C9.66797 3.25195 8.73047 4.17969 8.73047 5.83008L8.73047 10.4395C8.73047 12.0898 9.66797 13.0176 11.3477 13.0176ZM21.0254 25.3809C22.7051 25.3809 23.6426 24.4531 23.6426 22.8027L23.6426 18.1934C23.6426 16.543 22.7051 15.6152 21.0254 15.6152L2.61719 15.6152C0.9375 15.6152 0 16.543 0 18.1934L0 22.8027C0 24.4531 0.9375 25.3809 2.61719 25.3809Z", fillAlpha = 0.85f)
            addSfPath("M26.8164 28.6426C27.2363 28.6426 27.6074 28.3105 27.6074 27.8906L27.6074 0.751953C27.6074 0.332031 27.2363 0 26.8164 0C26.3867 0 26.0156 0.332031 26.0156 0.751953L26.0156 27.8906C26.0156 28.3105 26.3867 28.6426 26.8164 28.6426Z", fillAlpha = 0.85f)
        }
        return _sFAlignHorizontalRightFill!!
    }

private var _sFAlignHorizontalRightFill: ImageVector? = null
