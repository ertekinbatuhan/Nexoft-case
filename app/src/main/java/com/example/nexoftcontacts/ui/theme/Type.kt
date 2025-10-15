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
    
    // "New Contact" başlığı (20sp, W800)
    headlineLarge = TextStyle(
        fontFamily = FontFamily.Default,
        fontWeight = FontWeight.W800,
        fontSize = 20.sp,
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
    
    // "Create New Contact" butonu ve Camera/Gallery button text (16sp, W600, TextPrimary)
    labelLarge = TextStyle(
        fontFamily = FontFamily.Default,
        fontWeight = FontWeight.W600,
        fontSize = 16.sp,
        color = TextPrimary
    ),
    
    // "Search by name" placeholder (14sp, W600)
    labelMedium = TextStyle(
        fontFamily = FontFamily.Default,
        fontWeight = FontWeight.W600,
        fontSize = 14.sp,
        color = TextPlaceholder
    ),
    
    // TextField placeholder "First Name", "Last Name" (14sp, W600, TextFieldPlaceholder)
    displayLarge = TextStyle(
        fontFamily = FontFamily.Default,
        fontWeight = FontWeight.W600,
        fontSize = 14.sp,
        color = TextFieldPlaceholder
    ),
    
    // Dialog button text "No", "Yes" (16sp, SemiBold)
    labelSmall = TextStyle(
        fontFamily = FontFamily.Default,
        fontWeight = FontWeight.SemiBold,
        fontSize = 16.sp
    ),
    
    // "Cancel" button text (16sp, Medium, Primary)
    displaySmall = TextStyle(
        fontFamily = FontFamily.Default,
        fontWeight = FontWeight.Medium,
        fontSize = 16.sp,
        color = Primary
    ),
    
    // "Done" button text (16sp, Bold, Primary)
    displayMedium = TextStyle(
        fontFamily = FontFamily.Default,
        fontWeight = FontWeight.Bold,
        fontSize = 16.sp,
        color = Primary
    )
)

// Custom text styles for dropdown menu items
object DropdownTextStyles {
    // Dropdown menu items "Edit", "Delete" (14sp, Medium)
    val menuItem = TextStyle(
        fontFamily = FontFamily.Default,
        fontWeight = FontWeight.Medium,
        fontSize = 14.sp
    )
}

// Custom text styles for other components
object CustomTextStyles {
    // "Edit Contact" başlığı (20sp, Bold/W700, TextPrimary)
    val editContactTitle = TextStyle(
        fontFamily = FontFamily.Default,
        fontWeight = FontWeight.Bold,
        fontSize = 20.sp,
        color = TextPrimary
    )
    
    // "Change Photo" button text (16sp, Bold, Primary)
    val changePhotoButton = TextStyle(
        fontFamily = FontFamily.Default,
        fontWeight = FontWeight.Bold,
        fontSize = 16.sp,
        color = Primary
    )
    
    // "Save to My Phone Contact" button text (16sp, SemiBold)
    val saveButton = TextStyle(
        fontFamily = FontFamily.Default,
        fontWeight = FontWeight.SemiBold,
        fontSize = 16.sp
    )
    
    // Info message text (12sp, Medium, TextMuted)
    val infoMessage = TextStyle(
        fontFamily = FontFamily.Default,
        fontWeight = FontWeight.Medium,
        fontSize = 12.sp,
        color = TextMuted
    )
    
    // Success message text (14sp, Bold, Success)
    val successMessage = TextStyle(
        fontFamily = FontFamily.Default,
        fontWeight = FontWeight.Bold,
        fontSize = 14.sp,
        color = Success
    )
}
