package com.example.ui.navigation

import androidx.compose.animation.AnimatedContentTransitionScope
import androidx.compose.animation.core.tween
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.foundation.ScrollState
import androidx.compose.foundation.lazy.LazyListState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.navigation.NavHostController
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.navArgument
import com.example.data.catalog.PharmacyCatalog
import com.example.data.model.AppNotification
import com.example.data.model.CartItem
import com.example.data.model.FavoriteItem
import com.example.data.model.PharmacyOrder
import com.example.data.model.PharmacyScheduleFilter
import com.example.data.model.RecentSearch
import com.example.data.model.UserProfile
import com.example.data.update.AppUpdateInfo
import com.example.data.update.AppUpdateState
import com.example.ui.screens.CartScreen
import com.example.ui.screens.ChatListScreen
import com.example.ui.screens.CheckoutScreen
import com.example.ui.screens.FavoritesScreen
import com.example.ui.screens.HomeScreen
import com.example.ui.screens.LiveChatScreen
import com.example.ui.screens.NotificationsScreen
import com.example.ui.screens.OrdersScreen
import com.example.ui.screens.PharmaciesListScreen
import com.example.ui.screens.PharmacyProfileScreen
import com.example.ui.screens.ProductDetailScreen
import com.example.ui.screens.ProfileScreen
import com.example.ui.screens.SearchProductsScreen
import com.example.viewmodel.PharmacyViewModel
import com.example.viewmodel.UiState

object PharmacyRoutes {
    const val HOME = "home"
    const val NOTIFICATIONS = "notifications"
    const val FAVORITES = "favorites"
    const val PROFILE = "profile"
    const val SEARCH_PRODUCTS = "search_products"
    const val PHARMACIES_LIST = "pharmacies_list"
    const val PRODUCT_DETAIL = "product_detail"
    const val PHARMACY_PROFILE = "pharmacy_profile"
    const val CHATS = "chats"
    const val LIVE_CHAT = "live_chat"
    const val CART = "cart"
    const val CHECKOUT = "checkout"
    const val ORDERS = "orders"

    val topLevelRoutes = setOf(HOME, NOTIFICATIONS, FAVORITES, PROFILE)

    fun searchRoute(categoryId: String = "all"): String =
        "$SEARCH_PRODUCTS?category=$categoryId"

    fun pharmaciesRoute(scheduleFilter: PharmacyScheduleFilter = PharmacyScheduleFilter.ALL): String =
        "$PHARMACIES_LIST?schedule=${scheduleFilter.name}"
}

@Composable
internal fun PharmacyNavGraph(
    navController: NavHostController,
    viewModel: PharmacyViewModel,
    profile: UserProfile?,
    cartState: UiState<List<CartItem>>,
    cartItems: List<CartItem>,
    cartCount: Int,
    favoritesState: UiState<List<FavoriteItem>>,
    favoriteIds: Set<String>,
    ordersState: UiState<List<PharmacyOrder>>,
    recentSearchesState: UiState<List<RecentSearch>>,
    notifications: List<AppNotification>,
    unreadNotificationsCount: Int,
    selectedHomeCategory: String,
    isSubmittingOrder: Boolean,
    isAdminMode: Boolean,
    authDisplayName: String?,
    authEmail: String?,
    authPhotoUrl: String?,
    configuredRepo: String,
    appUpdateState: AppUpdateState,
    onLaunchUpdateDownload: (AppUpdateInfo) -> Unit,
    onSignedOut: () -> Unit,
    homeListState: LazyListState,
    notificationsListState: LazyListState,
    favoritesListState: LazyListState,
    profileScrollState: ScrollState
) {
    NavHost(
        navController = navController,
        startDestination = PharmacyRoutes.HOME,
        enterTransition = {
            val isTabSwitch = initialState.destination.route in PharmacyRoutes.topLevelRoutes &&
                targetState.destination.route in PharmacyRoutes.topLevelRoutes
            if (isTabSwitch) {
                fadeIn(animationSpec = tween(180))
            } else {
                slideIntoContainer(
                    towards = AnimatedContentTransitionScope.SlideDirection.Start,
                    animationSpec = tween(280)
                ) + fadeIn(animationSpec = tween(200))
            }
        },
        exitTransition = {
            val isTabSwitch = initialState.destination.route in PharmacyRoutes.topLevelRoutes &&
                targetState.destination.route in PharmacyRoutes.topLevelRoutes
            if (isTabSwitch) {
                fadeOut(animationSpec = tween(180))
            } else {
                slideOutOfContainer(
                    towards = AnimatedContentTransitionScope.SlideDirection.Start,
                    animationSpec = tween(280)
                ) + fadeOut(animationSpec = tween(180))
            }
        },
        popEnterTransition = {
            val isTabSwitch = initialState.destination.route in PharmacyRoutes.topLevelRoutes &&
                targetState.destination.route in PharmacyRoutes.topLevelRoutes
            if (isTabSwitch) {
                fadeIn(animationSpec = tween(180))
            } else {
                slideIntoContainer(
                    towards = AnimatedContentTransitionScope.SlideDirection.End,
                    animationSpec = tween(280)
                ) + fadeIn(animationSpec = tween(200))
            }
        },
        popExitTransition = {
            val isTabSwitch = initialState.destination.route in PharmacyRoutes.topLevelRoutes &&
                targetState.destination.route in PharmacyRoutes.topLevelRoutes
            if (isTabSwitch) {
                fadeOut(animationSpec = tween(180))
            } else {
                slideOutOfContainer(
                    towards = AnimatedContentTransitionScope.SlideDirection.End,
                    animationSpec = tween(280)
                ) + fadeOut(animationSpec = tween(180))
            }
        }
    ) {
        composable(PharmacyRoutes.HOME) {
            HomeScreen(
                selectedLocation = profile?.selectedLocation ?: "الموقع الحالي",
                selectedCategoryId = selectedHomeCategory,
                favoriteProductIds = favoriteIds,
                onSelectCategory = viewModel::selectHomeCategory,
                onSelectLocation = viewModel::updateLocation,
                onOpenSearch = { categoryId ->
                    navController.navigate(PharmacyRoutes.searchRoute(categoryId))
                },
                onOpenNotifications = { navController.navigate(PharmacyRoutes.NOTIFICATIONS) },
                onOpenChats = { navController.navigate(PharmacyRoutes.CHATS) },
                onOpenProduct = { prod ->
                    navController.navigate("${PharmacyRoutes.PRODUCT_DETAIL}/${prod.id}")
                },
                onOpenPharmacy = { pharmacy ->
                    navController.navigate("${PharmacyRoutes.PHARMACY_PROFILE}/${pharmacy.id}")
                },
                onOpenPharmaciesList = {
                    navController.navigate(PharmacyRoutes.pharmaciesRoute())
                },
                onOpen24hPharmacies = {
                    navController.navigate(
                        PharmacyRoutes.pharmaciesRoute(PharmacyScheduleFilter.ALWAYS_24H)
                    )
                },
                onAddToCart = { prod -> viewModel.addToCart(prod, 1) },
                onToggleFavorite = viewModel::toggleFavorite,
                updateState = appUpdateState,
                onUpdateNow = onLaunchUpdateDownload,
                onDismissUpdate = viewModel::dismissUpdateBanner,
                unreadNotificationsCount = unreadNotificationsCount,
                listState = homeListState
            )
        }

        composable(PharmacyRoutes.NOTIFICATIONS) {
            NotificationsScreen(
                notifications = notifications,
                onBack = { navController.popBackStack() },
                onMarkNotificationRead = viewModel::markNotificationRead,
                onMarkAllRead = viewModel::markAllNotificationsRead,
                onOpenPromoVitamins = {
                    navController.navigate(PharmacyRoutes.searchRoute("vitamins"))
                },
                onOpenChats = { pharmacyId ->
                    if (pharmacyId.isNullOrBlank()) {
                        navController.navigate(PharmacyRoutes.CHATS)
                    } else {
                        navController.navigate("${PharmacyRoutes.LIVE_CHAT}/$pharmacyId")
                    }
                },
                onOpenPharmacies = { navController.navigate(PharmacyRoutes.pharmaciesRoute()) },
                onOpenOrders = { navController.navigate(PharmacyRoutes.ORDERS) },
                listState = notificationsListState
            )
        }

        composable(PharmacyRoutes.FAVORITES) {
            FavoritesScreen(
                favoritesState = favoritesState,
                onBack = { navController.popBackStack() },
                onOpenProduct = { prod ->
                    navController.navigate("${PharmacyRoutes.PRODUCT_DETAIL}/${prod.id}")
                },
                onAddToCart = { prod -> viewModel.addToCart(prod, 1) },
                onToggleFavorite = viewModel::toggleFavorite,
                onRetry = viewModel::retryAll,
                listState = favoritesListState
            )
        }

        composable(PharmacyRoutes.PROFILE) {
            ProfileScreen(
                profile = profile,
                ordersState = ordersState,
                isAdminMode = isAdminMode,
                authDisplayName = authDisplayName,
                authEmail = authEmail,
                authPhotoUrl = authPhotoUrl,
                onBack = { navController.popBackStack() },
                onToggleLanguage = viewModel::toggleLanguage,
                onToggleNotifications = viewModel::toggleNotifications,
                onUpdateLocation = viewModel::updateLocation,
                onOpenOrders = { navController.navigate(PharmacyRoutes.ORDERS) },
                onSignedOut = onSignedOut,
                configuredRepo = configuredRepo,
                updateState = appUpdateState,
                onSaveRepoAndCheck = viewModel::saveRepoAndCheckUpdates,
                onCheckUpdates = { viewModel.checkForUpdates(manual = true) },
                onDownloadUpdate = onLaunchUpdateDownload,
                scrollState = profileScrollState
            )
        }

        composable(
            route = "${PharmacyRoutes.SEARCH_PRODUCTS}?category={category}",
            arguments = listOf(
                navArgument("category") {
                    type = NavType.StringType
                    defaultValue = "all"
                }
            )
        ) { backStackEntry ->
            val categoryArg = backStackEntry.arguments?.getString("category") ?: "all"
            SearchProductsScreen(
                initialCategoryId = categoryArg,
                recentSearchesState = recentSearchesState,
                favoriteProductIds = favoriteIds,
                cartCount = cartCount,
                onBack = { navController.popBackStack() },
                onOpenCart = { navController.navigate(PharmacyRoutes.CART) },
                onOpenProduct = { prod ->
                    navController.navigate("${PharmacyRoutes.PRODUCT_DETAIL}/${prod.id}")
                },
                onAddToCart = { prod -> viewModel.addToCart(prod, 1) },
                onToggleFavorite = viewModel::toggleFavorite,
                onSubmitSearch = viewModel::addSearchQuery,
                onDeleteRecentSearch = viewModel::deleteSearchChip,
                onRetry = viewModel::retryAll
            )
        }

        composable(
            route = "${PharmacyRoutes.PHARMACIES_LIST}?schedule={schedule}",
            arguments = listOf(
                navArgument("schedule") {
                    type = NavType.StringType
                    defaultValue = PharmacyScheduleFilter.ALL.name
                }
            )
        ) { backStackEntry ->
            val scheduleName = backStackEntry.arguments?.getString("schedule")
            val initialSchedule = runCatching {
                if (scheduleName.isNullOrBlank()) PharmacyScheduleFilter.ALL
                else PharmacyScheduleFilter.valueOf(scheduleName)
            }.getOrDefault(PharmacyScheduleFilter.ALL)

            PharmaciesListScreen(
                cartCount = cartCount,
                initialScheduleFilter = initialSchedule,
                onBack = { navController.popBackStack() },
                onOpenCart = { navController.navigate(PharmacyRoutes.CART) },
                onOpenPharmacy = { pharmacy ->
                    navController.navigate("${PharmacyRoutes.PHARMACY_PROFILE}/${pharmacy.id}")
                }
            )
        }

        composable(
            route = "${PharmacyRoutes.PRODUCT_DETAIL}/{productId}",
            arguments = listOf(navArgument("productId") { type = NavType.StringType })
        ) { backStackEntry ->
            val productId = backStackEntry.arguments?.getString("productId") ?: "prod_doliprane"
            val product = viewModel.findProduct(productId)
            val isFav = favoriteIds.contains(product.id)
            ProductDetailScreen(
                product = product,
                isFavorite = isFav,
                favoriteProductIds = favoriteIds,
                cartCount = cartCount,
                onBack = { navController.popBackStack() },
                onOpenCart = { navController.navigate(PharmacyRoutes.CART) },
                onToggleFavorite = viewModel::toggleFavorite,
                onAddToCart = { p, qty -> viewModel.addToCart(p, qty) },
                onOpenProduct = { related ->
                    navController.navigate("${PharmacyRoutes.PRODUCT_DETAIL}/${related.id}")
                },
                onOpenSearch = { catId ->
                    navController.navigate(PharmacyRoutes.searchRoute(catId))
                }
            )
        }

        composable(
            route = "${PharmacyRoutes.PHARMACY_PROFILE}/{pharmacyId}",
            arguments = listOf(navArgument("pharmacyId") { type = NavType.StringType })
        ) { backStackEntry ->
            val pharmacyId = backStackEntry.arguments?.getString("pharmacyId") ?: "pharm_du_lys"
            val pharmacy = PharmacyCatalog.pharmacies.firstOrNull { it.id == pharmacyId }
                ?: PharmacyCatalog.pharmacies.first()
            PharmacyProfileScreen(
                pharmacy = pharmacy,
                favoriteProductIds = favoriteIds,
                cartCount = cartCount,
                onBack = { navController.popBackStack() },
                onOpenCart = { navController.navigate(PharmacyRoutes.CART) },
                onOpenChat = { target ->
                    navController.navigate("${PharmacyRoutes.LIVE_CHAT}/${target.id}")
                },
                onOpenChatsList = { navController.navigate(PharmacyRoutes.CHATS) },
                onOpenProduct = { prod ->
                    navController.navigate("${PharmacyRoutes.PRODUCT_DETAIL}/${prod.id}")
                },
                onOpenSearch = { catId ->
                    navController.navigate(PharmacyRoutes.searchRoute(catId))
                },
                onAddToCart = { prod -> viewModel.addToCart(prod, 1) },
                onToggleFavorite = viewModel::toggleFavorite
            )
        }

        composable(PharmacyRoutes.CHATS) {
            ChatListScreen(
                onBack = { navController.popBackStack() },
                onOpenNotifications = { navController.navigate(PharmacyRoutes.NOTIFICATIONS) },
                onOpenLiveChat = { pharmacy ->
                    navController.navigate("${PharmacyRoutes.LIVE_CHAT}/${pharmacy.id}")
                },
                onOpenPharmaciesList = {
                    navController.navigate(PharmacyRoutes.pharmaciesRoute())
                }
            )
        }

        composable(
            route = "${PharmacyRoutes.LIVE_CHAT}/{pharmacyId}",
            arguments = listOf(navArgument("pharmacyId") { type = NavType.StringType })
        ) { backStackEntry ->
            val pharmacyId = backStackEntry.arguments?.getString("pharmacyId") ?: "pharm_khouloud"
            val pharmacy = PharmacyCatalog.pharmacies.firstOrNull { it.id == pharmacyId }
                ?: PharmacyCatalog.pharmacies.first()
            val chatState by remember(pharmacyId) {
                viewModel.observePharmacyChat(pharmacyId)
            }.collectAsStateWithLifecycle(initialValue = UiState.Loading)

            LiveChatScreen(
                pharmacy = pharmacy,
                chatState = chatState,
                onBack = { navController.popBackStack() },
                onSendMessage = { msg, attachmentUri, attachmentLabel ->
                    viewModel.sendMessage(pharmacy.id, msg, attachmentUri, attachmentLabel)
                },
                onRetry = viewModel::retryAll
            )
        }

        composable(PharmacyRoutes.CART) {
            CartScreen(
                cartState = cartState,
                onBack = { navController.popBackStack() },
                onUpdateQty = viewModel::updateCartQuantity,
                onRemoveItem = viewModel::removeFromCart,
                onProceedCheckout = { navController.navigate(PharmacyRoutes.CHECKOUT) },
                onBrowseProducts = {
                    navController.navigate(PharmacyRoutes.searchRoute("all"))
                },
                onRetry = viewModel::retryAll
            )
        }

        composable(PharmacyRoutes.CHECKOUT) {
            CheckoutScreen(
                cartItems = cartItems,
                deliveryLocation = profile?.selectedLocation ?: "الموقع الحالي",
                isSubmitting = isSubmittingOrder,
                onBack = { navController.popBackStack() },
                onConfirmOrder = { method, last4, streetAddress, contactPhone, hasPrescription, rxAttached, onSuccess ->
                    viewModel.placeOrder(
                        paymentMethod = method,
                        cartItems = cartItems,
                        deliveryLocation = profile?.selectedLocation ?: "الموقع الحالي",
                        cardLast4 = last4,
                        deliveryAddress = streetAddress,
                        contactPhone = contactPhone,
                        hasPrescription = hasPrescription,
                        prescriptionAttached = rxAttached,
                        onSuccess = onSuccess
                    )
                },
                onTrackOrder = {
                    navController.navigate(PharmacyRoutes.ORDERS) {
                        popUpTo(PharmacyRoutes.HOME)
                    }
                },
                onOrderCompleteGoHome = {
                    navController.navigate(PharmacyRoutes.HOME) {
                        popUpTo(PharmacyRoutes.HOME) { inclusive = true }
                    }
                }
            )
        }

        composable(PharmacyRoutes.ORDERS) {
            OrdersScreen(
                ordersState = ordersState,
                onBack = { navController.popBackStack() },
                onUpdateOrderStatus = viewModel::updateOrderStatus,
                onReorderOrder = { order ->
                    viewModel.reorderOrder(order) {
                        navController.navigate(PharmacyRoutes.CART)
                    }
                },
                onBrowseProducts = {
                    navController.navigate(PharmacyRoutes.searchRoute("all"))
                },
                onRetry = viewModel::retryAll
            )
        }
    }
}
