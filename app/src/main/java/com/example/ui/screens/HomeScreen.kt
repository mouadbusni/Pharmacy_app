package com.example.ui.screens

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.BoxWithConstraints
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.widthIn
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyListState
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Search
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import com.example.data.catalog.PharmacyCatalog
import com.example.data.model.Pharmacy
import com.example.data.model.PharmacySchedule
import com.example.data.model.PharmacyScheduleFilter
import com.example.data.model.Product
import com.example.data.update.AppUpdateInfo
import com.example.data.update.AppUpdateState
import com.example.ui.components.AppUpdateBannerCard
import com.example.ui.components.CategoryCircleItem
import com.example.ui.components.HomeQuickShortcutsRow
import com.example.ui.components.PharmacyListCard
import com.example.ui.components.PharmacyScheduleFilterRow
import com.example.ui.components.ProductCard
import com.example.ui.components.SectionHeaderRow
import com.example.ui.theme.BorderSubtle
import com.example.ui.theme.ForestGreen
import com.example.ui.theme.LocalAppStrings
import com.example.ui.theme.NeutralBackground
import com.example.ui.theme.PureWhite
import com.example.ui.theme.TextSecondary

@Composable
fun HomeScreen(
    selectedLocation: String,
    selectedCategoryId: String,
    favoriteProductIds: Set<String>,
    onSelectCategory: (String) -> Unit,
    onSelectLocation: (String) -> Unit,
    onOpenSearch: (String) -> Unit,
    onOpenNotifications: () -> Unit,
    onOpenChats: () -> Unit,
    onOpenProduct: (Product) -> Unit,
    onOpenPharmacy: (Pharmacy) -> Unit,
    onOpenPharmaciesList: () -> Unit,
    onOpen24hPharmacies: () -> Unit,
    onAddToCart: (Product) -> Unit,
    onToggleFavorite: (Product, Boolean) -> Unit,
    updateState: AppUpdateState = AppUpdateState.Idle,
    onUpdateNow: (AppUpdateInfo) -> Unit = {},
    onDismissUpdate: (String) -> Unit = {},
    listState: LazyListState = rememberLazyListState(),
    modifier: Modifier = Modifier
) {
    val strings = LocalAppStrings.current
    var showLocationSheet by remember { mutableStateOf(false) }
    var selectedScheduleFilter by rememberSaveable { mutableStateOf(PharmacyScheduleFilter.ALL) }

    val filteredProducts = remember(selectedCategoryId) {
        if (selectedCategoryId == "all") {
            PharmacyCatalog.products.filter { it.isBestSeller }
        } else {
            PharmacyCatalog.products.filter { it.categoryId == selectedCategoryId }
        }
    }

    val localizedSelectedLocation = PharmacyCatalog.localizeLocation(
        locationAr = selectedLocation,
        isEnglish = strings.isEnglish
    )

    val filteredPharmacies = remember(selectedScheduleFilter) {
        when (selectedScheduleFilter) {
            PharmacyScheduleFilter.ALL -> PharmacyCatalog.pharmacies
            PharmacyScheduleFilter.ALWAYS_24H -> PharmacyCatalog.pharmacies.filter { it.isAlways24h }
            PharmacyScheduleFilter.DAY_DUTY -> PharmacyCatalog.pharmacies.filter { it.schedule == PharmacySchedule.DAY_DUTY }
        }
    }

    BoxWithConstraints(
        modifier = modifier
            .fillMaxSize()
            .background(NeutralBackground),
        contentAlignment = Alignment.TopCenter
    ) {
        val isWide = maxWidth >= 600.dp
        val maxContentWidth = if (isWide) 860.dp else 600.dp
        val bottomClearance = if (isWide) 28.dp else 112.dp
        val pharmaciesPreview = remember(filteredPharmacies) { filteredPharmacies.take(4) }

        LazyColumn(
            state = listState,
            modifier = Modifier
                .widthIn(max = maxContentWidth)
                .fillMaxSize(),
            contentPadding = PaddingValues(top = 12.dp, bottom = bottomClearance),
            verticalArrangement = Arrangement.spacedBy(18.dp)
        ) {
            item {
                HomeTopBar(
                    localizedSelectedLocation = localizedSelectedLocation,
                    onOpenLocationSheet = { showLocationSheet = true },
                    onOpenChats = onOpenChats,
                    onOpenNotifications = onOpenNotifications
                )
            }

            if (updateState is AppUpdateState.UpdateAvailable && !updateState.isDismissed) {
                item(key = "home_update_banner_item") {
                    AppUpdateBannerCard(
                        updateState = updateState,
                        onUpdateNow = onUpdateNow,
                        onDismiss = onDismissUpdate
                    )
                }
            }

            item {
                Surface(
                    onClick = { onOpenSearch("all") },
                    shape = RoundedCornerShape(18.dp),
                    color = PureWhite,
                    shadowElevation = 2.dp,
                    border = BorderStroke(1.dp, BorderSubtle),
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 18.dp)
                        .height(54.dp)
                        .testTag("home_search_bar")
                ) {
                    Row(
                        modifier = Modifier
                            .fillMaxSize()
                            .padding(horizontal = 10.dp),
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        Text(
                            text = strings.searchPlaceholder,
                            style = MaterialTheme.typography.bodyMedium,
                            color = TextSecondary,
                            maxLines = 1,
                            overflow = TextOverflow.Ellipsis,
                            modifier = Modifier
                                .weight(1f)
                                .padding(horizontal = 8.dp)
                        )
                        Surface(
                            shape = CircleShape,
                            color = ForestGreen,
                            modifier = Modifier.size(38.dp)
                        ) {
                            Box(contentAlignment = Alignment.Center) {
                                Icon(
                                    imageVector = Icons.Filled.Search,
                                    contentDescription = strings.searchAction,
                                    tint = PureWhite,
                                    modifier = Modifier.size(18.dp)
                                )
                            }
                        }
                    }
                }
            }

            item {
                HomeQuickShortcutsRow(
                    onOpenPharmaciesList = onOpenPharmaciesList,
                    onOpen24hPharmacies = onOpen24hPharmacies,
                    onOpenChats = onOpenChats
                )
            }

            item {
                HomePromoBanner(
                    isWide = isWide,
                    onOpenVitamins = { onOpenSearch("vitamins") }
                )
            }

            item {
                Column(verticalArrangement = Arrangement.spacedBy(10.dp)) {
                    SectionHeaderRow(
                        title = strings.categories,
                        onActionClick = { onOpenSearch(selectedCategoryId) },
                        modifier = Modifier.padding(horizontal = 18.dp)
                    )
                    LazyRow(
                        contentPadding = PaddingValues(horizontal = 16.dp),
                        horizontalArrangement = Arrangement.spacedBy(10.dp)
                    ) {
                        items(PharmacyCatalog.categories, key = { it.id }) { category ->
                            CategoryCircleItem(
                                category = category,
                                selected = selectedCategoryId == category.id,
                                onClick = { onSelectCategory(category.id) }
                            )
                        }
                    }
                }
            }

            item {
                Column(verticalArrangement = Arrangement.spacedBy(10.dp)) {
                    SectionHeaderRow(
                        title = strings.bestSellingProducts,
                        onActionClick = { onOpenSearch(selectedCategoryId) },
                        modifier = Modifier.padding(horizontal = 18.dp)
                    )
                    LazyRow(
                        contentPadding = PaddingValues(horizontal = 18.dp),
                        horizontalArrangement = Arrangement.spacedBy(12.dp)
                    ) {
                        items(filteredProducts, key = { it.id }) { product ->
                            val isFav = favoriteProductIds.contains(product.id)
                            ProductCard(
                                product = product,
                                isFavorite = isFav,
                                onCardClick = { onOpenProduct(product) },
                                onAddClick = { onAddToCart(product) },
                                onToggleFavorite = { onToggleFavorite(product, isFav) },
                                modifier = Modifier.width(if (isWide) 182.dp else 168.dp)
                            )
                        }
                    }
                }
            }

            item {
                Column(verticalArrangement = Arrangement.spacedBy(10.dp)) {
                    SectionHeaderRow(
                        title = strings.nearestPharmacies,
                        onActionClick = onOpenPharmaciesList,
                        modifier = Modifier.padding(horizontal = 18.dp)
                    )
                    PharmacyScheduleFilterRow(
                        selected = selectedScheduleFilter,
                        onSelect = { selectedScheduleFilter = it }
                    )
                }
            }

            if (isWide) {
                items(pharmaciesPreview.chunked(2)) { pair ->
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(horizontal = 18.dp),
                        horizontalArrangement = Arrangement.spacedBy(12.dp)
                    ) {
                        pair.forEach { pharmacy ->
                            PharmacyListCard(
                                pharmacy = pharmacy,
                                onClick = { onOpenPharmacy(pharmacy) },
                                modifier = Modifier.weight(1f)
                            )
                        }
                        if (pair.size == 1) Spacer(modifier = Modifier.weight(1f))
                    }
                }
            } else {
                items(pharmaciesPreview.take(3), key = { it.id }) { pharmacy ->
                    PharmacyListCard(
                        pharmacy = pharmacy,
                        onClick = { onOpenPharmacy(pharmacy) },
                        modifier = Modifier.padding(horizontal = 18.dp)
                    )
                }
            }
        }
    }

    if (showLocationSheet) {
        HomeDeliveryLocationSheet(
            selectedLocation = selectedLocation,
            onSelectLocation = onSelectLocation,
            onDismiss = { showLocationSheet = false }
        )
    }
}
