package com.composables.sfsymbols.monochrome

import androidx.compose.ui.graphics.vector.ImageVector
import com.composables.sfsymbols.SfSymbols
import com.composables.sfsymbols.addSfPath
import com.composables.sfsymbols.sfIcon

/**
 * SF Symbol: SFMessageBadgeFill (monochrome)
 * Viewport: 32.334 x 29.8926
 */
public val SfSymbols.Monochrome.SFMessageBadgeFill: ImageVector
    get() {
        if (_sFMessageBadgeFill != null) {
            return _sFMessageBadgeFill!!
        }
        _sFMessageBadgeFill = sfIcon(
            name = "Monochrome.SFMessageBadgeFill",
            viewportWidth = 32.334f,
            viewportHeight = 29.8926f
        ) {
            addSfPath("M21.5488 2.87678C21.0349 3.7347 20.7422 4.73607 20.7422 5.80078C20.7422 8.97461 23.3594 11.5918 26.543 11.5918C27.7192 11.5918 28.8191 11.2323 29.7366 10.6168C30.0735 11.6514 30.2441 12.7613 30.2441 13.9258C30.2441 20.8105 24.2383 25.8301 15.918 25.8301C13.1445 25.8301 10.6152 25.293 8.45703 24.2773C7.19727 25.2246 5.41016 25.8301 3.7207 25.8301C3.00781 25.8301 2.75391 25.2539 3.25195 24.8145C3.99414 24.1406 4.3457 23.4961 4.3457 22.5C4.3457 20.2344 1.5918 18.9062 1.5918 13.9258C1.5918 7.01172 7.59766 2.02148 15.918 2.02148C17.9514 2.02148 19.8475 2.32025 21.5488 2.87678Z", fillAlpha = 0.85f)
            addSfPath("M26.543 10.0488C28.8672 10.0488 30.791 8.125 30.791 5.80078C30.791 3.4668 28.8672 1.55273 26.543 1.55273C24.209 1.55273 22.2949 3.45703 22.2949 5.80078C22.2949 8.13477 24.209 10.0488 26.543 10.0488ZM26.543 8.33984C25.1465 8.33984 23.9941 7.19727 23.9941 5.80078C23.9941 4.39453 25.1465 3.25195 26.543 3.25195C27.9297 3.25195 29.0918 4.4043 29.0918 5.80078C29.0918 7.1875 27.9297 8.33984 26.543 8.33984Z", fillAlpha = 0.85f)
        }
        return _sFMessageBadgeFill!!
    }

private var _sFMessageBadgeFill: ImageVector? = null
