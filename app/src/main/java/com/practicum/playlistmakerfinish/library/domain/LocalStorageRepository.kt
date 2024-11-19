package com.practicum.playlistmakerfinish.library.domain

import android.net.Uri

interface LocalStorageRepository {

    fun saveImageToLocalStorage(uri: Uri)
}