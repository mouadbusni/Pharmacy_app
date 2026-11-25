package com.example.ui.screens

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
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
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.KeyboardArrowDown
import androidx.compose.material.icons.filled.LocationOn
import androidx.compose.material.icons.filled.Notifications
import androidx.compose.material.icons.filled.Search
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.example.R
import com.example.data.catalog.PharmacyCatalog
import com.example.data.model.Pharmacy
import com.example.data.model.Product
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

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun HomeScreen(
    selectedLocation: String,
    selectedCategoryId: String,
    favoriteProductIds: Set<String>,
    onSelectCategory: (String) -> Unit,
    onSelectLocation: (String) -> Unit,
    onOpenSearch: () -> Unit,
    onOpenNotifications: () -> Unit,
    onOpenSettings: () -> Unit,
    onOpenProduct: (Product) -> Unit,
    onOpenPharmacy: (Pharmacy) -> Unit,
    onOpenPharmaciesList: () -> Unit,
    onAddToCart: (Product) -> Unit,
    onToggleFavorite: (Product, Boolean) -> Unit,
    modifier: Modifier = Modifier
) {
    var showLocationSheet by remember { mutableStateOf(false) }

    val filteredProducts = remember(selectedCategoryId) {
        if (selectedCategoryId == "all") {
            PharmacyCatalog.products.filter { it.isBestSeller }
        } else {
            PharmacyCatalog.products.filter { it.categoryId == selectedCategoryId }
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
            contentPadding = PaddingValues(
                top = 12.dp,
                bottom = 110.dp
            ),
            verticalArrangement = Arrangement.spacedBy(18.dp)
        ) {
            item {
                // Top Location + Actions Bar
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .statusBarsPadding()
                        .padding(horizontal = 18.dp),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        modifier = Modifier
                            .clip(RoundedCornerShape(14.dp))
                            .clickable { showLocationSheet = true }
                            .padding(vertical = 4.dp, horizontal = 6.dp)
                            .testTag("location_selector")
                    ) {
                        Icon(
                            imageVector = Icons.Filled.LocationOn,
                            contentDescription = "الموقع",
                            tint = ForestGreen,
                            modifier = Modifier.size(22.dp)
                        )
                        Spacer(modifier = Modifier.width(8.dp))
                        Column {
                            Text(
                                text = "التوصيل إلى",
                                style = MaterialTheme.typography.labelSmall,
                                color = TextSecondary
                            )
                            Row(verticalAlignment = Alignment.CenterVertically) {
                                Text(
                                    text = selectedLocation,
                                    style = MaterialTheme.typography.titleSmall,
                                    color = TextPrimary
                                )
                                Spacer(modifier = Modifier.width(2.dp))
                                Icon(
                                    imageVector = Icons.Filled.KeyboardArrowDown,
                                    contentDescription = null,
                                    tint = TextPrimary,
                                    modifier = Modifier.size(18.dp)
                                )
                            }
                        }
                    }

                    Row(horizontalArrangement = Arrangement.spacedBy(10.dp)) {
                        HeaderCircleIconButton(
                            onClick = onOpenNotifications,
                            contentDescription = "الإشعارات",
                            badgeCount = 2,
                            modifier = Modifier.testTag("home_notifications_button")
                        ) {
                            Icon(
                                imageVector = Icons.Filled.Notifications,
                                contentDescription = "الإشعارات",
                                tint = ForestGreen,
                                modifier = Modifier.size(20.dp)
                            )
                        }
                        HeaderCircleIconButton(
                            onClick = onOpenSettings,
                            contentDescription = "الإعدادات",
                            modifier = Modifier.testTag("home_settings_button")
                        ) {
                            Icon(
                                imageVector = Icons.Filled.Settings,
                                contentDescription = "الإعدادات",
                                tint = ForestGreen,
                                modifier = Modifier.size(20.dp)
                            )
                        }
                    }
                }
            }

            item {
                // Search Bar Pill
                Surface(
                    onClick = onOpenSearch,
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
                            text = "أبحث عن دواء أو صيدلية....",
                            style = MaterialTheme.typography.bodyMedium,
                            color = TextSecondary,
                            modifier = Modifier.padding(horizontal = 8.dp)
                        )
                        Surface(
                            shape = CircleShape,
                            color = ForestGreen,
                            modifier = Modifier.size(38.dp)
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
                    }
                }
            }

            item {
                // Promo Banner Card matching mockup
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 18.dp)
                        .height(152.dp)
                        .clip(RoundedCornerShape(22.dp))
                        .clickable(onClick = onOpenSearch)
                        .testTag("home_promo_banner")
                ) {
                    Image(
                        painter = painterResource(id = R.drawable.img_promo_banner),
                        contentDescription = "عرض خصومات 40%",
                        contentScale = ContentScale.Crop,
                        modifier = Modifier.fillMaxSize()
                    )
                    Column(
                        modifier = Modifier
                            .align(Alignment.CenterStart)
                            .fillMaxWidth(0.60f)
                            .fillMaxSize()
                            .padding(horizontal = 16.dp, vertical = 14.dp),
                        verticalArrangement = Arrangement.SpaceBetween,
                        horizontalAlignment = Alignment.Start
                    ) {
                        Surface(
                            shape = RoundedCornerShape(10.dp),
                            color = MintTeal
                        ) {
                            Text(
                                text = "خصومات 40 %",
                                style = MaterialTheme.typography.labelMedium,
                                color = PureWhite,
                                fontWeight = FontWeight.Bold,
                                modifier = Modifier.padding(horizontal = 10.dp, vertical = 4.dp)
                            )
                        }

                        Column {
                            Text(
                                text = "صحة أفضل تبدأ بعرض أفضل",
                                style = MaterialTheme.typography.titleLarge,
                                color = PureWhite,
                                fontWeight = FontWeight.Bold
                            )
                            Spacer(modifier = Modifier.height(4.dp))
                            Text(
                                text = "وفر أكثر وأهتم بصحتك بأقل تكلفة",
                                style = MaterialTheme.typography.bodySmall,
                                color = PureWhite.copy(alpha = 0.92f)
                            )
                        }
                    }
                }
            }

            item {
                Column(verticalArrangement = Arrangement.spacedBy(10.dp)) {
                    SectionHeaderRow(
                        title = "الأصناف",
                        actionLabel = "عرض المزيد",
                        onActionClick = onOpenSearch,
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
                                onClick = { onSelectCategory(category.id) }
                            )
                        }
                    }
                }
            }

            item {
                Column(verticalArrangement = Arrangement.spacedBy(10.dp)) {
                    SectionHeaderRow(
                        title = "المنتجات الأكثر طلباً",
                        actionLabel = "عرض المزيد",
                        onActionClick = onOpenSearch,
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
                                modifier = Modifier.width(168.dp)
                            )
                        }
                    }
                }
            }

            item {
                SectionHeaderRow(
                    title = "الصيدليات الأقرب إليك",
                    actionLabel = "عرض المزيد",
                    onActionClick = onOpenPharmaciesList,
                    modifier = Modifier.padding(horizontal = 18.dp)
                )
            }

            items(PharmacyCatalog.pharmacies.take(3), key = { it.id }) { pharmacy ->
                PharmacyListCard(
                    pharmacy = pharmacy,
                    onClick = { onOpenPharmacy(pharmacy) },
                    modifier = Modifier.padding(horizontal = 18.dp)
                )
            }
        }
    }

    if (showLocationSheet) {
        ModalBottomSheet(
            onDismissRequest = { showLocationSheet = false },
            containerColor = PureWhite
        ) {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 20.dp, vertical = 12.dp)
            ) {
                Text(
                    text = "اختر عنوان التوصيل",
                    style = MaterialTheme.typography.titleLarge,
                    color = TextPrimary
                )
                Spacer(modifier = Modifier.height(12.dp))
                PharmacyCatalog.deliveryLocations.forEach { loc ->
                    val isSelected = loc == selectedLocation
                    Surface(
                        onClick = {
                            onSelectLocation(loc)
                            showLocationSheet = false
                        },
                        shape = RoundedCornerShape(14.dp),
                        color = if (isSelected) MintSoft else NeutralBackground,
                        border = if (isSelected) BorderStroke(1.5.dp, ForestGreen) else null,
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(vertical = 4.dp)
                    ) {
                        Row(
                            modifier = Modifier.padding(horizontal = 14.dp, vertical = 12.dp),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Icon(
                                imageVector = Icons.Filled.LocationOn,
                                contentDescription = null,
                                tint = if (isSelected) ForestGreen else TextSecondary
                            )
                            Spacer(modifier = Modifier.width(10.dp))
                            Text(
                                text = loc,
                                style = MaterialTheme.typography.titleSmall,
                                color = TextPrimary
                            )
                        }
                    }
                }
                Spacer(modifier = Modifier.height(20.dp))
            }
        }
    }
}
