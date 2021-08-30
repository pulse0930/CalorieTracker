package com.pulse0930.tracker.util

import android.content.Context
import android.content.pm.PackageInfo
import android.content.pm.PackageManager
import android.graphics.drawable.Drawable
import android.util.Log
import androidx.appcompat.app.AlertDialog
import com.google.android.gms.fitness.data.DataPoint
import com.google.android.gms.fitness.data.DataSet
import com.google.android.gms.fitness.result.DataReadResponse
import com.pulse0930.tracker.R
import com.pulse0930.tracker.ui.calories.TAG
import java.text.DateFormat
import java.util.*
import java.util.concurrent.TimeUnit

fun DataPoint.getStartTimeString(): String = DateFormat.getTimeInstance()
    .format(this.getStartTime(TimeUnit.MILLISECONDS))

fun DataPoint.getEndTimeString(): String = DateFormat.getTimeInstance()
    .format(this.getEndTime(TimeUnit.MILLISECONDS))

fun getVersion(packageManager: PackageManager, context: Context):String{
    val pInfo: PackageInfo = packageManager.getPackageInfo(context.getPackageName(), 0)
    return pInfo.versionName
}
fun getCalendarNow(): Calendar {
    val now = Date()
    val calendar = Calendar.getInstance(TimeZone.getTimeZone("GMT+5:30"))
    calendar.time = now
    return calendar
}
fun getCalendar(hour:Int,minute:Int,second:Int): Calendar {
    val calendar = Calendar.getInstance(TimeZone.getTimeZone("GMT+5:30"))
    calendar.set(Calendar.HOUR_OF_DAY, hour)
    calendar.set(Calendar.MINUTE, minute)
    calendar.set(Calendar.SECOND, second)
    return calendar
}
fun IsBetweenTimeRange(calendar1:Calendar,calendar2:Calendar,calendar3:Calendar): Boolean {
    if(calendar3.time.after(calendar1.time) && calendar3.time.before(calendar2.time)){
        return true
    }
    return false
}
//    val calendar1 = getCalendar(t1[0].toInt(),t1[1].toInt(),t1[2].toInt())
//    Calendar.getInstance(TimeZone.getTimeZone("GMT+5:30"))
//    calendar1.set(Calendar.HOUR_OF_DAY, 8)
//    calendar1.set(Calendar.MINUTE, 59)
//    calendar1.set(Calendar.SECOND, 59)
//    val calendar2 = Calendar.getInstance(TimeZone.getTimeZone("GMT+5:30"))
//    calendar2.set(Calendar.HOUR_OF_DAY, 23)
//    calendar2.set(Calendar.MINUTE, 59)
//    calendar2.set(Calendar.SECOND, 59)
//    val now = Date()
//    val calendar3 = Calendar.getInstance(TimeZone.getTimeZone("GMT+5:30"))
//    calendar3.time = now
fun printData(dataReadResult: DataReadResponse) {
    if (dataReadResult.buckets.isNotEmpty()) {
        Log.i(TAG, "Number of returned buckets of DataSets is: " + dataReadResult.buckets.size)
        for (bucket in dataReadResult.buckets) {
            bucket.dataSets.forEach { dumpDataSet(it) }
        }
    } else if (dataReadResult.dataSets.isNotEmpty()) {
        Log.i(TAG, "Number of returned DataSets is: " + dataReadResult.dataSets.size)
        dataReadResult.dataSets.forEach { dumpDataSet(it) }
    }
}

fun dumpDataSet(dataSet: DataSet) {
    Log.i(TAG, "Data returned for Data type: ${dataSet.dataType.name}")
    for (dp in dataSet.dataPoints) {
        Log.i(TAG, "Data point:")
        Log.i(TAG, "\tType: ${dp.dataType.name}")
        Log.i(TAG, "\tStart: ${dp.getStartTimeString()}")
        Log.i(TAG, "\tEnd: ${dp.getEndTimeString()}")
        dp.dataType.fields.forEach {
            Log.i(TAG, "\tField: ${it.name} Value: ${dp.getValue(it)}")
        }
    }
}
