package com.composables.sfsymbols.monochrome

import androidx.compose.ui.graphics.vector.ImageVector
import com.composables.sfsymbols.SfSymbols
import com.composables.sfsymbols.addSfPath
import com.composables.sfsymbols.sfIcon

/**
 * SF Symbol: SFArrowUpFolderFill (monochrome)
 * Viewport: 28.7305 x 22.9395
 */
public val SfSymbols.Monochrome.SFArrowUpFolderFill: ImageVector
    get() {
        if (_sFArrowUpFolderFill != null) {
            return _sFArrowUpFolderFill!!
        }
        _sFArrowUpFolderFill = sfIcon(
            name = "Monochrome.SFArrowUpFolderFill",
            viewportWidth = 28.7305f,
            viewportHeight = 22.9395f
        ) {
            addSfPath("M9.59961 0.9375L10.3418 1.5625C10.9961 2.12891 11.4941 2.32422 12.4121 2.32422L24.5703 2.32422C27.0898 2.32422 28.3691 3.59375 28.3691 6.08398L28.3691 19.043C28.3691 21.5234 27.0996 22.8027 24.8633 22.8027L3.79883 22.8027C1.2793 22.8027 0 21.5332 0 19.043L0 3.57422C0 1.18164 1.19141 0 3.33008 0L6.91406 0C8.17383 0 8.76953 0.214844 9.59961 0.9375ZM13.5645 6.32812L9.27734 10.498C9.10156 10.6641 9.01367 10.8398 9.01367 11.084C9.01367 11.5332 9.3457 11.8652 9.80469 11.8652C10.0195 11.8652 10.2637 11.7773 10.4199 11.5918L12.5488 9.375L13.3789 8.49609L13.3203 10.4004L13.3203 18.0957C13.3203 18.5742 13.7207 18.9648 14.1992 18.9648C14.6875 18.9648 15.0879 18.5742 15.0879 18.0957L15.0879 10.4004L15.0195 8.50586L15.8496 9.375L17.9688 11.5918C18.125 11.7773 18.3594 11.8652 18.584 11.8652C19.0332 11.8652 19.3848 11.5332 19.3848 11.084C19.3848 10.8398 19.2871 10.6641 19.1211 10.498L14.8438 6.32812C14.6191 6.11328 14.4336 6.03516 14.1992 6.03516C13.9746 6.03516 13.7793 6.11328 13.5645 6.32812Z", fillAlpha = 0.85f)
        }
        return _sFArrowUpFolderFill!!
    }

private var _sFArrowUpFolderFill: ImageVector? = null
