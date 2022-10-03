package com.wyq0918dev.androntainer.core.app

import android.app.Application
import android.os.Build
import com.google.android.material.color.DynamicColors
import com.kongzue.dialogx.DialogX
import com.wyq0918dev.androntainer.core.initDialogX
import com.wyq0918dev.androntainer.core.initDynamicColors
import com.wyq0918dev.androntainer.core.initLibTaskbar

class App : Application() {

    override fun onCreate() {
        super.onCreate()
        initDynamicColors(this@App)
        initLibTaskbar(this@App)
        initDialogX(this@App)
    }
}