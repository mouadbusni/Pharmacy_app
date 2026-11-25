package com.example.ui.components

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowForward
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Favorite
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.LocationOn
import androidx.compose.material.icons.filled.Notifications
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.ShoppingBag
import androidx.compose.material.icons.filled.Star
import androidx.compose.material.icons.outlined.FavoriteBorder
import androidx.compose.material.icons.outlined.Home
import androidx.compose.material.icons.outlined.Notifications
import androidx.compose.material.icons.outlined.Person
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.minimumInteractiveComponentSize
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.geometry.CornerRadius
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.R
import com.example.data.model.PackagingStyle
import com.example.data.model.Pharmacy
import com.example.data.model.PharmacyCategory
import com.example.data.model.Product
import com.example.ui.theme.BorderSubtle
import com.example.ui.theme.ForestGreen
import com.example.ui.theme.MintSoft
import com.example.ui.theme.MintTeal
import com.example.ui.theme.NeutralBackground
import com.example.ui.theme.PureWhite
import com.example.ui.theme.StatusClosedBg
import com.example.ui.theme.StatusClosedRed
import com.example.ui.theme.StatusOpenBg
import com.example.ui.theme.StatusOpenGreen
import com.example.ui.theme.TextPrimary
import com.example.ui.theme.TextSecondary
import com.example.ui.theme.WarmGold

@Composable
fun HeaderCircleIconButton(
    onClick: () -> Unit,
    contentDescription: String,
    modifier: Modifier = Modifier,
    badgeCount: Int = 0,
    backgroundColor: Color = PureWhite,
    iconTint: Color = ForestGreen,
    icon: @Composable () -> Unit
) {
    Box(modifier = modifier.minimumInteractiveComponentSize()) {
        Surface(
            onClick = onClick,
            shape = CircleShape,
            color = backgroundColor,
            shadowElevation = 2.dp,
            border = BorderStroke(1.dp, BorderSubtle.copy(alpha = 0.7f)),
            modifier = Modifier.size(44.dp)
        ) {
            Box(contentAlignment = Alignment.Center) {
                icon()
            }
        }
        if (badgeCount > 0) {
            Box(
                modifier = Modifier
                    .align(Alignment.TopEnd)
                    .offset(x = 2.dp, y = (-2).dp)
                    .size(18.dp)
                    .clip(CircleShape)
                    .background(MintTeal),
                contentAlignment = Alignment.Center
            ) {
                Text(
                    text = if (badgeCount > 9) "9+" else badgeCount.toString(),
                    color = PureWhite,
                    fontSize = 10.sp,
                    fontWeight = FontWeight.Bold
                )
            }
        }
    }
}

@Composable
fun BackCircleButton(
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
    darkSurface: Boolean = false
) {
    HeaderCircleIconButton(
        onClick = onClick,
        contentDescription = "رجوع",
        backgroundColor = if (darkSurface) PureWhite.copy(alpha = 0.2f) else PureWhite,
        iconTint = if (darkSurface) PureWhite else ForestGreen,
        modifier = modifier.testTag("back_button")
    ) {
        Icon(
            imageVector = Icons.AutoMirrored.Filled.ArrowForward,
            contentDescription = "رجوع",
            tint = if (darkSurface) PureWhite else ForestGreen,
            modifier = Modifier.size(20.dp)
        )
    }
}

@Composable
fun SectionHeaderRow(
    title: String,
    actionLabel: String? = "عرض المزيد",
    onActionClick: (() -> Unit)? = null,
    modifier: Modifier = Modifier
) {
    Row(
        modifier = modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ) {
        Text(
            text = title,
            style = MaterialTheme.typography.titleLarge,
            color = TextPrimary
        )
        if (actionLabel != null && onActionClick != null) {
            Text(
                text = actionLabel,
                style = MaterialTheme.typography.labelLarge,
                color = ForestGreen,
                modifier = Modifier
                    .clip(RoundedCornerShape(10.dp))
                    .clickable(onClick = onActionClick)
                    .padding(horizontal = 8.dp, vertical = 6.dp)
            )
        }
    }
}

@Composable
fun CategoryCircleItem(
    category: PharmacyCategory,
    selected: Boolean,
    onClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    val accent = Color(category.accentColorHex)
    Column(
        horizontalAlignment = Alignment.CenterHorizontally,
        modifier = modifier
            .clip(RoundedCornerShape(16.dp))
            .clickable(onClick = onClick)
            .padding(4.dp)
    ) {
        Box(
            modifier = Modifier
                .size(62.dp)
                .clip(CircleShape)
                .background(if (selected) MintSoft else PureWhite)
                .border(
                    width = if (selected) 2.dp else 1.dp,
                    color = if (selected) ForestGreen else BorderSubtle,
                    shape = CircleShape
                ),
            contentAlignment = Alignment.Center
        ) {
            CategoryVectorArt(categoryId = category.id, accent = accent)
        }
        Spacer(modifier = Modifier.height(6.dp))
        Text(
            text = category.titleAr,
            style = MaterialTheme.typography.labelMedium,
            fontWeight = if (selected) FontWeight.Bold else FontWeight.Medium,
            color = if (selected) ForestGreen else TextPrimary,
            maxLines = 1,
            overflow = TextOverflow.Ellipsis
        )
    }
}

@Composable
private fun CategoryVectorArt(categoryId: String, accent: Color) {
    Canvas(modifier = Modifier.size(34.dp)) {
        when (categoryId) {
            "all" -> {
                // Medical cross badge
                drawRoundRect(
                    color = ForestGreen,
                    topLeft = Offset(size.width * 0.12f, size.height * 0.12f),
                    size = Size(size.width * 0.76f, size.height * 0.76f),
                    cornerRadius = CornerRadius(10f, 10f)
                )
                drawRect(
                    color = PureWhite,
                    topLeft = Offset(size.width * 0.43f, size.height * 0.27f),
                    size = Size(size.width * 0.14f, size.height * 0.46f)
                )
                drawRect(
                    color = PureWhite,
                    topLeft = Offset(size.width * 0.27f, size.height * 0.43f),
                    size = Size(size.width * 0.46f, size.height * 0.14f)
                )
            }
            "medicines" -> {
                // Medicine bottle with cross
                drawRoundRect(
                    color = Color(0xFFD5ECE6),
                    topLeft = Offset(size.width * 0.24f, size.height * 0.24f),
                    size = Size(size.width * 0.52f, size.height * 0.66f),
                    cornerRadius = CornerRadius(14f, 14f)
                )
                drawRoundRect(
                    color = ForestGreen,
                    topLeft = Offset(size.width * 0.30f, size.height * 0.08f),
                    size = Size(size.width * 0.40f, size.height * 0.18f),
                    cornerRadius = CornerRadius(8f, 8f)
                )
                drawRect(
                    color = Color(0xFFE05757),
                    topLeft = Offset(size.width * 0.45f, size.height * 0.44f),
                    size = Size(size.width * 0.10f, size.height * 0.26f)
                )
                drawRect(
                    color = Color(0xFFE05757),
                    topLeft = Offset(size.width * 0.37f, size.height * 0.52f),
                    size = Size(size.width * 0.26f, size.height * 0.10f)
                )
            }
            "skincare" -> {
                // Cream jar
                drawRoundRect(
                    color = Color(0xFFF6C2CE),
                    topLeft = Offset(size.width * 0.18f, size.height * 0.36f),
                    size = Size(size.width * 0.64f, size.height * 0.50f),
                    cornerRadius = CornerRadius(14f, 14f)
                )
                drawRoundRect(
                    color = Color(0xFFE996A8),
                    topLeft = Offset(size.width * 0.22f, size.height * 0.20f),
                    size = Size(size.width * 0.56f, size.height * 0.18f),
                    cornerRadius = CornerRadius(10f, 10f)
                )
            }
            "vitamins" -> {
                // Two-tone capsule + dots
                drawRoundRect(
                    color = accent,
                    topLeft = Offset(size.width * 0.20f, size.height * 0.20f),
                    size = Size(size.width * 0.60f, size.height * 0.34f),
                    cornerRadius = CornerRadius(24f, 24f)
                )
                drawRoundRect(
                    color = Color(0xFF4FB9F0),
                    topLeft = Offset(size.width * 0.46f, size.height * 0.20f),
                    size = Size(size.width * 0.34f, size.height * 0.34f),
                    cornerRadius = CornerRadius(20f, 20f)
                )
                drawCircle(color = MintTeal, radius = 4.5f, center = Offset(size.width * 0.35f, size.height * 0.74f))
                drawCircle(color = WarmGold, radius = 4f, center = Offset(size.width * 0.55f, size.height * 0.78f))
                drawCircle(color = accent, radius = 3.5f, center = Offset(size.width * 0.72f, size.height * 0.68f))
            }
            "cosmetics" -> {
                // Dropper serum bottle
                drawRoundRect(
                    color = Color(0xFFF7D5E4),
                    topLeft = Offset(size.width * 0.28f, size.height * 0.38f),
                    size = Size(size.width * 0.44f, size.height * 0.52f),
                    cornerRadius = CornerRadius(12f, 12f)
                )
                drawRoundRect(
                    color = accent,
                    topLeft = Offset(size.width * 0.38f, size.height * 0.12f),
                    size = Size(size.width * 0.24f, size.height * 0.28f),
                    cornerRadius = CornerRadius(10f, 10f)
                )
            }
            else -> {
                // First-aid kit
                drawRoundRect(
                    color = Color(0xFFE7F2FA),
                    topLeft = Offset(size.width * 0.14f, size.height * 0.28f),
                    size = Size(size.width * 0.72f, size.height * 0.56f),
                    cornerRadius = CornerRadius(12f, 12f)
                )
                drawRect(
                    color = accent,
                    topLeft = Offset(size.width * 0.44f, size.height * 0.42f),
                    size = Size(size.width * 0.12f, size.height * 0.28f)
                )
                drawRect(
                    color = accent,
                    topLeft = Offset(size.width * 0.36f, size.height * 0.50f),
                    size = Size(size.width * 0.28f, size.height * 0.12f)
                )
            }
        }
    }
}

@Composable
fun ProductPackageArt(
    product: Product,
    modifier: Modifier = Modifier
) {
    if (product.packagingStyle == PackagingStyle.BIOSTERON_PHOTO) {
        Box(
            modifier = modifier
                .clip(RoundedCornerShape(14.dp))
                .background(Color(0xFFE1E3E2)),
            contentAlignment = Alignment.Center
        ) {
            Image(
                painter = painterResource(id = R.drawable.img_biosteron_product),
                contentDescription = product.nameAr,
                contentScale = ContentScale.Fit,
                modifier = Modifier.fillMaxSize()
            )
        }
        return
    }

    val (primaryCol, secondaryCol, labelCode) = when (product.packagingStyle) {
        PackagingStyle.DETOCYN_GREEN -> Triple(Color(0xFF1F9D6D), Color(0xFFD6F3E7), "DETOCYN")
        PackagingStyle.UTORINIC_TEAL -> Triple(Color(0xFF005B4F), Color(0xFF8BE2CF), "UTORINIC")
        PackagingStyle.ASPIRINA_MINT -> Triple(Color(0xFF0E7C6B), Color(0xFFCDEEE7), "ASPIRINA")
        PackagingStyle.TRIBETRON_RED -> Triple(Color(0xFFB52B39), Color(0xFFF6D4D8), "TRIBETRON")
        PackagingStyle.NUTIVA_LIME -> Triple(Color(0xFF6BA82F), Color(0xFFE3F4C9), "NUTIVA")
        PackagingStyle.FLEX_NAVY -> Triple(Color(0xFF1D4F7A), Color(0xFFD2E5F6), "FLEX")
        PackagingStyle.PANADOL_BLUE -> Triple(Color(0xFF1667B1), Color(0xFFD8EBFA), "PANADOL")
        PackagingStyle.BIOSTERON_PHOTO -> Triple(ForestGreen, MintSoft, "BIOSTERON")
    }

    Box(
        modifier = modifier
            .clip(RoundedCornerShape(14.dp))
            .background(
                Brush.verticalGradient(
                    colors = listOf(NeutralBackground, secondaryCol.copy(alpha = 0.55f))
                )
            ),
        contentAlignment = Alignment.Center
    ) {
        // Stylized 3D Medicine Box + Bottle silhouette
        Row(
            verticalAlignment = Alignment.Bottom,
            horizontalArrangement = Arrangement.spacedBy(6.dp),
            modifier = Modifier.padding(8.dp)
        ) {
            Box(
                modifier = Modifier
                    .width(42.dp)
                    .height(54.dp)
                    .shadow(4.dp, RoundedCornerShape(8.dp))
                    .clip(RoundedCornerShape(8.dp))
                    .background(PureWhite)
                    .border(1.dp, primaryCol.copy(alpha = 0.25f), RoundedCornerShape(8.dp))
            ) {
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(16.dp)
                        .background(primaryCol)
                        .align(Alignment.TopCenter),
                    contentAlignment = Alignment.Center
                ) {
                    Text(
                        text = labelCode.take(6),
                        color = PureWhite,
                        fontSize = 6.5.sp,
                        fontWeight = FontWeight.Bold
                    )
                }
                Box(
                    modifier = Modifier
                        .size(16.dp)
                        .align(Alignment.Center)
                        .offset(y = 6.dp)
                        .clip(CircleShape)
                        .background(secondaryCol),
                    contentAlignment = Alignment.Center
                ) {
                    Text(
                        text = "+",
                        color = primaryCol,
                        fontSize = 11.sp,
                        fontWeight = FontWeight.Bold
                    )
                }
            }

            Box(
                modifier = Modifier
                    .width(24.dp)
                    .height(40.dp)
                    .shadow(3.dp, RoundedCornerShape(6.dp))
                    .clip(RoundedCornerShape(6.dp))
                    .background(primaryCol),
                contentAlignment = Alignment.TopCenter
            ) {
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(8.dp)
                        .background(PureWhite.copy(alpha = 0.9f))
                )
            }
        }
    }
}

@Composable
fun ProductCard(
    product: Product,
    isFavorite: Boolean,
    onCardClick: () -> Unit,
    onAddClick: () -> Unit,
    onToggleFavorite: () -> Unit,
    modifier: Modifier = Modifier
) {
    Card(
        onClick = onCardClick,
        shape = RoundedCornerShape(20.dp),
        colors = CardDefaults.cardColors(containerColor = PureWhite),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp),
        border = BorderStroke(1.dp, BorderSubtle.copy(alpha = 0.7f)),
        modifier = modifier.testTag("product_card_${product.id}")
    ) {
        Column(modifier = Modifier.padding(12.dp)) {
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(104.dp)
            ) {
                ProductPackageArt(
                    product = product,
                    modifier = Modifier.fillMaxSize()
                )
                IconButton(
                    onClick = onToggleFavorite,
                    modifier = Modifier
                        .align(Alignment.TopStart)
                        .size(34.dp)
                        .testTag("fav_btn_${product.id}")
                ) {
                    Icon(
                        imageVector = if (isFavorite) Icons.Filled.Favorite else Icons.Outlined.FavoriteBorder,
                        contentDescription = if (isFavorite) "إزالة من المفضلة" else "إضافة إلى المفضلة",
                        tint = if (isFavorite) StatusClosedRed else TextSecondary,
                        modifier = Modifier.size(20.dp)
                    )
                }
            }

            Spacer(modifier = Modifier.height(8.dp))

            Text(
                text = product.nameAr,
                style = MaterialTheme.typography.titleSmall,
                color = TextPrimary,
                maxLines = 1,
                overflow = TextOverflow.Ellipsis
            )

            Spacer(modifier = Modifier.height(6.dp))

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.Bottom
            ) {
                Column {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Icon(
                            imageVector = Icons.Filled.Star,
                            contentDescription = "تقييم",
                            tint = WarmGold,
                            modifier = Modifier.size(14.dp)
                        )
                        Spacer(modifier = Modifier.width(3.dp))
                        Text(
                            text = product.rating.toString(),
                            style = MaterialTheme.typography.bodySmall,
                            color = TextSecondary,
                            fontWeight = FontWeight.Medium
                        )
                    }
                    Spacer(modifier = Modifier.height(2.dp))
                    Text(
                        text = "${product.priceUsd.toInt()}$",
                        style = MaterialTheme.typography.titleMedium,
                        color = TextPrimary,
                        fontWeight = FontWeight.Bold
                    )
                }

                Surface(
                    onClick = onAddClick,
                    shape = RoundedCornerShape(10.dp),
                    color = ForestGreen,
                    modifier = Modifier
                        .size(36.dp)
                        .testTag("add_cart_${product.id}")
                ) {
                    Box(contentAlignment = Alignment.Center) {
                        Icon(
                            imageVector = Icons.Filled.Add,
                            contentDescription = "أضف للسلة",
                            tint = PureWhite,
                            modifier = Modifier.size(18.dp)
                        )
                    }
                }
            }
        }
    }
}

@Composable
fun PharmacyAvatarBadge(
    pharmacy: Pharmacy,
    size: Dp = 52.dp,
    modifier: Modifier = Modifier
) {
    val accent = Color(pharmacy.accentColorHex)
    Box(
        modifier = modifier
            .size(size)
            .clip(CircleShape)
            .background(PureWhite)
            .border(1.5.dp, accent.copy(alpha = 0.35f), CircleShape),
        contentAlignment = Alignment.Center
    ) {
        Column(horizontalAlignment = Alignment.CenterHorizontally) {
            Box(
                modifier = Modifier
                    .size(size * 0.42f)
                    .clip(CircleShape)
                    .background(accent.copy(alpha = 0.16f)),
                contentAlignment = Alignment.Center
            ) {
                Text(
                    text = pharmacy.initials,
                    color = accent,
                    fontSize = 11.sp,
                    fontWeight = FontWeight.Bold
                )
            }
        }
    }
}

@Composable
fun PharmacyListCard(
    pharmacy: Pharmacy,
    onClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    Card(
        onClick = onClick,
        shape = RoundedCornerShape(20.dp),
        colors = CardDefaults.cardColors(containerColor = PureWhite),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp),
        border = BorderStroke(1.dp, BorderSubtle.copy(alpha = 0.7f)),
        modifier = modifier
            .fillMaxWidth()
            .testTag("pharmacy_card_${pharmacy.id}")
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 14.dp, vertical = 12.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            Row(
                verticalAlignment = Alignment.CenterVertically,
                modifier = Modifier.weight(1f)
            ) {
                PharmacyAvatarBadge(pharmacy = pharmacy, size = 54.dp)
                Spacer(modifier = Modifier.width(12.dp))
                Column {
                    Text(
                        text = pharmacy.nameAr,
                        style = MaterialTheme.typography.titleMedium,
                        color = TextPrimary,
                        maxLines = 1,
                        overflow = TextOverflow.Ellipsis
                    )
                    Spacer(modifier = Modifier.height(2.dp))
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Icon(
                            imageVector = Icons.Filled.LocationOn,
                            contentDescription = "الموقع",
                            tint = TextSecondary,
                            modifier = Modifier.size(14.dp)
                        )
                        Spacer(modifier = Modifier.width(2.dp))
                        Text(
                            text = pharmacy.areaAr,
                            style = MaterialTheme.typography.bodySmall,
                            color = TextSecondary
                        )
                    }
                    Spacer(modifier = Modifier.height(6.dp))
                    Surface(
                        shape = RoundedCornerShape(10.dp),
                        color = if (pharmacy.openNow) StatusOpenBg else StatusClosedBg
                    ) {
                        Text(
                            text = if (pharmacy.openNow) "• مفتوح الآن" else "• مغلق الآن",
                            style = MaterialTheme.typography.labelSmall,
                            color = if (pharmacy.openNow) StatusOpenGreen else StatusClosedRed,
                            fontWeight = FontWeight.Bold,
                            modifier = Modifier.padding(horizontal = 10.dp, vertical = 4.dp)
                        )
                    }
                }
            }

            Surface(
                shape = RoundedCornerShape(10.dp),
                color = NeutralBackground
            ) {
                Text(
                    text = pharmacy.deliveryTimeAr,
                    style = MaterialTheme.typography.labelSmall,
                    color = TextSecondary,
                    textAlign = TextAlign.Center,
                    modifier = Modifier.padding(horizontal = 10.dp, vertical = 6.dp)
                )
            }
        }
    }
}

enum class BottomTab(val route: String, val labelAr: String) {
    HOME("home", "الرئيسية"),
    NOTIFICATIONS("notifications", "الإشعارات"),
    FAVORITES("favorites", "المفضلة"),
    PROFILE("profile", "حسابي")
}

@Composable
fun PharmacyBottomBar(
    currentRoute: String,
    cartCount: Int,
    onNavigateTab: (BottomTab) -> Unit,
    onOpenCart: () -> Unit,
    modifier: Modifier = Modifier
) {
    Box(
        modifier = modifier
            .fillMaxWidth()
            .navigationBarsPadding()
            .padding(horizontal = 16.dp, vertical = 8.dp),
        contentAlignment = Alignment.BottomCenter
    ) {
        Surface(
            shape = RoundedCornerShape(24.dp),
            color = PureWhite,
            shadowElevation = 8.dp,
            border = BorderStroke(1.dp, BorderSubtle),
            modifier = Modifier
                .fillMaxWidth()
                .height(68.dp)
        ) {
            Row(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(horizontal = 12.dp),
                horizontalArrangement = Arrangement.SpaceAround,
                verticalAlignment = Alignment.CenterVertically
            ) {
                BottomBarTabItem(
                    tab = BottomTab.HOME,
                    selected = currentRoute == BottomTab.HOME.route,
                    onClick = { onNavigateTab(BottomTab.HOME) }
                )
                BottomBarTabItem(
                    tab = BottomTab.NOTIFICATIONS,
                    selected = currentRoute == BottomTab.NOTIFICATIONS.route,
                    onClick = { onNavigateTab(BottomTab.NOTIFICATIONS) }
                )
                Spacer(modifier = Modifier.width(56.dp))
                BottomBarTabItem(
                    tab = BottomTab.FAVORITES,
                    selected = currentRoute == BottomTab.FAVORITES.route,
                    onClick = { onNavigateTab(BottomTab.FAVORITES) }
                )
                BottomBarTabItem(
                    tab = BottomTab.PROFILE,
                    selected = currentRoute == BottomTab.PROFILE.route,
                    onClick = { onNavigateTab(BottomTab.PROFILE) }
                )
            }
        }

        // Center elevated Cart FAB
        Box(
            modifier = Modifier
                .align(Alignment.TopCenter)
                .offset(y = (-16).dp)
        ) {
            Surface(
                onClick = onOpenCart,
                shape = RoundedCornerShape(18.dp),
                color = ForestGreen,
                shadowElevation = 8.dp,
                modifier = Modifier
                    .size(56.dp)
                    .testTag("bottom_cart_fab")
            ) {
                Box(contentAlignment = Alignment.Center) {
                    Icon(
                        imageVector = Icons.Filled.ShoppingBag,
                        contentDescription = "السلة",
                        tint = PureWhite,
                        modifier = Modifier.size(24.dp)
                    )
                }
            }
            if (cartCount > 0) {
                Box(
                    modifier = Modifier
                        .align(Alignment.TopEnd)
                        .offset(x = 4.dp, y = (-4).dp)
                        .size(20.dp)
                        .clip(CircleShape)
                        .background(MintTeal)
                        .border(1.5.dp, PureWhite, CircleShape),
                    contentAlignment = Alignment.Center
                ) {
                    Text(
                        text = if (cartCount > 9) "9+" else cartCount.toString(),
                        color = PureWhite,
                        fontSize = 10.sp,
                        fontWeight = FontWeight.Bold
                    )
                }
            }
        }
    }
}

@Composable
private fun BottomBarTabItem(
    tab: BottomTab,
    selected: Boolean,
    onClick: () -> Unit
) {
    val icon = when (tab) {
        BottomTab.HOME -> if (selected) Icons.Filled.Home else Icons.Outlined.Home
        BottomTab.NOTIFICATIONS -> if (selected) Icons.Filled.Notifications else Icons.Outlined.Notifications
        BottomTab.FAVORITES -> if (selected) Icons.Filled.Favorite else Icons.Outlined.FavoriteBorder
        BottomTab.PROFILE -> if (selected) Icons.Filled.Person else Icons.Outlined.Person
    }
    Column(
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center,
        modifier = Modifier
            .minimumInteractiveComponentSize()
            .clip(RoundedCornerShape(14.dp))
            .clickable(onClick = onClick)
            .padding(horizontal = 8.dp, vertical = 4.dp)
            .testTag("tab_${tab.route}")
    ) {
        Icon(
            imageVector = icon,
            contentDescription = tab.labelAr,
            tint = if (selected) ForestGreen else TextSecondary,
            modifier = Modifier.size(22.dp)
        )
        Spacer(modifier = Modifier.height(2.dp))
        Text(
            text = tab.labelAr,
            style = MaterialTheme.typography.labelSmall,
            color = if (selected) ForestGreen else TextSecondary,
            fontWeight = if (selected) FontWeight.Bold else FontWeight.Normal
        )
    }
}
