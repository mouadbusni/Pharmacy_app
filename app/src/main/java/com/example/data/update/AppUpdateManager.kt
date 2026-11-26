package com.example.data.update

import android.content.Context
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.stringPreferencesKey
import androidx.datastore.preferences.preferencesDataStore
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.withContext
import org.json.JSONObject
import java.net.HttpURLConnection
import java.net.URL

private val Context.updateDataStore by preferencesDataStore(name = "saydaliyati_update_prefs")

data class AppUpdateInfo(
    val tagName: String,
    val versionName: String,
    val versionCode: Int?,
    val releaseNotes: String,
    val apkDownloadUrl: String,
    val htmlReleaseUrl: String
)

sealed interface AppUpdateState {
    data object Idle : AppUpdateState
    data object Checking : AppUpdateState
    data class UpToDate(val currentVersion: String) : AppUpdateState
    data class UpdateAvailable(val info: AppUpdateInfo, val isDismissed: Boolean) : AppUpdateState
    data class Error(val message: String) : AppUpdateState
}

class UpdatePreferencesStore(private val context: Context) {

    private val repoSlugKey = stringPreferencesKey("github_repo_slug")
    private val dismissedTagKey = stringPreferencesKey("dismissed_release_tag")

    val repoSlugFlow: Flow<String> = context.updateDataStore.data.map { prefs ->
        prefs[repoSlugKey] ?: DEFAULT_REPO_SLUG
    }

    val dismissedTagFlow: Flow<String> = context.updateDataStore.data.map { prefs ->
        prefs[dismissedTagKey] ?: ""
    }

    suspend fun saveRepoSlug(rawInput: String) {
        val normalized = normalizeRepoSlug(rawInput)
        context.updateDataStore.edit { prefs ->
            prefs[repoSlugKey] = normalized
        }
    }

    suspend fun dismissReleaseTag(tagName: String) {
        context.updateDataStore.edit { prefs ->
            prefs[dismissedTagKey] = tagName
        }
    }

    companion object {
        const val DEFAULT_REPO_SLUG = "mouadbusni/Pharmacy_app"
        private val REPO_SLUG_REGEX = Regex("^[A-Za-z0-9_.-]+/[A-Za-z0-9_.-]+$")

        fun normalizeRepoSlug(rawInput: String): String {
            return rawInput.trim()
                .removePrefix("https://github.com/")
                .removePrefix("http://github.com/")
                .removePrefix("github.com/")
                .removeSuffix(".git")
                .trim('/')
        }

        fun isValidRepoSlug(rawInput: String): Boolean {
            val normalized = normalizeRepoSlug(rawInput)
            return normalized.isNotEmpty() && REPO_SLUG_REGEX.matches(normalized)
        }
    }
}

object GitHubReleaseChecker {

    suspend fun fetchLatestRelease(
        repoSlug: String,
        installedVersionName: String,
        installedVersionCode: Int
    ): Result<Pair<AppUpdateInfo, Boolean>> = withContext(Dispatchers.IO) {
        runCatching {
            val normalized = UpdatePreferencesStore.normalizeRepoSlug(repoSlug)
            require(UpdatePreferencesStore.isValidRepoSlug(normalized)) {
                "Invalid repository format. Use owner/repo"
            }

            val endpoint = "https://api.github.com/repos/$normalized/releases/latest"
            val conn = (URL(endpoint).openConnection() as HttpURLConnection).apply {
                requestMethod = "GET"
                connectTimeout = 8000
                readTimeout = 8000
                setRequestProperty("Accept", "application/vnd.github+json")
                setRequestProperty("User-Agent", "Saydaliyati-Android-Updater")
            }

            try {
                val status = conn.responseCode
                if (status == HttpURLConnection.HTTP_NOT_FOUND) {
                    throw IllegalStateException("No published GitHub Release found yet for $normalized")
                }
                if (status !in 200..299) {
                    throw IllegalStateException("GitHub API returned HTTP $status")
                }

                val payload = conn.inputStream.bufferedReader().use { it.readText() }
                val json = JSONObject(payload)
                val tagName = json.optString("tag_name").trim()
                val releaseTitle = json.optString("name").trim().ifBlank { tagName }
                val releaseBody = json.optString("body").trim()
                val htmlUrl = json.optString("html_url").trim()

                var apkUrl = ""
                val assets = json.optJSONArray("assets")
                if (assets != null) {
                    for (i in 0 until assets.length()) {
                        val asset = assets.optJSONObject(i) ?: continue
                        val assetName = asset.optString("name").lowercase()
                        if (assetName.endsWith(".apk")) {
                            apkUrl = asset.optString("browser_download_url").trim()
                            break
                        }
                    }
                }

                val resolvedDownload = apkUrl.ifBlank { htmlUrl }
                if (tagName.isBlank() || resolvedDownload.isBlank()) {
                    throw IllegalStateException("Release metadata is missing a tag or download link")
                }

                val parsedCode = extractBuildCode(tagName)
                val info = AppUpdateInfo(
                    tagName = tagName,
                    versionName = releaseTitle,
                    versionCode = parsedCode,
                    releaseNotes = releaseBody,
                    apkDownloadUrl = resolvedDownload,
                    htmlReleaseUrl = htmlUrl
                )
                val isNewer = isRemoteNewer(
                    remoteTag = tagName,
                    installedVersionName = installedVersionName,
                    installedVersionCode = installedVersionCode
                )
                info to isNewer
            } finally {
                conn.disconnect()
            }
        }
    }

    internal fun extractBuildCode(tagName: String): Int? {
        val plusMatch = Regex("\\+(\\d+)$").find(tagName.trim())
        if (plusMatch != null) return plusMatch.groupValues[1].toIntOrNull()
        val buildMatch = Regex("build[-_.]?(\\d+)", RegexOption.IGNORE_CASE).find(tagName)
        if (buildMatch != null) return buildMatch.groupValues[1].toIntOrNull()
        return null
    }

    internal fun isRemoteNewer(
        remoteTag: String,
        installedVersionName: String,
        installedVersionCode: Int
    ): Boolean {
        val remoteCode = extractBuildCode(remoteTag)
        if (remoteCode != null && remoteCode > installedVersionCode) {
            return true
        }
        val remoteParts = parseVersionParts(remoteTag.substringBefore('+'))
        val localParts = parseVersionParts(installedVersionName.substringBefore('+'))
        if (remoteParts.isEmpty()) return false
        val maxLen = maxOf(remoteParts.size, localParts.size)
        for (i in 0 until maxLen) {
            val r = remoteParts.getOrElse(i) { 0 }
            val l = localParts.getOrElse(i) { 0 }
            if (r > l) return true
            if (r < l) return false
        }
        return false
    }

    private fun parseVersionParts(raw: String): List<Int> {
        val cleaned = raw.trim().trimStart('v', 'V')
        return Regex("\\d+").findAll(cleaned).mapNotNull { it.value.toIntOrNull() }.toList()
    }

    const val GITHUB_WORKFLOW_TEMPLATE = """name: Build & Publish APK
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
          SIGNING_KEYSTORE_BASE64: ${'$'}{{ secrets.SIGNING_KEYSTORE_BASE64 }}
          STORE_PASSWORD: ${'$'}{{ secrets.STORE_PASSWORD }}
          KEY_PASSWORD: ${'$'}{{ secrets.KEY_PASSWORD }}
        run: |
          if [ -n "${'$'}SIGNING_KEYSTORE_BASE64" ]; then
            echo "${'$'}SIGNING_KEYSTORE_BASE64" | base64 -d > my-upload-key.jks
            export KEYSTORE_PATH="${'$'}{{ github.workspace }}/my-upload-key.jks"
            gradle :app:assembleRelease
            cp app/build/outputs/apk/release/app-release.apk Saydaliyati-v1.0.${'$'}{{ github.run_number }}.apk
          else
            gradle :app:assembleDebug
            cp app/build/outputs/apk/debug/app-debug.apk Saydaliyati-v1.0.${'$'}{{ github.run_number }}.apk
          fi
      - name: Publish GitHub Release
        uses: softprops/action-gh-release@v2
        with:
          tag_name: v1.0.${'$'}{{ github.run_number }}
          name: Saydaliyati v1.0.${'$'}{{ github.run_number }}
          body: Automated APK build from commit ${'$'}{{ github.sha }}
          files: Saydaliyati-v1.0.${'$'}{{ github.run_number }}.apk
"""
}
