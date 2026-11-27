package com.example.ui.components

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowForward
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.minimumInteractiveComponentSize
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.geometry.CornerRadius
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.model.PharmacyCategory
import com.example.ui.theme.BorderSubtle
import com.example.ui.theme.ForestGreen
import com.example.ui.theme.LocalAppStrings
import com.example.ui.theme.MintSoft
import com.example.ui.theme.MintTeal
import com.example.ui.theme.PureWhite
import com.example.ui.theme.TextPrimary
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
    Box(
        modifier = modifier.minimumInteractiveComponentSize(),
        contentAlignment = Alignment.Center
    ) {
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
    val strings = LocalAppStrings.current
    HeaderCircleIconButton(
        onClick = onClick,
        contentDescription = strings.back,
        backgroundColor = if (darkSurface) PureWhite.copy(alpha = 0.22f) else PureWhite,
        iconTint = if (darkSurface) PureWhite else ForestGreen,
        modifier = modifier.testTag("back_button")
    ) {
        Icon(
            imageVector = Icons.AutoMirrored.Filled.ArrowForward,
            contentDescription = strings.back,
            tint = if (darkSurface) PureWhite else ForestGreen,
            modifier = Modifier.size(20.dp)
        )
    }
}

@Composable
fun SectionHeaderRow(
    title: String,
    actionLabel: String? = null,
    onActionClick: (() -> Unit)? = null,
    modifier: Modifier = Modifier
) {
    val strings = LocalAppStrings.current
    val resolvedAction = actionLabel ?: if (onActionClick != null) strings.seeMore else null
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
        if (resolvedAction != null && onActionClick != null) {
            Text(
                text = resolvedAction,
                style = MaterialTheme.typography.labelLarge,
                color = ForestGreen,
                modifier = Modifier
                    .minimumInteractiveComponentSize()
                    .clip(RoundedCornerShape(10.dp))
                    .clickable(onClick = onActionClick)
                    .padding(horizontal = 10.dp, vertical = 10.dp)
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
    val strings = LocalAppStrings.current
    val accent = Color(category.accentColorHex)
    Column(
        horizontalAlignment = Alignment.CenterHorizontally,
        modifier = modifier
            .minimumInteractiveComponentSize()
            .clip(RoundedCornerShape(16.dp))
            .clickable(onClick = onClick)
            .padding(horizontal = 6.dp, vertical = 4.dp)
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
            text = category.localizedTitle(strings.isEnglish),
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
            "analgesics" -> {
                drawRoundRect(
                    color = Color(0xFFFEE2E2),
                    topLeft = Offset(size.width * 0.14f, size.height * 0.28f),
                    size = Size(size.width * 0.72f, size.height * 0.44f),
                    cornerRadius = CornerRadius(24f, 24f)
                )
                drawRoundRect(
                    color = accent,
                    topLeft = Offset(size.width * 0.48f, size.height * 0.28f),
                    size = Size(size.width * 0.38f, size.height * 0.44f),
                    cornerRadius = CornerRadius(22f, 22f)
                )
                drawCircle(color = PureWhite, radius = 3.5f, center = Offset(size.width * 0.66f, size.height * 0.44f))
            }
            "antibiotics" -> {
                drawRoundRect(
                    color = Color(0xFFD5ECE6),
                    topLeft = Offset(size.width * 0.24f, size.height * 0.24f),
                    size = Size(size.width * 0.52f, size.height * 0.66f),
                    cornerRadius = CornerRadius(14f, 14f)
                )
                drawRoundRect(
                    color = accent,
                    topLeft = Offset(size.width * 0.30f, size.height * 0.08f),
                    size = Size(size.width * 0.40f, size.height * 0.18f),
                    cornerRadius = CornerRadius(8f, 8f)
                )
                drawRect(
                    color = ForestGreen,
                    topLeft = Offset(size.width * 0.45f, size.height * 0.44f),
                    size = Size(size.width * 0.10f, size.height * 0.26f)
                )
                drawRect(
                    color = ForestGreen,
                    topLeft = Offset(size.width * 0.37f, size.height * 0.52f),
                    size = Size(size.width * 0.26f, size.height * 0.10f)
                )
            }
            "chronic" -> {
                drawRoundRect(
                    color = Color(0xFFEDE9FE),
                    topLeft = Offset(size.width * 0.14f, size.height * 0.20f),
                    size = Size(size.width * 0.72f, size.height * 0.60f),
                    cornerRadius = CornerRadius(14f, 14f)
                )
                drawCircle(color = accent, radius = 6f, center = Offset(size.width * 0.36f, size.height * 0.44f))
                drawCircle(color = accent, radius = 6f, center = Offset(size.width * 0.64f, size.height * 0.44f))
                drawRoundRect(
                    color = accent,
                    topLeft = Offset(size.width * 0.28f, size.height * 0.58f),
                    size = Size(size.width * 0.44f, size.height * 0.12f),
                    cornerRadius = CornerRadius(6f, 6f)
                )
            }
            "respiratory" -> {
                drawRoundRect(
                    color = Color(0xFFE0F2FE),
                    topLeft = Offset(size.width * 0.26f, size.height * 0.16f),
                    size = Size(size.width * 0.36f, size.height * 0.68f),
                    cornerRadius = CornerRadius(12f, 12f)
                )
                drawRoundRect(
                    color = accent,
                    topLeft = Offset(size.width * 0.26f, size.height * 0.58f),
                    size = Size(size.width * 0.56f, size.height * 0.26f),
                    cornerRadius = CornerRadius(10f, 10f)
                )
                drawCircle(color = accent.copy(alpha = 0.7f), radius = 3.5f, center = Offset(size.width * 0.84f, size.height * 0.44f))
            }
            "vitamins" -> {
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
            else -> {
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
