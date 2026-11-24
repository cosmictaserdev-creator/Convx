package com.convx.music.designsystem

import androidx.compose.runtime.Composable
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import com.convx.music.designsystem.resources.Res
import com.convx.music.designsystem.resources.sf_pro_text_regular
import com.convx.music.designsystem.resources.sf_pro_text_medium
import com.convx.music.designsystem.resources.sf_pro_text_semibold
import com.convx.music.designsystem.resources.sf_pro_text_bold
import com.convx.music.designsystem.resources.chillax_semibold
import org.jetbrains.compose.resources.Font

/** The same font assets and weight mapping used by the production Android client. */
@Composable
fun convxFontFamily(): FontFamily = FontFamily(
    Font(Res.font.sf_pro_text_regular, FontWeight.Normal),
    Font(Res.font.sf_pro_text_medium, FontWeight.Medium),
    Font(Res.font.sf_pro_text_semibold, FontWeight.SemiBold),
    Font(Res.font.sf_pro_text_bold, FontWeight.Bold),
)

@Composable
fun convxWordmarkFontFamily(): FontFamily = FontFamily(Font(Res.font.chillax_semibold, FontWeight.SemiBold))
