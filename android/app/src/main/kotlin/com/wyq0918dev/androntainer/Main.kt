/**
 * Androntainer Project
 * Copyright (c) 2022 wyq0918dev.
 */
package com.wyq0918dev.androntainer

import android.app.Activity
import android.app.Application
import android.content.Context
import android.content.Intent
import android.content.res.Resources.Theme
import android.os.Build
import android.os.Bundle
import android.view.View
import android.view.ViewAnimationUtils
import android.view.ViewGroup
import android.view.ViewGroup.*
import android.view.WindowManager
import android.widget.FrameLayout
import android.widget.ImageView
import android.widget.LinearLayout
import android.widget.Toast
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.AppCompatImageView
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Home
import androidx.compose.material3.*
import androidx.compose.runtime.*
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
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.core.view.ViewCompat
import androidx.core.view.WindowCompat
import androidx.core.view.marginTop
import androidx.fragment.app.FragmentContainerView
import androidx.fragment.app.FragmentManager
import com.google.android.material.appbar.AppBarLayout
import com.google.android.material.appbar.MaterialToolbar
import io.flutter.embedding.android.FlutterActivity
import io.flutter.embedding.android.FlutterEngineConfigurator
import io.flutter.embedding.android.FlutterFragment
import io.flutter.embedding.android.TransparencyMode
import io.flutter.embedding.engine.FlutterEngine
import io.flutter.plugin.common.MethodChannel
import kotlin.math.hypot


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

class MainActivity : AppCompatActivity(), Runnable, FlutterEngineConfigurator {

    // 上下文
    private lateinit var thisContext: Context

    private var dynamicColor: Boolean by mutableStateOf(true)

    // 控件ID
    private val mainId: Int = R.id.main_view
    private val composeId: Int = R.id.compose_view
    private val flutterId: Int = R.id.flutter_view
    private val logoId: Int = R.id.logo_view

    // Flutter相关
    private val tagFlutterFragment = "flutter_fragment"
    private var flutterFragment: FlutterFragment? = null


    /**
     * --Activity生命周期函数--
     * --此Activity启动时执行--
     * App入口
     */
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        init()
        postAnim()
    }

    override fun run() {
        val logo = findViewById<AppCompatImageView>(logoId)
        val main = findViewById<ConstraintLayout>(mainId)
        val cx = logo.x + logo.width / 2f
        val cy = logo.y + logo.height / 2f
        val startRadius = hypot(logo.width.toFloat(), logo.height.toFloat())
        val endRadius = hypot(main.width.toFloat(), main.height.toFloat())
        val circularAnim = ViewAnimationUtils
            .createCircularReveal(main, cx.toInt(), cy.toInt(), startRadius, endRadius)
            .setDuration(800)
        logo.animate()
            .alpha(0f)
            .scaleX(1.3f)
            .scaleY(1.3f)
            .setDuration(600)
            .withEndAction {
                logo.visibility = View.GONE
            }
            .withStartAction {
                main.visibility = View.VISIBLE
                circularAnim.start()
            }
            .start()
    }

    private fun postAnim() {
        findViewById<ConstraintLayout>(mainId).visibility = INVISIBLE
        findViewById<ConstraintLayout>(mainId).postDelayed(this, 200)
    }

    /**
     * --应用加载--
     * 主加载函数
     */

    private fun init() {
        initContext()
        initSystemBar()
        initUi()
    }

    /**
     * --应用加载--
     * 加载程序上下文
     */

    private fun initContext() {
        thisContext = this@MainActivity
    }

    @Suppress("DEPRECATION")
    private fun initSystemBar() {
        WindowCompat.setDecorFitsSystemWindows(window, false)
        window.setFlags(
            WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS,
            WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS)
        window.setFlags(
            WindowManager.LayoutParams.FLAG_TRANSLUCENT_NAVIGATION,
            WindowManager.LayoutParams.FLAG_TRANSLUCENT_NAVIGATION)
        val option = View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN or View.SYSTEM_UI_FLAG_LAYOUT_STABLE
        val decorView = window.decorView
        val visibility: Int = decorView.systemUiVisibility
        decorView.systemUiVisibility = visibility or option
    }

    /**
     * --应用加载--
     * 加载UI界面
     */

    private fun initUi() {
        setContentView(
            ConstraintLayout(
                thisContext
            ).apply {
                addView(
                    ConstraintLayout(
                        thisContext
                    ).apply {
                        id = mainId
                        addView(
                            FragmentContainerView(
                                context = thisContext
                            ).apply {
                                id = flutterId
                                visibility = VISIBLE
                                val fragmentManager: FragmentManager = supportFragmentManager
                                flutterFragment =
                                    fragmentManager.findFragmentByTag(tagFlutterFragment) as FlutterFragment?
                                if (flutterFragment == null) {
                                    flutterFragment = FlutterFragment
                                        .withNewEngine()
                                        .transparencyMode(TransparencyMode.transparent)
                                        .shouldAttachEngineToActivity(true)
                                        .build()
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
                        addView(
                            ComposeView(
                                context = thisContext
                            ).apply {
                                clipToPadding = true
                                fitsSystemWindows = true
                                visibility = VISIBLE
                                id = composeId
                                setContent {
                                    Layout()
                                }
                            },
                            LayoutParams(
                                LayoutParams.MATCH_PARENT,
                                LayoutParams.MATCH_PARENT
                            )
                        )
                    }
                )
                addView(
                    AppCompatImageView(
                        thisContext
                    ).apply {
                        id = logoId
                        setImageDrawable(getDrawable(R.drawable.ic_baseline_androntainer_plat_logo_24))
                        scaleType = ImageView.ScaleType.CENTER
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
        AndrontainerTheme(dynamicColor = dynamicColor) {
            Greeting(name = "sb")
        }
    }

    override fun configureFlutterEngine(flutterEngine: FlutterEngine) {
        val messenger = flutterEngine.dartExecutor.binaryMessenger
        val channel = MethodChannel(messenger, "compose_visibility")
        val compose = findViewById<ComposeView>(composeId)
        channel.setMethodCallHandler { call, res ->
            when (call.method) {
                "compose_visibility" -> {
                    if (compose.visibility == GONE) {
                        compose.visibility = VISIBLE
                    } else {
                        compose.visibility = GONE
                    }
                    res.success("这是执行的结果")
                }
                else -> {
                    res.error("error_code", "error_message", null)
                }
            }
        }
    }

    override fun cleanUpFlutterEngine(flutterEngine: FlutterEngine) {

    }

    override fun onPostResume() {
        super.onPostResume()
        flutterFragment?.onPostResume()
    }

    override fun onNewIntent(intent: Intent?) {
        super.onNewIntent(intent)
        if (intent != null) {
            flutterFragment?.onNewIntent(intent)
        }
    }

    @Deprecated("Deprecated in Java")
    override fun onBackPressed() {
        flutterFragment?.onBackPressed()
    }

    @Suppress("DEPRECATION")
    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<out String>,
        grantResults: IntArray
    ) {
        flutterFragment?.onRequestPermissionsResult(
            requestCode,
            permissions,
            grantResults
        )
    }

    override fun onUserLeaveHint() {
        flutterFragment?.onUserLeaveHint()
    }

    override fun onTrimMemory(level: Int) {
        super.onTrimMemory(level)
        flutterFragment?.onTrimMemory(level)
    }

    /**
     * --Activity生命周期函数--
     * --此Activity销毁时执行--
     * 将FlutterFragment内容清空
     */

    override fun onDestroy() {
        super.onDestroy()
        flutterFragment = null
        findViewById<ConstraintLayout>(mainId).removeCallbacks(this)
    }
}

class FlutterTest : FlutterActivity()

class ComposeTest : ComponentActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            AndrontainerTheme {
                Greeting(name = "sb")
            }
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


