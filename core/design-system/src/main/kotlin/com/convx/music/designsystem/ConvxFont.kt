package com.convx.music.designsystem

import androidx.compose.runtime.Composable
import androidx.compose.ui.text.font.Font
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import com.convx.shared.design.system.R

/** The same font assets and weight mapping used by the production Android client. */
@Composable
fun convxFontFamily(): FontFamily = FontFamily(
    Font(R.font.sf_pro_text_regular, FontWeight.Normal),
    Font(R.font.sf_pro_text_medium, FontWeight.Medium),
    Font(R.font.sf_pro_text_semibold, FontWeight.SemiBold),
    Font(R.font.sf_pro_text_bold, FontWeight.Bold),
)

@Composable
fun convxWordmarkFontFamily(): FontFamily = FontFamily(Font(R.font.chillax_semibold, FontWeight.SemiBold))
