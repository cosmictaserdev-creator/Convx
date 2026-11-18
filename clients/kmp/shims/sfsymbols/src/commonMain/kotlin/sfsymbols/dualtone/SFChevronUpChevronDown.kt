package com.composables.sfsymbols.dualtone

import androidx.compose.ui.graphics.vector.ImageVector
import com.composables.sfsymbols.SfSymbols
import com.composables.sfsymbols.addSfPath
import com.composables.sfsymbols.sfIcon

/**
 * SF Symbol: SFChevronUpChevronDown (dualtone)
 * Viewport: 17.0996 x 24.0527
 */
public val SfSymbols.Dualtone.SFChevronUpChevronDown: ImageVector
    get() {
        if (_sFChevronUpChevronDown != null) {
            return _sFChevronUpChevronDown!!
        }
        _sFChevronUpChevronDown = sfIcon(
            name = "Dualtone.SFChevronUpChevronDown",
            viewportWidth = 17.0996f,
            viewportHeight = 24.0527f
        ) {
            addSfPath("M8.36914 0C8.08594 0 7.83203 0.126953 7.61719 0.341797L0.283203 7.39258C0.107422 7.56836 0 7.79297 0 8.10547C0 8.65234 0.419922 9.07227 0.976562 9.07227C1.20117 9.07227 1.45508 9.00391 1.68945 8.7793L8.36914 2.23633L15.0488 8.7793C15.2832 8.99414 15.5371 9.07227 15.7617 9.07227C16.3184 9.07227 16.7383 8.65234 16.7383 8.10547C16.7383 7.79297 16.6211 7.56836 16.4551 7.39258L9.12109 0.341797C8.90625 0.126953 8.65234 0 8.36914 0ZM8.36914 24.0332C8.65234 24.0332 8.90625 23.916 9.12109 23.7012L16.4551 16.6406C16.6211 16.4648 16.7383 16.2402 16.7383 15.9375C16.7383 15.3809 16.3184 14.9707 15.7617 14.9707C15.5371 14.9707 15.2832 15.0391 15.0488 15.2539L8.36914 21.7969L1.68945 15.2539C1.45508 15.0293 1.20117 14.9707 0.976562 14.9707C0.419922 14.9707 0 15.3809 0 15.9375C0 16.2402 0.107422 16.4648 0.283203 16.6406L7.61719 23.7012C7.83203 23.916 8.08594 24.0332 8.36914 24.0332Z", fillAlpha = 0.85f)
        }
        return _sFChevronUpChevronDown!!
    }

private var _sFChevronUpChevronDown: ImageVector? = null
