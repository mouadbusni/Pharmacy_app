package com.example.data.model

import androidx.annotation.Keep
import com.google.firebase.Timestamp
import com.google.firebase.firestore.DocumentId
import com.google.firebase.firestore.IgnoreExtraProperties
import com.google.firebase.firestore.ServerTimestamp

enum class PackagingStyle {
    DOLIPRANE_CRIMSON,
    AUGMENTIN_TEAL,
    DOLIGRIPPE_BLUE,
    GLUCOPHAGE_PURPLE,
    VENTOLINE_SKY,
    VITAC_CITRUS,
    VOLTAREN_AMBER,
    LEVOTHYROX_ROSE,
    LOSEC_INDIGO,
    AUREOMYCINE_EMERALD
}

enum class PharmacySchedule {
    ALWAYS_24H,
    DAY_DUTY
}

enum class PharmacyScheduleFilter {
    ALL,
    ALWAYS_24H,
    DAY_DUTY
}

enum class PrescriptionFilter {
    ALL,
    OTC,
    RX_REQUIRED
}

enum class NotificationTarget {
    PROMO_VITAMINS,
    CHATS,
    PHARMACIES
}

data class PharmacyCategory(
    val id: String,
    val titleAr: String,
    val titleEn: String,
    val accentColorHex: Long
) {
    fun localizedTitle(isEnglish: Boolean): String = if (isEnglish) titleEn else titleAr
}

data class Product(
    val id: String,
    val nameAr: String,
    val nameEn: String,
    val subtitleAr: String,
    val subtitleEn: String,
    val descriptionAr: String,
    val descriptionEn: String,
    val categoryId: String,
    val categoryAr: String,
    val categoryEn: String,
    val priceUsd: Double,
    val rating: Double,
    val reviewCount: Int,
    val deliveryTimeAr: String,
    val deliveryTimeEn: String,
    val dosageMorningEvening: String,
    val dosageMorningEveningEn: String,
    val dosageMealTiming: String,
    val dosageMealTimingEn: String,
    val requiresPrescription: Boolean,
    val safetyNoteAr: String,
    val safetyNoteEn: String,
    val isBestSeller: Boolean,
    val packagingStyle: PackagingStyle
) {
    fun localizedName(isEnglish: Boolean): String = if (isEnglish) nameEn else nameAr
    fun localizedSubtitle(isEnglish: Boolean): String = if (isEnglish) subtitleEn else subtitleAr
    fun localizedDescription(isEnglish: Boolean): String = if (isEnglish) descriptionEn else descriptionAr
    fun localizedCategory(isEnglish: Boolean): String = if (isEnglish) categoryEn else categoryAr
    fun localizedDeliveryTime(isEnglish: Boolean): String = if (isEnglish) deliveryTimeEn else deliveryTimeAr
    fun localizedDosageTime(isEnglish: Boolean): String = if (isEnglish) dosageMorningEveningEn else dosageMorningEvening
    fun localizedMealTiming(isEnglish: Boolean): String = if (isEnglish) dosageMealTimingEn else dosageMealTiming
    fun localizedSafetyNote(isEnglish: Boolean): String = if (isEnglish) safetyNoteEn else safetyNoteAr
}

data class Pharmacy(
    val id: String,
    val nameAr: String,
    val nameEn: String,
    val areaAr: String,
    val areaEn: String,
    val addressAr: String,
    val addressEn: String,
    val distanceAr: String,
    val distanceEn: String,
    val rating: Double,
    val deliveryTimeAr: String,
    val deliveryTimeEn: String,
    val openNow: Boolean,
    val schedule: PharmacySchedule,
    val initials: String,
    val accentColorHex: Long,
    val phoneNumber: String,
    val lastMessagePreview: String,
    val lastMessagePreviewEn: String,
    val lastMessageTime: String
) {
    val isAlways24h: Boolean get() = schedule == PharmacySchedule.ALWAYS_24H
    fun localizedName(isEnglish: Boolean): String = if (isEnglish) nameEn else nameAr
    fun localizedArea(isEnglish: Boolean): String = if (isEnglish) areaEn else areaAr
    fun localizedAddress(isEnglish: Boolean): String = if (isEnglish) addressEn else addressAr
    fun localizedDistance(isEnglish: Boolean): String = if (isEnglish) distanceEn else distanceAr
    fun localizedDeliveryTime(isEnglish: Boolean): String = if (isEnglish) deliveryTimeEn else deliveryTimeAr
}

data class AppNotification(
    val id: String,
    val titleAr: String,
    val titleEn: String,
    val bodyAr: String,
    val bodyEn: String,
    val timeAr: String,
    val timeEn: String,
    val unread: Boolean,
    val target: NotificationTarget
) {
    fun localizedTitle(isEnglish: Boolean): String = if (isEnglish) titleEn else titleAr
    fun localizedBody(isEnglish: Boolean): String = if (isEnglish) bodyEn else bodyAr
    fun localizedTime(isEnglish: Boolean): String = if (isEnglish) timeEn else timeAr
}

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
    val deliveryUsd: Double = 20.0,
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
