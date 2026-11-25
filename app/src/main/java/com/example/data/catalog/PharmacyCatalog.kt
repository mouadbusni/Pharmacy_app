package com.example.data.catalog

import com.example.data.model.AppNotification
import com.example.data.model.PackagingStyle
import com.example.data.model.Pharmacy
import com.example.data.model.PharmacyCategory
import com.example.data.model.Product

object PharmacyCatalog {

    val deliveryLocations = listOf(
        "الموقع الحالي",
        "الفحامة - دمشق",
        "جرمانا - دمشق",
        "المزة - فيلات شرقية",
        "أبو رمانة - دمشق"
    )

    val categories = listOf(
        PharmacyCategory("all", "الكل", "All", 0xFF00402E),
        PharmacyCategory("medicines", "الأدوية", "Medicines", 0xFF1EAF8F),
        PharmacyCategory("skincare", "العناية بالبشرة", "Skincare", 0xFFE68A9E),
        PharmacyCategory("vitamins", "الفيتامينات", "Vitamins", 0xFFF2994A),
        PharmacyCategory("cosmetics", "التجميل", "Cosmetics", 0xFFD96B9B),
        PharmacyCategory("firstaid", "الإسعافات", "First Aid", 0xFF3E8ED0)
    )

    val products = listOf(
        Product(
            id = "prod_biosteron",
            nameAr = "Biosteron-D 25mg",
            subtitleAr = "دواء لمرضى السكري والمناعة",
            descriptionAr = "دواء لمرضى السكري من النوع الثاني لكبار السن فعال جداً ويساعدك في دعم التوازن الحيوي وعلاج السكري بشكل سريع وآمن",
            categoryId = "vitamins",
            categoryAr = "الفيتامينات",
            priceUsd = 20.0,
            rating = 4.5,
            reviewCount = 50,
            deliveryTimeAr = "التوصيل خلال 30 دقيقة",
            dosageMorningEvening = "مساءً وصباحاً",
            dosageMealTiming = "يفضل بعد الأكل",
            isBestSeller = true,
            packagingStyle = PackagingStyle.BIOSTERON_PHOTO
        ),
        Product(
            id = "prod_detocyn",
            nameAr = "دواء لمرضى السكري",
            subtitleAr = "Detocyn Glucose Support",
            descriptionAr = "تركيبة متطورة تساعد في تنظيم مستوى السكر في الدم وتحسين الحساسية للأنسولين بإشراف صيدلاني موثوق",
            categoryId = "medicines",
            categoryAr = "الأدوية",
            priceUsd = 20.0,
            rating = 4.5,
            reviewCount = 42,
            deliveryTimeAr = "التوصيل خلال 30 دقيقة",
            dosageMorningEvening = "مرة صباحاً",
            dosageMealTiming = "قبل الفطور بنصف ساعة",
            isBestSeller = true,
            packagingStyle = PackagingStyle.DETOCYN_GREEN
        ),
        Product(
            id = "prod_utorinic",
            nameAr = "دواء لمرضى الضغط",
            subtitleAr = "Utorinic Care 60 Caps",
            descriptionAr = "كبسولات متوازنة للمساعدة في استقرار ضغط الدم ودعم صحة الأوعية الدموية والقلب على مدار اليوم",
            categoryId = "medicines",
            categoryAr = "الأدوية",
            priceUsd = 60.0,
            rating = 4.5,
            reviewCount = 68,
            deliveryTimeAr = "التوصيل خلال 30 دقيقة",
            dosageMorningEvening = "مساءً وصباحاً",
            dosageMealTiming = "يفضل بعد الأكل",
            isBestSeller = true,
            packagingStyle = PackagingStyle.UTORINIC_TEAL
        ),
        Product(
            id = "prod_aspirina",
            nameAr = "Aspirina مسكن وخافض حرارة",
            subtitleAr = "مسكن للآلام والالتهابات",
            descriptionAr = "أقراص سريعة المفعول لتخفيف الصداع وآلام المفاصل والالتهابات اليومية بلطف على المعدة",
            categoryId = "medicines",
            categoryAr = "الأدوية",
            priceUsd = 30.0,
            rating = 4.7,
            reviewCount = 91,
            deliveryTimeAr = "التوصيل خلال 30 دقيقة",
            dosageMorningEvening = "عند الحاجة",
            dosageMealTiming = "بعد الطعام مع كوب ماء",
            isBestSeller = false,
            packagingStyle = PackagingStyle.ASPIRINA_MINT
        ),
        Product(
            id = "prod_tribetron",
            nameAr = "Tribetron مقوي الأعصاب",
            subtitleAr = "مجموعة فيتامين ب المركب",
            descriptionAr = "مركب فيتامينات B1 و B6 و B12 لدعم صحة الأعصاب وتقليل التنميل والإرهاق البدني",
            categoryId = "vitamins",
            categoryAr = "الفيتامينات",
            priceUsd = 30.0,
            rating = 4.5,
            reviewCount = 37,
            deliveryTimeAr = "التوصيل خلال 30 دقيقة",
            dosageMorningEvening = "حبة يومياً",
            dosageMealTiming = "بعد الغداء",
            isBestSeller = false,
            packagingStyle = PackagingStyle.TRIBETRON_RED
        ),
        Product(
            id = "prod_nutiva",
            nameAr = "Nutiva Forte كريم مرطب",
            subtitleAr = "عناية يومية للبشرة الحساسة",
            descriptionAr = "كريم غني بالسيراميد والبانثينول لترطيب البشرة الجافة وتجديد الحاجز الطبيعي طوال اليوم",
            categoryId = "skincare",
            categoryAr = "العناية بالبشرة",
            priceUsd = 10.0,
            rating = 4.8,
            reviewCount = 112,
            deliveryTimeAr = "التوصيل خلال 30 دقيقة",
            dosageMorningEvening = "صباحاً ومساءً",
            dosageMealTiming = "استخدام موضعي",
            isBestSeller = true,
            packagingStyle = PackagingStyle.NUTIVA_LIME
        ),
        Product(
            id = "prod_flex",
            nameAr = "Flex مكمل المفاصل",
            subtitleAr = "جلوكوزامين وكولاجين",
            descriptionAr = "مكمل غذائي يدعم مرونة المفاصل والغضاريف ويخفف تيبس الحركة اليومي",
            categoryId = "firstaid",
            categoryAr = "الإسعافات",
            priceUsd = 60.0,
            rating = 4.6,
            reviewCount = 54,
            deliveryTimeAr = "التوصيل خلال 30 دقيقة",
            dosageMorningEvening = "مساءً وصباحاً",
            dosageMealTiming = "مع الوجبة الرئيسية",
            isBestSeller = true,
            packagingStyle = PackagingStyle.FLEX_NAVY
        ),
        Product(
            id = "prod_panadol",
            nameAr = "بنادول إكسترا مسكن",
            subtitleAr = "Panadol Extra 24 Tablets",
            descriptionAr = "مسكن سريع وفعال للصداع وآلام الأسنان والعضلات، لطيف وسريع الامتصاص",
            categoryId = "cosmetics",
            categoryAr = "التجميل",
            priceUsd = 15.0,
            rating = 4.9,
            reviewCount = 180,
            deliveryTimeAr = "التوصيل خلال 20 دقيقة",
            dosageMorningEvening = "كل 6 ساعات",
            dosageMealTiming = "بعد الطعام",
            isBestSeller = false,
            packagingStyle = PackagingStyle.PANADOL_BLUE
        )
    )

    val pharmacies = listOf(
        Pharmacy(
            id = "pharm_perla",
            nameAr = "صيدلية بيرلا",
            areaAr = "الفحامة",
            distanceAr = "بعيد عنك 1.2 كم",
            rating = 4.8,
            deliveryTimeAr = "التوصيل خلال 30 دقيقة",
            openNow = true,
            initials = "BP",
            accentColorHex = 0xFF5D3A8E,
            phoneNumber = "+96311223344",
            lastMessagePreview = "متوفر دواء السكر والتوصيل سريع خلال 30 دقيقة",
            lastMessageTime = "8:45 AM"
        ),
        Pharmacy(
            id = "pharm_mariam",
            nameAr = "صيدلية مريم عساف",
            areaAr = "جرمانا / دمشق",
            distanceAr = "بعيد عنك 20 كم",
            rating = 4.5,
            deliveryTimeAr = "التوصيل خلال 30 دقيقة",
            openNow = true,
            initials = "MA",
            accentColorHex = 0xFF1D8F5A,
            phoneNumber = "+96311554433",
            lastMessagePreview = "أهلاً بك، يمكنك إرسال الوصفة الطبية هنا",
            lastMessageTime = "8:45 AM"
        ),
        Pharmacy(
            id = "pharm_ahmad",
            nameAr = "صيدلية أحمد",
            areaAr = "الفحامة",
            distanceAr = "بعيد عنك 2.5 كم",
            rating = 4.6,
            deliveryTimeAr = "التوصيل خلال 30 دقيقة",
            openNow = true,
            initials = "AH",
            accentColorHex = 0xFF1B8A99,
            phoneNumber = "+96311887766",
            lastMessagePreview = "تم تجهيز طلب الفيتامينات الخاص بك",
            lastMessageTime = "8:30 AM"
        ),
        Pharmacy(
            id = "pharm_ola",
            nameAr = "صيدلية علا",
            areaAr = "المزة",
            distanceAr = "بعيد عنك 3.8 كم",
            rating = 4.7,
            deliveryTimeAr = "التوصيل خلال 30 دقيقة",
            openNow = true,
            initials = "OL",
            accentColorHex = 0xFF1EAF8F,
            phoneNumber = "+96311998877",
            lastMessagePreview = "متاح الآن للاستشارات الدوائية",
            lastMessageTime = "8:45 AM"
        ),
        Pharmacy(
            id = "pharm_oya",
            nameAr = "صيدلية أويا",
            areaAr = "أبو رمانة",
            distanceAr = "بعيد عنك 4.1 كم",
            rating = 4.4,
            deliveryTimeAr = "التوصيل خلال 30 دقيقة",
            openNow = false,
            initials = "OY",
            accentColorHex = 0xFF2E9C6F,
            phoneNumber = "+96311445566",
            lastMessagePreview = "تفتح الصيدلية الساعة 9:00 صباحاً",
            lastMessageTime = "8:45 AM"
        ),
        Pharmacy(
            id = "pharm_adham",
            nameAr = "صيدلية الأدهم",
            areaAr = "الشعلان",
            distanceAr = "بعيد عنك 5.0 كم",
            rating = 4.5,
            deliveryTimeAr = "التوصيل خلال 30 دقيقة",
            openNow = false,
            initials = "AD",
            accentColorHex = 0xFF2872B8,
            phoneNumber = "+96311665544",
            lastMessagePreview = "مغلق الآن - نعود قريباً",
            lastMessageTime = "8:45 AM"
        )
    )

    val defaultRecentSearchChips = listOf("بنادول", "بروفين", "فيتامين")

    val sampleNotifications = listOf(
        AppNotification(
            id = "notif_1",
            titleAr = "خصم 40% على الفيتامينات",
            bodyAr = "صحة أفضل تبدأ بعرض أفضل! استفد من عروض الفيتامينات والمكملات اليوم.",
            timeAr = "منذ 15 دقيقة",
            unread = true
        ),
        AppNotification(
            id = "notif_2",
            titleAr = "صيدلية بيرلا متاحة للدردشة",
            bodyAr = "رد الصيدلي على استفسارك حول دواء السكري والتوصيل السريع.",
            timeAr = "منذ ساعة",
            unread = true
        ),
        AppNotification(
            id = "notif_3",
            titleAr = "توصيل سريع خلال 30 دقيقة",
            bodyAr = "اطلب الآن من أقرب صيدلية في منطقتك مع تتبع مباشر.",
            timeAr = "أمس",
            unread = false
        )
    )

    fun seedConversationForPharmacy(pharmacyId: String): List<Pair<String, String>> {
        return when (pharmacyId) {
            "pharm_perla" -> listOf(
                "USER" to "مرحبا يعطيك العافية",
                "PHARMACY" to "أهلا وسهلا تفضل",
                "USER" to "بدي أسألك عن دواء السكر هل متوفر للتوصيل بأسرع وقت ممكن",
                "PHARMACY" to "طبعا متوفر وفي توصيل سريع أيمت بتحبي أبعتلك الطلب؟",
                "USER" to "ليوم لو سمحت",
                "PHARMACY" to "تكرم معافاية يارب",
                "USER" to "تسلم شكراً"
            )
            else -> listOf(
                "PHARMACY" to "مرحباً بك في صيدليتنا، كيف يمكننا مساعدتك اليوم؟"
            )
        }
    }
}
