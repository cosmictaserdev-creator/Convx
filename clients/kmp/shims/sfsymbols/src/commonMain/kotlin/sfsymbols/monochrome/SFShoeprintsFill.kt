package com.composables.sfsymbols.monochrome

import androidx.compose.ui.graphics.vector.ImageVector
import com.composables.sfsymbols.SfSymbols
import com.composables.sfsymbols.addSfPath
import com.composables.sfsymbols.sfIcon

/**
 * SF Symbol: SFShoeprintsFill (monochrome)
 * Viewport: 23.4863 x 29.0723
 */
public val SfSymbols.Monochrome.SFShoeprintsFill: ImageVector
    get() {
        if (_sFShoeprintsFill != null) {
            return _sFShoeprintsFill!!
        }
        _sFShoeprintsFill = sfIcon(
            name = "Monochrome.SFShoeprintsFill",
            viewportWidth = 23.4863f,
            viewportHeight = 29.0723f
        ) {
            addSfPath("M20.3125 8.19336C17.7539 7.26562 14.0723 10.2539 12.9688 13.2617C12.4512 14.7168 11.8945 17.7637 12.2266 20.4492L18.623 22.7734C20.5957 20.9277 22.1387 18.2617 22.6758 16.7969C23.7598 13.7793 22.8711 9.12109 20.3125 8.19336ZM12.9492 28.4375C14.7754 29.1016 16.4258 27.6367 17.3633 25.0684C17.4609 24.8047 17.5781 24.3848 17.6465 24.0723L12.1582 22.0703C12.002 22.3535 11.8359 22.7441 11.7383 23.0176C10.8008 25.5859 11.123 27.7637 12.9492 28.4375Z", fillAlpha = 0.85f)
            addSfPath("M5.15625 0C2.44141 0 0 4.07227 0 7.27539C0 8.82812 0.527344 11.875 1.74805 14.2773L8.56445 14.2773C9.78516 11.875 10.3125 8.82812 10.3125 7.27539C10.3125 4.07227 7.88086 0 5.15625 0ZM5.16602 21.5332C7.10938 21.5332 8.1543 19.5996 8.1543 16.8652C8.1543 16.582 8.13477 16.1523 8.08594 15.8398L2.24609 15.8398C2.1875 16.1523 2.16797 16.582 2.16797 16.8652C2.16797 19.5996 3.21289 21.5332 5.16602 21.5332Z", fillAlpha = 0.85f)
        }
        return _sFShoeprintsFill!!
    }

private var _sFShoeprintsFill: ImageVector? = null
