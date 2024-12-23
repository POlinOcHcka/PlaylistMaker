package com.practicum.playlistmakerfinish.library.domain

import android.net.Uri

class LocalStorageInteractorImpl(private val localStorageRepository: LocalStorageRepository) :
    LocalStorageInteractor {

    override fun saveImageToLocalStorage(uri: Uri): Uri {
        return localStorageRepository.saveImageToLocalStorage(uri)
    }
}