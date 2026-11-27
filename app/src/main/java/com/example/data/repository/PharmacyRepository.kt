package com.example.data.repository

import android.content.Context
import com.example.R
import com.example.data.catalog.PharmacyCatalog
import com.example.data.firebase.OperationType
import com.example.data.firebase.handleFirestoreError
import com.example.data.model.CartItem
import com.example.data.model.ChatMessage
import com.example.data.model.FavoriteItem
import com.example.data.model.PharmacyOrder
import com.example.data.model.Product
import com.example.data.model.RecentSearch
import com.example.data.model.UserProfile
import com.google.firebase.Firebase
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.auth
import com.google.firebase.firestore.DocumentSnapshot
import com.google.firebase.firestore.FieldValue
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.Query
import com.google.firebase.firestore.snapshots
import kotlinx.coroutines.CancellationException
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.emitAll
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.tasks.await
import java.util.UUID

class PharmacyRepository(
    private val db: FirebaseFirestore,
    private val auth: FirebaseAuth = Firebase.auth
) : PharmacyDataSource {

    constructor(context: Context) : this(
        FirebaseFirestore.getInstance(
            context.applicationContext.getString(R.string.firestore_database_id)
        )
    )

    override fun observeProfile(userId: String): Flow<UserProfile?> = flow {
        val uid = requireSignedInUserId(auth)
        check(uid == userId) { "Signed-in user mismatch." }
        val path = "users/$uid"
        emitAll(
            db.collection("users").document(uid)
                .snapshots()
                .map { snapshot ->
                    if (snapshot.exists()) {
                        snapshot.toObject(
                            UserProfile::class.java,
                            DocumentSnapshot.ServerTimestampBehavior.ESTIMATE
                        )
                    } else null
                }
                .handleFirestoreStreamErrors(auth, uid, OperationType.GET, path)
        )
    }

    override fun observeCart(userId: String): Flow<List<CartItem>> = flow {
        val uid = requireSignedInUserId(auth)
        check(uid == userId)
        val path = "users/$uid/cart"
        emitAll(
            db.collection("users").document(uid).collection("cart")
                .snapshots()
                .map { snapshot ->
                    snapshot.toObjects(
                        CartItem::class.java,
                        DocumentSnapshot.ServerTimestampBehavior.ESTIMATE
                    )
                }
                .handleFirestoreStreamErrors(auth, uid, OperationType.LIST, path)
        )
    }

    override fun observeFavorites(userId: String): Flow<List<FavoriteItem>> = flow {
        val uid = requireSignedInUserId(auth)
        check(uid == userId)
        val path = "users/$uid/favorites"
        emitAll(
            db.collection("users").document(uid).collection("favorites")
                .snapshots()
                .map { snapshot ->
                    snapshot.toObjects(
                        FavoriteItem::class.java,
                        DocumentSnapshot.ServerTimestampBehavior.ESTIMATE
                    )
                }
                .handleFirestoreStreamErrors(auth, uid, OperationType.LIST, path)
        )
    }

    override fun observeOrders(userId: String): Flow<List<PharmacyOrder>> = flow {
        val uid = requireSignedInUserId(auth)
        check(uid == userId)
        val path = "users/$uid/orders"
        emitAll(
            db.collection("users").document(uid).collection("orders")
                .orderBy("createdAt", Query.Direction.DESCENDING)
                .snapshots()
                .map { snapshot ->
                    snapshot.toObjects(
                        PharmacyOrder::class.java,
                        DocumentSnapshot.ServerTimestampBehavior.ESTIMATE
                    )
                }
                .handleFirestoreStreamErrors(auth, uid, OperationType.LIST, path)
        )
    }

    override fun observeRecentSearches(userId: String): Flow<List<RecentSearch>> = flow {
        val uid = requireSignedInUserId(auth)
        check(uid == userId)
        val path = "users/$uid/recent_searches"
        emitAll(
            db.collection("users").document(uid).collection("recent_searches")
                .orderBy("createdAt", Query.Direction.DESCENDING)
                .snapshots()
                .map { snapshot ->
                    snapshot.toObjects(
                        RecentSearch::class.java,
                        DocumentSnapshot.ServerTimestampBehavior.ESTIMATE
                    )
                }
                .handleFirestoreStreamErrors(auth, uid, OperationType.LIST, path)
        )
    }

    override fun observeChatMessages(userId: String, pharmacyId: String): Flow<List<ChatMessage>> = flow {
        val uid = requireSignedInUserId(auth)
        check(uid == userId)
        require(pharmacyId.isNotBlank())
        val path = "users/$uid/chats/$pharmacyId/messages"
        emitAll(
            db.collection("users").document(uid)
                .collection("chats").document(pharmacyId)
                .collection("messages")
                .orderBy("createdAt", Query.Direction.ASCENDING)
                .snapshots()
                .map { snapshot ->
                    snapshot.toObjects(
                        ChatMessage::class.java,
                        DocumentSnapshot.ServerTimestampBehavior.ESTIMATE
                    )
                }
                .handleFirestoreStreamErrors(auth, uid, OperationType.LIST, path)
        )
    }

    override suspend fun ensureUserInitialized(): Result<Unit> = executeWrite("users", OperationType.WRITE) { setPath ->
        val uid = requireSignedInUserId(auth)
        val userRef = db.collection("users").document(uid)
        setPath(userRef.path)
        val snapshot = userRef.get().await()
        if (!snapshot.exists()) {
            userRef.set(FirestorePayloadBuilders.initialUserProfile(uid)).await()
            PharmacyCatalog.defaultRecentSearchChips.forEachIndexed { idx, query ->
                userRef.collection("recent_searches").document("seed_${idx + 1}").set(
                    mapOf(
                        "userId" to uid,
                        "query" to query,
                        "createdAt" to FieldValue.serverTimestamp()
                    )
                ).await()
            }
            val doliprane = PharmacyCatalog.products.first { it.id == "prod_doliprane" }
            val vitac = PharmacyCatalog.products.first { it.id == "prod_vitac" }
            listOf(doliprane to 2L, vitac to 1L).forEach { (prod, qty) ->
                userRef.collection("cart").document(prod.id)
                    .set(FirestorePayloadBuilders.cartItemCreatePayload(uid, prod, qty))
                    .await()
            }
        }
    }

    override suspend fun ensurePharmacyChatSeeded(pharmacyId: String): Result<Unit> =
        executeWrite("users", OperationType.CREATE) { setPath ->
            val uid = requireSignedInUserId(auth)
            require(pharmacyId.matches(Regex("^[a-zA-Z0-9_\\-]+$")))
            val messagesCol = db.collection("users").document(uid)
                .collection("chats").document(pharmacyId)
                .collection("messages")
            setPath(messagesCol.path)
            val existing = messagesCol.limit(1).get().await()
            if (existing.isEmpty) {
                PharmacyCatalog.seedConversationForPharmacy(pharmacyId).forEachIndexed { idx, (sender, text) ->
                    messagesCol.document("seed_msg_${idx + 1}")
                        .set(FirestorePayloadBuilders.chatMessagePayload(uid, pharmacyId, sender, text))
                        .await()
                }
            }
        }

    override suspend fun updateProfilePreferences(
        language: String?,
        notificationsEnabled: Boolean?,
        selectedLocation: String?
    ): Result<Unit> = executeWrite("users", OperationType.UPDATE) { setPath ->
        val uid = requireSignedInUserId(auth)
        val userRef = db.collection("users").document(uid)
        setPath(userRef.path)
        val updates = mutableMapOf<String, Any>("updatedAt" to FieldValue.serverTimestamp())
        if (language != null) {
            require(language == "ar" || language == "en")
            updates["language"] = language
        }
        if (notificationsEnabled != null) {
            updates["notificationsEnabled"] = notificationsEnabled
        }
        if (selectedLocation != null) {
            val cleanLoc = selectedLocation.trim().take(120)
            require(cleanLoc.isNotEmpty())
            updates["selectedLocation"] = cleanLoc
        }
        userRef.update(updates).await()
    }

    override suspend fun addOrIncrementCartItem(product: Product, quantityToAdd: Int): Result<Unit> =
        executeWrite("users", OperationType.WRITE) { setPath ->
            val uid = requireSignedInUserId(auth)
            require(quantityToAdd in 1..99)
            val cartRef = db.collection("users").document(uid).collection("cart").document(product.id)
            setPath(cartRef.path)
            val existing = cartRef.get().await()
            if (existing.exists()) {
                val currentQty = (existing.getLong("quantity") ?: 0L).toInt()
                val nextQty = (currentQty + quantityToAdd).coerceIn(1, 99).toLong()
                cartRef.update(FirestorePayloadBuilders.cartItemIncrementPayload(product, nextQty)).await()
            } else {
                cartRef.set(
                    FirestorePayloadBuilders.cartItemCreatePayload(uid, product, quantityToAdd.toLong())
                ).await()
            }
        }

    override suspend fun setCartItemQuantity(productId: String, quantity: Int): Result<Unit> =
        executeWrite("users", OperationType.UPDATE) { setPath ->
            val uid = requireSignedInUserId(auth)
            require(productId.isNotBlank())
            val cartRef = db.collection("users").document(uid).collection("cart").document(productId)
            setPath(cartRef.path)
            if (quantity <= 0) {
                cartRef.delete().await()
            } else {
                cartRef.update(
                    mapOf(
                        "quantity" to quantity.coerceIn(1, 99).toLong(),
                        "updatedAt" to FieldValue.serverTimestamp()
                    )
                ).await()
            }
        }

    override suspend fun removeCartItem(productId: String): Result<Unit> =
        executeWrite("users", OperationType.DELETE) { setPath ->
            val uid = requireSignedInUserId(auth)
            require(productId.isNotBlank())
            val cartRef = db.collection("users").document(uid).collection("cart").document(productId)
            setPath(cartRef.path)
            cartRef.delete().await()
        }

    override suspend fun toggleFavorite(product: Product, currentlyFavorite: Boolean): Result<Unit> =
        executeWrite("users", OperationType.WRITE) { setPath ->
            val uid = requireSignedInUserId(auth)
            val favRef = db.collection("users").document(uid).collection("favorites").document(product.id)
            setPath(favRef.path)
            if (currentlyFavorite) {
                favRef.delete().await()
            } else {
                favRef.set(
                    mapOf(
                        "userId" to uid,
                        "productId" to product.id,
                        "productName" to product.nameAr.take(120),
                        "createdAt" to FieldValue.serverTimestamp()
                    )
                ).await()
            }
        }

    override suspend fun addRecentSearch(queryText: String): Result<Unit> =
        executeWrite("users", OperationType.CREATE) { setPath ->
            val uid = requireSignedInUserId(auth)
            val clean = queryText.trim().take(80)
            require(clean.isNotEmpty())
            val searchId = "s_${UUID.randomUUID().toString().replace("-", "").take(12)}"
            val searchRef = db.collection("users").document(uid).collection("recent_searches").document(searchId)
            setPath(searchRef.path)
            searchRef.set(
                mapOf(
                    "userId" to uid,
                    "query" to clean,
                    "createdAt" to FieldValue.serverTimestamp()
                )
            ).await()
        }

    override suspend fun deleteRecentSearch(searchId: String): Result<Unit> =
        executeWrite("users", OperationType.DELETE) { setPath ->
            val uid = requireSignedInUserId(auth)
            require(searchId.isNotBlank())
            val searchRef = db.collection("users").document(uid).collection("recent_searches").document(searchId)
            setPath(searchRef.path)
            searchRef.delete().await()
        }

    override suspend fun placeOrder(
        paymentMethod: String,
        cartItems: List<CartItem>,
        deliveryLocation: String,
        cardLast4: String?
    ): Result<Unit> = executeWrite("users", OperationType.CREATE) { setPath ->
        val uid = requireSignedInUserId(auth)
        require(paymentMethod in listOf("mastercard", "visa", "cash"))
        require(cartItems.isNotEmpty())
        val orderId = "ord_${UUID.randomUUID().toString().replace("-", "").take(14)}"
        val orderRef = db.collection("users").document(uid).collection("orders").document(orderId)
        setPath(orderRef.path)

        val payload = FirestorePayloadBuilders.orderCreatePayload(
            uid = uid,
            paymentMethod = paymentMethod,
            cartItems = cartItems,
            deliveryLocation = deliveryLocation,
            cardLast4 = cardLast4
        )
        orderRef.set(payload).await()

        val cartCol = db.collection("users").document(uid).collection("cart")
        cartItems.forEach { item ->
            val docId = item.id.ifBlank { item.productId }
            if (docId.isNotBlank()) {
                cartCol.document(docId).delete().await()
            }
        }
    }

    override suspend fun sendChatMessage(pharmacyId: String, text: String): Result<Unit> =
        executeWrite("users", OperationType.CREATE) { setPath ->
            val uid = requireSignedInUserId(auth)
            require(pharmacyId.matches(Regex("^[a-zA-Z0-9_\\-]+$")))
            val cleanText = text.trim().take(1000)
            require(cleanText.isNotEmpty())
            val msgCol = db.collection("users").document(uid)
                .collection("chats").document(pharmacyId)
                .collection("messages")
            val msgId = "m_${UUID.randomUUID().toString().replace("-", "").take(14)}"
            setPath("${msgCol.path}/$msgId")
            msgCol.document(msgId)
                .set(FirestorePayloadBuilders.chatMessagePayload(uid, pharmacyId, "USER", cleanText))
                .await()
        }

    private suspend inline fun executeWrite(
        initialPath: String,
        operationType: OperationType,
        block: (setPath: (String) -> Unit) -> Unit
    ): Result<Unit> {
        var docPath = initialPath
        return try {
            block { docPath = it }
            Result.success(Unit)
        } catch (e: CancellationException) {
            throw e
        } catch (e: Exception) {
            handleFirestoreError(e, operationType, docPath)
            Result.failure(e)
        }
    }
}
