package com.pulse0930.tracker.util

import android.content.Context
import android.content.pm.PackageInfo
import android.content.pm.PackageManager
import com.google.android.gms.fitness.data.DataPoint
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


