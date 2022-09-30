/**
 * Androntainer Project
 * Copyright (c) 2022 wyq0918dev.
 */
package com.wyq0918dev.androntainer

import android.app.Activity
import android.app.Application
import android.content.Context
import android.graphics.Canvas
import android.os.Build
import android.os.Bundle
import android.util.AttributeSet
import android.view.MotionEvent
import android.view.ViewGroup
import android.view.ViewGroup.*
import android.widget.LinearLayout
import android.widget.SeekBar
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.appcompat.app.AppCompatActivity
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Home
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.SideEffect
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.toArgb
import androidx.compose.ui.platform.ComposeView
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalView
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.sp
import androidx.core.view.ViewCompat
import androidx.core.view.WindowCompat
import androidx.fragment.app.FragmentContainerView
import androidx.fragment.app.FragmentManager
import io.flutter.embedding.android.FlutterActivity
import io.flutter.embedding.android.FlutterFragment


/**
 * Application Global state class
 */

class App : Application() {

    /**
     * Application lifecycle function
     * Executed when the application is started
     * Initialize applications, third-party libraries, dynamic colors
     */

    override fun onCreate() {
        super.onCreate()

    }
}

/**
 * MainActivity
 */

class MainActivity : AppCompatActivity() {

    // 上下文
    private lateinit var thisContext: Context

    // 控件ID
    private val composeId: Int = R.id.compose_view
    private val flutterId: Int = R.id.flutter_view

    // Flutter相关
    private val tagFlutterFragment = "flutter_fragment"
    private var flutterFragment: FlutterFragment? = null

    /**
     * --Activity生命周期函数--
     * --此Activity启动时执行--
     * App入口
     */
    override fun onCreate(savedInstanceState: Bundle?) {
        WindowCompat.setDecorFitsSystemWindows(window, false)
        super.onCreate(savedInstanceState)
        init()
    }

    /**
     * --应用加载--
     * 主加载函数
     */

    private fun init() {
        initContext()
        initUi()
    }

    /**
     * --应用加载--
     * 加载程序上下文
     */

    private fun initContext() {
        thisContext = this@MainActivity
    }

    /**
     * --应用加载--
     * 加载UI界面
     */

    private fun initUi() {
        setContentView(
            LinearLayout(
                thisContext
            ).apply {
                orientation = LinearLayout.HORIZONTAL
                addView(
                    ComposeView(
                        context = thisContext
                    ).apply {
                        visibility = GONE
                        id = composeId
                        setContent {
                            Layout()
                        }
                    },
                    LayoutParams(
                        200,
                        LayoutParams.MATCH_PARENT
                    )
                )
                addView(
                    FragmentContainerView(
                        context = thisContext
                    ).apply {
                        id = flutterId
                        val fragmentManager: FragmentManager = supportFragmentManager
                        flutterFragment =
                            fragmentManager.findFragmentByTag(tagFlutterFragment) as FlutterFragment?
                        if (flutterFragment == null) {
                            flutterFragment = FlutterFragment.createDefault()
                            fragmentManager
                                .beginTransaction()
                                .add(
                                    flutterId,
                                    flutterFragment!!,
                                    tagFlutterFragment
                                )
                                .commit()
                        }
                    },
                    LayoutParams(
                        LayoutParams.MATCH_PARENT,
                        LayoutParams.MATCH_PARENT
                    )
                )
            }
        )
    }

    /**
     * --Jetpack Composes--
     * UI布局的引用
     */

    @Composable
    private fun Layout() {
        AndrontainerTheme(darkTheme = false, dynamicColor = true) {
            Greeting(name = "sb")
//            Column {
//                Text(text = "fuck!")
//                Text(text = "fuck!")
//                Text(text = "fuck!")
//
//
//            }

        }
    }

    /**
     * --Activity生命周期函数--
     * --此Activity销毁时执行--
     * 将FlutterFragment内容清空
     */

    override fun onDestroy() {
        super.onDestroy()
        flutterFragment = null
    }
}

class FlutterTest : FlutterActivity()

class ComposeTest : ComponentActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {

        }
    }
}

val Typography = Typography(
    bodyLarge = TextStyle(
        fontFamily = FontFamily.Default,
        fontWeight = FontWeight.Normal,
        fontSize = 16.sp,
        lineHeight = 24.sp,
        letterSpacing = 0.5.sp
    )
)

val Purple80 = Color(0xFFD0BCFF)
val PurpleGrey80 = Color(0xFFCCC2DC)
val Pink80 = Color(0xFFEFB8C8)
val Purple40 = Color(0xFF6650a4)
val PurpleGrey40 = Color(0xFF625b71)
val Pink40 = Color(0xFF7D5260)

private val DarkColorScheme = darkColorScheme(
    primary = Purple80,
    secondary = PurpleGrey80,
    tertiary = Pink80
)

private val LightColorScheme = lightColorScheme(
    primary = Purple40,
    secondary = PurpleGrey40,
    tertiary = Pink40
)

@Composable
fun AndrontainerTheme(
    darkTheme: Boolean = isSystemInDarkTheme(),
    dynamicColor: Boolean = true,
    content: @Composable () -> Unit
) {
    val colorScheme = when {
        dynamicColor && Build.VERSION.SDK_INT >= Build.VERSION_CODES.S -> {
            val context = LocalContext.current
            if (darkTheme) dynamicDarkColorScheme(context) else dynamicLightColorScheme(context)
        }
        darkTheme -> DarkColorScheme
        else -> LightColorScheme
    }
    val view = LocalView.current
    if (!view.isInEditMode) {
        SideEffect {
            (view.context as Activity).window.statusBarColor = colorScheme.primary.toArgb()
            ViewCompat.getWindowInsetsController(view)?.isAppearanceLightStatusBars = darkTheme
        }
    }

    MaterialTheme(
        colorScheme = colorScheme,
        typography = Typography,
        content = content
    )
}


@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun Greeting(name: String) {
    Scaffold(
        modifier = Modifier.fillMaxWidth(),
        topBar = {
            TopAppBar(
                title = {
                    Text(text = "sb")
                },
                modifier = Modifier.fillMaxWidth(),
                navigationIcon = {
                    IconButton(onClick = { /*TODO*/ }) {
                        Icon(imageVector = Icons.Filled.Home, contentDescription = null)
                    }
                },
                // scrollBehavior =
            )
        }
    ) { paddingValues ->
        Surface(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues),
            color = MaterialTheme.colorScheme.background
        ) {
            Text(text = "Hello $name!")
            // WidgetFlutter()
        }
    }
}

@Preview(showBackground = true)
@Composable
fun DefaultPreview() {
    AndrontainerTheme {
        Greeting("Android")
    }
}


