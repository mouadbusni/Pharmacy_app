package com.example.data.repository

import com.example.data.firebase.OperationType
import com.example.data.firebase.handleFirestoreError
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestoreException
import kotlinx.coroutines.CancellationException
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.catch

internal fun requireSignedInUserId(auth: FirebaseAuth): String {
    val uid = auth.currentUser?.uid
    check(!uid.isNullOrBlank()) { "User must be signed in with Google before accessing Firestore." }
    return uid
}

internal fun <T> Flow<T>.handleFirestoreStreamErrors(
    auth: FirebaseAuth,
    uid: String,
    operationType: OperationType,
    path: String
): Flow<T> = catch { error ->
    val firestoreError = generateSequence(error) { it.cause }
        .filterIsInstance<FirebaseFirestoreException>()
        .firstOrNull()
    if (firestoreError == null && error is CancellationException) throw error
    val exceptionToLog = firestoreError ?: (error as? Exception)
    if (exceptionToLog != null && auth.currentUser?.uid == uid) {
        handleFirestoreError(exceptionToLog, operationType, path)
    }
    throw error
}
