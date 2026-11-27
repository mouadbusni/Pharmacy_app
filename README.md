# Saydaliyati (صيدليتي)

**Saydaliyati** is a bilingual (Arabic RTL / English LTR) Android application built with Kotlin and Jetpack Compose for discovering pharmacies across Casablanca, browsing essential medications, consulting pharmacists, and placing delivery orders.

## Download

Latest Android `.apk` builds are available on **[GitHub Releases](https://github.com/mouadbusni/Pharmacy_app/releases/latest)**.

- **Minimum Android Version:** Android 7.0 (`minSdk 24`, `targetSdk 36`)
- **In-App Updates:** Checks `mouadbusni/Pharmacy_app` releases automatically and can be configured under **Settings -> App Updates & GitHub Repo**.

## Key Features

- **Casablanca Pharmacy Directory:** Browse 15 local pharmacies across Casablanca districts with `All`, `24h/24`, and `Day Duty (Garde)` filters, delivery ETAs, and one-tap calling.
- **Medication Catalog & Safety Info:** Explore medications priced in `MAD` with `OTC` / `Rx` badges, dosage schedules, pharmacist safety notes, category filters, and instant search.
- **Pharmacist Chat & Checkout:** Message pharmacies directly, manage favorites and cart quantities, check out with Visa, Mastercard, or Cash on Delivery, and track order history.
- **Bilingual & Adaptive UI:** Instant Arabic (RTL) and English (LTR) switching with responsive layouts for phones, foldables, and tablets.

## Tech Stack

- **UI:** Kotlin, Jetpack Compose, Material Design 3
- **Architecture:** MVVM (`StateFlow`, Navigation Compose)
- **Backend & Storage:** Firebase Authentication, Google Sign-In (Credential Manager), Cloud Firestore, Jetpack DataStore

## Building from Source

### Prerequisites

- Android Studio (Ladybug or newer) or JDK 17+ with Android SDK 36
- `app/google-services.json` configured for Firebase Auth and Firestore (optional for offline preview)

### Commands

```bash
# Build Debug APK
gradle :app:assembleDebug

# Build Release APK (uses KEYSTORE_PATH, STORE_PASSWORD, KEY_PASSWORD)
gradle :app:assembleRelease

# Run unit tests
gradle :app:testDebugUnitTest
```

If signing release builds locally, copy `.env.example` to `.env` and set `KEYSTORE_PATH`, `STORE_PASSWORD`, and `KEY_PASSWORD`.
