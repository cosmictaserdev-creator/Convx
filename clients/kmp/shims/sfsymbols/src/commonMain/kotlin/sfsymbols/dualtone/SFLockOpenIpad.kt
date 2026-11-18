package com.composables.sfsymbols.dualtone

import androidx.compose.ui.graphics.vector.ImageVector
import com.composables.sfsymbols.SfSymbols
import com.composables.sfsymbols.addSfPath
import com.composables.sfsymbols.sfIcon

/**
 * SF Symbol: SFLockOpenIpad (dualtone)
 * Viewport: 21.084 x 27.998
 */
public val SfSymbols.Dualtone.SFLockOpenIpad: ImageVector
    get() {
        if (_sFLockOpenIpad != null) {
            return _sFLockOpenIpad!!
        }
        _sFLockOpenIpad = sfIcon(
            name = "Dualtone.SFLockOpenIpad",
            viewportWidth = 21.084f,
            viewportHeight = 27.998f
        ) {
            addSfPath("M0 24.6777C0 26.6992 1.34766 27.998 3.4375 27.998L17.2852 27.998C19.375 27.998 20.7227 26.6992 20.7227 24.6777L20.7227 3.32031C20.7227 1.29883 19.375 0 17.2852 0L3.4375 0C1.34766 0 0 1.29883 0 3.32031ZM1.72852 24.4043L1.72852 3.59375C1.72852 2.40234 2.42188 1.72852 3.64258 1.72852L17.0703 1.72852C18.291 1.72852 18.9844 2.40234 18.9844 3.59375L18.9844 24.4043C18.9844 25.5859 18.291 26.2598 17.0703 26.2598L3.64258 26.2598C2.42188 26.2598 1.72852 25.5859 1.72852 24.4043Z", fillAlpha = 0.425f)
            addSfPath("M5.88867 17.1777C5.88867 17.8809 6.2207 18.2324 6.89453 18.2324L11.1621 18.2324C11.8457 18.2324 12.1777 17.8809 12.1777 17.1777L12.1777 13.8965C12.1777 13.1836 11.8457 12.832 11.1621 12.832L6.89453 12.832C6.2207 12.832 5.88867 13.1836 5.88867 13.8965ZM10.2539 13.4473L11.3867 13.4473L11.3867 11.7773C11.3867 10.8008 11.9141 10.1758 12.8027 10.1758C13.6914 10.1758 14.2188 10.8008 14.2188 11.7773L14.2188 12.5977C14.2188 12.9102 14.4727 13.1738 14.7852 13.1738C15.0879 13.1738 15.3516 12.9102 15.3516 12.5977L15.3516 11.7773C15.3516 10.2051 14.3359 9.07227 12.8027 9.07227C11.2598 9.07227 10.2539 10.2051 10.2539 11.8652Z", fillAlpha = 0.85f)
        }
        return _sFLockOpenIpad!!
    }

private var _sFLockOpenIpad: ImageVector? = null
