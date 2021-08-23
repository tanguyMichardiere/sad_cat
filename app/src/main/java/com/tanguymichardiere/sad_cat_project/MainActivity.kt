package com.tanguymichardiere.sad_cat_project

import android.content.Context
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.viewModels
import androidx.compose.animation.*
import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.layout.BoxWithConstraints
import androidx.compose.material.ExperimentalMaterialApi
import androidx.compose.material.MaterialTheme
import androidx.compose.runtime.*
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalDensity
import androidx.datastore.preferences.core.booleanPreferencesKey
import androidx.datastore.preferences.preferencesDataStore
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.google.accompanist.systemuicontroller.rememberSystemUiController
import com.google.firebase.storage.StorageReference
import com.tanguymichardiere.sad_cat_project.admin.SadCatAdmin
import com.tanguymichardiere.sad_cat_project.common.SadCatViewModel
import com.tanguymichardiere.sad_cat_project.main.SadCatPreview
import com.tanguymichardiere.sad_cat_project.main.SadCatScaffold
import com.tanguymichardiere.sad_cat_project.settings.SadCatSettings
import com.tanguymichardiere.sad_cat_project.ui.theme.SadCatTheme
import kotlinx.coroutines.DelicateCoroutinesApi

val Context.dataStore by preferencesDataStore(name = "settings")
val DARK_THEME = booleanPreferencesKey("dark_theme")
val REVERSE_ORDER = booleanPreferencesKey("reverse_order")

val LocalViewModel = compositionLocalOf<SadCatViewModel> { error("No viewModel found") }

class MainActivity : ComponentActivity() {
    private val viewModel by viewModels<SadCatViewModel>()

    @DelicateCoroutinesApi
    @ExperimentalMaterialApi
    @ExperimentalAnimationApi
    @ExperimentalFoundationApi
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        viewModel.init(applicationContext)
//        if (BuildConfig.DEBUG) {
//            StrictMode.setThreadPolicy(
//                ThreadPolicy.Builder()
//                    .detectAll()
//                    .penaltyLog()
//                    .build()
//            )
//            StrictMode.setVmPolicy(
//                VmPolicy.Builder()
//                    .detectAll()
//                    .penaltyLog()
//                    .build()
//            )
//        }
        setContent {
            val navController = rememberNavController()
            SadCatTheme {
                CompositionLocalProvider(LocalViewModel provides viewModel) {
                    NavHost(navController = navController, startDestination = "home") {
                        composable("home") {
                            var previewStorageReference by remember {
                                mutableStateOf<StorageReference?>(
                                    null
                                )
                            }
                            val systemUiController = rememberSystemUiController()
                            val colors = MaterialTheme.colors

                            LaunchedEffect(colors.isLight, previewStorageReference) {
                                if (!colors.isLight or (previewStorageReference != null)) {
                                    systemUiController.setStatusBarColor(Color.Black)
                                } else {
                                    systemUiController.setStatusBarColor(colors.primaryVariant)
                                }
                            }

                            BoxWithConstraints {
                                val localDensity = LocalDensity.current
                                val screenHeight = this.maxHeight
                                val screenWidth = this.maxWidth
                                val screenHeightPx = with(localDensity) { screenHeight.toPx() }
                                val screenWidthPx = with(localDensity) { screenWidth.toPx() }

                                SadCatScaffold(
                                    navController = navController,
                                    preview = {
                                        viewModel.firebaseAnalytics?.logEvent(
                                            "preview",
                                            Bundle().apply {
                                                this.putString("uri", it.toString())
                                            })
                                        previewStorageReference = it
                                    })

                                AnimatedVisibility(
                                    visible = previewStorageReference != null,
                                    enter = slideInVertically(
                                        initialOffsetY = { it }
                                    ) + fadeIn(),
                                    exit = ExitTransition.None
                                ) {
                                    previewStorageReference?.let {
                                        SadCatPreview(
                                            storageReference = it,
                                            close = { previewStorageReference = null },
                                            screenHeight = screenHeightPx,
                                            screenWidth = screenWidthPx
                                        )
                                    }
                                }
                            }
                        }

                        if (BuildConfig.FLAVOR == "admin") {
                            composable("admin") {
                                SadCatAdmin(close = { navController.popBackStack() })
                            }
                        }

                        composable("settings") {
                            SadCatSettings(close = { navController.popBackStack() })
                        }
                    }
                }
            }
        }
    }
}
