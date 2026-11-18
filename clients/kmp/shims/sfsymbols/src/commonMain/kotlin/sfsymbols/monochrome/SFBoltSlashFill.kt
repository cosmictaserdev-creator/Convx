package com.composables.sfsymbols.monochrome

import androidx.compose.ui.graphics.vector.ImageVector
import com.composables.sfsymbols.SfSymbols
import com.composables.sfsymbols.addSfPath
import com.composables.sfsymbols.sfIcon

/**
 * SF Symbol: SFBoltSlashFill (monochrome)
 * Viewport: 23.9038 x 27.9005
 */
public val SfSymbols.Monochrome.SFBoltSlashFill: ImageVector
    get() {
        if (_sFBoltSlashFill != null) {
            return _sFBoltSlashFill!!
        }
        _sFBoltSlashFill = sfIcon(
            name = "Monochrome.SFBoltSlashFill",
            viewportWidth = 23.9038f,
            viewportHeight = 27.9005f
        ) {
            addSfPath("M14.6325 19.8388L8.71949 27.2364C7.9773 28.1543 6.81519 27.5684 7.24488 26.4356L11.0103 16.2166ZM19.6472 11.5332C20.1062 11.5332 20.4089 11.8555 20.4089 12.2559C20.4089 12.5098 20.3015 12.7344 20.116 12.9785L17.3903 16.3885L12.5326 11.5332ZM3.87574 16.1524C3.41675 16.1524 3.10425 15.8399 3.10425 15.4395C3.10425 15.1856 3.22144 14.961 3.40699 14.7168L6.11837 11.3247L10.9461 16.1524ZM16.2781 1.2598L12.4936 11.4943L8.8743 7.87683L14.8035 0.459015C15.5457-0.46872 16.7078 0.117218 16.2781 1.2598Z", fillAlpha = 0.85f)
            addSfPath("M20.7117 23.9942C21.0339 24.3164 21.5711 24.3164 21.8933 23.9942C22.2058 23.6817 22.2156 23.1348 21.8933 22.8125L2.78199 3.7012C2.46949 3.3887 1.91285 3.37894 1.59058 3.7012C1.27808 4.0137 1.27808 4.56058 1.59058 4.88284Z", fillAlpha = 0.85f)
        }
        return _sFBoltSlashFill!!
    }

private var _sFBoltSlashFill: ImageVector? = null
