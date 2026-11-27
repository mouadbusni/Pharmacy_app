package com.example

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.BackHandler
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.key
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalLayoutDirection
import androidx.compose.ui.unit.LayoutDirection
import com.example.ui.auth.LoginScreen
import com.example.ui.auth.SplashScreen
import com.example.ui.navigation.AuthenticatedPharmacyApp
import com.example.ui.theme.ArabicStrings
import com.example.ui.theme.LocalAppStrings
import com.example.ui.theme.SaydaliyatiTheme
import com.google.firebase.Firebase
import com.google.firebase.FirebaseApp
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.auth

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            SaydaliyatiTheme {
                AppContent()
            }
        }
    }
}

@Composable
fun AppContent(auth: FirebaseAuth? = null) {
    val context = LocalContext.current
    val firebaseAuth = remember(auth, context) {
        auth ?: if (FirebaseApp.getApps(context).isNotEmpty()) Firebase.auth else null
    }
    var currentUser by remember { mutableStateOf(firebaseAuth?.currentUser) }
    var showLoginFromSplash by remember { mutableStateOf(false) }
    var isAdminBypass by remember { mutableStateOf(firebaseAuth == null) }

    DisposableEffect(firebaseAuth) {
        if (firebaseAuth == null) {
            onDispose { }
        } else {
            val listener = FirebaseAuth.AuthStateListener { nextAuth ->
                currentUser = nextAuth.currentUser
            }
            firebaseAuth.addAuthStateListener(listener)
            onDispose { firebaseAuth.removeAuthStateListener(listener) }
        }
    }

    val user = currentUser
    if (user == null && !isAdminBypass) {
        CompositionLocalProvider(
            LocalLayoutDirection provides LayoutDirection.Rtl,
            LocalAppStrings provides ArabicStrings
        ) {
            if (!showLoginFromSplash) {
                SplashScreen(
                    onStartClicked = { showLoginFromSplash = true },
                    onAdminSkipSignIn = { isAdminBypass = true }
                )
            } else {
                BackHandler { showLoginFromSplash = false }
                LoginScreen(
                    onBack = { showLoginFromSplash = false },
                    onAuthSuccess = {
                        currentUser = firebaseAuth?.currentUser
                    },
                    onAdminSkipSignIn = { isAdminBypass = true }
                )
            }
        }
    } else if (user == null && isAdminBypass) {
        key("admin_session") {
            AuthenticatedPharmacyApp(
                currentUserId = "admin_session",
                isAdminMode = true,
                onSignedOut = {
                    isAdminBypass = false
                    showLoginFromSplash = true
                }
            )
        }
    } else {
        val uid = user!!.uid
        key(uid) {
            AuthenticatedPharmacyApp(
                currentUserId = uid,
                isAdminMode = false,
                onSignedOut = {
                    showLoginFromSplash = true
                    currentUser = null
                }
            )
        }
    }
}
