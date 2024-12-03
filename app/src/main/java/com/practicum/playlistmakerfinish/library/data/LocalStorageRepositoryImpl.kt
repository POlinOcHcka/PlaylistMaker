package com.practicum.playlistmakerfinish.library.data

import android.content.Context
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.net.Uri
import android.os.Environment
import androidx.core.net.toUri
import com.practicum.playlistmakerfinish.library.domain.LocalStorageRepository
import java.io.File
import java.io.FileOutputStream
import java.util.UUID

class LocalStorageRepositoryImpl(private val context: Context) : LocalStorageRepository {

    companion object {
        const val FILE_NAME = "playlist_cover.jpg"
        const val DIRECTORY_NAME = "playlists"
    }

    override fun saveImageToLocalStorage(uri: Uri): Uri {

        val filePath =
            File(context.getExternalFilesDir(Environment.DIRECTORY_PICTURES), DIRECTORY_NAME)
        if (!filePath.exists()) {
            filePath.mkdirs()
        }
        val file = File(filePath, UUID.randomUUID().toString())
        val inputStream = context.contentResolver.openInputStream(uri)
        val outputStream = FileOutputStream(file)
        BitmapFactory
            .decodeStream(inputStream)
            .compress(Bitmap.CompressFormat.JPEG, 30, outputStream)
        return file.toUri()
    }
}