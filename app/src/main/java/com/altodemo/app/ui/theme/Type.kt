package com.altodemo.app.ui.theme

import androidx.compose.material3.Typography
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.Font
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.sp
import com.altodemo.R

// pxgrotesk Screen font family not appeared to be used in the comps
private val pxgroteskScreenFontFamily = FontFamily(
    Font(R.font.pxgrotesk_screen, FontWeight.Normal),
)
 val pxgroteskFontFamily = FontFamily(
    // thin
    // extra light
    // light
    Font(R.font.pxgrotesk_light, FontWeight.Light),
    Font(R.font.pxgrotesk_light_ita, FontWeight.Light, FontStyle.Italic),
    // normal
    Font(R.font.pxgrotesk_regular, FontWeight.Normal),
    Font(R.font.pxgrotesk_regular_ita, FontWeight.Normal, FontStyle.Italic),
    // medium
    // semi bold
    // bold
    Font(R.font.pxg_rotesk_bold, FontWeight.Bold),
    Font(R.font.pxgrotesk_bold_ita, FontWeight.Bold, FontStyle.Italic),
    // extra bold
    // black
)

 val linotypeFontFamily = FontFamily(
    // thin
    // extra light
    // light
    // normal
    Font(R.font.linotype_optimaltstd, FontWeight.Normal),
    Font(R.font.linotype_optimaltstd_italic, FontWeight.Normal, FontStyle.Italic),
    // medium
    Font(R.font.linotype_optimaltstd_medium, FontWeight.Medium),
    Font(R.font.linotype_optimaltstd_medium_italic, FontWeight.Medium, FontStyle.Italic),
    // semi bold
    // bold
    Font(R.font.linotype_optional_ltsd_bold, FontWeight.Bold),
    Font(R.font.linotype_optimaltstd_bold_italic, FontWeight.Bold, FontStyle.Italic),
    // extra bold
    Font(R.font.linotype_optimaltstd_black, FontWeight.Black),
    Font(R.font.linotype_optimaltstd_black_italic, FontWeight.Black, FontStyle.Italic),
    // black
    Font(R.font.linotype_optimaltstd_extra_black, FontWeight.ExtraBold),
    Font(R.font.linotype_optimaltstd_xblack_italic, FontWeight.ExtraBold, FontStyle.Italic),

    )

// Set of Material typography styles to start with
val AltoTypography = Typography(
    bodyLarge = TextStyle(
        fontFamily = FontFamily.Default,
        fontWeight = FontWeight.Normal,
        fontSize = 16.sp,
        lineHeight = 24.sp,
        letterSpacing = 0.5.sp,
    ),
    titleLarge = TextStyle(
        fontFamily = linotypeFontFamily,
        fontWeight = FontWeight.Medium,
        fontSize = 52.sp,
        lineHeight = 24.sp,
        letterSpacing = 0.7.sp,
        color = AltoDemoColor.Black90
    ),
    titleMedium = TextStyle(
        fontFamily = linotypeFontFamily,
        fontWeight = FontWeight.Normal,
        fontSize = 36.sp,
        lineHeight = 24.sp,
        color = AltoDemoColor.Black90
    ),
    titleSmall = TextStyle(
        fontFamily = linotypeFontFamily,
        fontWeight = FontWeight.Normal,
        fontSize = 24.sp,
        lineHeight = 24.sp,
        letterSpacing = 1.sp,
        color = AltoDemoColor.Black90
    )
    /* Other default text styles to override
    titleLarge = TextStyle(
        fontFamily = FontFamily.Default,
        fontWeight = FontWeight.Normal,
        fontSize = 22.sp,
        lineHeight = 28.sp,
        letterSpacing = 0.sp
    ),
    labelSmall = TextStyle(
        fontFamily = FontFamily.Default,
        fontWeight = FontWeight.Medium,
        fontSize = 11.sp,
        lineHeight = 16.sp,
        letterSpacing = 0.5.sp
    )
    */
)
