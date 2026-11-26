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
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.widthIn
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyListState
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.Favorite
import androidx.compose.material.icons.filled.Info
import androidx.compose.material.icons.filled.Notifications
import androidx.compose.material.icons.filled.Search
import androidx.compose.material.icons.filled.ShoppingBag
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.minimumInteractiveComponentSize
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import com.example.data.catalog.PharmacyCatalog
import com.example.data.model.FavoriteItem
import com.example.data.model.NotificationTarget
import com.example.data.model.Pharmacy
import com.example.data.model.PharmacySchedule
import com.example.data.model.PharmacyScheduleFilter
import com.example.data.model.PrescriptionFilter
import com.example.data.model.Product
import com.example.data.model.RecentSearch
import com.example.ui.components.BackCircleButton
import com.example.ui.components.CategoryCircleItem
import com.example.ui.components.ErrorStateCard
import com.example.ui.components.HeaderCircleIconButton
import com.example.ui.components.LoadingStateCard
import com.example.ui.components.PharmacyListCard
import com.example.ui.components.PharmacyScheduleFilterRow
import com.example.ui.components.PrescriptionFilterRow
import com.example.ui.components.ProductCard
import com.example.ui.components.SectionHeaderRow
import com.example.ui.theme.BorderSubtle
import com.example.ui.theme.ForestGreen
import com.example.ui.theme.LocalAppStrings
import com.example.ui.theme.MintSoft
import com.example.ui.theme.MintTeal
import com.example.ui.theme.NeutralBackground
import com.example.ui.theme.PureWhite
import com.example.ui.theme.StatusDutyAmber
import com.example.ui.theme.StatusDutyBg
import com.example.ui.theme.TextPrimary
import com.example.ui.theme.TextSecondary
import com.example.viewmodel.UiState

@Composable
fun SearchProductsScreen(
    initialCategoryId: String,
    recentSearchesState: UiState<List<RecentSearch>>,
    favoriteProductIds: Set<String>,
    cartCount: Int,
    onBack: () -> Unit,
    onOpenCart: () -> Unit,
    onOpenProduct: (Product) -> Unit,
    onAddToCart: (Product) -> Unit,
    onToggleFavorite: (Product, Boolean) -> Unit,
    onSubmitSearch: (String) -> Unit,
    onDeleteRecentSearch: (String) -> Unit,
    onRetry: () -> Unit,
    modifier: Modifier = Modifier
) {
    val strings = LocalAppStrings.current
    var query by rememberSaveable { mutableStateOf("") }
    var selectedCategoryId by rememberSaveable(initialCategoryId) { mutableStateOf(initialCategoryId) }
    var selectedPrescriptionFilter by rememberSaveable { mutableStateOf(PrescriptionFilter.ALL) }

    val recentSearches = (recentSearchesState as? UiState.Success)?.data ?: emptyList()

    val filteredProducts = remember(query, selectedCategoryId, selectedPrescriptionFilter) {
        val trimmed = query.trim()
        PharmacyCatalog.products.filter { prod ->
            val catMatch = selectedCategoryId == "all" || prod.categoryId == selectedCategoryId
            val rxMatch = when (selectedPrescriptionFilter) {
                PrescriptionFilter.ALL -> true
                PrescriptionFilter.OTC -> !prod.requiresPrescription
                PrescriptionFilter.RX_REQUIRED -> prod.requiresPrescription
            }
            val textMatch = trimmed.isBlank() ||
                prod.nameAr.contains(trimmed, ignoreCase = true) ||
                prod.nameEn.contains(trimmed, ignoreCase = true) ||
                prod.subtitleAr.contains(trimmed, ignoreCase = true) ||
                prod.subtitleEn.contains(trimmed, ignoreCase = true) ||
                prod.categoryAr.contains(trimmed, ignoreCase = true) ||
                prod.categoryEn.contains(trimmed, ignoreCase = true)
            catMatch && rxMatch && textMatch
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
        val columns = if (isWide) 3 else 2

        LazyColumn(
            modifier = Modifier
                .widthIn(max = maxContentWidth)
                .fillMaxSize(),
            contentPadding = PaddingValues(top = 12.dp, bottom = 28.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            item {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .statusBarsPadding()
                        .padding(horizontal = 18.dp),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    BackCircleButton(onClick = onBack)
                    Text(
                        text = strings.productsTitle,
                        style = MaterialTheme.typography.headlineSmall,
                        color = TextPrimary
                    )
                    HeaderCircleIconButton(
                        onClick = onOpenCart,
                        contentDescription = strings.cartTitle,
                        badgeCount = cartCount
                    ) {
                        Icon(
                            imageVector = Icons.Filled.ShoppingBag,
                            contentDescription = strings.cartTitle,
                            tint = ForestGreen,
                            modifier = Modifier.size(20.dp)
                        )
                    }
                }
            }

            item {
                OutlinedTextField(
                    value = query,
                    onValueChange = { query = it },
                    placeholder = {
                        Text(
                            text = strings.searchProductsOrPharmacies,
                            style = MaterialTheme.typography.bodyMedium,
                            color = TextSecondary
                        )
                    },
                    trailingIcon = {
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.spacedBy(2.dp),
                            modifier = Modifier.padding(end = 6.dp)
                        ) {
                            if (query.isNotEmpty()) {
                                IconButton(
                                    onClick = { query = "" },
                                    modifier = Modifier
                                        .minimumInteractiveComponentSize()
                                        .testTag("products_search_clear")
                                ) {
                                    Icon(
                                        imageVector = Icons.Filled.Close,
                                        contentDescription = strings.clearSearch,
                                        tint = TextSecondary,
                                        modifier = Modifier.size(18.dp)
                                    )
                                }
                            }
                            Surface(
                                onClick = { onSubmitSearch(query) },
                                shape = CircleShape,
                                color = ForestGreen,
                                modifier = Modifier
                                    .minimumInteractiveComponentSize()
                                    .size(38.dp)
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
                    },
                    keyboardOptions = KeyboardOptions(imeAction = ImeAction.Search),
                    keyboardActions = KeyboardActions(onSearch = { onSubmitSearch(query) }),
                    singleLine = true,
                    shape = RoundedCornerShape(18.dp),
                    colors = OutlinedTextFieldDefaults.colors(
                        focusedContainerColor = PureWhite,
                        unfocusedContainerColor = PureWhite,
                        focusedBorderColor = ForestGreen,
                        unfocusedBorderColor = BorderSubtle
                    ),
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 18.dp)
                        .testTag("products_search_field")
                )
            }

            when (recentSearchesState) {
                is UiState.Error -> {
                    item {
                        ErrorStateCard(
                            errorMessage = recentSearchesState.message,
                            onRetry = onRetry
                        )
                    }
                }
                is UiState.Success -> {
                    if (recentSearches.isNotEmpty()) {
                        item {
                            Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                                SectionHeaderRow(
                                    title = strings.recentSearchesTitle,
                                    actionLabel = null,
                                    modifier = Modifier.padding(horizontal = 18.dp)
                                )
                                LazyRow(
                                    contentPadding = PaddingValues(horizontal = 18.dp),
                                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                                ) {
                                    items(recentSearches, key = { it.id.ifBlank { it.query } }) { chip ->
                                        Surface(
                                            onClick = { query = chip.query },
                                            shape = RoundedCornerShape(14.dp),
                                            color = PureWhite,
                                            border = BorderStroke(1.dp, BorderSubtle)
                                        ) {
                                            Row(
                                                verticalAlignment = Alignment.CenterVertically,
                                                modifier = Modifier.padding(start = 12.dp, end = 2.dp)
                                            ) {
                                                Text(
                                                    text = chip.query,
                                                    style = MaterialTheme.typography.labelMedium,
                                                    color = TextPrimary
                                                )
                                                IconButton(
                                                    onClick = {
                                                        if (chip.id.isNotBlank()) onDeleteRecentSearch(chip.id)
                                                    },
                                                    modifier = Modifier.minimumInteractiveComponentSize()
                                                ) {
                                                    Icon(
                                                        imageVector = Icons.Filled.Close,
                                                        contentDescription = strings.deleteSearch,
                                                        tint = TextSecondary,
                                                        modifier = Modifier.size(16.dp)
                                                    )
                                                }
                                            }
                                        }
                                    }
                                }
                            }
                        }
                    }
                }
                UiState.Loading -> Unit
            }

            item {
                Column(verticalArrangement = Arrangement.spacedBy(10.dp)) {
                    SectionHeaderRow(
                        title = strings.sections,
                        actionLabel = null,
                        modifier = Modifier.padding(horizontal = 18.dp)
                    )
                    LazyRow(
                        contentPadding = PaddingValues(horizontal = 16.dp),
                        horizontalArrangement = Arrangement.spacedBy(12.dp)
                    ) {
                        items(PharmacyCatalog.categories, key = { it.id }) { category ->
                            CategoryCircleItem(
                                category = category,
                                selected = selectedCategoryId == category.id,
                                onClick = { selectedCategoryId = category.id }
                            )
                        }
                    }
                    PrescriptionFilterRow(
                        selected = selectedPrescriptionFilter,
                        onSelect = { selectedPrescriptionFilter = it }
                    )
                }
            }

            item {
                val activeCatName = PharmacyCatalog.categories
                    .firstOrNull { it.id == selectedCategoryId }
                    ?.localizedTitle(strings.isEnglish) ?: strings.allProducts
                SectionHeaderRow(
                    title = if (selectedCategoryId == "all") {
                        strings.allProducts
                    } else {
                        "${strings.categoryPrefix}: $activeCatName"
                    },
                    actionLabel = null,
                    modifier = Modifier.padding(horizontal = 18.dp)
                )
            }

            if (filteredProducts.isEmpty()) {
                item {
                    Surface(
                        shape = RoundedCornerShape(20.dp),
                        color = PureWhite,
                        border = BorderStroke(1.dp, BorderSubtle),
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(horizontal = 18.dp)
                    ) {
                        Column(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(24.dp),
                            horizontalAlignment = Alignment.CenterHorizontally,
                            verticalArrangement = Arrangement.spacedBy(12.dp)
                        ) {
                            Text(
                                text = strings.noMatchingProducts,
                                style = MaterialTheme.typography.bodyLarge,
                                color = TextSecondary,
                                textAlign = TextAlign.Center
                            )
                            OutlinedButton(
                                onClick = {
                                    query = ""
                                    selectedCategoryId = "all"
                                    selectedPrescriptionFilter = PrescriptionFilter.ALL
                                },
                                shape = RoundedCornerShape(12.dp),
                                border = BorderStroke(1.dp, ForestGreen),
                                modifier = Modifier
                                    .minimumInteractiveComponentSize()
                                    .testTag("products_reset_filters")
                            ) {
                                Text(
                                    text = strings.resetFilters,
                                    color = ForestGreen,
                                    style = MaterialTheme.typography.labelLarge
                                )
                            }
                        }
                    }
                }
            } else {
                items(filteredProducts.chunked(columns)) { rowItems ->
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(horizontal = 18.dp),
                        horizontalArrangement = Arrangement.spacedBy(12.dp)
                    ) {
                        rowItems.forEach { prod ->
                            val isFav = favoriteProductIds.contains(prod.id)
                            ProductCard(
                                product = prod,
                                isFavorite = isFav,
                                onCardClick = { onOpenProduct(prod) },
                                onAddClick = { onAddToCart(prod) },
                                onToggleFavorite = { onToggleFavorite(prod, isFav) },
                                modifier = Modifier.weight(1f)
                            )
                        }
                        repeat(columns - rowItems.size) {
                            Spacer(modifier = Modifier.weight(1f))
                        }
                    }
                }
            }
        }
    }
}

@Composable
fun PharmaciesListScreen(
    cartCount: Int,
    initialScheduleFilter: PharmacyScheduleFilter = PharmacyScheduleFilter.ALL,
    onBack: () -> Unit,
    onOpenCart: () -> Unit,
    onOpenPharmacy: (Pharmacy) -> Unit,
    modifier: Modifier = Modifier
) {
    val strings = LocalAppStrings.current
    var query by rememberSaveable { mutableStateOf("") }
    var selectedScheduleFilter by rememberSaveable(initialScheduleFilter) {
        mutableStateOf(initialScheduleFilter)
    }

    val filtered = remember(query, selectedScheduleFilter) {
        val trimmed = query.trim()
        PharmacyCatalog.pharmacies.filter { pharmacy ->
            val scheduleMatch = when (selectedScheduleFilter) {
                PharmacyScheduleFilter.ALL -> true
                PharmacyScheduleFilter.ALWAYS_24H -> pharmacy.isAlways24h
                PharmacyScheduleFilter.DAY_DUTY -> pharmacy.schedule == PharmacySchedule.DAY_DUTY
            }
            val queryMatch = trimmed.isBlank() ||
                pharmacy.nameAr.contains(trimmed, ignoreCase = true) ||
                pharmacy.nameEn.contains(trimmed, ignoreCase = true) ||
                pharmacy.areaAr.contains(trimmed, ignoreCase = true) ||
                pharmacy.areaEn.contains(trimmed, ignoreCase = true) ||
                pharmacy.addressAr.contains(trimmed, ignoreCase = true) ||
                pharmacy.addressEn.contains(trimmed, ignoreCase = true)
            scheduleMatch && queryMatch
        }
    }
    val always24hPharmacies = filtered.filter { it.isAlways24h }
    val dayDutyPharmacies = filtered.filter { it.schedule == PharmacySchedule.DAY_DUTY }

    BoxWithConstraints(
        modifier = modifier
            .fillMaxSize()
            .background(NeutralBackground),
        contentAlignment = Alignment.TopCenter
    ) {
        val isWide = maxWidth >= 600.dp
        val maxContentWidth = if (isWide) 860.dp else 600.dp

        LazyColumn(
            modifier = Modifier
                .widthIn(max = maxContentWidth)
                .fillMaxSize(),
            contentPadding = PaddingValues(top = 12.dp, bottom = 28.dp),
            verticalArrangement = Arrangement.spacedBy(14.dp)
        ) {
            item {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .statusBarsPadding()
                        .padding(horizontal = 18.dp),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    BackCircleButton(onClick = onBack)
                    Text(
                        text = strings.pharmaciesTitle,
                        style = MaterialTheme.typography.headlineSmall,
                        color = TextPrimary
                    )
                    HeaderCircleIconButton(
                        onClick = onOpenCart,
                        contentDescription = strings.cartTitle,
                        badgeCount = cartCount
                    ) {
                        Icon(
                            imageVector = Icons.Filled.ShoppingBag,
                            contentDescription = strings.cartTitle,
                            tint = ForestGreen,
                            modifier = Modifier.size(20.dp)
                        )
                    }
                }
            }

            item {
                OutlinedTextField(
                    value = query,
                    onValueChange = { query = it },
                    placeholder = {
                        Text(
                            text = strings.searchProductsOrPharmacies,
                            style = MaterialTheme.typography.bodyMedium,
                            color = TextSecondary
                        )
                    },
                    trailingIcon = {
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.spacedBy(2.dp),
                            modifier = Modifier.padding(end = 6.dp)
                        ) {
                            if (query.isNotEmpty()) {
                                IconButton(
                                    onClick = { query = "" },
                                    modifier = Modifier
                                        .minimumInteractiveComponentSize()
                                        .testTag("pharmacies_search_clear")
                                ) {
                                    Icon(
                                        imageVector = Icons.Filled.Close,
                                        contentDescription = strings.clearSearch,
                                        tint = TextSecondary,
                                        modifier = Modifier.size(18.dp)
                                    )
                                }
                            }
                            Surface(
                                shape = CircleShape,
                                color = ForestGreen,
                                modifier = Modifier.size(36.dp)
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
                    },
                    singleLine = true,
                    shape = RoundedCornerShape(18.dp),
                    colors = OutlinedTextFieldDefaults.colors(
                        focusedContainerColor = PureWhite,
                        unfocusedContainerColor = PureWhite,
                        focusedBorderColor = ForestGreen,
                        unfocusedBorderColor = BorderSubtle
                    ),
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 18.dp)
                        .testTag("pharmacies_search_field")
                )
            }

            item {
                PharmacyScheduleFilterRow(
                    selected = selectedScheduleFilter,
                    onSelect = { selectedScheduleFilter = it }
                )
            }

            item {
                Surface(
                    shape = RoundedCornerShape(16.dp),
                    color = StatusDutyBg.copy(alpha = 0.72f),
                    border = BorderStroke(1.dp, StatusDutyAmber.copy(alpha = 0.3f)),
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 18.dp)
                ) {
                    Row(
                        modifier = Modifier.padding(horizontal = 14.dp, vertical = 10.dp),
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(10.dp)
                    ) {
                        Icon(
                            imageVector = Icons.Filled.Info,
                            contentDescription = null,
                            tint = StatusDutyAmber,
                            modifier = Modifier.size(20.dp)
                        )
                        Text(
                            text = strings.gardeAdvisory,
                            style = MaterialTheme.typography.bodySmall,
                            color = TextPrimary,
                            fontWeight = FontWeight.Medium
                        )
                    }
                }
            }

            if (filtered.isEmpty()) {
                item {
                    Surface(
                        shape = RoundedCornerShape(20.dp),
                        color = PureWhite,
                        border = BorderStroke(1.dp, BorderSubtle),
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(horizontal = 18.dp)
                    ) {
                        Column(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(24.dp),
                            horizontalAlignment = Alignment.CenterHorizontally,
                            verticalArrangement = Arrangement.spacedBy(12.dp)
                        ) {
                            Text(
                                text = strings.noMatchingPharmacies,
                                style = MaterialTheme.typography.bodyLarge,
                                color = TextSecondary,
                                textAlign = TextAlign.Center
                            )
                            OutlinedButton(
                                onClick = {
                                    query = ""
                                    selectedScheduleFilter = PharmacyScheduleFilter.ALL
                                },
                                shape = RoundedCornerShape(12.dp),
                                border = BorderStroke(1.dp, ForestGreen),
                                modifier = Modifier
                                    .minimumInteractiveComponentSize()
                                    .testTag("pharmacies_reset_filters")
                            ) {
                                Text(
                                    text = strings.resetFilters,
                                    color = ForestGreen,
                                    style = MaterialTheme.typography.labelLarge
                                )
                            }
                        }
                    }
                }
            }

            if (always24hPharmacies.isNotEmpty()) {
                item {
                    SectionHeaderRow(
                        title = strings.schedule24h,
                        actionLabel = null,
                        modifier = Modifier.padding(horizontal = 18.dp)
                    )
                }
                if (isWide) {
                    items(always24hPharmacies.chunked(2)) { pair ->
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
                    items(always24hPharmacies, key = { it.id }) { pharmacy ->
                        PharmacyListCard(
                            pharmacy = pharmacy,
                            onClick = { onOpenPharmacy(pharmacy) },
                            modifier = Modifier.padding(horizontal = 18.dp)
                        )
                    }
                }
            }

            if (dayDutyPharmacies.isNotEmpty()) {
                item {
                    SectionHeaderRow(
                        title = strings.scheduleDayDuty,
                        actionLabel = null,
                        modifier = Modifier.padding(horizontal = 18.dp)
                    )
                }
                if (isWide) {
                    items(dayDutyPharmacies.chunked(2)) { pair ->
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
                    items(dayDutyPharmacies, key = { it.id }) { pharmacy ->
                        PharmacyListCard(
                            pharmacy = pharmacy,
                            onClick = { onOpenPharmacy(pharmacy) },
                            modifier = Modifier.padding(horizontal = 18.dp)
                        )
                    }
                }
            }
        }
    }
}

@Composable
fun FavoritesScreen(
    favoritesState: UiState<List<FavoriteItem>>,
    onBack: () -> Unit,
    onOpenProduct: (Product) -> Unit,
    onAddToCart: (Product) -> Unit,
    onToggleFavorite: (Product, Boolean) -> Unit,
    onRetry: () -> Unit,
    listState: LazyListState = rememberLazyListState(),
    modifier: Modifier = Modifier
) {
    val strings = LocalAppStrings.current
    val favoriteProductIds = remember(favoritesState) {
        ((favoritesState as? UiState.Success)?.data ?: emptyList())
            .map { it.productId }
            .toSet()
    }
    val favoriteProducts = remember(favoriteProductIds) {
        PharmacyCatalog.products.filter { favoriteProductIds.contains(it.id) }
    }

    BoxWithConstraints(
        modifier = modifier
            .fillMaxSize()
            .background(NeutralBackground),
        contentAlignment = Alignment.TopCenter
    ) {
        val isWide = maxWidth >= 600.dp
        val maxContentWidth = if (isWide) 860.dp else 600.dp
        val columns = if (isWide) 3 else 2
        val bottomClearance = if (isWide) 28.dp else 112.dp

        LazyColumn(
            state = listState,
            modifier = Modifier
                .widthIn(max = maxContentWidth)
                .fillMaxSize(),
            contentPadding = PaddingValues(top = 12.dp, bottom = bottomClearance),
            verticalArrangement = Arrangement.spacedBy(14.dp)
        ) {
            item {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .statusBarsPadding()
                        .padding(horizontal = 18.dp),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    BackCircleButton(onClick = onBack)
                    Text(
                        text = strings.tabFavorites,
                        style = MaterialTheme.typography.headlineSmall,
                        color = TextPrimary
                    )
                    Spacer(modifier = Modifier.size(48.dp))
                }
            }

            when (favoritesState) {
                UiState.Loading -> {
                    item { LoadingStateCard() }
                }
                is UiState.Error -> {
                    item {
                        ErrorStateCard(
                            errorMessage = favoritesState.message,
                            onRetry = onRetry
                        )
                    }
                }
                is UiState.Success -> {
                    if (favoriteProducts.isEmpty()) {
                        item {
                            Surface(
                                shape = RoundedCornerShape(22.dp),
                                color = PureWhite,
                                border = BorderStroke(1.dp, BorderSubtle),
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .padding(horizontal = 18.dp, vertical = 20.dp)
                            ) {
                                Column(
                                    modifier = Modifier.padding(28.dp),
                                    horizontalAlignment = Alignment.CenterHorizontally
                                ) {
                                    Icon(
                                        imageVector = Icons.Filled.Favorite,
                                        contentDescription = null,
                                        tint = MintTeal,
                                        modifier = Modifier.size(42.dp)
                                    )
                                    Spacer(modifier = Modifier.height(10.dp))
                                    Text(
                                        text = strings.emptyFavoritesTitle,
                                        style = MaterialTheme.typography.titleMedium,
                                        color = TextPrimary,
                                        textAlign = TextAlign.Center
                                    )
                                    Spacer(modifier = Modifier.height(4.dp))
                                    Text(
                                        text = strings.emptyFavoritesBody,
                                        style = MaterialTheme.typography.bodyMedium,
                                        color = TextSecondary,
                                        textAlign = TextAlign.Center
                                    )
                                }
                            }
                        }
                    } else {
                        items(favoriteProducts.chunked(columns)) { rowItems ->
                            Row(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .padding(horizontal = 18.dp),
                                horizontalArrangement = Arrangement.spacedBy(12.dp)
                            ) {
                                rowItems.forEach { prod ->
                                    ProductCard(
                                        product = prod,
                                        isFavorite = true,
                                        onCardClick = { onOpenProduct(prod) },
                                        onAddClick = { onAddToCart(prod) },
                                        onToggleFavorite = { onToggleFavorite(prod, true) },
                                        modifier = Modifier.weight(1f)
                                    )
                                }
                                repeat(columns - rowItems.size) {
                                    Spacer(modifier = Modifier.weight(1f))
                                }
                            }
                        }
                    }
                }
            }
        }
    }
}

@Composable
fun NotificationsScreen(
    onBack: () -> Unit,
    onOpenPromoVitamins: () -> Unit,
    onOpenChats: () -> Unit,
    onOpenPharmacies: () -> Unit,
    listState: LazyListState = rememberLazyListState(),
    modifier: Modifier = Modifier
) {
    val strings = LocalAppStrings.current
    BoxWithConstraints(
        modifier = modifier
            .fillMaxSize()
            .background(NeutralBackground),
        contentAlignment = Alignment.TopCenter
    ) {
        val isWide = maxWidth >= 600.dp
        val maxContentWidth = if (isWide) 800.dp else 600.dp
        val bottomClearance = if (isWide) 28.dp else 112.dp

        LazyColumn(
            state = listState,
            modifier = Modifier
                .widthIn(max = maxContentWidth)
                .fillMaxSize(),
            contentPadding = PaddingValues(top = 12.dp, bottom = bottomClearance),
            verticalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            item {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .statusBarsPadding()
                        .padding(horizontal = 18.dp),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    BackCircleButton(onClick = onBack)
                    Text(
                        text = strings.notificationsTitle,
                        style = MaterialTheme.typography.headlineSmall,
                        color = TextPrimary
                    )
                    Spacer(modifier = Modifier.size(48.dp))
                }
            }

            items(PharmacyCatalog.sampleNotifications, key = { it.id }) { notif ->
                Surface(
                    onClick = {
                        when (notif.target) {
                            NotificationTarget.PROMO_VITAMINS -> onOpenPromoVitamins()
                            NotificationTarget.CHATS -> onOpenChats()
                            NotificationTarget.PHARMACIES -> onOpenPharmacies()
                        }
                    },
                    shape = RoundedCornerShape(20.dp),
                    color = if (notif.unread) MintSoft else PureWhite,
                    border = BorderStroke(1.dp, BorderSubtle),
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 18.dp)
                        .testTag("notification_card_${notif.id}")
                ) {
                    Row(
                        modifier = Modifier.padding(16.dp),
                        verticalAlignment = Alignment.Top
                    ) {
                        Surface(
                            shape = CircleShape,
                            color = ForestGreen.copy(alpha = 0.12f),
                            modifier = Modifier.size(42.dp)
                        ) {
                            Box(contentAlignment = Alignment.Center) {
                                Icon(
                                    imageVector = Icons.Filled.Notifications,
                                    contentDescription = null,
                                    tint = ForestGreen,
                                    modifier = Modifier.size(20.dp)
                                )
                            }
                        }
                        Spacer(modifier = Modifier.width(12.dp))
                        Column(modifier = Modifier.weight(1f)) {
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.SpaceBetween
                            ) {
                                Text(
                                    text = notif.localizedTitle(strings.isEnglish),
                                    style = MaterialTheme.typography.titleSmall,
                                    color = TextPrimary,
                                    fontWeight = FontWeight.Bold,
                                    modifier = Modifier.weight(1f)
                                )
                                Spacer(modifier = Modifier.width(8.dp))
                                Text(
                                    text = notif.localizedTime(strings.isEnglish),
                                    style = MaterialTheme.typography.labelSmall,
                                    color = TextSecondary
                                )
                            }
                            Spacer(modifier = Modifier.height(4.dp))
                            Text(
                                text = notif.localizedBody(strings.isEnglish),
                                style = MaterialTheme.typography.bodyMedium,
                                color = TextSecondary
                            )
                        }
                    }
                }
            }
        }
    }
}
