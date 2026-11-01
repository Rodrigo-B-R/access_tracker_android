package com.example.access_tracker.ui.theme

import androidx.compose.material3.Typography
import androidx.compose.ui.text.ExperimentalTextApi
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.Font
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontVariation
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.em
import androidx.compose.ui.unit.sp
import com.example.access_tracker.R

// Bricolage Grotesque para títulos, números y etiquetas. DM Sans para texto corrido.
@OptIn(ExperimentalTextApi::class)
private fun variable(res: Int, peso: FontWeight) = Font(
    resId = res,
    weight = peso,
    variationSettings = FontVariation.Settings(FontVariation.weight(peso.weight)),
)

val Bricolage = FontFamily(
    variable(R.font.bricolage_grotesque, FontWeight.SemiBold),
    variable(R.font.bricolage_grotesque, FontWeight.Bold),
    variable(R.font.bricolage_grotesque, FontWeight.ExtraBold),
)

val DmSans = FontFamily(
    variable(R.font.dm_sans, FontWeight.Normal),
    variable(R.font.dm_sans, FontWeight.Medium),
    variable(R.font.dm_sans, FontWeight.SemiBold),
)

// Estilos de texto del Figma (Fundamentos → Tipografía)
object Texto {
    val DisplayXL = TextStyle(fontFamily = Bricolage, fontWeight = FontWeight.ExtraBold, fontSize = 76.sp, lineHeight = 73.sp, letterSpacing = (-0.04).em)
    val DisplayL = TextStyle(fontFamily = Bricolage, fontWeight = FontWeight.ExtraBold, fontSize = 44.sp, lineHeight = 44.sp, letterSpacing = (-0.03).em)
    val TitleL = TextStyle(fontFamily = Bricolage, fontWeight = FontWeight.ExtraBold, fontSize = 30.sp, lineHeight = 32.sp, letterSpacing = (-0.02).em)
    val TitleM = TextStyle(fontFamily = Bricolage, fontWeight = FontWeight.Bold, fontSize = 22.sp, lineHeight = 26.sp, letterSpacing = (-0.01).em)
    val TitleS = TextStyle(fontFamily = Bricolage, fontWeight = FontWeight.Bold, fontSize = 18.sp, lineHeight = 22.sp, letterSpacing = (-0.005).em)
    val LabelL = TextStyle(fontFamily = Bricolage, fontWeight = FontWeight.SemiBold, fontSize = 16.sp, lineHeight = 20.sp)
    val LabelM = TextStyle(fontFamily = Bricolage, fontWeight = FontWeight.SemiBold, fontSize = 14.sp, lineHeight = 17.sp)
    val LabelS = TextStyle(fontFamily = Bricolage, fontWeight = FontWeight.SemiBold, fontSize = 12.sp, lineHeight = 15.sp)
    val BodyL = TextStyle(fontFamily = DmSans, fontWeight = FontWeight.Normal, fontSize = 16.sp, lineHeight = 23.sp)
    val BodyM = TextStyle(fontFamily = DmSans, fontWeight = FontWeight.Normal, fontSize = 14.sp, lineHeight = 20.sp)
    val BodyMMedium = TextStyle(fontFamily = DmSans, fontWeight = FontWeight.Medium, fontSize = 14.sp, lineHeight = 20.sp)
    val BodyS = TextStyle(fontFamily = DmSans, fontWeight = FontWeight.Normal, fontSize = 12.sp, lineHeight = 17.sp)
    val Eyebrow = TextStyle(fontFamily = DmSans, fontWeight = FontWeight.SemiBold, fontSize = 12.sp, lineHeight = 14.sp, letterSpacing = 0.08.em)
}

val Typography = Typography(
    displayLarge = Texto.DisplayL,
    titleLarge = Texto.TitleL,
    titleMedium = Texto.TitleM,
    titleSmall = Texto.TitleS,
    labelLarge = Texto.LabelL,
    labelMedium = Texto.LabelM,
    labelSmall = Texto.LabelS,
    bodyLarge = Texto.BodyL,
    bodyMedium = Texto.BodyM,
    bodySmall = Texto.BodyS,
)
