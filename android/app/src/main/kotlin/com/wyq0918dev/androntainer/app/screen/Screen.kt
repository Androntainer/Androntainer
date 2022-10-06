package com.wyq0918dev.androntainer.app.screen

import androidx.annotation.StringRes
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.twotone.*
import androidx.compose.ui.graphics.vector.ImageVector
import com.wyq0918dev.androntainer.R
import com.wyq0918dev.androntainer.ui.values.*

sealed class Screen(val route: String, val imageVector: ImageVector, @StringRes val resourceId: Int) {
    object Navigation : Screen(navigation, Icons.TwoTone.Navigation, R.string.menu_home)
    object Launcher : Screen(launcher, Icons.TwoTone.Launch, R.string.menu_apps)
    object Flutter : Screen(flutter, Icons.TwoTone.FlutterDash, R.string.menu_home)
    object Dashboard : Screen(dashboard, Icons.TwoTone.Dashboard, R.string.menu_apps)
    object Settings : Screen(settings, Icons.TwoTone.Settings, R.string.menu_apps)
}