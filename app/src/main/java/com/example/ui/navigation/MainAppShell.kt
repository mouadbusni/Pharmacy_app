package com.example.ui.navigation

import android.content.Intent
import android.net.Uri
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.BoxWithConstraints
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Snackbar
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalLayoutDirection
import androidx.compose.ui.unit.LayoutDirection
import androidx.compose.ui.unit.dp
import androidx.lifecycle.ViewModelProvider.AndroidViewModelFactory.Companion.APPLICATION_KEY
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.lifecycle.viewmodel.initializer
import androidx.lifecycle.viewmodel.viewModelFactory
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.rememberNavController
import com.example.R
import com.example.data.notifications.NotificationPreferencesStore
import com.example.data.repository.LocalAdminPharmacyRepository
import com.example.data.repository.PharmacyRepository
import com.example.data.update.AppUpdateInfo
import com.example.data.update.UpdatePreferencesStore
import com.example.ui.components.BottomTab
import com.example.ui.components.PharmacyBottomBar
import com.example.ui.components.PharmacyNavigationRail
import com.example.ui.theme.ArabicStrings
import com.example.ui.theme.EnglishStrings
import com.example.ui.theme.ForestGreen
import com.example.ui.theme.LocalAppStrings
import com.example.ui.theme.PureWhite
import com.example.viewmodel.PharmacyViewModel
import com.example.viewmodel.UiState
import com.google.firebase.Firebase
import com.google.firebase.FirebaseApp
import com.google.firebase.auth.auth
import com.google.firebase.firestore.FirebaseFirestore

@Composable
internal fun AuthenticatedPharmacyApp(
    currentUserId: String,
    isAdminMode: Boolean,
    onSignedOut: () -> Unit
) {
    val viewModel: PharmacyViewModel = viewModel(
        key = currentUserId,
        factory = viewModelFactory {
            initializer {
                val app = checkNotNull(this[APPLICATION_KEY]) {
                    "APPLICATION_KEY missing from CreationExtras"
                }
                val updateStore = UpdatePreferencesStore(app.applicationContext)
                val notificationStore = NotificationPreferencesStore(app.applicationContext)
                val repo = if (isAdminMode) {
                    LocalAdminPharmacyRepository()
                } else {
                    val databaseId = app.getString(R.string.firestore_database_id)
                    val db = FirebaseFirestore.getInstance(databaseId)
                    PharmacyRepository(db)
                }
                PharmacyViewModel(
                    repository = repo,
                    currentUserId = currentUserId,
                    updatePreferencesStore = updateStore,
                    notificationPreferencesStore = notificationStore
                )
            }
        }
    )

    val context = LocalContext.current
    val currentAuthUser = remember(context, isAdminMode, currentUserId) {
        if (isAdminMode || FirebaseApp.getApps(context).isEmpty()) null else Firebase.auth.currentUser
    }
    val profileState by viewModel.profileState.collectAsStateWithLifecycle()
    val cartState by viewModel.cartState.collectAsStateWithLifecycle()
    val favoritesState by viewModel.favoritesState.collectAsStateWithLifecycle()
    val ordersState by viewModel.ordersState.collectAsStateWithLifecycle()
    val recentSearchesState by viewModel.recentSearchesState.collectAsStateWithLifecycle()
    val selectedHomeCategory by viewModel.selectedHomeCategory.collectAsStateWithLifecycle()
    val isSubmittingOrder by viewModel.isSubmittingOrder.collectAsStateWithLifecycle()
    val actionMessage by viewModel.actionMessage.collectAsStateWithLifecycle()
    val configuredRepo by viewModel.configuredRepo.collectAsStateWithLifecycle()
    val appUpdateState by viewModel.appUpdateState.collectAsStateWithLifecycle()
    val notifications by viewModel.notificationsState.collectAsStateWithLifecycle()
    val unreadNotificationsCount by viewModel.unreadNotificationsCount.collectAsStateWithLifecycle()

    val onLaunchUpdateDownload: (AppUpdateInfo) -> Unit = { info ->
        val intent = Intent(Intent.ACTION_VIEW, Uri.parse(info.apkDownloadUrl))
        runCatching { context.startActivity(intent) }
    }

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
    val homeListState = rememberLazyListState()
    val notificationsListState = rememberLazyListState()
    val favoritesListState = rememberLazyListState()
    val profileScrollState = rememberScrollState()

    LaunchedEffect(actionMessage) {
        val msg = actionMessage
        if (msg != null) {
            snackbarHostState.showSnackbar(msg)
            viewModel.clearActionMessage()
        }
    }

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
            val showTopLevelNav = currentRoute in PharmacyRoutes.topLevelRoutes

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
                    PharmacyNavGraph(
                        navController = navController,
                        viewModel = viewModel,
                        profile = profile,
                        cartState = cartState,
                        cartItems = cartItems,
                        cartCount = cartCount,
                        favoritesState = favoritesState,
                        favoriteIds = favoriteIds,
                        ordersState = ordersState,
                        recentSearchesState = recentSearchesState,
                        notifications = notifications,
                        unreadNotificationsCount = unreadNotificationsCount,
                        selectedHomeCategory = selectedHomeCategory,
                        isSubmittingOrder = isSubmittingOrder,
                        isAdminMode = isAdminMode,
                        authDisplayName = currentAuthUser?.displayName,
                        authEmail = currentAuthUser?.email,
                        authPhotoUrl = currentAuthUser?.photoUrl?.toString(),
                        configuredRepo = configuredRepo,
                        appUpdateState = appUpdateState,
                        onLaunchUpdateDownload = onLaunchUpdateDownload,
                        onSignedOut = onSignedOut,
                        homeListState = homeListState,
                        notificationsListState = notificationsListState,
                        favoritesListState = favoritesListState,
                        profileScrollState = profileScrollState
                    )

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
