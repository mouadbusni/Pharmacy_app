# Saydaliyati (صيدليتي)

**Saydaliyati** is a bilingual (Arabic RTL / English LTR) Android application built with Kotlin and Jetpack Compose for discovering pharmacies across Casablanca, browsing essential medications, consulting pharmacists in real time, and placing rapid delivery orders.

---

## Download APK & Automated Releases

Latest Android `.apk` builds are published through **[GitHub Releases](https://github.com/mouadbusni/Pharmacy_app/releases/latest)**:

- **Releases Page:** [github.com/mouadbusni/Pharmacy_app/releases](https://github.com/mouadbusni/Pharmacy_app/releases)
- **Open Pull Requests:** [github.com/mouadbusni/Pharmacy_app/pulls](https://github.com/mouadbusni/Pharmacy_app/pulls)
- **Version:** `1.0` (`versionCode 1`)
- **Compatibility:** Android 7.0+ (`minSdk 24`, `targetSdk 36`)

### First-Time Repository & APK Setup

If `main` or **Releases** does not show the latest APK yet:

1. **Merge Open Pull Requests into `main`:** Open [Pull Requests](https://github.com/mouadbusni/Pharmacy_app/pulls) and merge any pending PR so all updated app files land on `main`.
2. **Add the GitHub Actions Release Workflow:** Create [`.github/workflows/release-apk.yml`](https://github.com/mouadbusni/Pharmacy_app/new/main?filename=.github/workflows/release-apk.yml) directly in GitHub's web editor and paste:

```yaml
name: Build & Publish APK
on:
  push:
    branches: [ main ]
  workflow_dispatch:

permissions:
  contents: write

jobs:
  build-apk:
    runs-on: ubuntu-latest
    steps:
      - uses: actions/checkout@v4
      - uses: actions/setup-java@v4
        with:
          distribution: temurin
          java-version: 17
      - uses: gradle/actions/setup-gradle@v4
        with:
          gradle-version: 9.3.1
      - name: Build APK
        env:
          SIGNING_KEYSTORE_BASE64: ${{ secrets.SIGNING_KEYSTORE_BASE64 }}
          STORE_PASSWORD: ${{ secrets.STORE_PASSWORD }}
          KEY_PASSWORD: ${{ secrets.KEY_PASSWORD }}
        run: |
          if [ -n "$SIGNING_KEYSTORE_BASE64" ]; then
            echo "$SIGNING_KEYSTORE_BASE64" | base64 -d > my-upload-key.jks
            export KEYSTORE_PATH="${{ github.workspace }}/my-upload-key.jks"
            gradle :app:assembleRelease
            cp app/build/outputs/apk/release/app-release.apk Saydaliyati-v1.0.${{ github.run_number }}.apk
          else
            gradle :app:assembleDebug
            cp app/build/outputs/apk/debug/app-debug.apk Saydaliyati-v1.0.${{ github.run_number }}.apk
          fi
      - name: Publish GitHub Release
        uses: softprops/action-gh-release@v2
        with:
          tag_name: v1.0.${{ github.run_number }}
          name: Saydaliyati v1.0.${{ github.run_number }}
          body: Automated APK build from commit ${{ github.sha }}
          files: Saydaliyati-v1.0.${{ github.run_number }}.apk
```

3. **Run & Install on Phone:** Every commit on `main` (or manual run from the **Actions** tab) now builds and attaches an `.apk` under **Releases**, and the app's home screen prompts you to update in one tap.

> **Signing Tip:** Add `SIGNING_KEYSTORE_BASE64`, `STORE_PASSWORD`, and `KEY_PASSWORD` in GitHub Repository Secrets if updating over an existing release-signed demo without uninstalling first.

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

Saydaliyati includes a built-in GitHub Releases update checker configured for `mouadbusni/Pharmacy_app`:

1. Open **Settings** -> **App Updates & GitHub Repo**.
2. Confirm your repository (`mouadbusni/Pharmacy_app`) and tap **Save & Check**.
3. If no release exists yet, tap **Copy & Create Workflow on GitHub** to paste `.github/workflows/release-apk.yml` in one step.
4. Once a tagged GitHub Release with an `.apk` asset is published, the home screen displays an **Update Available** banner with a 1-tap **Update Now** download button.

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
