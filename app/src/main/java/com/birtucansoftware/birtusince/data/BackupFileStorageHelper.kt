package com.birtucansoftware.birtusince.data

import android.content.Context
import android.net.Uri
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import java.io.BufferedReader
import java.io.InputStreamReader
import java.io.OutputStreamWriter

/**
 * Handles reading and writing backup files from/to content URIs.
 * Decouples stream I/O from UI Activity.
 */
object BackupFileStorageHelper {

    suspend fun writeBackupToUri(context: Context, uri: Uri, json: String): Boolean {
        return withContext(Dispatchers.IO) {
            try {
                context.contentResolver.openOutputStream(uri)?.use { os ->
                    OutputStreamWriter(os, Charsets.UTF_8).use { writer ->
                        writer.write(json)
                        writer.flush()
                    }
                } != null
            } catch (e: Throwable) {
                false
            }
        }
    }

    suspend fun readBackupFromUri(context: Context, uri: Uri): String? {
        return withContext(Dispatchers.IO) {
            try {
                context.contentResolver.openInputStream(uri)?.use { inputStream ->
                    BufferedReader(InputStreamReader(inputStream, Charsets.UTF_8)).use { reader ->
                        reader.readText()
                    }
                }
            } catch (e: Throwable) {
                null
            }
        }
    }
}
