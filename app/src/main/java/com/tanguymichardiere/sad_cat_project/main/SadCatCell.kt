package com.tanguymichardiere.sad_cat_project.main

import android.content.Intent
import android.net.Uri
import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.Image
import androidx.compose.foundation.combinedClickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.material.CircularProgressIndicator
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import coil.compose.rememberImagePainter
import com.google.firebase.storage.StorageReference
import com.tanguymichardiere.sad_cat_project.actions.getShareIntent
import com.tanguymichardiere.sad_cat_project.actions.getUri

@ExperimentalFoundationApi
@Composable
fun SadCatCell(storageReference: StorageReference, preview: () -> Unit) {
    val context = LocalContext.current

    var uri by remember { mutableStateOf<Uri?>(null) }
    var aspectRatio by remember { mutableStateOf<Float?>(null) }
    LaunchedEffect(storageReference) {
        getUri(context, storageReference).let {
            uri = it.first
            aspectRatio = it.second
        }
    }

    var shareIntent by remember { mutableStateOf<Intent?>(null) }
    LaunchedEffect(uri) {
        uri?.let { shareIntent = getShareIntent(context, it) }
    }

    if (uri != null) {
        Image(
            painter = rememberImagePainter(uri, builder = { crossfade(true) }),
            contentDescription = null,
            modifier = Modifier
                .aspectRatio(aspectRatio ?: 1F)
                .combinedClickable(onLongClick = {
                    shareIntent?.let { context.startActivity(it) }
                }) {
                    preview()
                }
        )
    } else {
        Box(modifier = Modifier.aspectRatio(1F)) {
            CircularProgressIndicator(modifier = Modifier.align(Alignment.Center))
        }
    }
}
