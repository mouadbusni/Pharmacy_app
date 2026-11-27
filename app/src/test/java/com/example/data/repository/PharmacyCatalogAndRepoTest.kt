package com.example.data.repository

import com.example.data.catalog.PharmacyCatalog
import com.example.data.catalog.SmartPharmacyResponder
import com.example.data.model.PharmacyStockStatus
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.test.runTest
import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertNotNull
import org.junit.Assert.assertTrue
import org.junit.Test

class PharmacyCatalogAndRepoTest {

    @Test
    fun catalog_hasUniqueIdsAndValidCasablancaData() {
        val productIds = PharmacyCatalog.products.map { it.id }
        val pharmacyIds = PharmacyCatalog.pharmacies.map { it.id }
        val categoryIds = PharmacyCatalog.categories.map { it.id }

        assertTrue(PharmacyCatalog.products.isNotEmpty())
        assertTrue(PharmacyCatalog.pharmacies.isNotEmpty())
        assertEquals(productIds.size, productIds.toSet().size)
        assertEquals(pharmacyIds.size, pharmacyIds.toSet().size)
        assertEquals(categoryIds.size, categoryIds.toSet().size)
        assertEquals(20.0, PharmacyCatalog.DELIVERY_FEE_MAD, 0.001)
    }

    @Test
    fun catalog_appliesReal20PercentVitaminDiscountsAndOrderSummaries() {
        val vitac = PharmacyCatalog.products.first { it.id == "prod_vitac" }
        val magneb6 = PharmacyCatalog.products.first { it.id == "prod_magneb6" }
        assertTrue(vitac.hasDiscount)
        assertEquals(20, vitac.discountPercent)
        assertEquals(45.0, vitac.originalPriceUsd ?: 0.0, 0.01)
        assertEquals(36.0, vitac.priceUsd, 0.01)
        assertTrue(magneb6.hasDiscount)
        assertEquals(20, magneb6.discountPercent)
        assertEquals(75.0, magneb6.originalPriceUsd ?: 0.0, 0.01)
        assertEquals(60.0, magneb6.priceUsd, 0.01)

        val closedPharmacy = PharmacyCatalog.pharmacies.first { !it.openNow }
        assertEquals(PharmacyStockStatus.RESTOCKING, PharmacyCatalog.stockStatusFor(closedPharmacy, vitac))
    }

    @Test
    fun catalog_localizesDeliveryLocationForEnglishAndArabic() {
        assertEquals(
            "Current Location (Casablanca)",
            PharmacyCatalog.localizeLocation("الموقع الحالي", isEnglish = true)
        )
        assertEquals("الموقع الحالي", PharmacyCatalog.localizeLocation("الموقع الحالي", isEnglish = false))
    }

    @Test
    fun smartPharmacyResponder_returnsContextualArabicAndEnglishReplies() {
        val pharm24 = PharmacyCatalog.pharmacies.first { it.isAlways24h }
        val rxReply = SmartPharmacyResponder.buildReply(
            pharmacy = pharm24,
            userText = "photo",
            hasAttachment = true,
            isEnglish = true
        )
        val deliveryReply = SmartPharmacyResponder.buildReply(
            pharmacy = pharm24,
            userText = "كم مدة التوصيل؟",
            hasAttachment = false,
            isEnglish = false
        )
        assertTrue(rxReply.contains("prescription"))
        assertTrue(deliveryReply.contains("توصيل") || deliveryReply.contains("دقيقة"))
    }

    @Test
    fun localAdminRepo_addIncrementAndRemoveCartItems() = runTest {
        val repo = LocalAdminPharmacyRepository()
        val initialCart = repo.observeCart("admin_session").first()
        assertEquals(2, initialCart.size)

        val extraProduct = PharmacyCatalog.products.first { item ->
            initialCart.none { it.productId == item.id }
        }

        repo.addOrIncrementCartItem(extraProduct, 3).getOrThrow()
        val afterAdd = repo.observeCart("admin_session").first()
        assertEquals(3L, afterAdd.first { it.productId == extraProduct.id }.quantity)

        repo.setCartItemQuantity(extraProduct.id, 0).getOrThrow()
        val afterZeroQty = repo.observeCart("admin_session").first()
        assertFalse(afterZeroQty.any { it.productId == extraProduct.id })
    }

    @Test
    fun localAdminRepo_toggleFavoritesUpdatesFlow() = runTest {
        val repo = LocalAdminPharmacyRepository()
        val product = PharmacyCatalog.products.first()

        repo.toggleFavorite(product, currentlyFavorite = false).getOrThrow()
        assertTrue(repo.observeFavorites("admin_session").first().any { it.productId == product.id })

        repo.toggleFavorite(product, currentlyFavorite = true).getOrThrow()
        assertFalse(repo.observeFavorites("admin_session").first().any { it.productId == product.id })
    }

    @Test
    fun localAdminRepo_placeOrderRecordsEnrichedFieldsUpdatesStatusAndParsesItems() = runTest {
        val repo = LocalAdminPharmacyRepository()
        val cart = repo.observeCart("admin_session").first()
        val expectedSubtotal = cart.sumOf { it.priceUsd * it.quantity }

        val emptyResult = repo.placeOrder("cash", emptyList(), "الموقع الحالي", null)
        assertTrue(emptyResult.isFailure)

        repo.placeOrder(
            paymentMethod = "visa",
            cartItems = cart,
            deliveryLocation = "الموقع الحالي",
            cardLast4 = "4242",
            deliveryAddress = "14 Rue Allal Ben Abdellah",
            contactPhone = "0612345678",
            hasPrescription = true,
            prescriptionAttached = true
        ).getOrThrow()

        val orders = repo.observeOrders("admin_session").first()
        assertEquals(1, orders.size)
        val created = orders.first()
        assertEquals(expectedSubtotal + PharmacyCatalog.DELIVERY_FEE_MAD, created.totalUsd, 0.01)
        assertEquals("14 Rue Allal Ben Abdellah", created.deliveryAddress)
        assertEquals("0612345678", created.contactPhone)
        assertTrue(created.hasPrescription)
        assertTrue(created.prescriptionAttached)
        assertNotNull(created.itemsSummary)
        assertEquals(cart.size, PharmacyCatalog.parseOrderItemsSummary(created.itemsSummary).size)
        assertTrue(repo.observeCart("admin_session").first().isEmpty())

        repo.updateOrderStatus(created.id, "preparing").getOrThrow()
        assertEquals("preparing", repo.observeOrders("admin_session").first().first().status)
    }

    @Test
    fun localAdminRepo_sendChatMessageSeedsConversationAndPersistsAttachment() = runTest {
        val repo = LocalAdminPharmacyRepository()
        val pharmacyId = PharmacyCatalog.pharmacies.first().id

        repo.sendChatMessage(pharmacyId, "   ").getOrThrow()
        assertTrue(repo.observeChatMessages("admin_session", pharmacyId).first().isEmpty())

        repo.sendChatMessage(
            pharmacyId = pharmacyId,
            text = "تم إرفاق صورة الوصفة الطبية",
            attachmentUri = "content://media/picker/123",
            attachmentLabel = "prescription_photo.jpg",
            senderType = "USER"
        ).getOrThrow()
        val messages = repo.observeChatMessages("admin_session", pharmacyId).first()
        assertTrue(messages.size >= 2)
        assertEquals("USER", messages.last().senderType)
        assertEquals("content://media/picker/123", messages.last().attachmentUri)
        assertEquals("prescription_photo.jpg", messages.last().attachmentLabel)
    }
}

