package com.example.data.repository

import com.example.data.catalog.PharmacyCatalog
import com.example.data.model.CartItem
import com.example.data.model.ChatMessage
import com.example.data.model.FavoriteItem
import com.example.data.model.PharmacyOrder
import com.example.data.model.Product
import com.example.data.model.RecentSearch
import com.example.data.model.UserProfile
import com.google.firebase.Timestamp
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.update
import java.util.UUID

/**
 * In-memory repository for Admin bypass / preview mode.
 * Performs zero Firestore calls so unauthenticated admin sessions never trigger PERMISSION_DENIED.
 */
class LocalAdminPharmacyRepository : PharmacyDataSource {

    private val _profile = MutableStateFlow<UserProfile?>(
        UserProfile(
            id = "admin_session",
            userId = "admin_session",
            language = "ar",
            notificationsEnabled = true,
            selectedLocation = "الموقع الحالي",
            createdAt = Timestamp.now(),
            updatedAt = Timestamp.now()
        )
    )

    private val _cart = MutableStateFlow(
        listOf(
            PharmacyCatalog.products.first { it.id == "prod_biosteron" }.let { prod ->
                CartItem(
                    id = prod.id,
                    userId = "admin_session",
                    productId = prod.id,
                    productName = prod.nameAr,
                    category = prod.categoryAr,
                    priceUsd = prod.priceUsd,
                    quantity = 2L,
                    rating = prod.rating
                )
            },
            PharmacyCatalog.products.first { it.id == "prod_utorinic" }.let { prod ->
                CartItem(
                    id = prod.id,
                    userId = "admin_session",
                    productId = prod.id,
                    productName = prod.nameAr,
                    category = prod.categoryAr,
                    priceUsd = prod.priceUsd,
                    quantity = 1L,
                    rating = prod.rating
                )
            }
        )
    )

    private val _favorites = MutableStateFlow<List<FavoriteItem>>(emptyList())
    private val _orders = MutableStateFlow<List<PharmacyOrder>>(emptyList())
    private val _recentSearches = MutableStateFlow(
        PharmacyCatalog.defaultRecentSearchChips.mapIndexed { idx, q ->
            RecentSearch(id = "admin_search_${idx + 1}", userId = "admin_session", query = q)
        }
    )
    private val _chats = MutableStateFlow<Map<String, List<ChatMessage>>>(emptyMap())

    override fun observeProfile(userId: String): Flow<UserProfile?> = _profile

    override fun observeCart(userId: String): Flow<List<CartItem>> = _cart

    override fun observeFavorites(userId: String): Flow<List<FavoriteItem>> = _favorites

    override fun observeOrders(userId: String): Flow<List<PharmacyOrder>> = _orders

    override fun observeRecentSearches(userId: String): Flow<List<RecentSearch>> = _recentSearches

    override fun observeChatMessages(userId: String, pharmacyId: String): Flow<List<ChatMessage>> =
        _chats.map { it[pharmacyId].orEmpty() }

    override suspend fun ensureUserInitialized(): Result<Unit> =
        Result.success(Unit)

    override suspend fun ensurePharmacyChatSeeded(pharmacyId: String): Result<Unit> {
        _chats.update { map ->
            if (map.containsKey(pharmacyId)) map
            else {
                val seeded = PharmacyCatalog.seedConversationForPharmacy(pharmacyId)
                    .mapIndexed { idx, (sender, text) ->
                        ChatMessage(
                            id = "admin_seed_${pharmacyId}_${idx + 1}",
                            userId = "admin_session",
                            pharmacyId = pharmacyId,
                            senderType = sender,
                            text = text,
                            createdAt = Timestamp.now()
                        )
                    }
                map + (pharmacyId to seeded)
            }
        }
        return Result.success(Unit)
    }

    override suspend fun updateProfilePreferences(
        language: String?,
        notificationsEnabled: Boolean?,
        selectedLocation: String?
    ): Result<Unit> {
        _profile.update { current ->
            val base = current ?: UserProfile(id = "admin_session", userId = "admin_session")
            base.copy(
                language = language ?: base.language,
                notificationsEnabled = notificationsEnabled ?: base.notificationsEnabled,
                selectedLocation = selectedLocation ?: base.selectedLocation,
                updatedAt = Timestamp.now()
            )
        }
        return Result.success(Unit)
    }

    override suspend fun addOrIncrementCartItem(product: Product, quantityToAdd: Int): Result<Unit> {
        _cart.update { items ->
            val existing = items.firstOrNull { it.productId == product.id }
            if (existing != null) {
                items.map {
                    if (it.productId == product.id) {
                        it.copy(quantity = (it.quantity + quantityToAdd).coerceIn(1L, 99L))
                    } else it
                }
            } else {
                items + CartItem(
                    id = product.id,
                    userId = "admin_session",
                    productId = product.id,
                    productName = product.nameAr,
                    category = product.categoryAr,
                    priceUsd = product.priceUsd,
                    quantity = quantityToAdd.coerceIn(1, 99).toLong(),
                    rating = product.rating
                )
            }
        }
        return Result.success(Unit)
    }

    override suspend fun setCartItemQuantity(productId: String, quantity: Int): Result<Unit> {
        _cart.update { items ->
            if (quantity <= 0) items.filterNot { it.productId == productId || it.id == productId }
            else items.map {
                if (it.productId == productId || it.id == productId) {
                    it.copy(quantity = quantity.coerceIn(1, 99).toLong())
                } else it
            }
        }
        return Result.success(Unit)
    }

    override suspend fun removeCartItem(productId: String): Result<Unit> {
        _cart.update { items -> items.filterNot { it.productId == productId || it.id == productId } }
        return Result.success(Unit)
    }

    override suspend fun toggleFavorite(product: Product, currentlyFavorite: Boolean): Result<Unit> {
        _favorites.update { favs ->
            if (currentlyFavorite) favs.filterNot { it.productId == product.id }
            else favs + FavoriteItem(
                id = product.id,
                userId = "admin_session",
                productId = product.id,
                productName = product.nameAr,
                createdAt = Timestamp.now()
            )
        }
        return Result.success(Unit)
    }

    override suspend fun addRecentSearch(queryText: String): Result<Unit> {
        val clean = queryText.trim().take(80)
        if (clean.isEmpty()) return Result.success(Unit)
        _recentSearches.update { list ->
            val id = "admin_s_${UUID.randomUUID().toString().take(8)}"
            listOf(RecentSearch(id = id, userId = "admin_session", query = clean)) +
                list.filterNot { it.query == clean }
        }
        return Result.success(Unit)
    }

    override suspend fun deleteRecentSearch(searchId: String): Result<Unit> {
        _recentSearches.update { list -> list.filterNot { it.id == searchId } }
        return Result.success(Unit)
    }

    override suspend fun placeOrder(
        paymentMethod: String,
        cartItems: List<CartItem>,
        deliveryLocation: String,
        cardLast4: String?
    ): Result<Unit> {
        if (cartItems.isEmpty()) return Result.failure(IllegalArgumentException("Cart is empty"))
        val subtotal = cartItems.sumOf { it.priceUsd * it.quantity }
        val delivery = 5.0
        val order = PharmacyOrder(
            id = "admin_ord_${UUID.randomUUID().toString().take(8)}",
            userId = "admin_session",
            paymentMethod = paymentMethod,
            subtotalUsd = subtotal,
            deliveryUsd = delivery,
            totalUsd = subtotal + delivery,
            itemCount = cartItems.sumOf { it.quantity },
            status = "confirmed",
            deliveryLocation = deliveryLocation,
            cardLast4 = cardLast4,
            createdAt = Timestamp.now(),
            updatedAt = Timestamp.now()
        )
        _orders.update { listOf(order) + it }
        _cart.value = emptyList()
        return Result.success(Unit)
    }

    override suspend fun sendChatMessage(pharmacyId: String, text: String): Result<Unit> {
        val clean = text.trim().take(1000)
        if (clean.isEmpty()) return Result.success(Unit)
        ensurePharmacyChatSeeded(pharmacyId)
        val msg = ChatMessage(
            id = "admin_m_${UUID.randomUUID().toString().take(8)}",
            userId = "admin_session",
            pharmacyId = pharmacyId,
            senderType = "USER",
            text = clean,
            createdAt = Timestamp.now()
        )
        _chats.update { map ->
            val current = map[pharmacyId].orEmpty()
            map + (pharmacyId to (current + msg))
        }
        return Result.success(Unit)
    }
}
