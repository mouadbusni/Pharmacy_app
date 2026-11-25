package com.example.ui.screens

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.border
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
import androidx.compose.material.icons.automirrored.filled.StarHalf
import androidx.compose.material.icons.filled.AccessTime
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Favorite
import androidx.compose.material.icons.filled.Info
import androidx.compose.material.icons.filled.Remove
import androidx.compose.material.icons.filled.Restaurant
import androidx.compose.material.icons.filled.ShoppingBag
import androidx.compose.material.icons.filled.Star
import androidx.compose.material.icons.filled.VerifiedUser
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
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
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
import com.example.ui.theme.MintTeal
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
import com.example.ui.theme.WarmGold

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
                        Text(
                            text = strings.formatPrice(product.priceUsd),
                            style = MaterialTheme.typography.headlineMedium,
                            color = TextPrimary,
                            fontWeight = FontWeight.Bold
                        )
                    }

                    Spacer(modifier = Modifier.height(8.dp))

                    Text(
                        text = product.localizedDescription(strings.isEnglish),
                        style = MaterialTheme.typography.bodyLarge,
                        color = TextSecondary
                    )
                }
            }

            item {
                Surface(
                    shape = RoundedCornerShape(20.dp),
                    color = NeutralBackground,
                    border = BorderStroke(1.dp, BorderSubtle),
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 18.dp)
                ) {
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(16.dp),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Column {
                            Text(
                                text = "${product.reviewCount} ${strings.reviewsSuffix} • ${product.rating}",
                                style = MaterialTheme.typography.titleMedium,
                                color = TextPrimary
                            )
                            Spacer(modifier = Modifier.height(4.dp))
                            DynamicStarRow(rating = product.rating)
                        }

                        Row(horizontalArrangement = Arrangement.spacedBy((-10).dp)) {
                            val colors = listOf(
                                Color(0xFF1EAF8F),
                                Color(0xFFF2994A),
                                Color(0xFF5D3A8E),
                                Color(0xFF00402E)
                            )
                            val letters = if (strings.isEnglish) {
                                listOf("N", "S", "M", "A")
                            } else {
                                listOf("ن", "س", "م", "ع")
                            }
                            colors.forEachIndexed { idx, bg ->
                                Box(
                                    modifier = Modifier
                                        .size(36.dp)
                                        .clip(CircleShape)
                                        .background(bg)
                                        .border(2.dp, PureWhite, CircleShape),
                                    contentAlignment = Alignment.Center
                                ) {
                                    Text(
                                        text = letters[idx],
                                        color = PureWhite,
                                        fontWeight = FontWeight.Bold,
                                        fontSize = 13.sp
                                    )
                                }
                            }
                        }
                    }
                }
            }

            item {
                Column(
                    modifier = Modifier.padding(horizontal = 18.dp),
                    verticalArrangement = Arrangement.spacedBy(10.dp)
                ) {
                    SectionHeaderRow(
                        title = strings.dosageUsageTitle,
                        actionLabel = null
                    )
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(12.dp)
                    ) {
                        Surface(
                            shape = RoundedCornerShape(16.dp),
                            color = NeutralBackground,
                            border = BorderStroke(1.dp, BorderSubtle),
                            modifier = Modifier.weight(1f)
                        ) {
                            Row(
                                modifier = Modifier.padding(14.dp),
                                verticalAlignment = Alignment.CenterVertically,
                                horizontalArrangement = Arrangement.Center
                            ) {
                                Icon(
                                    imageVector = Icons.Filled.AccessTime,
                                    contentDescription = null,
                                    tint = MintTeal,
                                    modifier = Modifier.size(20.dp)
                                )
                                Spacer(modifier = Modifier.width(8.dp))
                                Text(
                                    text = product.localizedDosageTime(strings.isEnglish),
                                    style = MaterialTheme.typography.titleSmall,
                                    color = TextPrimary
                                )
                            }
                        }
                        Surface(
                            shape = RoundedCornerShape(16.dp),
                            color = NeutralBackground,
                            border = BorderStroke(1.dp, BorderSubtle),
                            modifier = Modifier.weight(1f)
                        ) {
                            Row(
                                modifier = Modifier.padding(14.dp),
                                verticalAlignment = Alignment.CenterVertically,
                                horizontalArrangement = Arrangement.Center
                            ) {
                                Icon(
                                    imageVector = Icons.Filled.Restaurant,
                                    contentDescription = null,
                                    tint = ForestGreen,
                                    modifier = Modifier.size(20.dp)
                                )
                                Spacer(modifier = Modifier.width(8.dp))
                                Text(
                                    text = product.localizedMealTiming(strings.isEnglish),
                                    style = MaterialTheme.typography.titleSmall,
                                    color = TextPrimary
                                )
                            }
                        }
                    }
                }
            }

            item {
                val noteBg = if (product.requiresPrescription) StatusClosedBg.copy(alpha = 0.7f) else StatusDutyBg.copy(alpha = 0.65f)
                val noteAccent = if (product.requiresPrescription) StatusClosedRed else StatusDutyAmber
                Surface(
                    shape = RoundedCornerShape(18.dp),
                    color = noteBg,
                    border = BorderStroke(1.dp, noteAccent.copy(alpha = 0.3f)),
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 18.dp)
                        .testTag("pharmacist_safety_note_card")
                ) {
                    Row(
                        modifier = Modifier.padding(16.dp),
                        verticalAlignment = Alignment.Top,
                        horizontalArrangement = Arrangement.spacedBy(12.dp)
                    ) {
                        Icon(
                            imageVector = if (product.requiresPrescription) Icons.Filled.Info else Icons.Filled.VerifiedUser,
                            contentDescription = strings.pharmacistSafetyNoteTitle,
                            tint = noteAccent,
                            modifier = Modifier.size(22.dp)
                        )
                        Column(verticalArrangement = Arrangement.spacedBy(4.dp)) {
                            Text(
                                text = strings.pharmacistSafetyNoteTitle,
                                style = MaterialTheme.typography.titleSmall,
                                color = noteAccent,
                                fontWeight = FontWeight.Bold
                            )
                            Text(
                                text = product.localizedSafetyNote(strings.isEnglish),
                                style = MaterialTheme.typography.bodyMedium,
                                color = TextPrimary
                            )
                        }
                    }
                }
            }

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

@Composable
private fun DynamicStarRow(rating: Double) {
    Row(horizontalArrangement = Arrangement.spacedBy(4.dp)) {
        for (i in 1..5) {
            val thresholdFull = i.toDouble()
            val thresholdHalf = i - 0.5
            when {
                rating >= thresholdFull -> {
                    Icon(
                        imageVector = Icons.Filled.Star,
                        contentDescription = null,
                        tint = WarmGold,
                        modifier = Modifier.size(18.dp)
                    )
                }
                rating >= thresholdHalf -> {
                    Icon(
                        imageVector = Icons.AutoMirrored.Filled.StarHalf,
                        contentDescription = null,
                        tint = WarmGold,
                        modifier = Modifier.size(18.dp)
                    )
                }
                else -> {
                    Icon(
                        imageVector = Icons.Filled.Star,
                        contentDescription = null,
                        tint = BorderSubtle,
                        modifier = Modifier.size(18.dp)
                    )
                }
            }
        }
    }
}
