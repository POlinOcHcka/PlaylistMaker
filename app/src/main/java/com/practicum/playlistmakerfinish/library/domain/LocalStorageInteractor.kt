package com.practicum.playlistmakerfinish.library.domain

import android.net.Uri

interface LocalStorageInteractor {

    fun saveImageToLocalStorage(uri: Uri)
}