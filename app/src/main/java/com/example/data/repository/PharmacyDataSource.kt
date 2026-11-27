package com.example.data.repository

import com.example.data.model.CartItem
import com.example.data.model.ChatMessage
import com.example.data.model.FavoriteItem
import com.example.data.model.PharmacyOrder
import com.example.data.model.Product
import com.example.data.model.RecentSearch
import com.example.data.model.UserProfile
import kotlinx.coroutines.flow.Flow

interface PharmacyDataSource {
    fun observeProfile(userId: String): Flow<UserProfile?>
    fun observeCart(userId: String): Flow<List<CartItem>>
    fun observeFavorites(userId: String): Flow<List<FavoriteItem>>
    fun observeOrders(userId: String): Flow<List<PharmacyOrder>>
    fun observeRecentSearches(userId: String): Flow<List<RecentSearch>>
    fun observeChatMessages(userId: String, pharmacyId: String): Flow<List<ChatMessage>>

    suspend fun ensureUserInitialized(): Result<Unit>
    suspend fun ensurePharmacyChatSeeded(pharmacyId: String): Result<Unit>
    suspend fun updateProfilePreferences(
        language: String? = null,
        notificationsEnabled: Boolean? = null,
        selectedLocation: String? = null
    ): Result<Unit>

    suspend fun addOrIncrementCartItem(product: Product, quantityToAdd: Int): Result<Unit>
    suspend fun setCartItemQuantity(productId: String, quantity: Int): Result<Unit>
    suspend fun removeCartItem(productId: String): Result<Unit>
    suspend fun toggleFavorite(product: Product, currentlyFavorite: Boolean): Result<Unit>
    suspend fun addRecentSearch(queryText: String): Result<Unit>
    suspend fun deleteRecentSearch(searchId: String): Result<Unit>
    suspend fun placeOrder(
        paymentMethod: String,
        cartItems: List<CartItem>,
        deliveryLocation: String,
        cardLast4: String?,
        deliveryAddress: String? = null,
        contactPhone: String? = null,
        hasPrescription: Boolean = false,
        prescriptionAttached: Boolean = false
    ): Result<Unit>

    suspend fun updateOrderStatus(orderId: String, nextStatus: String): Result<Unit>

    suspend fun sendChatMessage(
        pharmacyId: String,
        text: String,
        attachmentUri: String? = null,
        attachmentLabel: String? = null,
        senderType: String = "USER"
    ): Result<Unit>
}
