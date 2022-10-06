package com.wyq0918dev.androntainer.app

import android.app.Application
import android.content.Context
import android.os.Build
import com.google.android.material.color.DynamicColors

fun initDynamicColors(context: Context){
    DynamicColors.applyToActivitiesIfAvailable(context as Application)
}

fun initLibTaskbar(context: Context){
    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q){

    }
}

