package com.composables.sfsymbols.monochrome

import androidx.compose.ui.graphics.vector.ImageVector
import com.composables.sfsymbols.SfSymbols
import com.composables.sfsymbols.addSfPath
import com.composables.sfsymbols.sfIcon

/**
 * SF Symbol: SF5SquareFill (monochrome)
 * Viewport: 23.3203 x 22.959
 */
public val SfSymbols.Monochrome.SF5SquareFill: ImageVector
    get() {
        if (_sF5SquareFill != null) {
            return _sF5SquareFill!!
        }
        _sF5SquareFill = sfIcon(
            name = "Monochrome.SF5SquareFill",
            viewportWidth = 23.3203f,
            viewportHeight = 22.959f
        ) {
            addSfPath("M22.959 3.76953L22.959 19.1992C22.959 21.6797 21.6797 22.959 19.1504 22.959L3.79883 22.959C1.2793 22.959 0 21.6992 0 19.1992L0 3.76953C0 1.26953 1.2793 0 3.79883 0L19.1504 0C21.6797 0 22.959 1.2793 22.959 3.76953ZM9.20898 5.64453C8.47656 5.64453 8.00781 6.06445 7.94922 6.71875L7.58789 11.2402C7.53906 11.8848 7.91016 12.3145 8.53516 12.3145C8.94531 12.3145 9.19922 12.2168 9.62891 11.875C10.3027 11.25 10.8496 11.0156 11.5918 11.0156C13.0469 11.0156 14.0234 12.0312 14.0234 13.5254C14.0234 15.0195 12.9004 16.1328 11.4062 16.1328C10.2637 16.1328 9.28711 15.5566 8.88672 14.6387C8.70117 14.2676 8.42773 14.082 8.0957 14.082C7.64648 14.082 7.33398 14.3945 7.33398 14.8242C7.33398 15.0195 7.38281 15.1953 7.46094 15.3711C7.92969 16.6895 9.39453 17.5586 11.416 17.5586C13.9746 17.5586 15.7227 15.9375 15.7227 13.4961C15.7227 11.2402 14.1113 9.66797 11.7969 9.66797C10.7324 9.66797 9.6582 10.1367 9.15039 10.8301L9.07227 10.8301L9.38477 7.05078L14.5996 7.05078C14.9902 7.05078 15.2734 6.75781 15.2734 6.35742C15.2734 5.9375 15 5.64453 14.5996 5.64453Z", fillAlpha = 0.85f)
        }
        return _sF5SquareFill!!
    }

private var _sF5SquareFill: ImageVector? = null
