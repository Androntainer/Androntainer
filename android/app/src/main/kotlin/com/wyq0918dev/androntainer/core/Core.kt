package com.wyq0918dev.androntainer.core

import android.app.Application
import android.content.Context
import android.graphics.Color
import android.os.Build
import androidx.compose.runtime.Composable
import com.google.android.material.color.DynamicColors
import com.kongzue.dialogx.DialogX
import com.kongzue.dialogx.style.IOSStyle
import com.wyq0918dev.androntainer.ui.layout.Greeting

@Composable
fun Compose(){
    Greeting(name = "sb")
}

fun initDynamicColors(context: Context){
    DynamicColors.applyToActivitiesIfAvailable(context as Application)
}

fun initLibTaskbar(context: Context){
    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q){

    }
}

fun initDialogX(context: Context){
    DialogX.init(context)
    DialogX.globalStyle = IOSStyle()
    DialogX.globalTheme = DialogX.THEME.AUTO
    DialogX.autoShowInputKeyboard = true
    DialogX.onlyOnePopTip = true
    DialogX.cancelable = true
    DialogX.cancelableTipDialog = false
    DialogX.cancelButtonText = "关闭"
    DialogX.bottomDialogNavbarColor = Color.TRANSPARENT
    DialogX.autoRunOnUIThread = false
    DialogX.useHaptic = true
}