package com.example.ui.screens

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
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.widthIn
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import android.content.ClipData
import android.content.ClipboardManager
import android.content.Context
import android.content.Intent
import android.net.Uri
import android.widget.Toast
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ExitToApp
import androidx.compose.material.icons.automirrored.filled.HelpOutline
import androidx.compose.material.icons.automirrored.filled.KeyboardArrowLeft
import androidx.compose.material.icons.automirrored.filled.ReceiptLong
import androidx.compose.material.icons.filled.ContentCopy
import androidx.compose.material.icons.filled.Info
import androidx.compose.material.icons.filled.Language
import androidx.compose.material.icons.filled.OpenInBrowser
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material.icons.filled.Share
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Surface
import androidx.compose.material3.Switch
import androidx.compose.material3.SwitchDefaults
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.credentials.CredentialManager
import com.example.R
import com.example.data.model.PharmacyOrder
import com.example.data.model.UserProfile
import com.example.ui.auth.signOut
import com.example.ui.components.BackCircleButton
import com.example.ui.components.HeaderCircleIconButton
import com.example.ui.theme.BorderSubtle
import com.example.ui.theme.ForestGreen
import com.example.ui.theme.ForestGreenLight
import com.example.ui.theme.MintSoft
import com.example.ui.theme.MintTeal
import com.example.ui.theme.NeutralBackground
import com.example.ui.theme.PureWhite
import com.example.ui.theme.TextPrimary
import com.example.ui.theme.TextSecondary

@Composable
fun ProfileScreen(
    profile: UserProfile?,
    orders: List<PharmacyOrder>,
    isAdminMode: Boolean = false,
    onBack: () -> Unit,
    onToggleLanguage: (String) -> Unit,
    onToggleNotifications: (Boolean) -> Unit,
    onSignedOut: () -> Unit,
    modifier: Modifier = Modifier
) {
    val context = LocalContext.current
    val scope = rememberCoroutineScope()
    val credentialManager = remember(context) { CredentialManager.create(context) }

    var showSettingsDialog by remember { mutableStateOf(false) }
    var showHelpOrdersDialog by remember { mutableStateOf(false) }
    var showAboutDialog by remember { mutableStateOf(false) }
    var showAppLinkDialog by remember { mutableStateOf(false) }
    val appShareUrl = stringResource(id = R.string.app_share_url)

    val currentLanguage = profile?.language ?: "ar"
    val notificationsEnabled = profile?.notificationsEnabled ?: true

    Box(
        modifier = modifier
            .fillMaxSize()
            .background(PureWhite),
        contentAlignment = Alignment.TopCenter
    ) {
        Column(
            modifier = Modifier
                .widthIn(max = 600.dp)
                .fillMaxSize()
                .verticalScroll(rememberScrollState())
                .padding(bottom = 110.dp)
        ) {
            // Curved Dark Green Top Banner + Overlapping Avatar
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(220.dp)
            ) {
                Canvas(modifier = Modifier.fillMaxSize()) {
                    val path = Path().apply {
                        moveTo(0f, 0f)
                        lineTo(size.width, 0f)
                        lineTo(size.width, size.height * 0.72f)
                        cubicTo(
                            size.width * 0.7f,
                            size.height * 1.02f,
                            size.width * 0.3f,
                            size.height * 0.56f,
                            0f,
                            size.height * 0.85f
                        )
                        close()
                    }
                    drawPath(path = path, color = ForestGreen)
                }

                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .statusBarsPadding()
                        .padding(horizontal = 18.dp, vertical = 14.dp),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    BackCircleButton(onClick = onBack, darkSurface = true)
                    Text(
                        text = "الإعدادات",
                        style = MaterialTheme.typography.titleLarge,
                        color = PureWhite
                    )
                    HeaderCircleIconButton(
                        onClick = { showSettingsDialog = true },
                        contentDescription = "تعديل الإعدادات",
                        backgroundColor = PureWhite.copy(alpha = 0.2f),
                        iconTint = PureWhite
                    ) {
                        Icon(
                            imageVector = Icons.Filled.Settings,
                            contentDescription = "الإعدادات",
                            tint = PureWhite,
                            modifier = Modifier.size(18.dp)
                        )
                    }
                }

                Surface(
                    shape = CircleShape,
                    color = MintSoft,
                    modifier = Modifier
                        .align(Alignment.BottomCenter)
                        .offset(y = 14.dp)
                        .size(96.dp)
                        .border(4.dp, PureWhite, CircleShape)
                ) {
                    Box(contentAlignment = Alignment.Center) {
                        Icon(
                            imageVector = Icons.Filled.Settings,
                            contentDescription = "إعدادات التطبيق",
                            tint = ForestGreen,
                            modifier = Modifier.size(42.dp)
                        )
                    }
                }
            }

            Spacer(modifier = Modifier.height(22.dp))

            // Dark Forest Green Rounded Profile Card containing white settings rows + Sign Out
            Surface(
                shape = RoundedCornerShape(28.dp),
                color = ForestGreen,
                shadowElevation = 6.dp,
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 18.dp)
            ) {
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 18.dp, vertical = 22.dp),
                    horizontalAlignment = Alignment.CenterHorizontally,
                    verticalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    Text(
                        text = "تفضيلات التطبيق",
                        style = MaterialTheme.typography.headlineSmall,
                        color = PureWhite
                    )
                    if (isAdminMode) {
                        Surface(
                            shape = RoundedCornerShape(10.dp),
                            color = MintTeal.copy(alpha = 0.25f)
                        ) {
                            Text(
                                text = "وضع المسؤول (بدون تسجيل دخول)",
                                style = MaterialTheme.typography.labelSmall,
                                color = PureWhite,
                                modifier = Modifier.padding(horizontal = 10.dp, vertical = 4.dp)
                            )
                        }
                    }

                    Spacer(modifier = Modifier.height(4.dp))

                    ProfileMenuRow(
                        title = if (currentLanguage == "ar") "تغير اللغة (العربية)" else "تغير اللغة (English)",
                        icon = Icons.Filled.Language,
                        onClick = { onToggleLanguage(currentLanguage) },
                        modifier = Modifier.testTag("profile_language_row")
                    )

                    ProfileMenuRow(
                        title = "الإعدادات",
                        icon = Icons.Filled.Settings,
                        onClick = { showSettingsDialog = true },
                        modifier = Modifier.testTag("profile_settings_row")
                    )

                    ProfileMenuRow(
                        title = "الطلبات والمساعدة (${orders.size})",
                        icon = Icons.AutoMirrored.Filled.HelpOutline,
                        onClick = { showHelpOrdersDialog = true },
                        modifier = Modifier.testTag("profile_help_row")
                    )

                    ProfileMenuRow(
                        title = "رابط التطبيق وتحميله",
                        icon = Icons.Filled.Share,
                        onClick = { showAppLinkDialog = true },
                        modifier = Modifier.testTag("profile_app_link_row")
                    )

                    ProfileMenuRow(
                        title = "حول التطبيق",
                        icon = Icons.Filled.Info,
                        onClick = { showAboutDialog = true },
                        modifier = Modifier.testTag("profile_about_row")
                    )

                    Spacer(modifier = Modifier.height(8.dp))

                    Surface(
                        onClick = {
                            if (isAdminMode) {
                                onSignedOut()
                            } else {
                                signOut(
                                    credentialManager = credentialManager,
                                    scope = scope,
                                    onSignOutComplete = onSignedOut
                                )
                            }
                        },
                        shape = RoundedCornerShape(16.dp),
                        color = ForestGreenLight,
                        border = BorderStroke(1.dp, PureWhite.copy(alpha = 0.18f)),
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(52.dp)
                            .testTag("sign_out_button")
                    ) {
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.Center
                        ) {
                            Icon(
                                imageVector = Icons.AutoMirrored.Filled.ExitToApp,
                                contentDescription = "تسجيل الخروج",
                                tint = PureWhite,
                                modifier = Modifier.size(20.dp)
                            )
                            Spacer(modifier = Modifier.width(8.dp))
                            Text(
                                text = "تسجيل الخروج",
                                style = MaterialTheme.typography.titleMedium,
                                color = PureWhite,
                                fontWeight = FontWeight.Bold
                            )
                        }
                    }
                }
            }
        }
    }

    if (showSettingsDialog) {
        AlertDialog(
            onDismissRequest = { showSettingsDialog = false },
            containerColor = PureWhite,
            title = {
                Text(
                    text = "إعدادات الحساب",
                    style = MaterialTheme.typography.titleLarge,
                    color = TextPrimary
                )
            },
            text = {
                Column(verticalArrangement = Arrangement.spacedBy(12.dp)) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text(
                            text = "إشعارات الطلبات والعروض",
                            style = MaterialTheme.typography.bodyLarge,
                            color = TextPrimary
                        )
                        Switch(
                            checked = notificationsEnabled,
                            onCheckedChange = onToggleNotifications,
                            colors = SwitchDefaults.colors(
                                checkedThumbColor = PureWhite,
                                checkedTrackColor = MintTeal
                            )
                        )
                    }
                    Text(
                        text = "عنوان التوصيل الافتراضي: ${profile?.selectedLocation ?: "الموقع الحالي"}",
                        style = MaterialTheme.typography.bodyMedium,
                        color = TextSecondary
                    )
                }
            },
            confirmButton = {
                TextButton(onClick = { showSettingsDialog = false }) {
                    Text("حسناً", color = ForestGreen, fontWeight = FontWeight.Bold)
                }
            }
        )
    }

    if (showHelpOrdersDialog) {
        AlertDialog(
            onDismissRequest = { showHelpOrdersDialog = false },
            containerColor = PureWhite,
            title = {
                Text(
                    text = "طلباتي السابقة والمساعدة",
                    style = MaterialTheme.typography.titleLarge,
                    color = TextPrimary
                )
            },
            text = {
                Column(verticalArrangement = Arrangement.spacedBy(10.dp)) {
                    if (orders.isEmpty()) {
                        Text(
                            text = "لا توجد طلبات مكتملة بعد. يمكنك إضافة الأدوية للسلة وإتمام الطلب خلال 30 دقيقة.",
                            style = MaterialTheme.typography.bodyMedium,
                            color = TextSecondary
                        )
                    } else {
                        orders.take(4).forEach { order ->
                            Surface(
                                shape = RoundedCornerShape(12.dp),
                                color = NeutralBackground,
                                border = BorderStroke(1.dp, BorderSubtle),
                                modifier = Modifier.fillMaxWidth()
                            ) {
                                Row(
                                    modifier = Modifier.padding(10.dp),
                                    horizontalArrangement = Arrangement.SpaceBetween,
                                    verticalAlignment = Alignment.CenterVertically
                                ) {
                                    Row(verticalAlignment = Alignment.CenterVertically) {
                                        Icon(
                                            imageVector = Icons.AutoMirrored.Filled.ReceiptLong,
                                            contentDescription = null,
                                            tint = ForestGreen,
                                            modifier = Modifier.size(18.dp)
                                        )
                                        Spacer(modifier = Modifier.width(8.dp))
                                        Column {
                                            Text(
                                                text = "${order.itemCount} منتجات • ${order.paymentMethod}",
                                                style = MaterialTheme.typography.titleSmall,
                                                color = TextPrimary
                                            )
                                            Text(
                                                text = order.deliveryLocation,
                                                style = MaterialTheme.typography.bodySmall,
                                                color = TextSecondary
                                            )
                                        }
                                    }
                                    Text(
                                        text = "${order.totalUsd.toInt()}$",
                                        style = MaterialTheme.typography.titleSmall,
                                        color = ForestGreen,
                                        fontWeight = FontWeight.Bold
                                    )
                                }
                            }
                        }
                    }
                }
            },
            confirmButton = {
                TextButton(onClick = { showHelpOrdersDialog = false }) {
                    Text("إغلاق", color = ForestGreen, fontWeight = FontWeight.Bold)
                }
            }
        )
    }

    if (showAppLinkDialog) {
        AlertDialog(
            onDismissRequest = { showAppLinkDialog = false },
            containerColor = PureWhite,
            title = {
                Text(
                    text = "رابط التطبيق",
                    style = MaterialTheme.typography.titleLarge,
                    color = TextPrimary
                )
            },
            text = {
                Column(verticalArrangement = Arrangement.spacedBy(12.dp)) {
                    Text(
                        text = "يمكنك فتح معاينة التطبيق عبر الرابط التالي، ولتحميل ملف APK مباشرة افتح قائمة الإعدادات (Settings) في المنصة واختر تصدير أو إنشاء APK:",
                        style = MaterialTheme.typography.bodyMedium,
                        color = TextSecondary
                    )
                    Surface(
                        shape = RoundedCornerShape(12.dp),
                        color = NeutralBackground,
                        border = BorderStroke(1.dp, BorderSubtle),
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Text(
                            text = appShareUrl,
                            style = MaterialTheme.typography.bodySmall,
                            color = ForestGreen,
                            maxLines = 2,
                            overflow = TextOverflow.Ellipsis,
                            modifier = Modifier.padding(12.dp)
                        )
                    }
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        OutlinedButton(
                            onClick = {
                                val clipboard = context.getSystemService(Context.CLIPBOARD_SERVICE) as ClipboardManager
                                clipboard.setPrimaryClip(ClipData.newPlainText("App URL", appShareUrl))
                                Toast.makeText(context, "تم نسخ الرابط", Toast.LENGTH_SHORT).show()
                            },
                            shape = RoundedCornerShape(12.dp),
                            border = BorderStroke(1.dp, ForestGreen),
                            modifier = Modifier.weight(1f)
                        ) {
                            Icon(
                                imageVector = Icons.Filled.ContentCopy,
                                contentDescription = null,
                                tint = ForestGreen,
                                modifier = Modifier.size(16.dp)
                            )
                            Spacer(modifier = Modifier.width(4.dp))
                            Text("نسخ", color = ForestGreen)
                        }
                        Button(
                            onClick = {
                                val intent = Intent(Intent.ACTION_VIEW, Uri.parse(appShareUrl))
                                runCatching { context.startActivity(intent) }
                            },
                            shape = RoundedCornerShape(12.dp),
                            colors = ButtonDefaults.buttonColors(containerColor = ForestGreen),
                            modifier = Modifier.weight(1f)
                        ) {
                            Icon(
                                imageVector = Icons.Filled.OpenInBrowser,
                                contentDescription = null,
                                tint = PureWhite,
                                modifier = Modifier.size(16.dp)
                            )
                            Spacer(modifier = Modifier.width(4.dp))
                            Text("فتح", color = PureWhite)
                        }
                    }
                    OutlinedButton(
                        onClick = {
                            val shareIntent = Intent(Intent.ACTION_SEND).apply {
                                type = "text/plain"
                                putExtra(Intent.EXTRA_TEXT, "تطبيق صيدليتي:\n$appShareUrl")
                            }
                            runCatching {
                                context.startActivity(Intent.createChooser(shareIntent, "مشاركة رابط التطبيق"))
                            }
                        },
                        shape = RoundedCornerShape(12.dp),
                        border = BorderStroke(1.dp, BorderSubtle),
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Icon(
                            imageVector = Icons.Filled.Share,
                            contentDescription = null,
                            tint = ForestGreen,
                            modifier = Modifier.size(16.dp)
                        )
                        Spacer(modifier = Modifier.width(6.dp))
                        Text("مشاركة الرابط", color = ForestGreen)
                    }
                }
            },
            confirmButton = {
                TextButton(onClick = { showAppLinkDialog = false }) {
                    Text("إغلاق", color = ForestGreen, fontWeight = FontWeight.Bold)
                }
            }
        )
    }

    if (showAboutDialog) {
        AlertDialog(
            onDismissRequest = { showAboutDialog = false },
            containerColor = PureWhite,
            title = {
                Text(
                    text = "حول تطبيق صيدليتي",
                    style = MaterialTheme.typography.titleLarge,
                    color = TextPrimary
                )
            },
            text = {
                Text(
                    text = "تطبيق صيدليتي يوفر لك طلب الأدوية ومنتجات العناية والتواصل المباشر مع أقرب الصيدليات المعتمدة مع توصيل سريع خلال 30 دقيقة.",
                    style = MaterialTheme.typography.bodyMedium,
                    color = TextSecondary,
                    textAlign = TextAlign.Start
                )
            },
            confirmButton = {
                TextButton(onClick = { showAboutDialog = false }) {
                    Text("تم", color = ForestGreen, fontWeight = FontWeight.Bold)
                }
            }
        )
    }
}

@Composable
private fun ProfileMenuRow(
    title: String,
    icon: ImageVector,
    onClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    Surface(
        onClick = onClick,
        shape = RoundedCornerShape(16.dp),
        color = PureWhite,
        modifier = modifier
            .fillMaxWidth()
            .height(54.dp)
    ) {
        Row(
            modifier = Modifier
                .fillMaxSize()
                .padding(horizontal = 14.dp),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Row(verticalAlignment = Alignment.CenterVertically) {
                Icon(
                    imageVector = icon,
                    contentDescription = null,
                    tint = ForestGreen,
                    modifier = Modifier.size(20.dp)
                )
                Spacer(modifier = Modifier.width(12.dp))
                Text(
                    text = title,
                    style = MaterialTheme.typography.titleSmall,
                    color = TextPrimary
                )
            }
            Icon(
                imageVector = Icons.AutoMirrored.Filled.KeyboardArrowLeft,
                contentDescription = null,
                tint = TextSecondary,
                modifier = Modifier.size(20.dp)
            )
        }
    }
}
