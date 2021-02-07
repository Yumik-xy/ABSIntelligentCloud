package com.absintelligentcloud.android.ui.main

import androidx.lifecycle.ViewModel
import com.absintelligentcloud.android.logic.Repository

class MainViewModel : ViewModel() {

    fun getSavedToken() = Repository.getSavedToken()

    fun isTokenSaved() = Repository.isTokenSaved()

}