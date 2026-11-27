package com.example.data.update

import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertNull
import org.junit.Assert.assertTrue
import org.junit.Test

class GitHubReleaseCheckerTest {

    @Test
    fun normalizeRepoSlug_stripsSchemesSuffixesAndWhitespace() {
        assertEquals(
            "mouadbusni/Pharmacy_app",
            UpdatePreferencesStore.normalizeRepoSlug("  https://github.com/mouadbusni/Pharmacy_app.git/  ")
        )
        assertEquals(
            "octocat/Hello-World",
            UpdatePreferencesStore.normalizeRepoSlug("http://github.com/octocat/Hello-World")
        )
        assertEquals(
            "my_org/repo.v2",
            UpdatePreferencesStore.normalizeRepoSlug("github.com/my_org/repo.v2/")
        )
    }

    @Test
    fun isValidRepoSlug_validatesOwnerRepoFormat() {
        assertTrue(UpdatePreferencesStore.isValidRepoSlug("mouadbusni/Pharmacy_app"))
        assertTrue(UpdatePreferencesStore.isValidRepoSlug("https://github.com/owner-1/app.repo_2"))
        assertFalse(UpdatePreferencesStore.isValidRepoSlug(""))
        assertFalse(UpdatePreferencesStore.isValidRepoSlug("onlyowner"))
        assertFalse(UpdatePreferencesStore.isValidRepoSlug("a/b/c"))
        assertFalse(UpdatePreferencesStore.isValidRepoSlug("owner/invalid space"))
    }

    @Test
    fun extractBuildCode_parsesPlusAndBuildTags() {
        assertEquals(42, GitHubReleaseChecker.extractBuildCode("v1.0.5+42"))
        assertEquals(15, GitHubReleaseChecker.extractBuildCode("v1.2-build15"))
        assertEquals(9, GitHubReleaseChecker.extractBuildCode("release_build.9"))
        assertNull(GitHubReleaseChecker.extractBuildCode("v1.0.3"))
    }

    @Test
    fun isRemoteNewer_comparesBuildCodeAndSemanticVersion() {
        assertTrue(
            GitHubReleaseChecker.isRemoteNewer(
                remoteTag = "v1.0.0+2",
                installedVersionName = "1.0.0",
                installedVersionCode = 1
            )
        )
        assertTrue(
            GitHubReleaseChecker.isRemoteNewer(
                remoteTag = "v1.1.0",
                installedVersionName = "1.0.5",
                installedVersionCode = 1
            )
        )
        assertFalse(
            GitHubReleaseChecker.isRemoteNewer(
                remoteTag = "v1.0.0",
                installedVersionName = "1.0",
                installedVersionCode = 1
            )
        )
        assertFalse(
            GitHubReleaseChecker.isRemoteNewer(
                remoteTag = "v0.9.9",
                installedVersionName = "1.0.0",
                installedVersionCode = 1
            )
        )
    }

    @Test
    fun urlBuilders_fallbackToDefaultAndNormalizeCustomSlug() {
        assertEquals(
            "https://github.com/mouadbusni/Pharmacy_app/releases",
            GitHubReleaseChecker.releasesUrl("")
        )
        assertEquals(
            "https://github.com/octocat/demo/new/main?filename=.github/workflows/release-apk.yml",
            GitHubReleaseChecker.newWorkflowUrl("https://github.com/octocat/demo.git")
        )
        assertEquals(
            "https://github.com/octocat/demo/pulls",
            GitHubReleaseChecker.pullRequestsUrl("octocat/demo")
        )
    }
}
