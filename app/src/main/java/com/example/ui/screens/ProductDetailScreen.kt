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
import androidx.compose.foundation.layout.navigationBarsPadding
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
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Favorite
import androidx.compose.material.icons.filled.Remove
import androidx.compose.material.icons.filled.ShoppingBag
import androidx.compose.material.icons.outlined.FavoriteBorder
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.minimumInteractiveComponentSize
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextDecoration
import androidx.compose.ui.unit.dp
import com.example.data.catalog.PharmacyCatalog
import com.example.data.model.Product
import com.example.ui.components.BackCircleButton
import com.example.ui.components.HeaderCircleIconButton
import com.example.ui.components.ProductCard
import com.example.ui.components.ProductPackageArt
import com.example.ui.components.SectionHeaderRow
import com.example.ui.theme.BorderSubtle
import com.example.ui.theme.ForestGreen
import com.example.ui.theme.LocalAppStrings
import com.example.ui.theme.MintSoft
import com.example.ui.theme.NeutralBackground
import com.example.ui.theme.PureWhite
import com.example.ui.theme.StatusClosedBg
import com.example.ui.theme.StatusClosedRed
import com.example.ui.theme.StatusDutyAmber
import com.example.ui.theme.StatusDutyBg
import com.example.ui.theme.StatusOpenBg
import com.example.ui.theme.StatusOpenGreen
import com.example.ui.theme.TextPrimary
import com.example.ui.theme.TextSecondary

@Composable
fun ProductDetailScreen(
    product: Product,
    isFavorite: Boolean,
    favoriteProductIds: Set<String>,
    cartCount: Int,
    onBack: () -> Unit,
    onOpenCart: () -> Unit,
    onToggleFavorite: (Product, Boolean) -> Unit,
    onAddToCart: (Product, Int) -> Unit,
    onOpenProduct: (Product) -> Unit,
    onOpenSearch: (String) -> Unit,
    modifier: Modifier = Modifier
) {
    val strings = LocalAppStrings.current
    var quantity by remember(product.id) { mutableIntStateOf(1) }
    val relatedProducts = remember(product.id) {
        PharmacyCatalog.products.filter { it.id != product.id }.take(4)
    }

    BoxWithConstraints(
        modifier = modifier
            .fillMaxSize()
            .background(PureWhite),
        contentAlignment = Alignment.TopCenter
    ) {
        val maxContentWidth = if (maxWidth >= 600.dp) 760.dp else 600.dp

        LazyColumn(
            modifier = Modifier
                .widthIn(max = maxContentWidth)
                .fillMaxSize(),
            contentPadding = PaddingValues(bottom = 124.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            item {
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .background(NeutralBackground)
                        .statusBarsPadding()
                        .padding(horizontal = 18.dp, vertical = 12.dp)
                ) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        BackCircleButton(onClick = onBack)

                        Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                            HeaderCircleIconButton(
                                onClick = { onToggleFavorite(product, isFavorite) },
                                contentDescription = if (isFavorite) strings.removeFavorite else strings.addFavorite,
                                modifier = Modifier.testTag("detail_favorite_button")
                            ) {
                                Icon(
                                    imageVector = if (isFavorite) Icons.Filled.Favorite else Icons.Outlined.FavoriteBorder,
                                    contentDescription = if (isFavorite) strings.removeFavorite else strings.addFavorite,
                                    tint = if (isFavorite) StatusClosedRed else ForestGreen,
                                    modifier = Modifier.size(20.dp)
                                )
                            }
                            HeaderCircleIconButton(
                                onClick = onOpenCart,
                                contentDescription = strings.cartTitle,
                                badgeCount = cartCount,
                                modifier = Modifier.testTag("detail_cart_button")
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

                    Spacer(modifier = Modifier.height(10.dp))

                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(236.dp),
                        contentAlignment = Alignment.Center
                    ) {
                        ProductPackageArt(
                            product = product,
                            modifier = Modifier
                                .fillMaxWidth(0.84f)
                                .height(220.dp)
                        )
                        if (product.isBestSeller) {
                            Surface(
                                shape = RoundedCornerShape(12.dp),
                                color = PureWhite,
                                shadowElevation = 2.dp,
                                modifier = Modifier.align(Alignment.TopEnd)
                            ) {
                                Text(
                                    text = strings.bestSellerBadge,
                                    style = MaterialTheme.typography.labelMedium,
                                    color = TextPrimary,
                                    fontWeight = FontWeight.Bold,
                                    modifier = Modifier.padding(horizontal = 12.dp, vertical = 6.dp)
                                )
                            }
                        }
                    }
                }
            }

            item {
                Column(modifier = Modifier.padding(horizontal = 18.dp)) {
                    Row(
                        horizontalArrangement = Arrangement.spacedBy(8.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Surface(
                            shape = RoundedCornerShape(10.dp),
                            color = MintSoft
                        ) {
                            Text(
                                text = product.localizedDeliveryTime(strings.isEnglish),
                                style = MaterialTheme.typography.labelMedium,
                                color = ForestGreen,
                                fontWeight = FontWeight.Bold,
                                modifier = Modifier.padding(horizontal = 10.dp, vertical = 5.dp)
                            )
                        }
                        Surface(
                            shape = RoundedCornerShape(10.dp),
                            color = if (product.requiresPrescription) StatusClosedBg else StatusOpenBg,
                            border = BorderStroke(
                                1.dp,
                                if (product.requiresPrescription) StatusClosedRed.copy(alpha = 0.35f) else StatusOpenGreen.copy(alpha = 0.35f)
                            ),
                            modifier = Modifier.testTag("detail_prescription_badge")
                        ) {
                            Text(
                                text = if (product.requiresPrescription) strings.prescriptionRequiredLabel else strings.otcLabel,
                                style = MaterialTheme.typography.labelMedium,
                                color = if (product.requiresPrescription) StatusClosedRed else StatusOpenGreen,
                                fontWeight = FontWeight.Bold,
                                modifier = Modifier.padding(horizontal = 10.dp, vertical = 5.dp)
                            )
                        }
                    }

                    Spacer(modifier = Modifier.height(10.dp))

                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Column(modifier = Modifier.weight(1f)) {
                            Text(
                                text = product.localizedName(strings.isEnglish),
                                style = MaterialTheme.typography.headlineSmall,
                                color = TextPrimary
                            )
                            Text(
                                text = strings.aboutProduct,
                                style = MaterialTheme.typography.titleMedium,
                                color = ForestGreen
                            )
                        }
                        Spacer(modifier = Modifier.width(12.dp))
                        Column(horizontalAlignment = Alignment.End) {
                            if (product.hasDiscount) {
                                Surface(
                                    shape = RoundedCornerShape(8.dp),
                                    color = StatusDutyBg,
                                    border = BorderStroke(0.8.dp, StatusDutyAmber.copy(alpha = 0.35f))
                                ) {
                                    Text(
                                        text = "-${product.discountPercent}%",
                                        style = MaterialTheme.typography.labelSmall,
                                        color = StatusDutyAmber,
                                        fontWeight = FontWeight.Bold,
                                        modifier = Modifier.padding(horizontal = 8.dp, vertical = 2.dp)
                                    )
                                }
                                Spacer(modifier = Modifier.height(2.dp))
                            }
                            Text(
                                text = strings.formatPrice(product.priceUsd),
                                style = MaterialTheme.typography.headlineMedium,
                                color = if (product.hasDiscount) ForestGreen else TextPrimary,
                                fontWeight = FontWeight.Bold
                            )
                            val oldPrice = product.originalPriceUsd
                            if (product.hasDiscount && oldPrice != null) {
                                Text(
                                    text = strings.formatPrice(oldPrice),
                                    style = MaterialTheme.typography.bodySmall.copy(
                                        textDecoration = TextDecoration.LineThrough
                                    ),
                                    color = TextSecondary
                                )
                            }
                        }
                    }

                    Spacer(modifier = Modifier.height(8.dp))

                    Text(
                        text = product.localizedDescription(strings.isEnglish),
                        style = MaterialTheme.typography.bodyLarge,
                        color = TextSecondary
                    )
                }
            }

            item { ProductRatingSummaryCard(product = product) }
            item { ProductDosageSection(product = product) }
            item { ProductSafetyNoteCard(product = product) }

            item {
                Column(verticalArrangement = Arrangement.spacedBy(10.dp)) {
                    SectionHeaderRow(
                        title = strings.bestSellingProducts,
                        onActionClick = { onOpenSearch(product.categoryId) },
                        modifier = Modifier.padding(horizontal = 18.dp)
                    )
                    LazyRow(
                        contentPadding = PaddingValues(horizontal = 18.dp),
                        horizontalArrangement = Arrangement.spacedBy(12.dp)
                    ) {
                        items(relatedProducts, key = { it.id }) { related ->
                            val relFav = favoriteProductIds.contains(related.id)
                            ProductCard(
                                product = related,
                                isFavorite = relFav,
                                onCardClick = { onOpenProduct(related) },
                                onAddClick = { onAddToCart(related, 1) },
                                onToggleFavorite = { onToggleFavorite(related, relFav) },
                                modifier = Modifier.width(164.dp)
                            )
                        }
                    }
                }
            }
        }

        Surface(
            color = PureWhite,
            shadowElevation = 12.dp,
            border = BorderStroke(1.dp, BorderSubtle),
            modifier = Modifier
                .align(Alignment.BottomCenter)
                .widthIn(max = maxContentWidth)
                .fillMaxWidth()
        ) {
            Row(
                modifier = Modifier
                    .navigationBarsPadding()
                    .padding(horizontal = 18.dp, vertical = 14.dp),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(6.dp)
                ) {
                    Box(
                        modifier = Modifier.minimumInteractiveComponentSize(),
                        contentAlignment = Alignment.Center
                    ) {
                        Surface(
                            onClick = { if (quantity < 99) quantity++ },
                            shape = CircleShape,
                            color = ForestGreen,
                            modifier = Modifier
                                .size(40.dp)
                                .testTag("detail_qty_plus")
                        ) {
                            Box(contentAlignment = Alignment.Center) {
                                Icon(
                                    imageVector = Icons.Filled.Add,
                                    contentDescription = strings.increaseQty,
                                    tint = PureWhite,
                                    modifier = Modifier.size(18.dp)
                                )
                            }
                        }
                    }
                    Text(
                        text = quantity.toString(),
                        style = MaterialTheme.typography.titleLarge,
                        color = TextPrimary,
                        modifier = Modifier.padding(horizontal = 4.dp)
                    )
                    Box(
                        modifier = Modifier.minimumInteractiveComponentSize(),
                        contentAlignment = Alignment.Center
                    ) {
                        Surface(
                            onClick = { if (quantity > 1) quantity-- },
                            shape = CircleShape,
                            color = NeutralBackground,
                            border = BorderStroke(1.dp, BorderSubtle),
                            modifier = Modifier
                                .size(40.dp)
                                .testTag("detail_qty_minus")
                        ) {
                            Box(contentAlignment = Alignment.Center) {
                                Icon(
                                    imageVector = Icons.Filled.Remove,
                                    contentDescription = strings.decreaseQty,
                                    tint = ForestGreen,
                                    modifier = Modifier.size(18.dp)
                                )
                            }
                        }
                    }
                }

                Button(
                    onClick = { onAddToCart(product, quantity) },
                    shape = RoundedCornerShape(18.dp),
                    colors = ButtonDefaults.buttonColors(
                        containerColor = ForestGreen,
                        contentColor = PureWhite
                    ),
                    modifier = Modifier
                        .weight(1f)
                        .height(54.dp)
                        .testTag("add_to_cart_cta")
                ) {
                    Icon(
                        imageVector = Icons.Filled.ShoppingBag,
                        contentDescription = null,
                        modifier = Modifier.size(20.dp)
                    )
                    Spacer(modifier = Modifier.width(8.dp))
                    Text(
                        text = strings.addToCartCta,
                        style = MaterialTheme.typography.titleMedium,
                        fontWeight = FontWeight.Bold
                    )
                }
            }
        }
    }
}
