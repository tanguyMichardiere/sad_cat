package com.tanguymichardiere.sad_cat_project.main

import android.net.Uri
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.*
import androidx.compose.material.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.Upload
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import coil.compose.rememberImagePainter
import com.tanguymichardiere.sad_cat_project.BuildConfig
import com.tanguymichardiere.sad_cat_project.LocalViewModel
import com.tanguymichardiere.sad_cat_project.R
import com.tanguymichardiere.sad_cat_project.actions.CompressException
import com.tanguymichardiere.sad_cat_project.actions.DecodeException
import com.tanguymichardiere.sad_cat_project.actions.UploadException
import com.tanguymichardiere.sad_cat_project.actions.upload
import kotlinx.coroutines.launch

@Composable
fun SadCatFloatingActionButton(scaffoldState: ScaffoldState) {
    val context = LocalContext.current
    val coroutineScope = rememberCoroutineScope()

    val viewModel = LocalViewModel.current

    var toUpload by remember { mutableStateOf<Uri?>(null) }
    var snackbarMessage by remember { mutableStateOf<String?>(null) }
    val launcher = rememberLauncherForActivityResult(ActivityResultContracts.GetContent()) {
        it?.let { toUpload = it }
    }

    toUpload?.let {
        AlertDialog(onDismissRequest = { toUpload = null }, text = {
            Box(modifier = Modifier.fillMaxWidth()) {
                Image(
                    painter = rememberImagePainter(toUpload, builder = { crossfade(true) }),
                    contentDescription = "image to upload",
                    modifier = Modifier
                        .height(128.dp)
                        .align(Alignment.Center)
                )
            }
        }, buttons = {
            Row(
                modifier = Modifier
                    .padding(all = 8.dp)
                    .fillMaxWidth(),
                horizontalArrangement = Arrangement.End
            ) {
                IconButton(onClick = { toUpload = null }) {
                    Icon(Icons.Filled.Close, "cancel", tint = Color.Red)
                }
                IconButton(onClick = {
                    toUpload = null
                    snackbarMessage = context.getString(R.string.uploading)
                    coroutineScope.launch {
                        snackbarMessage = try {
                            upload(context, it)
                            context.getString(R.string.upload_successful)
                        } catch (exception: UploadException) {
                            when (exception) {
                                is CompressException -> context.getString(R.string.compress_error)
                                is DecodeException -> context.getString(R.string.decode_error)
                                else -> context.getString(R.string.upload_error)
                            }
                        }
                        if (BuildConfig.FLAVOR == "admin") {
                            viewModel.fetchPendingStorageReferences()
                        }
                    }
                }) {
                    Icon(Icons.Filled.Check, "confirm", tint = Color.Green)
                }
            }
        })
    }

    snackbarMessage?.let {
        LaunchedEffect(scaffoldState.snackbarHostState, snackbarMessage) {
            scaffoldState.snackbarHostState.showSnackbar(it)
        }
    }

    FloatingActionButton(onClick = { launcher.launch("image/jpeg") }) {
        Icon(Icons.Filled.Upload, "upload")
    }
}
