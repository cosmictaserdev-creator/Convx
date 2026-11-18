package com.composables.sfsymbols.dualtone

import androidx.compose.ui.graphics.vector.ImageVector
import com.composables.sfsymbols.SfSymbols
import com.composables.sfsymbols.addSfPath
import com.composables.sfsymbols.sfIcon

/**
 * SF Symbol: SFDoorSlidingLeftHandOpen (dualtone)
 * Viewport: 30.5176 x 26.4551
 */
public val SfSymbols.Dualtone.SFDoorSlidingLeftHandOpen: ImageVector
    get() {
        if (_sFDoorSlidingLeftHandOpen != null) {
            return _sFDoorSlidingLeftHandOpen!!
        }
        _sFDoorSlidingLeftHandOpen = sfIcon(
            name = "Dualtone.SFDoorSlidingLeftHandOpen",
            viewportWidth = 30.5176f,
            viewportHeight = 26.4551f
        ) {
            addSfPath("M0.869141 26.4355C1.34766 26.4355 1.73828 26.0449 1.73828 25.5664L1.73828 2.40234C1.73828 2.00195 1.99219 1.73828 2.37305 1.73828L27.7832 1.73828C28.1543 1.73828 28.418 2.00195 28.418 2.40234L28.418 25.5664C28.418 26.0449 28.7988 26.4355 29.2871 26.4355C29.7656 26.4355 30.1562 26.0449 30.1562 25.5664L30.1562 2.28516C30.1562 0.917969 29.2188 0 27.8125 0L2.34375 0C0.9375 0 0 0.917969 0 2.28516L0 25.5664C0 26.0449 0.380859 26.4355 0.869141 26.4355Z", fillAlpha = 0.425f)
            addSfPath("M15.6348 26.0156L26.6895 26.0156C26.8945 26.0156 27.0508 25.8496 27.0508 25.6445L27.0508 3.4668C27.0508 3.26172 26.8945 3.0957 26.6895 3.0957L15.6348 3.0957C15.4297 3.0957 15.2637 3.26172 15.2637 3.4668L15.2637 25.6445C15.2637 25.8496 15.4297 26.0156 15.6348 26.0156ZM18.0859 16.4453C17.7441 16.4453 17.5 16.2012 17.5 15.8594L17.5 13.6523C17.5 13.3105 17.7441 13.0664 18.0859 13.0664C18.4277 13.0664 18.6621 13.3105 18.6621 13.6523L18.6621 15.8594C18.6621 16.2012 18.4277 16.4453 18.0859 16.4453Z", fillAlpha = 0.85f)
        }
        return _sFDoorSlidingLeftHandOpen!!
    }

private var _sFDoorSlidingLeftHandOpen: ImageVector? = null
