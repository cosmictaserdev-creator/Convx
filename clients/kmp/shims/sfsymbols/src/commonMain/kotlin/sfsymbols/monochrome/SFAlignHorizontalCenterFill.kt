package com.composables.sfsymbols.monochrome

import androidx.compose.ui.graphics.vector.ImageVector
import com.composables.sfsymbols.SfSymbols
import com.composables.sfsymbols.addSfPath
import com.composables.sfsymbols.sfIcon

/**
 * SF Symbol: SFAlignHorizontalCenterFill (monochrome)
 * Viewport: 24.0039 x 28.6621
 */
public val SfSymbols.Monochrome.SFAlignHorizontalCenterFill: ImageVector
    get() {
        if (_sFAlignHorizontalCenterFill != null) {
            return _sFAlignHorizontalCenterFill!!
        }
        _sFAlignHorizontalCenterFill = sfIcon(
            name = "Monochrome.SFAlignHorizontalCenterFill",
            viewportWidth = 24.0039f,
            viewportHeight = 28.6621f
        ) {
            addSfPath("M12.6172 27.8906C12.6172 28.3105 12.2461 28.6426 11.8164 28.6426C11.3965 28.6426 11.0254 28.3105 11.0254 27.8906L11.0254 25.3809L12.6172 25.3809ZM12.6172 15.6152L11.0254 15.6152L11.0254 13.0176L12.6172 13.0176ZM12.6172 0.751953L12.6172 3.25195L11.0254 3.25195L11.0254 0.751953C11.0254 0.332031 11.3965 0 11.8164 0C12.2461 0 12.6172 0.332031 12.6172 0.751953Z", fillAlpha = 0.85f)
            addSfPath("M6.98242 13.0176L16.6504 13.0176C18.3398 13.0176 19.2773 12.0898 19.2773 10.4395L19.2773 5.83008C19.2773 4.17969 18.3398 3.25195 16.6504 3.25195L6.98242 3.25195C5.30273 3.25195 4.36523 4.17969 4.36523 5.83008L4.36523 10.4395C4.36523 12.0898 5.30273 13.0176 6.98242 13.0176ZM2.61719 25.3809L21.0254 25.3809C22.7051 25.3809 23.6426 24.4531 23.6426 22.8027L23.6426 18.1934C23.6426 16.543 22.7051 15.6152 21.0254 15.6152L2.61719 15.6152C0.9375 15.6152 0 16.543 0 18.1934L0 22.8027C0 24.4531 0.9375 25.3809 2.61719 25.3809Z", fillAlpha = 0.85f)
        }
        return _sFAlignHorizontalCenterFill!!
    }

private var _sFAlignHorizontalCenterFill: ImageVector? = null
