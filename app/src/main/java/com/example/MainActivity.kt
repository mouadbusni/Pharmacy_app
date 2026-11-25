package com.example

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.BackHandler
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.BoxWithConstraints
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Snackbar
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.key
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalLayoutDirection
import androidx.compose.ui.unit.LayoutDirection
import androidx.compose.ui.unit.dp
import androidx.lifecycle.ViewModelProvider.AndroidViewModelFactory.Companion.APPLICATION_KEY
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.lifecycle.viewmodel.initializer
import androidx.lifecycle.viewmodel.viewModelFactory
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.rememberNavController
import androidx.navigation.navArgument
import com.example.data.catalog.PharmacyCatalog
import com.example.data.repository.LocalAdminPharmacyRepository
import com.example.data.repository.PharmacyRepository
import com.example.ui.auth.LoginScreen
import com.example.ui.auth.SplashScreen
import com.example.ui.components.BottomTab
import com.example.ui.components.PharmacyBottomBar
import com.example.ui.components.PharmacyNavigationRail
import com.example.ui.screens.CartScreen
import com.example.ui.screens.ChatListScreen
import com.example.ui.screens.CheckoutScreen
import com.example.ui.screens.FavoritesScreen
import com.example.ui.screens.HomeScreen
import com.example.ui.screens.LiveChatScreen
import com.example.ui.screens.NotificationsScreen
import com.example.ui.screens.PharmaciesListScreen
import com.example.ui.screens.PharmacyProfileScreen
import com.example.ui.screens.ProductDetailScreen
import com.example.ui.screens.ProfileScreen
import com.example.ui.screens.SearchProductsScreen
import com.example.ui.theme.ArabicStrings
import com.example.ui.theme.EnglishStrings
import com.example.ui.theme.ForestGreen
import com.example.ui.theme.LocalAppStrings
import com.example.ui.theme.PureWhite
import com.example.ui.theme.SaydaliyatiTheme
import com.example.viewmodel.PharmacyViewModel
import com.example.viewmodel.UiState
import com.google.firebase.Firebase
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.auth
import com.google.firebase.firestore.FirebaseFirestore

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

    fun searchRoute(categoryId: String = "all"): String =
        "$SEARCH_PRODUCTS?category=$categoryId"
}

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            SaydaliyatiTheme {
                AppContent()
            }
        }
    }
}

@Composable
fun AppContent(auth: FirebaseAuth = Firebase.auth) {
    var currentUser by remember { mutableStateOf(auth.currentUser) }
    var showLoginFromSplash by remember { mutableStateOf(false) }
    var isAdminBypass by remember { mutableStateOf(false) }

    DisposableEffect(auth) {
        val listener = FirebaseAuth.AuthStateListener { firebaseAuth ->
            currentUser = firebaseAuth.currentUser
        }
        auth.addAuthStateListener(listener)
        onDispose { auth.removeAuthStateListener(listener) }
    }

    val user = currentUser
    if (user == null && !isAdminBypass) {
        CompositionLocalProvider(
            LocalLayoutDirection provides LayoutDirection.Rtl,
            LocalAppStrings provides ArabicStrings
        ) {
            if (!showLoginFromSplash) {
                SplashScreen(
                    onStartClicked = { showLoginFromSplash = true },
                    onAdminSkipSignIn = { isAdminBypass = true }
                )
            } else {
                BackHandler { showLoginFromSplash = false }
                LoginScreen(
                    onBack = { showLoginFromSplash = false },
                    onAuthSuccess = {
                        currentUser = Firebase.auth.currentUser
                    },
                    onAdminSkipSignIn = { isAdminBypass = true }
                )
            }
        }
    } else if (user == null && isAdminBypass) {
        key("admin_session") {
            AuthenticatedPharmacyApp(
                currentUserId = "admin_session",
                isAdminMode = true,
                onSignedOut = {
                    isAdminBypass = false
                    showLoginFromSplash = true
                }
            )
        }
    } else {
        val uid = user!!.uid
        key(uid) {
            AuthenticatedPharmacyApp(
                currentUserId = uid,
                isAdminMode = false,
                onSignedOut = {
                    showLoginFromSplash = true
                    currentUser = null
                }
            )
        }
    }
}

@Composable
private fun AuthenticatedPharmacyApp(
    currentUserId: String,
    isAdminMode: Boolean,
    onSignedOut: () -> Unit
) {
    val viewModel: PharmacyViewModel = viewModel(
        key = currentUserId,
        factory = viewModelFactory {
            initializer {
                val repo = if (isAdminMode) {
                    LocalAdminPharmacyRepository()
                } else {
                    val app = checkNotNull(this[APPLICATION_KEY]) {
                        "APPLICATION_KEY missing from CreationExtras"
                    }
                    val databaseId = app.getString(R.string.firestore_database_id)
                    val db = FirebaseFirestore.getInstance(databaseId)
                    PharmacyRepository(db)
                }
                PharmacyViewModel(
                    repository = repo,
                    currentUserId = currentUserId
                )
            }
        }
    )

    val profileState by viewModel.profileState.collectAsStateWithLifecycle()
    val cartState by viewModel.cartState.collectAsStateWithLifecycle()
    val favoritesState by viewModel.favoritesState.collectAsStateWithLifecycle()
    val ordersState by viewModel.ordersState.collectAsStateWithLifecycle()
    val recentSearchesState by viewModel.recentSearchesState.collectAsStateWithLifecycle()
    val selectedHomeCategory by viewModel.selectedHomeCategory.collectAsStateWithLifecycle()
    val isSubmittingOrder by viewModel.isSubmittingOrder.collectAsStateWithLifecycle()
    val actionMessage by viewModel.actionMessage.collectAsStateWithLifecycle()

    val profile = (profileState as? UiState.Success)?.data
    val cartItems = (cartState as? UiState.Success)?.data ?: emptyList()
    val favoriteIds = ((favoritesState as? UiState.Success)?.data ?: emptyList())
        .map { it.productId }
        .toSet()
    val cartCount = cartItems.sumOf { it.quantity }.toInt()

    val isEnglish = profile?.language == "en"
    val appStrings = if (isEnglish) EnglishStrings else ArabicStrings
    val layoutDirection = if (isEnglish) LayoutDirection.Ltr else LayoutDirection.Rtl

    val navController = rememberNavController()
    val navBackStackEntry by navController.currentBackStackEntryAsState()
    val currentRoute = navBackStackEntry?.destination?.route ?: PharmacyRoutes.HOME
    val snackbarHostState = remember { SnackbarHostState() }

    LaunchedEffect(actionMessage) {
        val msg = actionMessage
        if (msg != null) {
            snackbarHostState.showSnackbar(msg)
            viewModel.clearActionMessage()
        }
    }

    val topLevelRoutes = setOf(
        PharmacyRoutes.HOME,
        PharmacyRoutes.NOTIFICATIONS,
        PharmacyRoutes.FAVORITES,
        PharmacyRoutes.PROFILE
    )

    val onNavigateTab: (BottomTab) -> Unit = { tab ->
        val targetRoute = when (tab) {
            BottomTab.HOME -> PharmacyRoutes.HOME
            BottomTab.NOTIFICATIONS -> PharmacyRoutes.NOTIFICATIONS
            BottomTab.FAVORITES -> PharmacyRoutes.FAVORITES
            BottomTab.PROFILE -> PharmacyRoutes.PROFILE
        }
        if (currentRoute != targetRoute) {
            navController.navigate(targetRoute) {
                popUpTo(PharmacyRoutes.HOME) { saveState = true }
                launchSingleTop = true
                restoreState = true
            }
        }
    }

    CompositionLocalProvider(
        LocalLayoutDirection provides layoutDirection,
        LocalAppStrings provides appStrings
    ) {
        BoxWithConstraints(modifier = Modifier.fillMaxSize()) {
            val isWide = maxWidth >= 600.dp
            val showTopLevelNav = currentRoute in topLevelRoutes

            Row(modifier = Modifier.fillMaxSize()) {
                if (showTopLevelNav && isWide) {
                    PharmacyNavigationRail(
                        currentRoute = currentRoute,
                        cartCount = cartCount,
                        onNavigateTab = onNavigateTab,
                        onOpenCart = { navController.navigate(PharmacyRoutes.CART) }
                    )
                }

                Box(
                    modifier = Modifier
                        .weight(1f)
                        .fillMaxSize()
                ) {
                    NavHost(
                        navController = navController,
                        startDestination = PharmacyRoutes.HOME
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
                                onOpenSettings = { navController.navigate(PharmacyRoutes.PROFILE) },
                                onOpenProduct = { prod ->
                                    navController.navigate("${PharmacyRoutes.PRODUCT_DETAIL}/${prod.id}")
                                },
                                onOpenPharmacy = { pharmacy ->
                                    navController.navigate("${PharmacyRoutes.PHARMACY_PROFILE}/${pharmacy.id}")
                                },
                                onOpenPharmaciesList = {
                                    navController.navigate(PharmacyRoutes.PHARMACIES_LIST)
                                },
                                onAddToCart = { prod -> viewModel.addToCart(prod, 1) },
                                onToggleFavorite = viewModel::toggleFavorite
                            )
                        }

                        composable(PharmacyRoutes.NOTIFICATIONS) {
                            NotificationsScreen(
                                onBack = { navController.popBackStack() },
                                onOpenPromoVitamins = {
                                    navController.navigate(PharmacyRoutes.searchRoute("vitamins"))
                                },
                                onOpenChats = { navController.navigate(PharmacyRoutes.CHATS) },
                                onOpenPharmacies = { navController.navigate(PharmacyRoutes.PHARMACIES_LIST) }
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
                                onRetry = viewModel::retryAll
                            )
                        }

                        composable(PharmacyRoutes.PROFILE) {
                            ProfileScreen(
                                profile = profile,
                                ordersState = ordersState,
                                isAdminMode = isAdminMode,
                                onBack = { navController.popBackStack() },
                                onToggleLanguage = viewModel::toggleLanguage,
                                onToggleNotifications = viewModel::toggleNotifications,
                                onRetry = viewModel::retryAll,
                                onSignedOut = onSignedOut
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

                        composable(PharmacyRoutes.PHARMACIES_LIST) {
                            PharmaciesListScreen(
                                cartCount = cartCount,
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
                                    navController.navigate(PharmacyRoutes.PHARMACIES_LIST)
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
                                onSendMessage = { msg -> viewModel.sendMessage(pharmacy.id, msg) },
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
                                onConfirmOrder = { method, last4, onSuccess ->
                                    viewModel.placeOrder(
                                        paymentMethod = method,
                                        cartItems = cartItems,
                                        deliveryLocation = profile?.selectedLocation ?: "الموقع الحالي",
                                        cardLast4 = last4,
                                        onSuccess = onSuccess
                                    )
                                },
                                onOrderCompleteGoHome = {
                                    navController.navigate(PharmacyRoutes.HOME) {
                                        popUpTo(PharmacyRoutes.HOME) { inclusive = true }
                                    }
                                }
                            )
                        }
                    }

                    if (showTopLevelNav && !isWide) {
                        PharmacyBottomBar(
                            currentRoute = currentRoute,
                            cartCount = cartCount,
                            onNavigateTab = onNavigateTab,
                            onOpenCart = { navController.navigate(PharmacyRoutes.CART) },
                            modifier = Modifier.align(Alignment.BottomCenter)
                        )
                    }

                    SnackbarHost(
                        hostState = snackbarHostState,
                        modifier = Modifier
                            .align(Alignment.BottomCenter)
                            .navigationBarsPadding()
                            .padding(
                                bottom = if (showTopLevelNav && !isWide) 86.dp else 16.dp,
                                start = 16.dp,
                                end = 16.dp
                            )
                    ) { data ->
                        Snackbar(
                            snackbarData = data,
                            shape = RoundedCornerShape(14.dp),
                            containerColor = ForestGreen,
                            contentColor = PureWhite
                        )
                    }
                }
            }
        }
    }
}
