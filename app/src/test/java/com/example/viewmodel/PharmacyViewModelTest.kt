package com.example.viewmodel

import com.example.BuildConfig
import com.example.data.catalog.PharmacyCatalog
import com.example.data.repository.LocalAdminPharmacyRepository
import com.example.data.update.AppUpdateInfo
import com.example.data.update.AppUpdateState
import com.example.data.update.ReleaseCheckOutcome
import com.example.data.update.ReleaseLookup
import com.example.data.update.UpdatePreferencesReader
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.launch
import kotlinx.coroutines.test.UnconfinedTestDispatcher
import kotlinx.coroutines.test.resetMain
import kotlinx.coroutines.test.runTest
import kotlinx.coroutines.test.setMain
import org.junit.After
import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertTrue
import org.junit.Before
import org.junit.Test

@OptIn(ExperimentalCoroutinesApi::class)
class PharmacyViewModelTest {

    private val testDispatcher = UnconfinedTestDispatcher()

    @Before
    fun setUp() {
        Dispatchers.setMain(testDispatcher)
    }

    @After
    fun tearDown() {
        Dispatchers.resetMain()
    }

    private class FakeUpdatePreferencesReader(
        initialRepo: String = ""
    ) : UpdatePreferencesReader {
        private val _repoSlug = MutableStateFlow(initialRepo)
        private val _dismissedTag = MutableStateFlow("")

        override val repoSlugFlow: Flow<String> = _repoSlug
        override val dismissedTagFlow: Flow<String> = _dismissedTag

        override suspend fun saveRepoSlug(rawInput: String) {
            _repoSlug.value = rawInput
        }

        override suspend fun dismissReleaseTag(tagName: String) {
            _dismissedTag.value = tagName
        }
    }

    @Test
    fun initialFlows_emitProfileAndSeededCart() = runTest(testDispatcher) {
        val viewModel = PharmacyViewModel(
            repository = LocalAdminPharmacyRepository(),
            currentUserId = "admin_session"
        )
        backgroundScope.launch { viewModel.profileState.collect {} }
        backgroundScope.launch { viewModel.cartState.collect {} }

        val profileState = viewModel.profileState.value
        val cartState = viewModel.cartState.value

        assertTrue(profileState is UiState.Success)
        assertEquals("ar", (profileState as UiState.Success).data?.language)
        assertTrue(cartState is UiState.Success)
        assertEquals(2, (cartState as UiState.Success).data.size)
    }

    @Test
    fun cartFavoriteAndLanguageActions_updateStateAndActionMessages() = runTest(testDispatcher) {
        val viewModel = PharmacyViewModel(
            repository = LocalAdminPharmacyRepository(),
            currentUserId = "admin_session"
        )
        backgroundScope.launch { viewModel.profileState.collect {} }
        backgroundScope.launch { viewModel.cartState.collect {} }
        backgroundScope.launch { viewModel.favoritesState.collect {} }

        val product = PharmacyCatalog.products.last()
        viewModel.toggleFavorite(product, currentlyFavorite = false)
        assertTrue((viewModel.favoritesState.value as UiState.Success).data.any { it.productId == product.id })
        assertTrue(viewModel.actionMessage.value?.isNotBlank() == true)

        viewModel.toggleLanguage("ar")
        assertEquals("en", (viewModel.profileState.value as UiState.Success).data?.language)

        viewModel.updateLocation("المعاريف - الدار البيضاء")
        assertEquals("المعاريف - الدار البيضاء", (viewModel.profileState.value as UiState.Success).data?.selectedLocation)
        assertTrue(viewModel.actionMessage.value?.contains("Maarif") == true)
    }

    @Test
    fun placeOrder_completesOrderAndClearsCart() = runTest(testDispatcher) {
        val viewModel = PharmacyViewModel(
            repository = LocalAdminPharmacyRepository(),
            currentUserId = "admin_session"
        )
        backgroundScope.launch { viewModel.cartState.collect {} }
        backgroundScope.launch { viewModel.ordersState.collect {} }

        val cart = (viewModel.cartState.value as UiState.Success).data
        var callbackInvoked = false
        viewModel.placeOrder(
            paymentMethod = "cash",
            cartItems = cart,
            deliveryLocation = "الموقع الحالي",
            cardLast4 = null,
            onSuccess = { callbackInvoked = true }
        )

        assertTrue(callbackInvoked)
        assertFalse(viewModel.isSubmittingOrder.value)
        assertEquals(1, (viewModel.ordersState.value as UiState.Success).data.size)
        assertTrue((viewModel.cartState.value as UiState.Success).data.isEmpty())
    }

    @Test
    fun updateChecks_handleValidationNoReleasesUpdateAvailableAndDismissal() = runTest(testDispatcher) {
        val prefs = FakeUpdatePreferencesReader(initialRepo = "")
        var nextOutcome: Result<ReleaseCheckOutcome> = Result.success(
            ReleaseCheckOutcome.NoReleasesYet("owner/app")
        )
        val releaseLookup = ReleaseLookup { _, _, _ -> nextOutcome }

        val viewModel = PharmacyViewModel(
            repository = LocalAdminPharmacyRepository(),
            currentUserId = "admin_session",
            updatePreferencesStore = prefs,
            releaseLookup = releaseLookup
        )

        viewModel.saveRepoAndCheckUpdates("bad_repo")
        assertTrue(viewModel.appUpdateState.value is AppUpdateState.Error)

        viewModel.saveRepoAndCheckUpdates("owner/app")
        assertEquals(AppUpdateState.NoReleasesYet("owner/app"), viewModel.appUpdateState.value)

        val updateInfo = AppUpdateInfo(
            tagName = "v9.9.9",
            versionName = "9.9.9",
            versionCode = 999,
            releaseNotes = "Release",
            apkDownloadUrl = "https://github.com/owner/app/releases/download/v9.9.9/app.apk",
            htmlReleaseUrl = "https://github.com/owner/app/releases/tag/v9.9.9"
        )
        nextOutcome = Result.success(ReleaseCheckOutcome.Found(info = updateInfo, isNewer = true))
        viewModel.checkForUpdates(manual = true)

        val available = viewModel.appUpdateState.value as AppUpdateState.UpdateAvailable
        assertEquals("v9.9.9", available.info.tagName)
        assertFalse(available.isDismissed)

        viewModel.dismissUpdateBanner("v9.9.9")
        assertTrue((viewModel.appUpdateState.value as AppUpdateState.UpdateAvailable).isDismissed)

        nextOutcome = Result.success(ReleaseCheckOutcome.Found(info = updateInfo, isNewer = false))
        viewModel.checkForUpdates(manual = true)
        assertEquals(
            AppUpdateState.UpToDate("v${BuildConfig.VERSION_NAME} (${BuildConfig.VERSION_CODE})"),
            viewModel.appUpdateState.value
        )
    }
}
