package com.example.data.catalog

import com.example.data.model.AppNotification
import com.example.data.model.CartItem
import com.example.data.model.Pharmacy
import com.example.data.model.PharmacyCategory
import com.example.data.model.PharmacyOrder
import com.example.data.model.PharmacyStockStatus
import com.example.data.model.Product
import kotlin.math.abs

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

    fun stockStatusFor(pharmacy: Pharmacy, product: Product): PharmacyStockStatus {
        if (!pharmacy.openNow) return PharmacyStockStatus.RESTOCKING
        if (product.isBestSeller && pharmacy.isAlways24h) return PharmacyStockStatus.IN_STOCK
        val bucket = abs((pharmacy.id.hashCode() * 31) + product.id.hashCode()) % 7
        return when {
            bucket == 0 && product.requiresPrescription -> PharmacyStockStatus.RESTOCKING
            bucket <= 2 -> PharmacyStockStatus.LOW_STOCK
            else -> PharmacyStockStatus.IN_STOCK
        }
    }

    fun formatOrderItemsSummary(cartItems: List<CartItem>): String {
        return cartItems
            .filter { it.productId.isNotBlank() }
            .joinToString(separator = ";") { "${it.productId}:${it.quantity.coerceIn(1L, 99L)}" }
            .take(400)
    }

    fun parseOrderItemsSummary(summary: String?): List<Pair<Product, Int>> {
        if (summary.isNullOrBlank()) return emptyList()
        return summary.split(";")
            .mapNotNull { entry ->
                val parts = entry.split(":")
                val prodId = parts.firstOrNull()?.trim().orEmpty()
                val qty = parts.getOrNull(1)?.trim()?.toIntOrNull() ?: 1
                val prod = products.firstOrNull { it.id == prodId } ?: return@mapNotNull null
                prod to qty.coerceIn(1, 99)
            }
    }

    fun formatLocalizedOrderSummary(
        order: PharmacyOrder,
        isEnglish: Boolean,
        itemsWord: String
    ): String {
        val parsed = parseOrderItemsSummary(order.itemsSummary)
        if (parsed.isEmpty()) return "${order.itemCount} $itemsWord"
        return parsed.joinToString(separator = " • ") { (prod, qty) ->
            "${prod.localizedName(isEnglish)} ×$qty"
        }
    }
}
