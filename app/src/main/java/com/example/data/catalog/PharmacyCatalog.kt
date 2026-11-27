package com.example.data.catalog

import com.example.data.model.AppNotification
import com.example.data.model.Pharmacy
import com.example.data.model.PharmacyCategory
import com.example.data.model.Product

object PharmacyCatalog {

    const val DELIVERY_FEE_MAD = 20.0

    val deliveryLocations: List<String> = casablancaDeliveryLocations

    fun localizeLocation(locationAr: String, isEnglish: Boolean): String =
        if (isEnglish) casablancaLocationEnMap[locationAr] ?: locationAr else locationAr

    val categories: List<PharmacyCategory> = medicationCategories

    val products: List<Product> = medicationProducts

    val pharmacies: List<Pharmacy> = casablancaPharmacies

    val defaultRecentSearchChips: List<String> = defaultSearchChips

    val sampleNotifications: List<AppNotification> = sampleNotificationsList

    fun seedConversationForPharmacy(pharmacyId: String): List<Pair<String, String>> =
        buildSeedConversationForPharmacy(pharmacyId)
}
