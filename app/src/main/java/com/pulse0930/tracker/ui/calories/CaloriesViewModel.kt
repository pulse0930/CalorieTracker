package com.pulse0930.tracker.ui.calories

import android.app.Application
import android.content.res.Resources
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import com.pulse0930.tracker.R
import java.text.Format
import java.text.SimpleDateFormat
import java.util.*

class CaloriesViewModel(application: Application) : AndroidViewModel(application) {
    var resources: Resources = getApplication<Application>().resources
    private val _text = MutableLiveData<String>().apply {
        value = resources.getString(R.string.info_calorie_burnt_today)
    }
    val text: LiveData<String> = _text
    private val _textDate = MutableLiveData<String>().apply {
        val calendar = Calendar.getInstance(TimeZone.getTimeZone("GMT+5:30"))
        val now = Date()
        calendar.time = now
        val dateFormat: Format = SimpleDateFormat("EEE, dd MMM yyyy")
        value = dateFormat.format(calendar.timeInMillis).toString()
    }
    val textDate: LiveData<String> = _textDate
}