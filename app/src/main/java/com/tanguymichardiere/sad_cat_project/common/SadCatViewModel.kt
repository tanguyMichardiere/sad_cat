package com.tanguymichardiere.sad_cat_project.common

import android.content.Context
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import com.google.firebase.FirebaseApp
import com.google.firebase.analytics.FirebaseAnalytics
import com.google.firebase.analytics.ktx.analytics
import com.google.firebase.appcheck.FirebaseAppCheck
import com.google.firebase.appcheck.debug.DebugAppCheckProviderFactory
import com.google.firebase.appcheck.safetynet.SafetyNetAppCheckProviderFactory
import com.google.firebase.auth.ktx.auth
import com.google.firebase.crashlytics.FirebaseCrashlytics
import com.google.firebase.ktx.Firebase
import com.google.firebase.storage.StorageReference
import com.google.firebase.storage.ktx.storage
import com.tanguymichardiere.sad_cat_project.BuildConfig
import kotlinx.coroutines.*
import kotlinx.coroutines.tasks.await
import java.io.File
import kotlin.io.path.Path
import kotlin.io.path.createDirectories

class SadCatViewModel : ViewModel() {
    var firebaseAnalytics by mutableStateOf<FirebaseAnalytics?>(null)
    var storageReferences by mutableStateOf<List<StorageReference>?>(null)
    var pendingStorageReferences by mutableStateOf<List<StorageReference>?>(null)

    @DelicateCoroutinesApi
    fun init(context: Context) = GlobalScope.launch {
        while (true) {
            Path(File(context.filesDir, "images").path).createDirectories()
            try {
                FirebaseApp.initializeApp(context)
                if (BuildConfig.DEBUG) {
                    FirebaseCrashlytics.getInstance().setCrashlyticsCollectionEnabled(false)
                } else {
                    firebaseAnalytics = Firebase.analytics
                }
                FirebaseAppCheck.getInstance().installAppCheckProviderFactory(
                    if (BuildConfig.DEBUG) {
                        DebugAppCheckProviderFactory.getInstance()
                    } else {
                        SafetyNetAppCheckProviderFactory.getInstance()
                    }
                )
                if (BuildConfig.FLAVOR == "admin") {
                    Firebase.auth.signInWithEmailAndPassword(
                        BuildConfig.EMAIL,
                        BuildConfig.PASSWORD
                    ).await()
                } else {
                    Firebase.auth.signInAnonymously().await()
                }
                fetchStorageReferences()
                if (BuildConfig.FLAVOR == "admin") {
                    fetchPendingStorageReferences()
                }
                return@launch
            } catch (exception: Exception) {
            }
        }
    }

    suspend fun fetchStorageReferences() = coroutineScope {
        while (true) {
            try {
                storageReferences =
                    Firebase.storage.getReference("/approved").listAll().await().items
                return@coroutineScope
            } catch (exception: Exception) {
            }
        }
    }

    suspend fun fetchPendingStorageReferences() = coroutineScope {
        if (BuildConfig.FLAVOR == "admin") {
            while (true) {
                try {
                    pendingStorageReferences =
                        Firebase.storage.getReference("/pending").listAll().await().items
                    return@coroutineScope
                } catch (exception: CancellationException) {
                    throw exception
                } catch (exception: Exception) {
                    exception.printStackTrace()
                }
            }
        }
    }
}
