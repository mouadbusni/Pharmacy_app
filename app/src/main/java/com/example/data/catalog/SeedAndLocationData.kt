package com.example.data.catalog

import com.example.data.model.AppNotification
import com.example.data.model.NotificationTarget

internal val casablancaDeliveryLocations = listOf(
    "الموقع الحالي",
    "المعاريف - الدار البيضاء",
    "الألفة - الدار البيضاء",
    "عين الشق - الدار البيضاء",
    "أنفا - الدار البيضاء",
    "سيدي معروف - الدار البيضاء",
    "الحي المحمدي - الدار البيضاء"
)

internal val casablancaLocationEnMap = mapOf(
    "الموقع الحالي" to "Current Location (Casablanca)",
    "المعاريف - الدار البيضاء" to "Maarif - Casablanca",
    "الألفة - الدار البيضاء" to "Hay El Oulfa - Casablanca",
    "عين الشق - الدار البيضاء" to "Aïn Chock - Casablanca",
    "أنفا - الدار البيضاء" to "Anfa - Casablanca",
    "سيدي معروف - الدار البيضاء" to "Sidi Maârouf - Casablanca",
    "الحي المحمدي - الدار البيضاء" to "Hay Mohammadi - Casablanca"
)

internal val defaultSearchChips = listOf("دوليبران", "فيتامين C", "فانتولين")

internal val sampleNotificationsList = listOf(
    AppNotification(
        id = "notif_1",
        titleAr = "خصم 20% على الفيتامينات والمناعة",
        titleEn = "20% Off Vitamins & Immunity",
        bodyAr = "وفّر 20% فوراً على Vita-C 1000 وMagné-B6 مع توصيل سريع في الدار البيضاء.",
        bodyEn = "Save 20% instantly on Vita-C 1000 and Magné-B6 with fast Casablanca delivery.",
        timeAr = "منذ 15 دقيقة",
        timeEn = "15 mins ago",
        unread = true,
        target = NotificationTarget.PROMO_VITAMINS
    ),
    AppNotification(
        id = "notif_2",
        titleAr = "صيدلية دو ليس (24س/24) متاحة للدردشة",
        titleEn = "Pharmacie du Lys (24h/24) is Online",
        bodyAr = "تواصل مباشرة مع صيدلي مناوب في الألفة أو المعاريف للاستشارة وإرفاق الوصفة الطبية.",
        bodyEn = "Chat directly with a pharmacist on duty in El Oulfa or Maarif and attach prescriptions.",
        timeAr = "منذ ساعة",
        timeEn = "1 hour ago",
        unread = true,
        target = NotificationTarget.CHATS
    ),
    AppNotification(
        id = "notif_3",
        titleAr = "دليل صيدليات الحراسة والمداومة 24/24 بالدار البيضاء",
        titleEn = "Casablanca 24h/24 & Day Duty Directory",
        bodyAr = "تصفح 15 صيدلية موثقة في الدار البيضاء مع أرقام الهاتف — يُنصح بالاتصال قبل التوجه.",
        bodyEn = "Browse 15 verified Casablanca pharmacies with direct dial — call ahead to confirm rotation.",
        timeAr = "اليوم",
        timeEn = "Today",
        unread = false,
        target = NotificationTarget.PHARMACIES
    )
)

internal fun buildSeedConversationForPharmacy(pharmacyId: String): List<Pair<String, String>> {
    return when (pharmacyId) {
        "pharm_du_lys" -> listOf(
            "USER" to "السلام عليكم، هل دوليبران 1000 متوفر للتوصيل الآن؟",
            "PHARMACY" to "وعليكم السلام مرحباً بك في صيدلية دو ليس، نعم متوفر ومفتوحون 24/24.",
            "USER" to "ممتاز، وهل يمكن طلب أوغمنتين معاه؟",
            "PHARMACY" to "نعم بكل تأكيد، فقط أرفق صورة الوصفة الطبية (Ordonnance) وسنجهز الطلب فوراً."
        )
        "pharm_clinique_andalouss", "pharm_al_hikma" -> listOf(
            "PHARMACY" to "مرحباً بك، صيدليتنا في المداومة النهارية (Jour). يرجى الاتصال قبل التنقل أو ترك رسالتك هنا."
        )
        else -> listOf(
            "PHARMACY" to "مرحباً بك في صيدليتنا بالدار البيضاء (24/24)، كيف يمكننا مساعدتك اليوم؟"
        )
    }
}
