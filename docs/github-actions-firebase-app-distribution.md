# GitHub Actions + Firebase App Distribution

This repository's CI pipeline is in `.github/workflows/firebase-app-distribution.yml`.

## What happens

- A push to `main` starts the Android build.
- GitHub Actions uses Gradle 9.3.1 and JDK 17, runs JVM unit tests, and builds a signed APK.
- The build's `versionCode` increments automatically using the GitHub Actions run number. The first CI APK is versionCode 2, above the original repository value of 1.
- The APK is saved as a GitHub Actions artifact and distributed to the selected Firebase App Distribution testers/groups.
- The app includes the Firebase App Distribution Android SDK. When a tester opens or resumes the app, it checks for a new release and displays Firebase's update flow.

Firebase App Distribution does **not** silently install APKs. Android requires the user to approve installation. Testers receive release notifications, and the in-app update prompt can be enabled by signing into App Distribution in the app.

## Critical: keep the signing certificate already installed on the phone

Android only allows an in-place update when the new APK is signed with the same signing certificate as the installed app (and has a higher versionCode).

This project already has two signing configurations:

- `debug`: `debug.keystore`, alias `androiddebugkey`, password `android`.
- `release`: `my-upload-key.jks`, alias `upload`, with `STORE_PASSWORD` and `KEY_PASSWORD`.

The workflow supports both. Set `ANDROID_BUILD_TYPE` to the signing type that matches the APK currently installed on the phone. Do not create a new key to replace the original one. If the existing key cannot be recovered, an in-place update is not possible; the app would need to be uninstalled and installed again.

For a USB-installed Android Studio/AI Studio debug APK, the matching key is commonly the project's existing `debug.keystore`—but verify its certificate before choosing. If the USB tool installed a release APK, use the release keystore instead.

## GitHub Actions secrets

In GitHub, open **Settings → Secrets and variables → Actions → Secrets** and add:

| Secret | Value |
|---|---|
| `GOOGLE_SERVICES_JSON_BASE64` | Base64-encoded `app/google-services.json` for package `com.aistudio.arabicpharmacy.qrxmzk`. This file is not currently tracked in the repository. |
| `FIREBASE_SERVICE_ACCOUNT_JSON` | Full JSON key for a Google service account with the Firebase App Distribution Admin role on the same Firebase project. |
| `ANDROID_DEBUG_KEYSTORE_BASE64` | Base64 of the exact `debug.keystore` used to sign the installed phone app; needed only for debug mode. |
| `ANDROID_RELEASE_KEYSTORE_BASE64` | Base64 of the existing `my-upload-key.jks`; needed only for release mode. |
| `STORE_PASSWORD` | Existing release keystore password; needed only for release mode. |
| `KEY_PASSWORD` | Existing release key password; needed only for release mode. |

Do not paste keystores, passwords, or service-account JSON into chat, issues, or source files.

### Base64-encode a file on Windows PowerShell

Run locally, replacing the path with the existing key/config file. Copy the output into the matching GitHub secret:

```powershell
[Convert]::ToBase64String([IO.File]::ReadAllBytes("C:\path\to\debug.keystore")) | Set-Clipboard
```

For the Firebase config, use `google-services.json` in place of the keystore path. For a release key, use the path to `my-upload-key.jks`.

## GitHub Actions variables

Under **Settings → Secrets and variables → Actions → Variables**, add:

| Variable | Value |
|---|---|
| `ANDROID_BUILD_TYPE` | Required: `debug` or `release`, matching the certificate used by the currently installed app. The workflow deliberately fails rather than guessing. |
| `FIREBASE_TESTERS` | Comma-separated tester email addresses, including the Google account used on the phone; or leave blank and use groups. |
| `FIREBASE_GROUPS` | Optional comma-separated Firebase App Distribution group aliases. Create the group in Firebase App Distribution first. |

At least one of `FIREBASE_TESTERS` or `FIREBASE_GROUPS` must be set.

## Firebase setup

1. Open the Firebase project and register the Android app with the exact package name `com.aistudio.arabicpharmacy.qrxmzk`.
2. Download its `google-services.json` and add the base64 content as `GOOGLE_SERVICES_JSON_BASE64`.
3. Enable Firebase App Distribution and the Firebase App Tester API for the project.
4. Create a service account, grant it **Firebase App Distribution Admin**, create a JSON key, and store the full JSON as `FIREBASE_SERVICE_ACCOUNT_JSON`.
5. Add your Google account as a tester (or join the configured group) and accept the invitation on the phone.

For in-app update prompts, open the installed app after accepting the invitation, sign in with the invited Google account when prompted, and enable tester alerts. Subsequent releases can then be surfaced by the app's update check.

## Run manually

Open **Actions → Android CI and Firebase App Distribution → Run workflow**. Choose `debug` or `release` if you need to override the repository variable. The selected mode must use the same signing certificate as the installed APK.

## Notes

- The workflow runs automatically for pushes to `main`. Feature branches should be merged into `main` to distribute them to the phone.
- Build numbers are based on the workflow run number, not Git commit count.
- The workflow intentionally fails early when Firebase config, a matching signing key, authentication, or a tester/group target is missing.
- Firebase App Distribution releases expire after 150 days; distribute subsequent builds to keep testing current.
