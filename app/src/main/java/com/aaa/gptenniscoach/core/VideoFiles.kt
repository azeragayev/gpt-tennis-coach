package com.aaa.gptenniscoach.core

import android.content.ContentResolver
import android.content.Context
import android.net.Uri
import java.io.File
import java.util.UUID

object VideoFiles {
    /**
     * Copies video file from content URI to app-internal storage directory.
     */
    fun copyToAppStorage(context: Context, uri: Uri): File {
        val resolver: ContentResolver = context.contentResolver
        val input = resolver.openInputStream(uri) ?: error("Cannot open input stream for $uri")
        val dir = File(context.filesDir, "videos").apply { mkdirs() }
        val outFile = File(dir, "video_${UUID.randomUUID()}.mp4")
        input.use { ins ->
            outFile.outputStream().use { outs ->
                ins.copyTo(outs)
            }
        }
        return outFile
    }
}

