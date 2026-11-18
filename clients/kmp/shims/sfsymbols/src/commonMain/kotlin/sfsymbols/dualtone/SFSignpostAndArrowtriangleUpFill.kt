package com.composables.sfsymbols.dualtone

import androidx.compose.ui.graphics.vector.ImageVector
import com.composables.sfsymbols.SfSymbols
import com.composables.sfsymbols.addSfPath
import com.composables.sfsymbols.sfIcon

/**
 * SF Symbol: SFSignpostAndArrowtriangleUpFill (dualtone)
 * Viewport: 22.3047 x 26.084
 */
public val SfSymbols.Dualtone.SFSignpostAndArrowtriangleUpFill: ImageVector
    get() {
        if (_sFSignpostAndArrowtriangleUpFill != null) {
            return _sFSignpostAndArrowtriangleUpFill!!
        }
        _sFSignpostAndArrowtriangleUpFill = sfIcon(
            name = "Dualtone.SFSignpostAndArrowtriangleUpFill",
            viewportWidth = 22.3047f,
            viewportHeight = 26.084f
        ) {
            addSfPath("M10.1074 4.32617L11.8359 4.32617L11.8359 0.869141C11.8359 0.390625 11.4453 0 10.9668 0C10.498 0 10.1074 0.390625 10.1074 0.869141ZM0 12.9883C0 14.5703 0.908203 15.4688 2.51953 15.4688L19.4238 15.4688C21.0352 15.4688 21.9434 14.5703 21.9434 12.9883L21.9434 5.58594C21.9434 4.00391 21.0352 3.11523 19.4238 3.11523L2.51953 3.11523C0.908203 3.11523 0 4.00391 0 5.58594ZM10.1074 14.834L10.1074 25.9375L11.8359 25.9375L11.8359 14.834ZM5.36133 25.2051C5.36133 25.6738 5.76172 26.0645 6.23047 26.0645L15.7227 26.0645C16.2012 26.0645 16.582 25.6738 16.582 25.2051C16.582 24.7266 16.2012 24.3359 15.7227 24.3359L6.23047 24.3359C5.76172 24.3359 5.36133 24.7266 5.36133 25.2051Z", fillAlpha = 0.2125f)
            addSfPath("M7.95898 12.002C7.10938 12.002 6.88477 11.4355 7.37305 10.752L10.3125 6.5625C10.6934 6.00586 11.2598 6.00586 11.6504 6.5625L14.5996 10.7617C15.0684 11.4355 14.8145 12.002 14.0039 12.002Z", fillAlpha = 0.85f)
        }
        return _sFSignpostAndArrowtriangleUpFill!!
    }

private var _sFSignpostAndArrowtriangleUpFill: ImageVector? = null
