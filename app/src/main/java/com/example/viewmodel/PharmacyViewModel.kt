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
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch

sealed interface UiState<out T> {
    data object Loading : UiState<Nothing>
    data class Success<T>(val data: T) : UiState<T>
    data class Error(val message: String) : UiState<Nothing>
}

class PharmacyViewModel(
    private val repository: PharmacyDataSource,
    private val currentUserId: String
) : ViewModel() {

    private val _actionMessage = MutableStateFlow<String?>(null)
    val actionMessage: StateFlow<String?> = _actionMessage.asStateFlow()

    private val _selectedHomeCategory = MutableStateFlow("all")
    val selectedHomeCategory: StateFlow<String> = _selectedHomeCategory.asStateFlow()

    private val _orderConfirmedBanner = MutableStateFlow(false)
    val orderConfirmedBanner: StateFlow<Boolean> = _orderConfirmedBanner.asStateFlow()

    init {
        viewModelScope.launch {
            repository.ensureUserInitialized()
                .onFailure { err ->
                    _actionMessage.value = err.localizedMessage ?: "تعذر تهيئة الحساب"
                }
        }
    }

    private fun <T> Flow<T>.toUiStateFlow(tagLabel: String): StateFlow<UiState<T>> =
        map<T, UiState<T>> { UiState.Success(it) }
            .catch { error ->
                val firestoreError = generateSequence(error) { it.cause }
                    .filterIsInstance<FirebaseFirestoreException>()
                    .firstOrNull()
                if (firestoreError == null && error is CancellationException) throw error
                val rootError = firestoreError ?: error
                Log.w(TAG, "Error observing $tagLabel", rootError)
                emit(UiState.Error(rootError.message ?: "حدث خطأ أثناء تحميل البيانات"))
            }
            .stateIn(
                scope = viewModelScope,
                started = SharingStarted.WhileSubscribed(STOP_TIMEOUT_MILLIS),
                initialValue = UiState.Loading
            )

    val profileState: StateFlow<UiState<UserProfile?>> =
        repository.observeProfile(currentUserId).toUiStateFlow("profile")

    val cartState: StateFlow<UiState<List<CartItem>>> =
        repository.observeCart(currentUserId).toUiStateFlow("cart")

    val favoritesState: StateFlow<UiState<List<FavoriteItem>>> =
        repository.observeFavorites(currentUserId).toUiStateFlow("favorites")

    val ordersState: StateFlow<UiState<List<PharmacyOrder>>> =
        repository.observeOrders(currentUserId).toUiStateFlow("orders")

    val recentSearchesState: StateFlow<UiState<List<RecentSearch>>> =
        repository.observeRecentSearches(currentUserId).toUiStateFlow("recent_searches")

    fun observePharmacyChat(pharmacyId: String): Flow<UiState<List<ChatMessage>>> {
        viewModelScope.launch {
            repository.ensurePharmacyChatSeeded(pharmacyId)
        }
        return repository.observeChatMessages(currentUserId, pharmacyId)
            .map<List<ChatMessage>, UiState<List<ChatMessage>>> { UiState.Success(it) }
            .catch { error ->
                val firestoreError = generateSequence(error) { it.cause }
                    .filterIsInstance<FirebaseFirestoreException>()
                    .firstOrNull()
                if (firestoreError == null && error is CancellationException) throw error
                val rootError = firestoreError ?: error
                Log.w(TAG, "Error observing chat $pharmacyId", rootError)
                emit(UiState.Error(rootError.message ?: "تعذر تحميل المحادثة"))
            }
    }

    fun selectHomeCategory(categoryId: String) {
        _selectedHomeCategory.value = categoryId
    }

    fun clearActionMessage() {
        _actionMessage.value = null
    }

    fun dismissOrderConfirmed() {
        _orderConfirmedBanner.value = false
    }

    fun addToCart(product: Product, quantity: Int = 1) {
        viewModelScope.launch {
            repository.addOrIncrementCartItem(product, quantity)
                .onSuccess {
                    _actionMessage.value = "تمت إضافة ${product.nameAr} إلى السلة"
                }
                .onFailure {
                    _actionMessage.value = it.localizedMessage ?: "تعذر الإضافة إلى السلة"
                }
        }
    }

    fun updateCartQuantity(productId: String, nextQuantity: Int) {
        viewModelScope.launch {
            repository.setCartItemQuantity(productId, nextQuantity)
                .onFailure {
                    _actionMessage.value = it.localizedMessage ?: "تعذر تحديث الكمية"
                }
        }
    }

    fun removeFromCart(productId: String) {
        viewModelScope.launch {
            repository.removeCartItem(productId)
                .onFailure {
                    _actionMessage.value = it.localizedMessage ?: "تعذر حذف المنتج"
                }
        }
    }

    fun toggleFavorite(product: Product, currentlyFavorite: Boolean) {
        viewModelScope.launch {
            repository.toggleFavorite(product, currentlyFavorite)
                .onSuccess {
                    _actionMessage.value = if (currentlyFavorite) {
                        "تمت الإزالة من المفضلة"
                    } else {
                        "تمت الإضافة إلى المفضلة"
                    }
                }
                .onFailure {
                    _actionMessage.value = it.localizedMessage ?: "تعذر تحديث المفضلة"
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
        viewModelScope.launch {
            repository.updateProfilePreferences(selectedLocation = location)
                .onSuccess {
                    _actionMessage.value = "تم تغيير موقع التوصيل إلى $location"
                }
                .onFailure {
                    _actionMessage.value = it.localizedMessage ?: "تعذر تحديث الموقع"
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
        viewModelScope.launch {
            repository.placeOrder(paymentMethod, cartItems, deliveryLocation, cardLast4)
                .onSuccess {
                    _orderConfirmedBanner.value = true
                    onSuccess()
                }
                .onFailure {
                    _actionMessage.value = it.localizedMessage ?: "تعذر تأكيد الطلب"
                }
        }
    }

    fun sendMessage(pharmacyId: String, text: String) {
        if (text.isBlank()) return
        viewModelScope.launch {
            repository.sendChatMessage(pharmacyId, text)
                .onFailure {
                    _actionMessage.value = it.localizedMessage ?: "تعذر إرسال الرسالة"
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
