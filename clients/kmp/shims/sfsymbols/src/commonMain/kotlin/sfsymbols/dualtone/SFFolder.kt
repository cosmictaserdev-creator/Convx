package com.composables.sfsymbols.dualtone

import androidx.compose.ui.graphics.vector.ImageVector
import com.composables.sfsymbols.SfSymbols
import com.composables.sfsymbols.addSfPath
import com.composables.sfsymbols.sfIcon

/**
 * SF Symbol: SFFolder (dualtone)
 * Viewport: 28.7305 x 22.9395
 */
public val SfSymbols.Dualtone.SFFolder: ImageVector
    get() {
        if (_sFFolder != null) {
            return _sFFolder!!
        }
        _sFFolder = sfIcon(
            name = "Dualtone.SFFolder",
            viewportWidth = 28.7305f,
            viewportHeight = 22.9395f
        ) {
            addSfPath("M3.79883 22.8027L24.8633 22.8027C27.0996 22.8027 28.3691 21.5234 28.3691 19.043L28.3691 6.08398C28.3691 3.59375 27.0898 2.32422 24.5703 2.32422L12.4121 2.32422C11.4941 2.32422 10.9961 2.12891 10.3418 1.5625L9.59961 0.9375C8.76953 0.214844 8.17383 0 6.91406 0L3.33008 0C1.19141 0 0 1.18164 0 3.57422L0 19.043C0 21.5332 1.2793 22.8027 3.79883 22.8027ZM3.83789 21.0742C2.4707 21.0742 1.72852 20.3516 1.72852 18.9453L1.72852 3.68164C1.72852 2.37305 2.40234 1.71875 3.67188 1.71875L6.48438 1.71875C7.38281 1.71875 7.87109 1.91406 8.53516 2.5L9.27734 3.125C10.0781 3.81836 10.7227 4.05273 11.9824 4.05273L24.541 4.05273C25.8789 4.05273 26.6406 4.77539 26.6406 6.17188L26.6406 18.9551C26.6406 20.3516 25.8789 21.0742 24.541 21.0742ZM3.125 7.72461L25.2344 7.72461L25.2344 7.09961C25.2344 6.2793 24.7949 5.83008 23.8672 5.83008L4.49219 5.83008C3.56445 5.83008 3.125 6.2793 3.125 7.09961Z", fillAlpha = 0.85f)
        }
        return _sFFolder!!
    }

private var _sFFolder: ImageVector? = null
