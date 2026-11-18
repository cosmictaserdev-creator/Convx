package com.composables.sfsymbols.monochrome

import androidx.compose.ui.graphics.vector.ImageVector
import com.composables.sfsymbols.SfSymbols
import com.composables.sfsymbols.addSfPath
import com.composables.sfsymbols.sfIcon

/**
 * SF Symbol: SFMusicNoteSlash (monochrome)
 * Viewport: 23.8452 x 26.1841
 */
public val SfSymbols.Monochrome.SFMusicNoteSlash: ImageVector
    get() {
        if (_sFMusicNoteSlash != null) {
            return _sFMusicNoteSlash!!
        }
        _sFMusicNoteSlash = sfIcon(
            name = "Monochrome.SFMusicNoteSlash",
            viewportWidth = 23.8452f,
            viewportHeight = 26.1841f
        ) {
            addSfPath("M12.2498 18.8036L12.2498 20.9485C12.2498 24.552 9.54468 25.8801 7.71851 25.8801C5.6775 25.8801 4.23218 24.5715 4.23218 22.6965C4.23218 20.8215 5.39429 19.5813 7.865 19.0735L9.86695 18.6536C10.4431 18.5364 10.7654 18.0969 10.7166 17.4426L10.7162 17.2709ZM19.2517 1.86646L19.2517 6.52466C19.2517 6.92505 19.0076 7.21802 18.6072 7.30591L13.031 8.52662C12.3962 8.6731 12.2498 8.83912 12.2498 9.54224L12.2498 12.0422L10.7019 10.4944L10.6873 3.55591C10.6873 2.90162 11.0877 2.49146 11.8298 2.33521L18.1384 0.958257C18.7439 0.831304 19.2517 1.24146 19.2517 1.86646Z", fillAlpha = 0.85f)
            addSfPath("M20.033 24.3762C20.3552 24.6985 20.8923 24.7083 21.2146 24.3762C21.5369 24.0442 21.5369 23.5266 21.2146 23.1946L2.97242 4.97193C2.65015 4.64966 2.12281 4.6399 1.79078 4.97193C1.46851 5.28443 1.46851 5.8313 1.79078 6.15357Z", fillAlpha = 0.85f)
        }
        return _sFMusicNoteSlash!!
    }

private var _sFMusicNoteSlash: ImageVector? = null
