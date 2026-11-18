package com.composables.sfsymbols.monochrome

import androidx.compose.ui.graphics.vector.ImageVector
import com.composables.sfsymbols.SfSymbols
import com.composables.sfsymbols.addSfPath
import com.composables.sfsymbols.sfIcon

/**
 * SF Symbol: SFArrowForwardFolderFill (monochrome)
 * Viewport: 28.7305 x 22.9395
 */
public val SfSymbols.Monochrome.SFArrowForwardFolderFill: ImageVector
    get() {
        if (_sFArrowForwardFolderFill != null) {
            return _sFArrowForwardFolderFill!!
        }
        _sFArrowForwardFolderFill = sfIcon(
            name = "Monochrome.SFArrowForwardFolderFill",
            viewportWidth = 28.7305f,
            viewportHeight = 22.9395f
        ) {
            addSfPath("M9.59961 0.9375L10.3418 1.5625C10.9961 2.12891 11.4941 2.32422 12.4121 2.32422L24.5703 2.32422C27.0898 2.32422 28.3691 3.59375 28.3691 6.08398L28.3691 19.043C28.3691 21.5234 27.0996 22.8027 24.8633 22.8027L3.79883 22.8027C1.2793 22.8027 0 21.5332 0 19.043L0 3.57422C0 1.18164 1.19141 0 3.33008 0L6.91406 0C8.17383 0 8.76953 0.214844 9.59961 0.9375ZM14.873 8.11523C14.873 8.33008 14.9609 8.56445 15.1367 8.7207L17.3633 10.8398L18.2239 11.6616L16.709 11.6113L8.63281 11.6016C8.1543 11.6016 7.76367 12.0117 7.76367 12.4902C7.76367 12.9785 8.1543 13.3789 8.63281 13.3789L16.709 13.3691L18.2292 13.3187L17.3633 14.1504L15.1367 16.2695C14.9609 16.4258 14.873 16.6699 14.873 16.8848C14.873 17.3438 15.2051 17.6758 15.6445 17.6758C15.8887 17.6758 16.0742 17.5879 16.2305 17.4121L20.4004 13.1348C20.625 12.9102 20.6934 12.7246 20.6934 12.4902C20.6934 12.2656 20.625 12.0703 20.4004 11.8555L16.2305 7.56836C16.0742 7.40234 15.8887 7.30469 15.6445 7.30469C15.2051 7.30469 14.873 7.65625 14.873 8.11523Z", fillAlpha = 0.85f)
        }
        return _sFArrowForwardFolderFill!!
    }

private var _sFArrowForwardFolderFill: ImageVector? = null
