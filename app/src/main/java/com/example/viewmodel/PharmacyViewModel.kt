package com.example.viewmodel

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.data.catalog.PharmacyCatalog
import com.example.data.model.CartItem
import com.example.data.model.ChatMessage
import com.example.data.model.FavoriteItem
import com.example.data.model.PharmacyOrder
import com.example.data.model.Product
import com.example.data.model.RecentSearch
import com.example.data.model.UserProfile
import com.example.data.repository.PharmacyDataSource
import com.google.firebase.firestore.FirebaseFirestoreException
import kotlinx.coroutines.CancellationException
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.flatMapLatest
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.onStart
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch

sealed interface UiState<out T> {
    data object Loading : UiState<Nothing>
    data class Success<T>(val data: T) : UiState<T>
    data class Error(val message: String) : UiState<Nothing>
}

@OptIn(ExperimentalCoroutinesApi::class)
class PharmacyViewModel(
    private val repository: PharmacyDataSource,
    private val currentUserId: String
) : ViewModel() {

    private val _actionMessage = MutableStateFlow<String?>(null)
    val actionMessage: StateFlow<String?> = _actionMessage.asStateFlow()

    private val _selectedHomeCategory = MutableStateFlow("all")
    val selectedHomeCategory: StateFlow<String> = _selectedHomeCategory.asStateFlow()

    private val _isSubmittingOrder = MutableStateFlow(false)
    val isSubmittingOrder: StateFlow<Boolean> = _isSubmittingOrder.asStateFlow()

    private val _refreshSignal = MutableStateFlow(0)

    init {
        initializeUser()
    }

    private fun initializeUser() {
        viewModelScope.launch {
            repository.ensureUserInitialized()
                .onFailure { err ->
                    _actionMessage.value = err.localizedMessage
                }
        }
    }

    fun retryAll() {
        initializeUser()
        _refreshSignal.value += 1
    }

    private fun <T> buildUiStateFlow(
        tagLabel: String,
        sourceProvider: () -> Flow<T>
    ): StateFlow<UiState<T>> =
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
            .stateIn(
                scope = viewModelScope,
                started = SharingStarted.WhileSubscribed(STOP_TIMEOUT_MILLIS),
                initialValue = UiState.Loading
            )

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
        viewModelScope.launch {
            repository.ensurePharmacyChatSeeded(pharmacyId)
        }
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
        viewModelScope.launch {
            repository.addRecentSearch(query)
        }
    }

    fun deleteSearchChip(searchId: String) {
        viewModelScope.launch {
            repository.deleteRecentSearch(searchId)
        }
    }

    fun updateLocation(location: String) {
        val en = isEnglish()
        val displayLoc = PharmacyCatalog.localizeLocation(location, en)
        viewModelScope.launch {
            repository.updateProfilePreferences(selectedLocation = location)
                .onSuccess {
                    _actionMessage.value = if (en) {
                        "Delivery address updated to $displayLoc"
                    } else {
                        "تم تغيير موقع التوصيل إلى $displayLoc"
                    }
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
                .onSuccess {
                    _actionMessage.value = if (next == "ar") "تم التبديل إلى العربية" else "Switched to English"
                }
        }
    }

    fun toggleNotifications(enabled: Boolean) {
        viewModelScope.launch {
            repository.updateProfilePreferences(notificationsEnabled = enabled)
        }
    }

    fun placeOrder(
        paymentMethod: String,
        cartItems: List<CartItem>,
        deliveryLocation: String,
        cardLast4: String?,
        onSuccess: () -> Unit
    ) {
        if (_isSubmittingOrder.value) return
        val en = isEnglish()
        _isSubmittingOrder.value = true
        viewModelScope.launch {
            repository.placeOrder(paymentMethod, cartItems, deliveryLocation, cardLast4)
                .onSuccess {
                    _isSubmittingOrder.value = false
                    onSuccess()
                }
                .onFailure {
                    _isSubmittingOrder.value = false
                    _actionMessage.value = it.localizedMessage ?: if (en) "Could not confirm order" else "تعذر تأكيد الطلب"
                }
        }
    }

    fun sendMessage(pharmacyId: String, text: String) {
        if (text.isBlank()) return
        val en = isEnglish()
        viewModelScope.launch {
            repository.sendChatMessage(pharmacyId, text)
                .onFailure {
                    _actionMessage.value = it.localizedMessage ?: if (en) "Failed to send message" else "تعذر إرسال الرسالة"
                }
        }
    }

    fun findProduct(productId: String): Product {
        return PharmacyCatalog.products.firstOrNull { it.id == productId }
            ?: PharmacyCatalog.products.first()
    }

    private companion object {
        const val TAG = "PharmacyViewModel"
        const val STOP_TIMEOUT_MILLIS = 5000L
    }
}
