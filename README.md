# Saydaliyati (صيدليتي)

**Saydaliyati** is a bilingual (Arabic RTL / English LTR) Android application built with Kotlin and Jetpack Compose for discovering pharmacies across Casablanca, browsing essential medications, consulting pharmacists in real time, and placing rapid delivery orders.

---

## Download APK

Download the latest Android APK from **[GitHub Releases](https://github.com/mouadbusni/Pharmacy_app/releases/latest)** (binary APKs are distributed via GitHub Releases rather than committed directly to Git history):

- **Releases Page:** [github.com/mouadbusni/Pharmacy_app/releases](https://github.com/mouadbusni/Pharmacy_app/releases)
- **Version:** `1.0` (`versionCode 1`)
- **Compatibility:** Android 7.0+ (`minSdk 24`, `targetSdk 36`)

### Installing on Your Phone

1. Open [Releases](https://github.com/mouadbusni/Pharmacy_app/releases/latest) on your Android device and download the attached `.apk` asset.
2. Open the downloaded `.apk` file and allow installation from your browser if prompted.
3. Alternatively, build and install locally via ADB over USB:
   ```bash
   gradle :app:assembleDebug
   adb install -r app/build/outputs/apk/debug/app-debug.apk
   ```

> **Note:** If you previously installed a release-signed APK with a different keystore, uninstall the existing package once before installing a debug-signed or differently signed build.

---

## Key Features

### Casablanca Pharmacy Directory & Duty Schedules
- **15 Casablanca Pharmacies** across major districts including Maarif, Gautier, Gironde, Oasis, Anfa, Ain Diab, Bourgogne, Roches Noires, Mers Sultan, and Derb Ghallef.
- **24h/24 & Day Duty (Garde) Badges:** Filter pharmacies by `All`, `24h/24`, or `Day Duty`, view street addresses and estimated delivery times, and call pharmacies directly.
- **Emergency & Garde Advisory:** Built-in daily rotation reminder and quick SAMU `15` reference.

### Moroccan Medication Catalog & Safety Guidance
- **10 Essential Medications** priced in Moroccan Dirhams (`MAD` / `د.م.`) with white-background product packshots across product cards, detail screens, and cart thumbnails.
- **OTC vs. Prescription (Rx) Classification:** Clear `OTC` and `Rx` badges, morning/evening and meal-timing dosage guides, and dedicated **Pharmacist Safety Note** cards.
- **Instant Filtering & Search:** Search medications with a 1-tap clear button, recent search history chips, category circles, and `All` / `OTC` / `Rx Required` filter chips.

### Pharmacist Consultation & Ordering
- **Direct Pharmacist Chat:** Message any pharmacy with quick-reply prompts, prescription attachment actions, and one-tap phone dialing.
- **Cart, Favorites & Checkout:** Manage item quantities, save favorite medications, and check out with Visa, Mastercard, or Cash on Delivery.
- **Order History & Cloud Sync:** Signed-in users sync profile preferences, cart items, favorites, recent searches, chats, and order history with Firebase Firestore; an Admin preview mode is also available for offline evaluation.

### Bilingual & Adaptive Experience
- **Arabic (RTL) & English (LTR):** Toggle languages seamlessly from Settings with full layout direction mirroring.
- **Responsive Layouts:** Floating 4-tab bottom bar with centered Cart FAB on phones and an adaptive `NavigationRail` + multi-column grids on tablets and foldables (`>= 600dp`).

---

## Over-the-Air Updates via GitHub Releases

Saydaliyati includes a built-in GitHub Releases update checker so new builds pushed to your repository appear directly on your phone:

1. Open **Settings** -> **App Updates & GitHub Repo**.
2. Enter your repository in `owner/repo` format and tap **Save & Check**.
3. Tap **Copy GitHub Action YAML** and add it to `.github/workflows/release-apk.yml` in your repository on GitHub.
4. Configure your repository secrets (`SIGNING_KEYSTORE_BASE64`, `STORE_PASSWORD`, and `KEY_PASSWORD`) if signing release APKs with your upload keystore.
5. Whenever a new tagged GitHub Release with an `.apk` asset is published, the home screen displays an **Update Available** banner with a 1-tap **Update Now** download button.

---

## Tech Stack & Architecture

- **Language:** Kotlin (JVM 11)
- **UI Framework:** Jetpack Compose + Material Design 3 (Edge-to-Edge)
- **Navigation:** Navigation Compose with direction-aware RTL/LTR transitions and preserved tab scroll state
- **Architecture:** MVVM (`PharmacyViewModel`, `StateFlow`, `UiState` sealed interfaces) with repository abstraction (`PharmacyDataSource`)
- **Backend & Auth:** Firebase Authentication, Jetpack Credential Manager (Google Sign-In), Cloud Firestore
- **Local Storage:** Jetpack DataStore Preferences (update repository settings and dismissed release tags)

---

## Building from Source

### Prerequisites
- Android Studio (Ladybug or newer) or JDK 17+ with Android SDK 36
- Gradle `9.3.1`
- A configured `app/google-services.json` for Firebase Auth and Firestore

### Optional Release Signing Setup
Copy `.env.example` to `.env` (excluded from Git) and configure your keystore credentials if building signed release binaries:

```properties
KEYSTORE_PATH=/path/to/my-upload-key.jks
STORE_PASSWORD=your_store_password
KEY_PASSWORD=your_key_password
```

### Build Commands

```bash
# Build Debug APK
gradle :app:assembleDebug

# Build Release APK (requires signing environment variables or keystore)
gradle :app:assembleRelease

# Run JVM unit tests
gradle :app:testDebugUnitTest
```

The generated debug APK is written to `app/build/outputs/apk/debug/app-debug.apk`.
