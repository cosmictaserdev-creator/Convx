package com.composables.sfsymbols.dualtone

import androidx.compose.ui.graphics.vector.ImageVector
import com.composables.sfsymbols.SfSymbols
import com.composables.sfsymbols.addSfPath
import com.composables.sfsymbols.sfIcon

/**
 * SF Symbol: SFArrowUpFolderFill (dualtone)
 * Viewport: 28.7305 x 22.9395
 */
public val SfSymbols.Dualtone.SFArrowUpFolderFill: ImageVector
    get() {
        if (_sFArrowUpFolderFill != null) {
            return _sFArrowUpFolderFill!!
        }
        _sFArrowUpFolderFill = sfIcon(
            name = "Dualtone.SFArrowUpFolderFill",
            viewportWidth = 28.7305f,
            viewportHeight = 22.9395f
        ) {
            addSfPath("M3.79883 22.8027L24.8633 22.8027C27.0996 22.8027 28.3691 21.5234 28.3691 19.043L28.3691 6.08398C28.3691 3.59375 27.0898 2.32422 24.5703 2.32422L12.4121 2.32422C11.4941 2.32422 10.9961 2.12891 10.3418 1.5625L9.59961 0.9375C8.76953 0.214844 8.17383 0 6.91406 0L3.33008 0C1.19141 0 0 1.18164 0 3.57422L0 19.043C0 21.5332 1.2793 22.8027 3.79883 22.8027Z", fillAlpha = 0.2125f)
            addSfPath("M14.1992 6.03516C14.4336 6.03516 14.6191 6.11328 14.8438 6.32812L19.1211 10.498C19.2871 10.6641 19.3848 10.8398 19.3848 11.084C19.3848 11.5332 19.0332 11.8652 18.584 11.8652C18.3594 11.8652 18.125 11.7773 17.9688 11.5918L15.8496 9.375L15.0195 8.50586L15.0879 10.4004L15.0879 18.0957C15.0879 18.5742 14.6875 18.9648 14.1992 18.9648C13.7207 18.9648 13.3203 18.5742 13.3203 18.0957L13.3203 10.4004L13.3789 8.49609L12.5488 9.375L10.4199 11.5918C10.2637 11.7773 10.0195 11.8652 9.80469 11.8652C9.3457 11.8652 9.01367 11.5332 9.01367 11.084C9.01367 10.8398 9.10156 10.6641 9.27734 10.498L13.5645 6.32812C13.7793 6.11328 13.9746 6.03516 14.1992 6.03516Z", fillAlpha = 0.85f)
        }
        return _sFArrowUpFolderFill!!
    }

private var _sFArrowUpFolderFill: ImageVector? = null
