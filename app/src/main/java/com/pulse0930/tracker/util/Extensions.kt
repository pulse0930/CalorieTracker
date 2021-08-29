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
import java.util.concurrent.TimeUnit

fun DataPoint.getStartTimeString(): String = DateFormat.getTimeInstance()
    .format(this.getStartTime(TimeUnit.MILLISECONDS))

fun DataPoint.getEndTimeString(): String = DateFormat.getTimeInstance()
    .format(this.getEndTime(TimeUnit.MILLISECONDS))

fun getVersion(packageManager: PackageManager, context: Context):String{
    val pInfo: PackageInfo = packageManager.getPackageInfo(context.getPackageName(), 0)
    return pInfo.versionName
}

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
