package com.example.data.repository

import com.example.data.catalog.PharmacyCatalog
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.test.runTest
import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
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
    fun catalog_localizesDeliveryLocationForEnglishAndArabic() {
        assertEquals(
            "Current Location (Casablanca)",
            PharmacyCatalog.localizeLocation("الموقع الحالي", isEnglish = true)
        )
        assertEquals("الموقع الحالي", PharmacyCatalog.localizeLocation("الموقع الحالي", isEnglish = false))
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
    fun localAdminRepo_placeOrderRecordsTotalWithDeliveryAndClearsCart() = runTest {
        val repo = LocalAdminPharmacyRepository()
        val cart = repo.observeCart("admin_session").first()
        val expectedSubtotal = cart.sumOf { it.priceUsd * it.quantity }

        val emptyResult = repo.placeOrder("cash", emptyList(), "الموقع الحالي", null)
        assertTrue(emptyResult.isFailure)

        repo.placeOrder("visa", cart, "الموقع الحالي", "4242").getOrThrow()

        val orders = repo.observeOrders("admin_session").first()
        assertEquals(1, orders.size)
        assertEquals(expectedSubtotal + PharmacyCatalog.DELIVERY_FEE_MAD, orders.first().totalUsd, 0.01)
        assertTrue(repo.observeCart("admin_session").first().isEmpty())
    }

    @Test
    fun localAdminRepo_sendChatMessageSeedsConversationAndAppendsUserMessage() = runTest {
        val repo = LocalAdminPharmacyRepository()
        val pharmacyId = PharmacyCatalog.pharmacies.first().id

        repo.sendChatMessage(pharmacyId, "   ").getOrThrow()
        assertTrue(repo.observeChatMessages("admin_session", pharmacyId).first().isEmpty())

        repo.sendChatMessage(pharmacyId, "هل الدواء متوفر؟").getOrThrow()
        val messages = repo.observeChatMessages("admin_session", pharmacyId).first()
        assertTrue(messages.size >= 2)
        assertEquals("USER", messages.last().senderType)
        assertEquals("هل الدواء متوفر؟", messages.last().text)
    }
}
