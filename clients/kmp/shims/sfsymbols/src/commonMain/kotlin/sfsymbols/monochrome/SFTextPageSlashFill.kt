package com.composables.sfsymbols.monochrome

import androidx.compose.ui.graphics.vector.ImageVector
import com.composables.sfsymbols.SfSymbols
import com.composables.sfsymbols.addSfPath
import com.composables.sfsymbols.sfIcon

/**
 * SF Symbol: SFTextPageSlashFill (monochrome)
 * Viewport: 28.8306 x 28.4841
 */
public val SfSymbols.Monochrome.SFTextPageSlashFill: ImageVector
    get() {
        if (_sFTextPageSlashFill != null) {
            return _sFTextPageSlashFill!!
        }
        _sFTextPageSlashFill = sfIcon(
            name = "Monochrome.SFTextPageSlashFill",
            viewportWidth = 28.8306f,
            viewportHeight = 28.4841f
        ) {
            addSfPath("M11.8808 15.243L9.24927 15.243C8.83912 15.243 8.53638 15.5555 8.53638 15.9462C8.53638 16.3368 8.83912 16.6395 9.24927 16.6395L13.2779 16.6395L23.5776 26.935C22.9593 27.4472 22.0881 27.704 20.968 27.704L7.52076 27.704C5.02076 27.704 3.76099 26.4247 3.76099 23.9052L3.76099 7.12654ZM24.7278 4.58874L24.7278 21.3491L15.4892 12.118L19.2493 12.118C19.6497 12.118 19.9524 11.8055 19.9524 11.4052C19.9524 11.0243 19.6497 10.7216 19.2493 10.7216L14.0915 10.7216L10.9639 7.59656L19.2493 7.59656C19.6497 7.59656 19.9524 7.28406 19.9524 6.88367C19.9524 6.50281 19.6497 6.20007 19.2493 6.20007L9.5663 6.20007L4.91651 1.55411C5.53473 1.04519 6.404 0.789916 7.52076 0.789916L20.968 0.789916C23.468 0.789916 24.7278 2.06921 24.7278 4.58874Z", fillAlpha = 0.85f)
            addSfPath("M25.4993 26.6786C25.8411 27.0009 26.3586 27.0009 26.6809 26.6786C27.0032 26.3466 27.0032 25.8094 26.6809 25.4872L2.96998 1.79578C2.65748 1.47351 2.13013 1.46374 1.78833 1.79578C1.46607 2.10828 1.46607 2.65515 1.78833 2.96765Z", fillAlpha = 0.85f)
        }
        return _sFTextPageSlashFill!!
    }

private var _sFTextPageSlashFill: ImageVector? = null
