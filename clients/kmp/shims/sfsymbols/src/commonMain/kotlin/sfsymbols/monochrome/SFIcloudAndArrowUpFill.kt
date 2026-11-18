package com.composables.sfsymbols.monochrome

import androidx.compose.ui.graphics.vector.ImageVector
import com.composables.sfsymbols.SfSymbols
import com.composables.sfsymbols.addSfPath
import com.composables.sfsymbols.sfIcon

/**
 * SF Symbol: SFIcloudAndArrowUpFill (monochrome)
 * Viewport: 31.5625 x 28.0176
 */
public val SfSymbols.Monochrome.SFIcloudAndArrowUpFill: ImageVector
    get() {
        if (_sFIcloudAndArrowUpFill != null) {
            return _sFIcloudAndArrowUpFill!!
        }
        _sFIcloudAndArrowUpFill = sfIcon(
            name = "Monochrome.SFIcloudAndArrowUpFill",
            viewportWidth = 31.5625f,
            viewportHeight = 28.0176f
        ) {
            addSfPath("M0 15.7812C0 19.0039 2.37305 21.5723 6.40625 21.5723L25.2441 21.5723C28.9941 21.5723 31.2012 19.3066 31.2012 16.3281C31.2012 13.8477 29.7363 11.7773 27.2559 10.8496C27.3242 5.30273 23.3594 1.26953 18.3008 1.26953C14.873 1.26953 12.4805 3.125 11.0352 5.36133C8.17383 4.54102 4.62891 6.72852 4.58984 10.2148C1.78711 10.5957 0 12.8613 0 15.7812ZM15.6055 28.0176C15.1367 28.0176 14.7461 27.6367 14.7461 27.1875L14.7461 14.6191L14.8145 12.7637L14.0332 13.5645L11.9141 15.8008C11.7578 15.9668 11.5234 16.0547 11.3184 16.0547C10.8691 16.0547 10.5371 15.7324 10.5371 15.3027C10.5371 15.0684 10.6348 14.8926 10.791 14.7363L14.9805 10.6641C15.1953 10.4492 15.3809 10.3809 15.6055 10.3809C15.8203 10.3809 16.0059 10.4492 16.2207 10.6641L20.4004 14.7363C20.5664 14.8926 20.6543 15.0684 20.6543 15.3027C20.6543 15.7324 20.3125 16.0547 19.873 16.0547C19.6582 16.0547 19.4336 15.9668 19.2871 15.8008L17.1582 13.5645L16.3867 12.7637L16.4551 14.6191L16.4551 27.1875C16.4551 27.6367 16.0645 28.0176 15.6055 28.0176Z", fillAlpha = 0.85f)
        }
        return _sFIcloudAndArrowUpFill!!
    }

private var _sFIcloudAndArrowUpFill: ImageVector? = null
