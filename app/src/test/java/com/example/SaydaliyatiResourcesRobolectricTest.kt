package com.example

import android.content.Context
import androidx.test.core.app.ApplicationProvider
import com.example.ui.theme.ArabicStrings
import com.example.ui.theme.EnglishStrings
import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Test
import org.junit.runner.RunWith
import org.robolectric.RobolectricTestRunner
import org.robolectric.annotation.Config

@RunWith(RobolectricTestRunner::class)
@Config(sdk = [36])
class SaydaliyatiResourcesRobolectricTest {

    @Test
    fun appMetadataAndResourcesMatchConfiguredBrand() {
        val context = ApplicationProvider.getApplicationContext<Context>()
        assertEquals("صيدليتي", context.getString(R.string.app_name))
        assertEquals("com.aistudio.arabicpharmacy.qrxmzk", BuildConfig.APPLICATION_ID)
        assertTrue(BuildConfig.VERSION_CODE >= 1)
    }

    @Test
    fun localizedStringsFormatCurrencyConsistently() {
        assertEquals("45 د.م.", ArabicStrings.formatPrice(45.0))
        assertEquals("45 MAD", EnglishStrings.formatPrice(45.0))
        assertTrue(ArabicStrings.promoDiscount.contains("20"))
        assertTrue(EnglishStrings.promoDiscount.contains("20"))
        assertTrue(ArabicStrings.myOrdersTitle.isNotBlank())
        assertTrue(EnglishStrings.myOrdersTitle.isNotBlank())
        assertTrue(ArabicStrings.markAllRead.isNotBlank())
        assertTrue(EnglishStrings.markAllRead.isNotBlank())
        assertTrue(ArabicStrings.getDirectionsLabel.isNotBlank())
        assertTrue(EnglishStrings.getDirectionsLabel.isNotBlank())
    }
}
