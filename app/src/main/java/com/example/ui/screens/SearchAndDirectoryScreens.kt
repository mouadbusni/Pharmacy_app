package com.example.ui.screens

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
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
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.Favorite
import androidx.compose.material.icons.filled.Notifications
import androidx.compose.material.icons.filled.Search
import androidx.compose.material.icons.filled.ShoppingBag
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import com.example.data.catalog.PharmacyCatalog
import com.example.data.model.Pharmacy
import com.example.data.model.Product
import com.example.data.model.RecentSearch
import com.example.ui.components.BackCircleButton
import com.example.ui.components.CategoryCircleItem
import com.example.ui.components.HeaderCircleIconButton
import com.example.ui.components.PharmacyListCard
import com.example.ui.components.ProductCard
import com.example.ui.components.SectionHeaderRow
import com.example.ui.theme.BorderSubtle
import com.example.ui.theme.ForestGreen
import com.example.ui.theme.MintSoft
import com.example.ui.theme.MintTeal
import com.example.ui.theme.NeutralBackground
import com.example.ui.theme.PureWhite
import com.example.ui.theme.TextPrimary
import com.example.ui.theme.TextSecondary

@Composable
fun SearchProductsScreen(
    recentSearches: List<RecentSearch>,
    favoriteProductIds: Set<String>,
    cartCount: Int,
    onBack: () -> Unit,
    onOpenCart: () -> Unit,
    onOpenProduct: (Product) -> Unit,
    onAddToCart: (Product) -> Unit,
    onToggleFavorite: (Product, Boolean) -> Unit,
    onSubmitSearch: (String) -> Unit,
    onDeleteRecentSearch: (String) -> Unit,
    modifier: Modifier = Modifier
) {
    var query by remember { mutableStateOf("") }
    var selectedCategoryId by remember { mutableStateOf("all") }

    val filteredProducts = remember(query, selectedCategoryId) {
        PharmacyCatalog.products.filter { prod ->
            val catMatch = selectedCategoryId == "all" || prod.categoryId == selectedCategoryId
            val textMatch = query.isBlank() ||
                prod.nameAr.contains(query.trim(), ignoreCase = true) ||
                prod.subtitleAr.contains(query.trim(), ignoreCase = true) ||
                prod.categoryAr.contains(query.trim(), ignoreCase = true)
            catMatch && textMatch
        }
    }

    Box(
        modifier = modifier
            .fillMaxSize()
            .background(NeutralBackground),
        contentAlignment = Alignment.TopCenter
    ) {
        LazyColumn(
            modifier = Modifier
                .widthIn(max = 600.dp)
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
                        text = "المنتجات",
                        style = MaterialTheme.typography.headlineSmall,
                        color = TextPrimary
                    )
                    HeaderCircleIconButton(
                        onClick = onOpenCart,
                        contentDescription = "السلة",
                        badgeCount = cartCount
                    ) {
                        Icon(
                            imageVector = Icons.Filled.ShoppingBag,
                            contentDescription = "السلة",
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
                            text = "أبحث عن دواء أو صيدلية....",
                            style = MaterialTheme.typography.bodyMedium,
                            color = TextSecondary
                        )
                    },
                    trailingIcon = {
                        Surface(
                            onClick = { onSubmitSearch(query) },
                            shape = CircleShape,
                            color = ForestGreen,
                            modifier = Modifier.size(36.dp)
                        ) {
                            Box(contentAlignment = Alignment.Center) {
                                Icon(
                                    imageVector = Icons.Filled.Search,
                                    contentDescription = "بحث",
                                    tint = PureWhite,
                                    modifier = Modifier.size(18.dp)
                                )
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

            if (recentSearches.isNotEmpty()) {
                item {
                    Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                        SectionHeaderRow(
                            title = "عمليات البحث الأخيرة",
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
                                        modifier = Modifier.padding(start = 12.dp, end = 4.dp, top = 4.dp, bottom = 4.dp)
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
                                            modifier = Modifier.size(28.dp)
                                        ) {
                                            Icon(
                                                imageVector = Icons.Filled.Close,
                                                contentDescription = "حذف البحث",
                                                tint = TextSecondary,
                                                modifier = Modifier.size(14.dp)
                                            )
                                        }
                                    }
                                }
                            }
                        }
                    }
                }
            }

            item {
                Column(verticalArrangement = Arrangement.spacedBy(10.dp)) {
                    SectionHeaderRow(
                        title = "الأقسام",
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
                }
            }

            item {
                val activeCatName = PharmacyCatalog.categories
                    .firstOrNull { it.id == selectedCategoryId }?.titleAr ?: "الأدوية"
                SectionHeaderRow(
                    title = if (selectedCategoryId == "all") "جميع المنتجات" else "قسم $activeCatName",
                    actionLabel = null,
                    modifier = Modifier.padding(horizontal = 18.dp)
                )
            }

            if (filteredProducts.isEmpty()) {
                item {
                    Surface(
                        shape = RoundedCornerShape(20.dp),
                        color = PureWhite,
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(horizontal = 18.dp)
                    ) {
                        Text(
                            text = "لا توجد منتجات مطابقة لبحثك حالياً.",
                            style = MaterialTheme.typography.bodyLarge,
                            color = TextSecondary,
                            textAlign = TextAlign.Center,
                            modifier = Modifier.padding(24.dp)
                        )
                    }
                }
            } else {
                items(filteredProducts.chunked(2)) { pair ->
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(horizontal = 18.dp),
                        horizontalArrangement = Arrangement.spacedBy(12.dp)
                    ) {
                        pair.forEach { prod ->
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
                        if (pair.size == 1) {
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
    onBack: () -> Unit,
    onOpenCart: () -> Unit,
    onOpenPharmacy: (Pharmacy) -> Unit,
    modifier: Modifier = Modifier
) {
    var query by remember { mutableStateOf("") }
    val filtered = remember(query) {
        if (query.isBlank()) PharmacyCatalog.pharmacies
        else PharmacyCatalog.pharmacies.filter {
            it.nameAr.contains(query.trim(), ignoreCase = true) ||
                it.areaAr.contains(query.trim(), ignoreCase = true)
        }
    }
    val openPharmacies = filtered.filter { it.openNow }
    val closedPharmacies = filtered.filter { !it.openNow }

    Box(
        modifier = modifier
            .fillMaxSize()
            .background(NeutralBackground),
        contentAlignment = Alignment.TopCenter
    ) {
        LazyColumn(
            modifier = Modifier
                .widthIn(max = 600.dp)
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
                        text = "الصيدليات",
                        style = MaterialTheme.typography.headlineSmall,
                        color = TextPrimary
                    )
                    HeaderCircleIconButton(
                        onClick = onOpenCart,
                        contentDescription = "السلة",
                        badgeCount = cartCount
                    ) {
                        Icon(
                            imageVector = Icons.Filled.ShoppingBag,
                            contentDescription = "السلة",
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
                            text = "أبحث عن دواء أو صيدلية....",
                            style = MaterialTheme.typography.bodyMedium,
                            color = TextSecondary
                        )
                    },
                    trailingIcon = {
                        Surface(
                            shape = CircleShape,
                            color = ForestGreen,
                            modifier = Modifier.size(36.dp)
                        ) {
                            Box(contentAlignment = Alignment.Center) {
                                Icon(
                                    imageVector = Icons.Filled.Search,
                                    contentDescription = "بحث",
                                    tint = PureWhite,
                                    modifier = Modifier.size(18.dp)
                                )
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
                )
            }

            if (openPharmacies.isNotEmpty()) {
                item {
                    SectionHeaderRow(
                        title = "الصيدليات القريبة منك",
                        actionLabel = null,
                        modifier = Modifier.padding(horizontal = 18.dp)
                    )
                }
                items(openPharmacies, key = { it.id }) { pharmacy ->
                    PharmacyListCard(
                        pharmacy = pharmacy,
                        onClick = { onOpenPharmacy(pharmacy) },
                        modifier = Modifier.padding(horizontal = 18.dp)
                    )
                }
            }

            if (closedPharmacies.isNotEmpty()) {
                item {
                    SectionHeaderRow(
                        title = "الصيدليات المغلقة",
                        actionLabel = null,
                        modifier = Modifier.padding(horizontal = 18.dp)
                    )
                }
                items(closedPharmacies, key = { it.id }) { pharmacy ->
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

@Composable
fun FavoritesScreen(
    favoriteProductIds: Set<String>,
    onBack: () -> Unit,
    onOpenProduct: (Product) -> Unit,
    onAddToCart: (Product) -> Unit,
    onToggleFavorite: (Product, Boolean) -> Unit,
    modifier: Modifier = Modifier
) {
    val favoriteProducts = remember(favoriteProductIds) {
        PharmacyCatalog.products.filter { favoriteProductIds.contains(it.id) }
    }

    Box(
        modifier = modifier
            .fillMaxSize()
            .background(NeutralBackground),
        contentAlignment = Alignment.TopCenter
    ) {
        LazyColumn(
            modifier = Modifier
                .widthIn(max = 600.dp)
                .fillMaxSize(),
            contentPadding = PaddingValues(top = 12.dp, bottom = 110.dp),
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
                        text = "المفضلة",
                        style = MaterialTheme.typography.headlineSmall,
                        color = TextPrimary
                    )
                    Spacer(modifier = Modifier.size(44.dp))
                }
            }

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
                                text = "لا توجد منتجات في المفضلة بعد",
                                style = MaterialTheme.typography.titleMedium,
                                color = TextPrimary
                            )
                            Spacer(modifier = Modifier.height(4.dp))
                            Text(
                                text = "اضغط على رمز القلب لحفظ الأدوية والفيتامينات التي تحتاجها باستمرار.",
                                style = MaterialTheme.typography.bodyMedium,
                                color = TextSecondary,
                                textAlign = TextAlign.Center
                            )
                        }
                    }
                }
            } else {
                items(favoriteProducts.chunked(2)) { pair ->
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(horizontal = 18.dp),
                        horizontalArrangement = Arrangement.spacedBy(12.dp)
                    ) {
                        pair.forEach { prod ->
                            ProductCard(
                                product = prod,
                                isFavorite = true,
                                onCardClick = { onOpenProduct(prod) },
                                onAddClick = { onAddToCart(prod) },
                                onToggleFavorite = { onToggleFavorite(prod, true) },
                                modifier = Modifier.weight(1f)
                            )
                        }
                        if (pair.size == 1) Spacer(modifier = Modifier.weight(1f))
                    }
                }
            }
        }
    }
}

@Composable
fun NotificationsScreen(
    onBack: () -> Unit,
    onOpenChats: () -> Unit,
    modifier: Modifier = Modifier
) {
    Box(
        modifier = modifier
            .fillMaxSize()
            .background(NeutralBackground),
        contentAlignment = Alignment.TopCenter
    ) {
        LazyColumn(
            modifier = Modifier
                .widthIn(max = 600.dp)
                .fillMaxSize(),
            contentPadding = PaddingValues(top = 12.dp, bottom = 110.dp),
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
                        text = "الإشعارات",
                        style = MaterialTheme.typography.headlineSmall,
                        color = TextPrimary
                    )
                    Spacer(modifier = Modifier.size(44.dp))
                }
            }

            items(PharmacyCatalog.sampleNotifications, key = { it.id }) { notif ->
                Surface(
                    onClick = onOpenChats,
                    shape = RoundedCornerShape(20.dp),
                    color = if (notif.unread) MintSoft else PureWhite,
                    border = BorderStroke(1.dp, BorderSubtle),
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 18.dp)
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
                                    text = notif.titleAr,
                                    style = MaterialTheme.typography.titleSmall,
                                    color = TextPrimary,
                                    fontWeight = FontWeight.Bold
                                )
                                Text(
                                    text = notif.timeAr,
                                    style = MaterialTheme.typography.labelSmall,
                                    color = TextSecondary
                                )
                            }
                            Spacer(modifier = Modifier.height(4.dp))
                            Text(
                                text = notif.bodyAr,
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
