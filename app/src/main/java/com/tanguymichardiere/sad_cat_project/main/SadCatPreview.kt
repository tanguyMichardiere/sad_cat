package com.tanguymichardiere.sad_cat_project.main

import android.content.Intent
import android.net.Uri
import androidx.activity.compose.BackHandler
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.gestures.Orientation
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.material.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.Share
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.IntOffset
import androidx.compose.ui.unit.dp
import coil.compose.rememberImagePainter
import com.google.firebase.storage.StorageReference
import com.tanguymichardiere.sad_cat_project.BuildConfig
import com.tanguymichardiere.sad_cat_project.LocalViewModel
import com.tanguymichardiere.sad_cat_project.actions.getShareIntent
import com.tanguymichardiere.sad_cat_project.actions.getUri
import kotlinx.coroutines.launch
import kotlinx.coroutines.tasks.await
import kotlin.math.roundToInt

@ExperimentalMaterialApi
@Composable
fun SadCatPreview(
    storageReference: StorageReference,
    close: () -> Unit,
    screenHeight: Float,
    screenWidth: Float
) {
    val context = LocalContext.current
    val coroutineScope = rememberCoroutineScope()

    val viewModel = LocalViewModel.current

    var uri by remember { mutableStateOf<Uri?>(null) }
    var aspectRatio by remember { mutableStateOf<Float?>(null) }
    LaunchedEffect(storageReference) {
        getUri(context, storageReference).let {
            uri = it.first
            aspectRatio = it.second
        }
    }

    val swipeableState = rememberSwipeableState(0)
    val maxOffset = ((screenWidth / (aspectRatio ?: 1F)) + screenHeight) / 2
    val anchors = mapOf(-maxOffset to -1, 0f to 0, maxOffset to 1)

    BackHandler {
        coroutineScope.launch {
            swipeableState.animateTo(1)
        }
    }

    LaunchedEffect(swipeableState.currentValue) {
        if (swipeableState.currentValue != 0) {
            close()
        }
    }

    Box(
        modifier = Modifier
            .swipeable(
                state = swipeableState,
                anchors = anchors,
                thresholds = { _, _ -> FractionalThreshold(0.5F) },
                orientation = Orientation.Vertical
            )
            .alpha(
                if (swipeableState.progress.to == 0) {
                    1F
                } else {
                    1F - swipeableState.progress.fraction
                }
            )
            .background(Color.Black)
    ) {
        var shareIntent by remember { mutableStateOf<Intent?>(null) }
        LaunchedEffect(uri) {
            uri?.let { shareIntent = getShareIntent(context, it) }
        }

        Image(
            painter = rememberImagePainter(uri,
                builder = { crossfade(true) }),
            contentDescription = null,
            modifier = Modifier
                .fillMaxSize()
                .offset {
                    IntOffset(
                        0,
                        swipeableState.offset.value.roundToInt()
                    )
                }
        )

        IconButton(onClick = {
            coroutineScope.launch {
                swipeableState.animateTo(1)
            }
        }, modifier = Modifier.padding(8.dp)) {
            Icon(
                Icons.Filled.ArrowBack,
                "back",
                tint = Color.White
            )
        }

        if (BuildConfig.FLAVOR == "admin") {
            IconButton(
                onClick = {
                    coroutineScope.launch {
                        storageReference.delete().await()
                        viewModel.fetchStorageReferences()
                        swipeableState.animateTo(1)
                    }
                },
                modifier = Modifier
                    .align(Alignment.TopEnd)
                    .padding(8.dp)
            ) {
                Icon(Icons.Filled.Delete, "delete", tint = Color.Red)
            }
        }

        IconButton(
            onClick = { shareIntent?.let { context.startActivity(it) } },
            modifier = Modifier
                .align(Alignment.BottomCenter)
                .padding(8.dp)
        ) {
            Icon(Icons.Filled.Share, "share", tint = Color.White)
        }
    }
}
