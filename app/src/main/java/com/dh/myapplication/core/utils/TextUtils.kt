package com.dh.myapplication.core.utils

import android.content.Context
import android.net.Uri
import android.util.Log
import androidx.core.app.ShareCompat
import androidx.core.content.FileProvider
import java.io.File

class TextUtils {

    companion object {
        private const val TAG = "TextUtils"
    }


    fun createFileInCache(context: Context, fileName: String, content: String): File? {
        return try {
            val file = File(context.externalCacheDir, fileName)

            // Check if the file already exists
            if (file.exists()) {
                // Clear the contents of the existing file
                file.writeText("")
            }

            // Write the new content to the file
            file.writeText(content)

            Log.d(TAG, "File $fileName created successfully in cache")
            file
        } catch (e: Exception) {
            Log.e(TAG, "Error while creating file in cache", e)
            null
        }
    }

    fun shareVideo2(context: Context, filePath: String) {
        Log.d(TAG, "shareVideo2() called with: context = $context, filePath = $filePath")
        val pdfFile = File(filePath)

        // Get the content URI using FileProvider
        val contentUri: Uri = FileProvider.getUriForFile(
            context,
            "com.dh.myapplication.file",
            pdfFile
        )

        /*
                // Create an Intent to share the PDF
                val shareIntent = Intent(Intent.ACTION_SEND)
                shareIntent.type = "application/pdf"
                shareIntent.putExtra(Intent.EXTRA_STREAM, contentUri)
                shareIntent.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION)

                // Start the sharing activity
                context.startActivity(Intent.createChooser(shareIntent, "Share PDF"))
        */

        try {
            val uri = FileProvider.getUriForFile(context, "com.dh.myapplication.file", pdfFile)

            ShareCompat.IntentBuilder(context)
                .setType("application/pdf")
                .setSubject("Shared files")
                .addStream(uri)
                .setChooserTitle("Shared Videos")
                .startChooser()
        } catch (e: Exception) {
            Log.i(TAG, "shareVideo2: ${e.message}")

        }


    }

    fun shareVideo(videoFile: File, context: Context) {
        if (videoFile.exists()) {
            Log.i(TAG, "shareVideo: Video file exists and the length in bytes is: ${videoFile.length()}")

        } else {
            Log.i(TAG, "Video file does not exist. Exiting.")

            return
        }

        val uri = FileProvider.getUriForFile(context, "com.dh.myapplication.file", videoFile)

        ShareCompat.IntentBuilder(context)
            .setType("text/plain")
            .setSubject("Shared files")
            .addStream(uri)
            .setChooserTitle("Shared Videos")
            .startChooser()

    }


}




