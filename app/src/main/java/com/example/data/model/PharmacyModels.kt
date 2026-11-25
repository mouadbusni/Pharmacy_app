package com.example.data.model

import androidx.annotation.Keep
import com.google.firebase.Timestamp
import com.google.firebase.firestore.DocumentId
import com.google.firebase.firestore.IgnoreExtraProperties
import com.google.firebase.firestore.ServerTimestamp

enum class PackagingStyle {
    BIOSTERON_PHOTO,
    DETOCYN_GREEN,
    UTORINIC_TEAL,
    ASPIRINA_MINT,
    TRIBETRON_RED,
    NUTIVA_LIME,
    FLEX_NAVY,
    PANADOL_BLUE
}

data class PharmacyCategory(
    val id: String,
    val titleAr: String,
    val titleEn: String,
    val accentColorHex: Long
)

data class Product(
    val id: String,
    val nameAr: String,
    val subtitleAr: String,
    val descriptionAr: String,
    val categoryId: String,
    val categoryAr: String,
    val priceUsd: Double,
    val rating: Double,
    val reviewCount: Int,
    val deliveryTimeAr: String,
    val dosageMorningEvening: String,
    val dosageMealTiming: String,
    val isBestSeller: Boolean,
    val packagingStyle: PackagingStyle
)

data class Pharmacy(
    val id: String,
    val nameAr: String,
    val areaAr: String,
    val distanceAr: String,
    val rating: Double,
    val deliveryTimeAr: String,
    val openNow: Boolean,
    val initials: String,
    val accentColorHex: Long,
    val phoneNumber: String,
    val lastMessagePreview: String,
    val lastMessageTime: String
)

data class AppNotification(
    val id: String,
    val titleAr: String,
    val bodyAr: String,
    val timeAr: String,
    val unread: Boolean
)

@Keep
@IgnoreExtraProperties
data class UserProfile(
    @DocumentId val id: String = "",
    val userId: String = "",
    val language: String = "ar",
    val notificationsEnabled: Boolean = true,
    val selectedLocation: String = "الموقع الحالي",
    @ServerTimestamp val createdAt: Timestamp? = null,
    @ServerTimestamp val updatedAt: Timestamp? = null
)

@Keep
@IgnoreExtraProperties
data class CartItem(
    @DocumentId val id: String = "",
    val userId: String = "",
    val productId: String = "",
    val productName: String = "",
    val category: String = "",
    val priceUsd: Double = 0.0,
    val quantity: Long = 1L,
    val rating: Double = 4.5,
    @ServerTimestamp val createdAt: Timestamp? = null,
    @ServerTimestamp val updatedAt: Timestamp? = null
)

@Keep
@IgnoreExtraProperties
data class FavoriteItem(
    @DocumentId val id: String = "",
    val userId: String = "",
    val productId: String = "",
    val productName: String = "",
    @ServerTimestamp val createdAt: Timestamp? = null
)

@Keep
@IgnoreExtraProperties
data class PharmacyOrder(
    @DocumentId val id: String = "",
    val userId: String = "",
    val paymentMethod: String = "visa",
    val subtotalUsd: Double = 0.0,
    val deliveryUsd: Double = 5.0,
    val totalUsd: Double = 0.0,
    val itemCount: Long = 1L,
    val status: String = "confirmed",
    val deliveryLocation: String = "الموقع الحالي",
    val cardLast4: String? = null,
    @ServerTimestamp val createdAt: Timestamp? = null,
    @ServerTimestamp val updatedAt: Timestamp? = null
)

@Keep
@IgnoreExtraProperties
data class RecentSearch(
    @DocumentId val id: String = "",
    val userId: String = "",
    val query: String = "",
    @ServerTimestamp val createdAt: Timestamp? = null
)

@Keep
@IgnoreExtraProperties
data class ChatMessage(
    @DocumentId val id: String = "",
    val userId: String = "",
    val pharmacyId: String = "",
    val senderType: String = "USER",
    val text: String = "",
    @ServerTimestamp val createdAt: Timestamp? = null
)
