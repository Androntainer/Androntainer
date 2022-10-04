package com.wyq0918dev.androntainer.core.app

import com.wyq0918dev.androntainer.core.initDialogX
import com.wyq0918dev.androntainer.core.initDynamicColors
import com.wyq0918dev.androntainer.core.initLibTaskbar
import io.flutter.app.FlutterApplication

class App : FlutterApplication() {

    override fun onCreate() {
        super.onCreate()
        initDynamicColors(this@App)
        initLibTaskbar(this@App)
        initDialogX(this@App)
    }
}