package com.example.data.repository

import com.example.data.catalog.PharmacyCatalog
import com.example.data.model.CartItem
import com.example.data.model.Product
import com.google.firebase.firestore.FieldValue

internal object FirestorePayloadBuilders {

    fun initialUserProfile(uid: String): Map<String, Any> = mapOf(
        "userId" to uid,
        "language" to "ar",
        "notificationsEnabled" to true,
        "selectedLocation" to "الموقع الحالي",
        "createdAt" to FieldValue.serverTimestamp(),
        "updatedAt" to FieldValue.serverTimestamp()
    )

    fun cartItemCreatePayload(uid: String, product: Product, quantity: Long): Map<String, Any> = mapOf(
        "userId" to uid,
        "productId" to product.id,
        "productName" to product.nameAr.take(120),
        "category" to product.categoryAr.take(60),
        "priceUsd" to product.priceUsd,
        "quantity" to quantity,
        "rating" to product.rating,
        "createdAt" to FieldValue.serverTimestamp(),
        "updatedAt" to FieldValue.serverTimestamp()
    )

    fun cartItemIncrementPayload(product: Product, nextQty: Long): Map<String, Any> = mapOf(
        "quantity" to nextQty,
        "priceUsd" to product.priceUsd,
        "productName" to product.nameAr.take(120),
        "category" to product.categoryAr.take(60),
        "rating" to product.rating,
        "updatedAt" to FieldValue.serverTimestamp()
    )

    fun orderCreatePayload(
        uid: String,
        paymentMethod: String,
        cartItems: List<CartItem>,
        deliveryLocation: String,
        cardLast4: String?
    ): Map<String, Any> {
        val subtotal = cartItems.sumOf { it.priceUsd * it.quantity }
        val deliveryFee = PharmacyCatalog.DELIVERY_FEE_MAD
        val total = subtotal + deliveryFee
        val itemCount = cartItems.sumOf { it.quantity }.coerceIn(1L, 500L)
        val cleanLoc = deliveryLocation.trim().take(160).ifBlank { "الموقع الحالي" }

        return mapOf(
            "userId" to uid,
            "paymentMethod" to paymentMethod,
            "subtotalUsd" to subtotal,
            "deliveryUsd" to deliveryFee,
            "totalUsd" to total,
            "itemCount" to itemCount,
            "status" to "confirmed",
            "deliveryLocation" to cleanLoc,
            "cardLast4" to cardLast4?.take(4)?.takeIf { it.isNotBlank() && paymentMethod != "cash" },
            "createdAt" to FieldValue.serverTimestamp(),
            "updatedAt" to FieldValue.serverTimestamp()
        ).filterValues { it != null } as Map<String, Any>
    }

    fun chatMessagePayload(
        uid: String,
        pharmacyId: String,
        senderType: String,
        text: String
    ): Map<String, Any> = mapOf(
        "userId" to uid,
        "pharmacyId" to pharmacyId,
        "senderType" to senderType,
        "text" to text,
        "createdAt" to FieldValue.serverTimestamp()
    )
}
