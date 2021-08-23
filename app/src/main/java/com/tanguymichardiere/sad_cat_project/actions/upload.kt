package com.tanguymichardiere.sad_cat_project.actions

import android.content.Context
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.net.Uri
import com.google.firebase.ktx.Firebase
import com.google.firebase.storage.ktx.storage
import com.tanguymichardiere.sad_cat_project.BuildConfig
import kotlinx.coroutines.tasks.await
import java.io.File

open class UploadException : Exception()
class CompressException : UploadException()
class DecodeException : UploadException()

suspend fun upload(context: Context, uri: Uri) {
    try {
        val bitmap =
            BitmapFactory.decodeStream(
                context.contentResolver.openInputStream(uri)
            )
        if (bitmap != null) {
            val fileName = "${System.currentTimeMillis()}000.jpg"
            val file = File(File(context.filesDir, "images"), fileName)
            if (bitmap.compress(Bitmap.CompressFormat.JPEG, 100, file.outputStream())) {
                Firebase.storage.getReference("pending/${fileName}").putFile(
                    Uri.fromFile(
                        file
                    )
                ).await()
            } else {
                throw CompressException()
            }
        } else {
            throw DecodeException()
        }
    } catch (exception: Exception) {
        throw UploadException()
    }
}

suspend fun uploadApproved(uri: Uri) {
    if (BuildConfig.FLAVOR == "admin") {
        try {
            Firebase.storage.getReference("approved/${System.currentTimeMillis()}000.jpg")
                .putFile(uri)
                .await()
        } catch (exception: Exception) {
            throw UploadException()
        }
    }
}
