package com.example.nexoftcontacts.ui.theme

import androidx.compose.material3.Typography
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.sp

// Material3 Typography set customized with app styles
val Typography = Typography(
    // "Contacts" başlığı (24sp, W800)
    titleLarge = TextStyle(
        fontFamily = FontFamily.Default,
        fontWeight = FontWeight.W800,
        fontSize = 24.sp,
        color = TextPrimary
    ),
    
    // "No Contacts" başlığı (20sp, SemiBold)
    titleMedium = TextStyle(
        fontFamily = FontFamily.Default,
        fontWeight = FontWeight.SemiBold,
        fontSize = 20.sp,
        color = TextDark
    ),
    
    // Bölüm harfleri "A", "B" (14sp, W600, TextPlaceholder)
    titleSmall = TextStyle(
        fontFamily = FontFamily.Default,
        fontWeight = FontWeight.W600,
        fontSize = 14.sp,
        color = TextPlaceholder
    ),
    
    // Kişi isimleri - Liste (14sp, Bold)
    bodyLarge = TextStyle(
        fontFamily = FontFamily.Default,
        fontWeight = FontWeight.Bold,
        fontSize = 14.sp,
        color = TextPrimary
    ),
    
    // Telefon numaraları - Liste (12sp, Medium)
    bodyMedium = TextStyle(
        fontFamily = FontFamily.Default,
        fontWeight = FontWeight.Medium,
        fontSize = 12.sp,
        color = TextMuted
    ),
    
    // "TOP NAME MATCHES" header (14sp, W600, TextPlaceholder)
    bodySmall = TextStyle(
        fontFamily = FontFamily.Default,
        fontWeight = FontWeight.W600,
        fontSize = 14.sp,
        letterSpacing = 0.5.sp,
        color = TextPlaceholder
    ),
    
    // Dialog başlıkları "Delete Contact" (20sp, Bold)
    headlineSmall = TextStyle(
        fontFamily = FontFamily.Default,
        fontWeight = FontWeight.Bold,
        fontSize = 20.sp,
        color = TextSecondary
    ),
    
    // Dialog açıklama metinleri "Are you sure..." (14sp, Medium)
    headlineMedium = TextStyle(
        fontFamily = FontFamily.Default,
        fontWeight = FontWeight.Medium,
        fontSize = 14.sp,
        color = TextSecondary
    ),
    
    // "Create New Contact" butonu (16sp, Medium)
    labelLarge = TextStyle(
        fontFamily = FontFamily.Default,
        fontWeight = FontWeight.Medium,
        fontSize = 16.sp,
        color = Primary
    ),
    
    // "Search by name" placeholder (14sp, W600)
    labelMedium = TextStyle(
        fontFamily = FontFamily.Default,
        fontWeight = FontWeight.W600,
        fontSize = 14.sp,
        color = TextPlaceholder
    ),
    
    // Dialog button text "No", "Yes" (16sp, SemiBold)
    labelSmall = TextStyle(
        fontFamily = FontFamily.Default,
        fontWeight = FontWeight.SemiBold,
        fontSize = 16.sp
    )
)
