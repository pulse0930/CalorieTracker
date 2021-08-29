package com.pulse0930.tracker.ui.profile

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel

class ProfileViewModel : ViewModel() {
    val text: LiveData<String> = MutableLiveData<String>()
}