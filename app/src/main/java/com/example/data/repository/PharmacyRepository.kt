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
import com.google.firebase.firestore.FirebaseFirestoreException
import com.google.firebase.firestore.Query
import com.google.firebase.firestore.snapshots
import kotlinx.coroutines.CancellationException
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.catch
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

    private fun requireUserId(): String {
        val uid = auth.currentUser?.uid
        check(!uid.isNullOrBlank()) { "User must be signed in with Google before accessing Firestore." }
        return uid
    }

    private fun <T> Flow<T>.handleFirestoreStreamErrors(
        uid: String,
        operationType: OperationType,
        path: String
    ): Flow<T> = catch { error ->
        val firestoreError = generateSequence(error) { it.cause }
            .filterIsInstance<FirebaseFirestoreException>()
            .firstOrNull()
        if (firestoreError == null && error is CancellationException) throw error
        val exceptionToLog = firestoreError ?: (error as? Exception)
        if (exceptionToLog != null && auth.currentUser?.uid == uid) {
            handleFirestoreError(exceptionToLog, operationType, path)
        }
        throw error
    }

    override fun observeProfile(userId: String): Flow<UserProfile?> = flow {
        val uid = requireUserId()
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
                .handleFirestoreStreamErrors(uid, OperationType.GET, path)
        )
    }

    override fun observeCart(userId: String): Flow<List<CartItem>> = flow {
        val uid = requireUserId()
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
                .handleFirestoreStreamErrors(uid, OperationType.LIST, path)
        )
    }

    override fun observeFavorites(userId: String): Flow<List<FavoriteItem>> = flow {
        val uid = requireUserId()
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
                .handleFirestoreStreamErrors(uid, OperationType.LIST, path)
        )
    }

    override fun observeOrders(userId: String): Flow<List<PharmacyOrder>> = flow {
        val uid = requireUserId()
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
                .handleFirestoreStreamErrors(uid, OperationType.LIST, path)
        )
    }

    override fun observeRecentSearches(userId: String): Flow<List<RecentSearch>> = flow {
        val uid = requireUserId()
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
                .handleFirestoreStreamErrors(uid, OperationType.LIST, path)
        )
    }

    override fun observeChatMessages(userId: String, pharmacyId: String): Flow<List<ChatMessage>> = flow {
        val uid = requireUserId()
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
                .handleFirestoreStreamErrors(uid, OperationType.LIST, path)
        )
    }

    override suspend fun ensureUserInitialized(): Result<Unit> {
        var docPath = "users"
        return try {
            val uid = requireUserId()
            val userRef = db.collection("users").document(uid)
            docPath = userRef.path
            val snapshot = userRef.get().await()
            if (!snapshot.exists()) {
                val profilePayload = mapOf(
                    "userId" to uid,
                    "language" to "ar",
                    "notificationsEnabled" to true,
                    "selectedLocation" to "الموقع الحالي",
                    "createdAt" to FieldValue.serverTimestamp(),
                    "updatedAt" to FieldValue.serverTimestamp()
                )
                userRef.set(profilePayload).await()

                // Seed initial recent search chips ("بنادول", "بروفين", "فيتامين")
                PharmacyCatalog.defaultRecentSearchChips.forEachIndexed { idx, query ->
                    val searchId = "seed_${idx + 1}"
                    val searchRef = userRef.collection("recent_searches").document(searchId)
                    searchRef.set(
                        mapOf(
                            "userId" to uid,
                            "query" to query,
                            "createdAt" to FieldValue.serverTimestamp()
                        )
                    ).await()
                }

                // Seed initial cart showcase items totaling $100 subtotal (+ $5 delivery = $105)
                val biosteron = PharmacyCatalog.products.first { it.id == "prod_biosteron" }
                val utorinic = PharmacyCatalog.products.first { it.id == "prod_utorinic" }
                listOf(biosteron to 2L, utorinic to 1L).forEach { (prod, qty) ->
                    userRef.collection("cart").document(prod.id).set(
                        mapOf(
                            "userId" to uid,
                            "productId" to prod.id,
                            "productName" to prod.nameAr,
                            "category" to prod.categoryAr,
                            "priceUsd" to prod.priceUsd,
                            "quantity" to qty,
                            "rating" to prod.rating,
                            "createdAt" to FieldValue.serverTimestamp(),
                            "updatedAt" to FieldValue.serverTimestamp()
                        )
                    ).await()
                }
            }
            Result.success(Unit)
        } catch (e: CancellationException) {
            throw e
        } catch (e: Exception) {
            handleFirestoreError(e, OperationType.WRITE, docPath)
            Result.failure(e)
        }
    }

    override suspend fun ensurePharmacyChatSeeded(pharmacyId: String): Result<Unit> {
        var docPath = "users"
        return try {
            val uid = requireUserId()
            require(pharmacyId.matches(Regex("^[a-zA-Z0-9_\\-]+$")))
            val messagesCol = db.collection("users").document(uid)
                .collection("chats").document(pharmacyId)
                .collection("messages")
            docPath = messagesCol.path
            val existing = messagesCol.limit(1).get().await()
            if (existing.isEmpty) {
                val seeds = PharmacyCatalog.seedConversationForPharmacy(pharmacyId)
                seeds.forEachIndexed { idx, (sender, text) ->
                    messagesCol.document("seed_msg_${idx + 1}").set(
                        mapOf(
                            "userId" to uid,
                            "pharmacyId" to pharmacyId,
                            "senderType" to sender,
                            "text" to text,
                            "createdAt" to FieldValue.serverTimestamp()
                        )
                    ).await()
                }
            }
            Result.success(Unit)
        } catch (e: CancellationException) {
            throw e
        } catch (e: Exception) {
            handleFirestoreError(e, OperationType.CREATE, docPath)
            Result.failure(e)
        }
    }

    override suspend fun updateProfilePreferences(
        language: String?,
        notificationsEnabled: Boolean?,
        selectedLocation: String?
    ): Result<Unit> {
        var docPath = "users"
        return try {
            val uid = requireUserId()
            val userRef = db.collection("users").document(uid)
            docPath = userRef.path
            val updates = mutableMapOf<String, Any>(
                "updatedAt" to FieldValue.serverTimestamp()
            )
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
            Result.success(Unit)
        } catch (e: CancellationException) {
            throw e
        } catch (e: Exception) {
            handleFirestoreError(e, OperationType.UPDATE, docPath)
            Result.failure(e)
        }
    }

    override suspend fun addOrIncrementCartItem(product: Product, quantityToAdd: Int): Result<Unit> {
        var docPath = "users"
        return try {
            val uid = requireUserId()
            require(quantityToAdd in 1..99)
            val cartRef = db.collection("users").document(uid)
                .collection("cart").document(product.id)
            docPath = cartRef.path
            val existing = cartRef.get().await()
            if (existing.exists()) {
                val currentQty = (existing.getLong("quantity") ?: 0L).toInt()
                val nextQty = (currentQty + quantityToAdd).coerceIn(1, 99).toLong()
                cartRef.update(
                    mapOf(
                        "quantity" to nextQty,
                        "priceUsd" to product.priceUsd,
                        "productName" to product.nameAr.take(120),
                        "category" to product.categoryAr.take(60),
                        "rating" to product.rating,
                        "updatedAt" to FieldValue.serverTimestamp()
                    )
                ).await()
            } else {
                cartRef.set(
                    mapOf(
                        "userId" to uid,
                        "productId" to product.id,
                        "productName" to product.nameAr.take(120),
                        "category" to product.categoryAr.take(60),
                        "priceUsd" to product.priceUsd,
                        "quantity" to quantityToAdd.toLong(),
                        "rating" to product.rating,
                        "createdAt" to FieldValue.serverTimestamp(),
                        "updatedAt" to FieldValue.serverTimestamp()
                    )
                ).await()
            }
            Result.success(Unit)
        } catch (e: CancellationException) {
            throw e
        } catch (e: Exception) {
            handleFirestoreError(e, OperationType.WRITE, docPath)
            Result.failure(e)
        }
    }

    override suspend fun setCartItemQuantity(productId: String, quantity: Int): Result<Unit> {
        var docPath = "users"
        return try {
            val uid = requireUserId()
            require(productId.isNotBlank())
            val cartRef = db.collection("users").document(uid)
                .collection("cart").document(productId)
            docPath = cartRef.path
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
            Result.success(Unit)
        } catch (e: CancellationException) {
            throw e
        } catch (e: Exception) {
            handleFirestoreError(e, OperationType.UPDATE, docPath)
            Result.failure(e)
        }
    }

    override suspend fun removeCartItem(productId: String): Result<Unit> {
        var docPath = "users"
        return try {
            val uid = requireUserId()
            require(productId.isNotBlank())
            val cartRef = db.collection("users").document(uid)
                .collection("cart").document(productId)
            docPath = cartRef.path
            cartRef.delete().await()
            Result.success(Unit)
        } catch (e: CancellationException) {
            throw e
        } catch (e: Exception) {
            handleFirestoreError(e, OperationType.DELETE, docPath)
            Result.failure(e)
        }
    }

    override suspend fun toggleFavorite(product: Product, currentlyFavorite: Boolean): Result<Unit> {
        var docPath = "users"
        return try {
            val uid = requireUserId()
            val favRef = db.collection("users").document(uid)
                .collection("favorites").document(product.id)
            docPath = favRef.path
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
            Result.success(Unit)
        } catch (e: CancellationException) {
            throw e
        } catch (e: Exception) {
            handleFirestoreError(e, OperationType.WRITE, docPath)
            Result.failure(e)
        }
    }

    override suspend fun addRecentSearch(queryText: String): Result<Unit> {
        var docPath = "users"
        return try {
            val uid = requireUserId()
            val clean = queryText.trim().take(80)
            require(clean.isNotEmpty())
            val searchId = "s_${UUID.randomUUID().toString().replace("-", "").take(12)}"
            val searchRef = db.collection("users").document(uid)
                .collection("recent_searches").document(searchId)
            docPath = searchRef.path
            searchRef.set(
                mapOf(
                    "userId" to uid,
                    "query" to clean,
                    "createdAt" to FieldValue.serverTimestamp()
                )
            ).await()
            Result.success(Unit)
        } catch (e: CancellationException) {
            throw e
        } catch (e: Exception) {
            handleFirestoreError(e, OperationType.CREATE, docPath)
            Result.failure(e)
        }
    }

    override suspend fun deleteRecentSearch(searchId: String): Result<Unit> {
        var docPath = "users"
        return try {
            val uid = requireUserId()
            require(searchId.isNotBlank())
            val searchRef = db.collection("users").document(uid)
                .collection("recent_searches").document(searchId)
            docPath = searchRef.path
            searchRef.delete().await()
            Result.success(Unit)
        } catch (e: CancellationException) {
            throw e
        } catch (e: Exception) {
            handleFirestoreError(e, OperationType.DELETE, docPath)
            Result.failure(e)
        }
    }

    override suspend fun placeOrder(
        paymentMethod: String,
        cartItems: List<CartItem>,
        deliveryLocation: String,
        cardLast4: String?
    ): Result<Unit> {
        var docPath = "users"
        return try {
            val uid = requireUserId()
            require(paymentMethod in listOf("mastercard", "visa", "cash"))
            require(cartItems.isNotEmpty())
            val subtotal = cartItems.sumOf { it.priceUsd * it.quantity }
            val deliveryFee = 5.0
            val total = subtotal + deliveryFee
            val itemCount = cartItems.sumOf { it.quantity }.coerceIn(1L, 500L)
            val cleanLoc = deliveryLocation.trim().take(160).ifBlank { "الموقع الحالي" }
            val orderId = "ord_${UUID.randomUUID().toString().replace("-", "").take(14)}"
            val orderRef = db.collection("users").document(uid)
                .collection("orders").document(orderId)
            docPath = orderRef.path

            val payload = mapOf(
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

            orderRef.set(payload).await()

            // Clear cart items after successful order creation
            val cartCol = db.collection("users").document(uid).collection("cart")
            cartItems.forEach { item ->
                val docId = item.id.ifBlank { item.productId }
                if (docId.isNotBlank()) {
                    cartCol.document(docId).delete().await()
                }
            }
            Result.success(Unit)
        } catch (e: CancellationException) {
            throw e
        } catch (e: Exception) {
            handleFirestoreError(e, OperationType.CREATE, docPath)
            Result.failure(e)
        }
    }

    override suspend fun sendChatMessage(pharmacyId: String, text: String): Result<Unit> {
        var docPath = "users"
        return try {
            val uid = requireUserId()
            require(pharmacyId.matches(Regex("^[a-zA-Z0-9_\\-]+$")))
            val cleanText = text.trim().take(1000)
            require(cleanText.isNotEmpty())
            val msgCol = db.collection("users").document(uid)
                .collection("chats").document(pharmacyId)
                .collection("messages")
            val msgId = "m_${UUID.randomUUID().toString().replace("-", "").take(14)}"
            docPath = "${msgCol.path}/$msgId"

            msgCol.document(msgId).set(
                mapOf(
                    "userId" to uid,
                    "pharmacyId" to pharmacyId,
                    "senderType" to "USER",
                    "text" to cleanText,
                    "createdAt" to FieldValue.serverTimestamp()
                )
            ).await()
            Result.success(Unit)
        } catch (e: CancellationException) {
            throw e
        } catch (e: Exception) {
            handleFirestoreError(e, OperationType.CREATE, docPath)
            Result.failure(e)
        }
    }
}
