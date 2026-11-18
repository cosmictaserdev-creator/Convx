package com.composables.sfsymbols.dualtone

import androidx.compose.ui.graphics.vector.ImageVector
import com.composables.sfsymbols.SfSymbols
import com.composables.sfsymbols.addSfPath
import com.composables.sfsymbols.sfIcon

/**
 * SF Symbol: SFWebCamera (dualtone)
 * Viewport: 20.2637 x 24.9707
 */
public val SfSymbols.Dualtone.SFWebCamera: ImageVector
    get() {
        if (_sFWebCamera != null) {
            return _sFWebCamera!!
        }
        _sFWebCamera = sfIcon(
            name = "Dualtone.SFWebCamera",
            viewportWidth = 20.2637f,
            viewportHeight = 24.9707f
        ) {
            addSfPath("M9.96094 19.9121C15.4492 19.9121 19.9023 15.459 19.9023 9.95117C19.9023 4.45312 15.4492 0 9.96094 0C4.46289 0 0 4.45312 0 9.95117C0 15.459 4.46289 19.9121 9.96094 19.9121ZM9.96094 18.1836C5.3418 18.1836 1.72852 14.5801 1.72852 9.95117C1.72852 5.3418 5.3418 1.72852 9.96094 1.72852C14.5605 1.72852 18.1738 5.3418 18.1738 9.95117C18.1738 14.5801 14.5605 18.1836 9.96094 18.1836ZM9.0918 24.0918L10.8301 24.0918L10.8301 19.1211L9.0918 19.1211ZM5.625 24.9512L14.2871 24.9512C14.7656 24.9512 15.1562 24.5605 15.1562 24.0918C15.1562 23.6133 14.7656 23.2227 14.2871 23.2227L5.63477 23.2227C5.15625 23.2227 4.76562 23.6133 4.76562 24.0918C4.76562 24.5605 5.15625 24.9512 5.625 24.9512Z", fillAlpha = 0.425f)
            addSfPath("M9.96094 15.7227C13.1836 15.7227 15.7031 13.1836 15.7031 9.95117C15.7031 6.73828 13.1836 4.18945 9.96094 4.18945C6.73828 4.18945 4.19922 6.73828 4.19922 9.95117C4.19922 13.1836 6.73828 15.7227 9.96094 15.7227ZM9.96094 12.6465C8.4668 12.6465 7.28516 11.4551 7.28516 9.95117C7.28516 8.45703 8.4668 7.27539 9.96094 7.27539C11.4551 7.27539 12.627 8.45703 12.627 9.95117C12.627 11.4551 11.4551 12.6465 9.96094 12.6465Z", fillAlpha = 0.85f)
        }
        return _sFWebCamera!!
    }

private var _sFWebCamera: ImageVector? = null
