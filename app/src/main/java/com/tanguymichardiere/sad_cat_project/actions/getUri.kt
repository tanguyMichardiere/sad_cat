package com.tanguymichardiere.sad_cat_project.actions

import android.content.Context
import android.graphics.BitmapFactory
import android.net.Uri
import android.util.Log
import androidx.core.content.FileProvider
import com.google.firebase.storage.StorageReference
import kotlinx.coroutines.tasks.await
import java.io.File

suspend fun getUri(
    context: Context,
    storageReference: StorageReference
): Pair<Uri, Float> {
    while (true) {
        try {
            val file = File(File(context.filesDir, "images"), storageReference.name)
            val options = BitmapFactory.Options()
            options.inJustDecodeBounds = true
            if (file.createNewFile()) {
                storageReference.getFile(file).await()
                BitmapFactory.decodeFile(file.absolutePath, options)
                return Pair(
                    FileProvider.getUriForFile(
                        context,
                        context.applicationContext.packageName + ".provider",
                        file
                    ), options.outWidth.toFloat() / options.outHeight.toFloat()
                )
            } else {
                BitmapFactory.decodeFile(file.absolutePath, options)
                return Pair(
                    FileProvider.getUriForFile(
                        context,
                        context.applicationContext.packageName + ".provider",
                        file
                    ), options.outWidth.toFloat() / options.outHeight.toFloat()
                )
            }
        } catch (exception: Exception) {
            Log.e("fetchOne", exception.toString())
        }
    }
}
