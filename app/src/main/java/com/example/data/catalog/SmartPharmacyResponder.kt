package com.example.data.catalog

import com.example.data.model.Pharmacy

object SmartPharmacyResponder {

    fun buildReply(
        pharmacy: Pharmacy,
        userText: String,
        hasAttachment: Boolean,
        isEnglish: Boolean
    ): String {
        val lower = userText.lowercase()
        val pharmacyName = pharmacy.localizedName(isEnglish)
        val eta = pharmacy.localizedDeliveryTime(isEnglish)

        if (hasAttachment ||
            lower.contains("وصفة") ||
            lower.contains("prescription") ||
            lower.contains("ordonnance")
        ) {
            return if (isEnglish) {
                "$pharmacyName received your prescription photo. Our pharmacist on duty is verifying dosage & stock now ($eta)."
            } else {
                "استلمت $pharmacyName صورة وصفتك الطبية بنجاح. يقوم الصيدلي المناوب بمراجعتها الآن ($eta)."
            }
        }

        if (lower.contains("متوفر") ||
            lower.contains("موجود") ||
            lower.contains("stock") ||
            lower.contains("available")
        ) {
            return if (isEnglish) {
                if (pharmacy.openNow) {
                    "Yes, standard OTC & essential medicines are in stock at $pharmacyName (${pharmacy.localizedArea(true)}). Express courier dispatch takes ~$eta."
                } else {
                    "$pharmacyName is currently off daytime rotation, but our 24/7 partner pharmacies in Casablanca have stock ready."
                }
            } else {
                if (pharmacy.openNow) {
                    "نعم، الأدوية الأساسية والفيتامينات متوفرة حالياً في $pharmacyName (${pharmacy.localizedArea(false)}) مع توصيل سريع ($eta)."
                } else {
                    "$pharmacyName خارج المداومة حالياً، ويمكنك طلب نفس الدواء فوراً من صيدليات 24س/24 المفتوحة."
                }
            }
        }

        if (lower.contains("توصيل") ||
            lower.contains("مدة") ||
            lower.contains("delivery") ||
            lower.contains("fast") ||
            lower.contains("eta")
        ) {
            return if (isEnglish) {
                "Estimated courier delivery from $pharmacyName is $eta for a flat 20 MAD fee across Casablanca."
            } else {
                "مدة التوصيل المتوقعة من $pharmacyName هي $eta بتكلفة توصيل موحدة 20 د.م. داخل الدار البيضاء."
            }
        }

        if (lower.contains("جرعة") ||
            lower.contains("استعمال") ||
            lower.contains("dosage") ||
            lower.contains("advise") ||
            lower.contains("dose")
        ) {
            return if (isEnglish) {
                "Our pharmacist at $pharmacyName recommends checking meal timing and max daily dose on the product card. For personalized dosage advice, tap Call (${pharmacy.phoneNumber})."
            } else {
                "ينصح صيدلي $pharmacyName بمراجعة الجرعة اليومية وتوقيت الأكل في بطاقة الدواء، أو الاتصال بنا مباشرة على ${pharmacy.phoneNumber} للاستشارة."
            }
        }

        return if (isEnglish) {
            "Thank you for messaging $pharmacyName. Share the medicine name or attach your prescription photo and we will confirm availability right away."
        } else {
            "شكراً لتواصلك مع $pharmacyName. أرسل اسم الدواء أو أرفق صورة الوصفة الطبية وسنؤكد لك التوفر فوراً."
        }
    }
}
