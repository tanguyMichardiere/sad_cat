package com.tanguymichardiere.sad_cat_project.admin

import androidx.compose.animation.*
import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.layout.BoxWithConstraints
import androidx.compose.material.ExperimentalMaterialApi
import androidx.compose.material.MaterialTheme
import androidx.compose.runtime.*
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalDensity
import com.google.accompanist.systemuicontroller.rememberSystemUiController
import com.google.firebase.storage.StorageReference

@ExperimentalMaterialApi
@ExperimentalAnimationApi
@ExperimentalFoundationApi
@Composable
fun SadCatAdmin(close: () -> Unit) {
    var previewStorageReference by remember { mutableStateOf<StorageReference?>(null) }
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

        AdminScaffold(
            close = close,
            preview = { previewStorageReference = it })

        AnimatedVisibility(
            visible = previewStorageReference != null,
            enter = slideInVertically(
                initialOffsetY = { it }
            ) + fadeIn(),
            exit = ExitTransition.None
        ) {
            previewStorageReference?.let {
                AdminPreview(
                    storageReference = it,
                    close = { previewStorageReference = null },
                    screenHeight = screenHeightPx,
                    screenWidth = screenWidthPx
                )
            }
        }
    }
}
