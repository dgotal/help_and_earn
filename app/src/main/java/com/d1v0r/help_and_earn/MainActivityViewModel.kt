package com.d1v0r.help_and_earn

import androidx.lifecycle.ViewModel
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject

@HiltViewModel
class MainActivityViewModel @Inject constructor(preferencesManager: PreferencesManager) :
    ViewModel() {
    val isAuthenticated = preferencesManager.getUser() != null
    val isParent = preferencesManager.getIsParent()
}