package com.name.petmemo.ui.theme

import androidx.compose.material3.Typography
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.sp
import androidx.compose.ui.text.PlatformTextStyle
import androidx.compose.ui.text.EmojiSupportMatch

val DisableEmojiPlatformStyle = PlatformTextStyle(
    emojiSupportMatch = EmojiSupportMatch.None
)
val Typography = Typography(
    bodyLarge = TextStyle(
        platformStyle = DisableEmojiPlatformStyle,
        fontFamily = FontFamily.Default,
        fontWeight = FontWeight.Normal,
        fontSize = 15.sp,
        lineHeight = 20.sp,
        letterSpacing = 0.5.sp
    ),
    titleLarge = TextStyle(
        platformStyle = DisableEmojiPlatformStyle,
        fontFamily = FontFamily.Default,
        fontWeight = FontWeight.Bold,
        fontSize = 20.sp,
        lineHeight = 24.sp,
        letterSpacing = 0.sp
    ),
    titleMedium = TextStyle(
        platformStyle = DisableEmojiPlatformStyle,
        fontFamily = FontFamily.Default,
        fontWeight = FontWeight.Bold,
        fontSize = 17.sp,
        lineHeight = 20.sp,
        letterSpacing = 0.15.sp
    ),
    headlineSmall = TextStyle(
        platformStyle = DisableEmojiPlatformStyle,
        fontFamily = FontFamily.Default,
        fontWeight = FontWeight.Bold,
        fontSize = 24.sp,
        lineHeight = 30.sp,
        letterSpacing = 0.sp
    ),
    labelSmall = TextStyle(
        platformStyle = DisableEmojiPlatformStyle,
        fontFamily = FontFamily.Default,
        fontWeight = FontWeight.Medium,
        fontSize = 11.sp,
        lineHeight = 16.sp,
        letterSpacing = 0.5.sp
    )
)