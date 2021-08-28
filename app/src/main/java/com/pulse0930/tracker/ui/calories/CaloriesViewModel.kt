package com.pulse0930.tracker.ui.calories

import android.app.Application
import android.content.res.Resources
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import com.pulse0930.tracker.R
class CaloriesViewModel (application: Application): AndroidViewModel(application)  {
    var resources: Resources = getApplication<Application>().resources
    private val _text = MutableLiveData<String>().apply {
        value = resources.getString(R.string.info_calorie_burnt_today)
    }
    val text: LiveData<String> = _text
}