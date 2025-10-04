package com.shareconnect.transmissionconnect.theme

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.shareconnect.transmissionconnect.logging.Logger
import com.shareconnect.transmissionconnect.preferences.PreferencesRepository

class ThemeViewModelFactory(
    private val preferencesRepository: PreferencesRepository,
    private val logger: Logger
) : ViewModelProvider.Factory {

    @Suppress("UNCHECKED_CAST")
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(ThemeViewModel::class.java)) {
            return ThemeViewModel(preferencesRepository, logger) as T
        }
        throw IllegalArgumentException("Unknown ViewModel class")
    }
}