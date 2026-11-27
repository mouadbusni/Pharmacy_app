package com.example.viewmodel

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.BuildConfig
import com.example.data.catalog.PharmacyCatalog
import com.example.data.catalog.SmartPharmacyResponder
import com.example.data.model.AppNotification
import com.example.data.model.CartItem
import com.example.data.model.ChatMessage
import com.example.data.model.FavoriteItem
import com.example.data.model.NotificationTarget
import com.example.data.model.PharmacyOrder
import com.example.data.model.Product
import com.example.data.model.RecentSearch
import com.example.data.model.UserProfile
import com.example.data.notifications.NotificationPreferencesReader
import com.example.data.repository.PharmacyDataSource
import com.example.data.update.AppUpdateState
import com.example.data.update.GitHubReleaseChecker
import com.example.data.update.ReleaseCheckOutcome
import com.example.data.update.ReleaseLookup
import com.example.data.update.UpdatePreferencesReader
import com.example.data.update.UpdatePreferencesStore
import com.google.firebase.firestore.FirebaseFirestoreException
import kotlinx.coroutines.CancellationException
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.flatMapLatest
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.onStart
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

sealed interface UiState<out T> {
    data object Loading : UiState<Nothing>
    data class Success<T>(val data: T) : UiState<T>
    data class Error(val message: String) : UiState<Nothing>
}

@OptIn(ExperimentalCoroutinesApi::class)
class PharmacyViewModel(
    private val repository: PharmacyDataSource,
    private val currentUserId: String,
    private val updatePreferencesStore: UpdatePreferencesReader? = null,
    private val notificationPreferencesStore: NotificationPreferencesReader? = null,
    private val releaseLookup: ReleaseLookup = GitHubReleaseChecker
) : ViewModel() {

    private val _actionMessage = MutableStateFlow<String?>(null)
    val actionMessage: StateFlow<String?> = _actionMessage.asStateFlow()

    private val _selectedHomeCategory = MutableStateFlow("all")
    val selectedHomeCategory: StateFlow<String> = _selectedHomeCategory.asStateFlow()

    private val _isSubmittingOrder = MutableStateFlow(false)
    val isSubmittingOrder: StateFlow<Boolean> = _isSubmittingOrder.asStateFlow()

    private val _appUpdateState = MutableStateFlow<AppUpdateState>(AppUpdateState.Idle)
    val appUpdateState: StateFlow<AppUpdateState> = _appUpdateState.asStateFlow()

    val configuredRepo: StateFlow<String> = (updatePreferencesStore?.repoSlugFlow ?: MutableStateFlow(""))
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(STOP_TIMEOUT_MILLIS), "")

    private val _sessionNotifications = MutableStateFlow<List<AppNotification>>(emptyList())
    private val _fallbackReadIds = MutableStateFlow<Set<String>>(emptySet())
    private val readIdsFlow: Flow<Set<String>> = notificationPreferencesStore?.readIdsFlow ?: _fallbackReadIds

    val notificationsState: StateFlow<List<AppNotification>> =
        combine(_sessionNotifications, readIdsFlow) { dynamicAlerts, readIds ->
            (dynamicAlerts + PharmacyCatalog.sampleNotifications).map { item ->
                item.copy(unread = item.unread && !readIds.contains(item.id))
            }
        }.stateIn(
            viewModelScope,
            SharingStarted.WhileSubscribed(STOP_TIMEOUT_MILLIS),
            PharmacyCatalog.sampleNotifications
        )

    val unreadNotificationsCount: StateFlow<Int> = notificationsState
        .map { list -> list.count { it.unread } }
        .stateIn(
            viewModelScope,
            SharingStarted.WhileSubscribed(STOP_TIMEOUT_MILLIS),
            PharmacyCatalog.sampleNotifications.count { it.unread }
        )

    private val _refreshSignal = MutableStateFlow(0)

    init {
        initializeUser()
        autoCheckForUpdates()
    }

    private fun autoCheckForUpdates() {
        val store = updatePreferencesStore ?: return
        viewModelScope.launch {
            val savedRepo = store.repoSlugFlow.first()
            if (UpdatePreferencesStore.isValidRepoSlug(savedRepo)) {
                checkForUpdates(repoOverride = savedRepo, manual = false)
            }
        }
    }

    fun saveRepoAndCheckUpdates(rawRepo: String) {
        val store = updatePreferencesStore ?: return
        val normalized = UpdatePreferencesStore.normalizeRepoSlug(rawRepo)
        if (!UpdatePreferencesStore.isValidRepoSlug(normalized)) {
            _appUpdateState.value = AppUpdateState.Error(
                if (isEnglish()) "Invalid repository format. Use owner/repo" else "صيغة غير صحيحة — استخدم owner/repo"
            )
            return
        }
        viewModelScope.launch {
            store.saveRepoSlug(normalized)
            checkForUpdates(repoOverride = normalized, manual = true)
        }
    }

    fun checkForUpdates(repoOverride: String? = null, manual: Boolean = true) {
        val store = updatePreferencesStore ?: return
        viewModelScope.launch {
            val targetRepo = (repoOverride ?: store.repoSlugFlow.first()).trim()
            if (!UpdatePreferencesStore.isValidRepoSlug(targetRepo)) {
                if (manual) {
                    _appUpdateState.value = AppUpdateState.Error(
                        if (isEnglish()) "Enter a GitHub repository (owner/repo) first"
                        else "يرجى إدخال مستودع GitHub أولاً بصيغة owner/repo"
                    )
                }
                return@launch
            }
            _appUpdateState.value = AppUpdateState.Checking
            val dismissedTag = store.dismissedTagFlow.first()
            releaseLookup.fetchLatestRelease(targetRepo, BuildConfig.VERSION_NAME, BuildConfig.VERSION_CODE)
                .onSuccess { outcome ->
                    _appUpdateState.value = when (outcome) {
                        is ReleaseCheckOutcome.NoReleasesYet ->
                            if (manual) AppUpdateState.NoReleasesYet(outcome.repoSlug) else AppUpdateState.Idle
                        is ReleaseCheckOutcome.Found -> if (outcome.isNewer) {
                            AppUpdateState.UpdateAvailable(outcome.info, !manual && dismissedTag == outcome.info.tagName)
                        } else {
                            AppUpdateState.UpToDate("v${BuildConfig.VERSION_NAME} (${BuildConfig.VERSION_CODE})")
                        }
                    }
                }.onFailure { err ->
                    _appUpdateState.value = if (manual) {
                        AppUpdateState.Error(err.localizedMessage ?: "Failed to check GitHub Releases")
                    } else AppUpdateState.Idle
                }
        }
    }

    fun dismissUpdateBanner(tagName: String) {
        val store = updatePreferencesStore ?: return
        val current = _appUpdateState.value
        if (current is AppUpdateState.UpdateAvailable && current.info.tagName == tagName) {
            _appUpdateState.value = current.copy(isDismissed = true)
        }
        viewModelScope.launch { store.dismissReleaseTag(tagName) }
    }

    private fun initializeUser() {
        viewModelScope.launch {
            repository.ensureUserInitialized().onFailure { _actionMessage.value = it.localizedMessage }
        }
    }

    fun retryAll() {
        initializeUser()
        _refreshSignal.value += 1
    }

    private fun <T> buildUiStateFlow(tagLabel: String, sourceProvider: () -> Flow<T>): StateFlow<UiState<T>> =
        _refreshSignal
            .flatMapLatest {
                sourceProvider()
                    .map<T, UiState<T>> { UiState.Success(it) }
                    .onStart { emit(UiState.Loading) }
                    .catch { error ->
                        val firestoreError = generateSequence(error) { it.cause }
                            .filterIsInstance<FirebaseFirestoreException>()
                            .firstOrNull()
                        if (firestoreError == null && error is CancellationException) throw error
                        val rootError = firestoreError ?: error
                        Log.w(TAG, "Error observing $tagLabel", rootError)
                        emit(UiState.Error(rootError.message ?: "Error loading data"))
                    }
            }
            .stateIn(viewModelScope, SharingStarted.WhileSubscribed(STOP_TIMEOUT_MILLIS), UiState.Loading)

    val profileState: StateFlow<UiState<UserProfile?>> =
        buildUiStateFlow("profile") { repository.observeProfile(currentUserId) }

    val cartState: StateFlow<UiState<List<CartItem>>> =
        buildUiStateFlow("cart") { repository.observeCart(currentUserId) }

    val favoritesState: StateFlow<UiState<List<FavoriteItem>>> =
        buildUiStateFlow("favorites") { repository.observeFavorites(currentUserId) }

    val ordersState: StateFlow<UiState<List<PharmacyOrder>>> =
        buildUiStateFlow("orders") { repository.observeOrders(currentUserId) }

    val recentSearchesState: StateFlow<UiState<List<RecentSearch>>> =
        buildUiStateFlow("recent_searches") { repository.observeRecentSearches(currentUserId) }

    fun observePharmacyChat(pharmacyId: String): Flow<UiState<List<ChatMessage>>> {
        viewModelScope.launch { repository.ensurePharmacyChatSeeded(pharmacyId) }
        return _refreshSignal.flatMapLatest {
            repository.observeChatMessages(currentUserId, pharmacyId)
                .map<List<ChatMessage>, UiState<List<ChatMessage>>> { UiState.Success(it) }
                .onStart { emit(UiState.Loading) }
                .catch { error ->
                    val firestoreError = generateSequence(error) { it.cause }
                        .filterIsInstance<FirebaseFirestoreException>()
                        .firstOrNull()
                    if (firestoreError == null && error is CancellationException) throw error
                    val rootError = firestoreError ?: error
                    Log.w(TAG, "Error observing chat $pharmacyId", rootError)
                    emit(UiState.Error(rootError.message ?: "Failed to load conversation"))
                }
        }
    }

    fun selectHomeCategory(categoryId: String) {
        _selectedHomeCategory.value = categoryId
    }

    fun markNotificationRead(notificationId: String) {
        if (notificationId.isBlank()) return
        _fallbackReadIds.update { it + notificationId }
        notificationPreferencesStore?.let { store ->
            viewModelScope.launch { store.markRead(notificationId) }
        }
    }

    fun markAllNotificationsRead() {
        val ids = notificationsState.value.map { it.id }
        if (ids.isEmpty()) return
        _fallbackReadIds.update { it + ids }
        notificationPreferencesStore?.let { store ->
            viewModelScope.launch { store.markAllRead(ids) }
        }
    }

    fun clearActionMessage() {
        _actionMessage.value = null
    }

    private fun isEnglish(): Boolean =
        (profileState.value as? UiState.Success)?.data?.language == "en"

    fun addToCart(product: Product, quantity: Int = 1) {
        val en = isEnglish()
        viewModelScope.launch {
            repository.addOrIncrementCartItem(product, quantity)
                .onSuccess {
                    val name = product.localizedName(en)
                    _actionMessage.value = if (en) "Added $name to cart" else "تمت إضافة $name إلى السلة"
                }
                .onFailure {
                    _actionMessage.value = it.localizedMessage ?: if (en) "Could not add to cart" else "تعذر الإضافة إلى السلة"
                }
        }
    }

    fun updateCartQuantity(productId: String, nextQuantity: Int) {
        val en = isEnglish()
        viewModelScope.launch {
            repository.setCartItemQuantity(productId, nextQuantity)
                .onFailure {
                    _actionMessage.value = it.localizedMessage ?: if (en) "Could not update quantity" else "تعذر تحديث الكمية"
                }
        }
    }

    fun removeFromCart(productId: String) {
        val en = isEnglish()
        viewModelScope.launch {
            repository.removeCartItem(productId)
                .onFailure {
                    _actionMessage.value = it.localizedMessage ?: if (en) "Could not remove item" else "تعذر حذف المنتج"
                }
        }
    }

    fun toggleFavorite(product: Product, currentlyFavorite: Boolean) {
        val en = isEnglish()
        viewModelScope.launch {
            repository.toggleFavorite(product, currentlyFavorite)
                .onSuccess {
                    _actionMessage.value = when {
                        currentlyFavorite && en -> "Removed from favorites"
                        currentlyFavorite -> "تمت الإزالة من المفضلة"
                        en -> "Added to favorites"
                        else -> "تمت الإضافة إلى المفضلة"
                    }
                }
                .onFailure {
                    _actionMessage.value = it.localizedMessage ?: if (en) "Could not update favorites" else "تعذر تحديث المفضلة"
                }
        }
    }

    fun addSearchQuery(query: String) {
        if (query.isBlank()) return
        viewModelScope.launch { repository.addRecentSearch(query) }
    }

    fun deleteSearchChip(searchId: String) {
        viewModelScope.launch { repository.deleteRecentSearch(searchId) }
    }

    fun updateLocation(location: String) {
        val en = isEnglish()
        val displayLoc = PharmacyCatalog.localizeLocation(location, en)
        viewModelScope.launch {
            repository.updateProfilePreferences(selectedLocation = location)
                .onSuccess {
                    _actionMessage.value = if (en) "Delivery address updated to $displayLoc" else "تم تغيير موقع التوصيل إلى $displayLoc"
                }
                .onFailure {
                    _actionMessage.value = it.localizedMessage ?: if (en) "Could not update address" else "تعذر تحديث الموقع"
                }
        }
    }

    fun toggleLanguage(currentLanguage: String) {
        val next = if (currentLanguage == "ar") "en" else "ar"
        viewModelScope.launch {
            repository.updateProfilePreferences(language = next)
                .onSuccess { _actionMessage.value = if (next == "ar") "تم التبديل إلى العربية" else "Switched to English" }
        }
    }

    fun toggleNotifications(enabled: Boolean) {
        viewModelScope.launch { repository.updateProfilePreferences(notificationsEnabled = enabled) }
    }

    fun placeOrder(
        paymentMethod: String,
        cartItems: List<CartItem>,
        deliveryLocation: String,
        cardLast4: String?,
        deliveryAddress: String? = null,
        contactPhone: String? = null,
        hasPrescription: Boolean = false,
        prescriptionAttached: Boolean = false,
        onSuccess: () -> Unit
    ) {
        if (_isSubmittingOrder.value) return
        val en = isEnglish()
        _isSubmittingOrder.value = true
        viewModelScope.launch {
            repository.placeOrder(
                paymentMethod = paymentMethod,
                cartItems = cartItems,
                deliveryLocation = deliveryLocation,
                cardLast4 = cardLast4,
                deliveryAddress = deliveryAddress,
                contactPhone = contactPhone,
                hasPrescription = hasPrescription,
                prescriptionAttached = prescriptionAttached
            ).onSuccess {
                _isSubmittingOrder.value = false
                val locEn = PharmacyCatalog.localizeLocation(deliveryLocation, true)
                val itemTotal = cartItems.sumOf { it.quantity }
                val orderAlert = AppNotification(
                    id = "notif_order_${System.currentTimeMillis()}",
                    titleAr = "تم تأكيد طلبك ($itemTotal منتجات)",
                    titleEn = "Order Confirmed ($itemTotal items)",
                    bodyAr = "جاري تجهيز أدويتك في أقرب صيدلية للتوصيل إلى $deliveryLocation.",
                    bodyEn = "A nearby pharmacy is preparing your order for delivery to $locEn.",
                    timeAr = "الآن",
                    timeEn = "Just now",
                    unread = true,
                    target = NotificationTarget.ORDERS
                )
                _sessionNotifications.update { listOf(orderAlert) + it }
                onSuccess()
            }.onFailure {
                _isSubmittingOrder.value = false
                _actionMessage.value = it.localizedMessage ?: if (en) "Could not confirm order" else "تعذر تأكيد الطلب"
            }
        }
    }

    fun updateOrderStatus(orderId: String, nextStatus: String) {
        val en = isEnglish()
        viewModelScope.launch {
            repository.updateOrderStatus(orderId, nextStatus)
                .onFailure {
                    _actionMessage.value = it.localizedMessage ?: if (en) "Could not update order" else "تعذر تحديث حالة الطلب"
                }
        }
    }

    fun reorderOrder(order: PharmacyOrder, onDone: () -> Unit = {}) {
        val en = isEnglish()
        val parsed = PharmacyCatalog.parseOrderItemsSummary(order.itemsSummary)
        if (parsed.isEmpty()) {
            _actionMessage.value = if (en) "Order items unavailable for quick reorder" else "تفاصيل منتجات هذا الطلب غير متوفرة لإعادة الطلب"
            return
        }
        viewModelScope.launch {
            var allOk = true
            parsed.forEach { (product, qty) ->
                if (repository.addOrIncrementCartItem(product, qty).isFailure) allOk = false
            }
            if (allOk) {
                _actionMessage.value = if (en) "Added previous items to cart" else "تمت إضافة منتجات الطلب السابق إلى السلة"
                onDone()
            } else {
                _actionMessage.value = if (en) "Some items could not be added" else "تعذر إضافة بعض المنتجات"
            }
        }
    }

    fun sendMessage(
        pharmacyId: String,
        text: String,
        attachmentUri: String? = null,
        attachmentLabel: String? = null
    ) {
        if (text.isBlank() && attachmentUri.isNullOrBlank()) return
        val en = isEnglish()
        val resolvedText = text.trim().ifBlank {
            if (en) "Prescription photo attached" else "تم إرفاق صورة الوصفة الطبية"
        }
        viewModelScope.launch {
            repository.sendChatMessage(pharmacyId, resolvedText, attachmentUri, attachmentLabel, "USER")
                .onSuccess {
                    val pharmacy = PharmacyCatalog.pharmacies.firstOrNull { it.id == pharmacyId }
                        ?: PharmacyCatalog.pharmacies.first()
                    val reply = SmartPharmacyResponder.buildReply(
                        pharmacy = pharmacy,
                        userText = resolvedText,
                        hasAttachment = !attachmentUri.isNullOrBlank(),
                        isEnglish = en
                    )
                    repository.sendChatMessage(pharmacyId, reply, senderType = "PHARMACY")
                    val chatAlert = AppNotification(
                        id = "notif_chat_${pharmacy.id}",
                        titleAr = "رد جديد من ${pharmacy.nameAr}",
                        titleEn = "Reply from ${pharmacy.nameEn}",
                        bodyAr = reply,
                        bodyEn = reply,
                        timeAr = "الآن",
                        timeEn = "Just now",
                        unread = true,
                        target = NotificationTarget.CHATS,
                        pharmacyId = pharmacy.id
                    )
                    _sessionNotifications.update { current ->
                        listOf(chatAlert) + current.filterNot { it.id == chatAlert.id }
                    }
                }.onFailure {
                    _actionMessage.value = it.localizedMessage ?: if (en) "Failed to send message" else "تعذر إرسال الرسالة"
                }
        }
    }

    fun findProduct(productId: String): Product =
        PharmacyCatalog.products.firstOrNull { it.id == productId } ?: PharmacyCatalog.products.first()

    private companion object {
        const val TAG = "PharmacyViewModel"
        const val STOP_TIMEOUT_MILLIS = 5000L
    }
}
