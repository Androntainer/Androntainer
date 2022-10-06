package com.wyq0918dev.androntainer

import android.content.ComponentName
import android.content.Context
import android.content.Intent
import android.content.ServiceConnection
import android.graphics.Color
import android.os.Bundle
import android.os.IBinder
import android.view.View
import android.view.View.*
import android.view.ViewAnimationUtils
import android.view.ViewGroup.LayoutParams
import android.widget.ImageView
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.content.res.AppCompatResources
import androidx.appcompat.widget.AppCompatImageView
import androidx.appcompat.widget.LinearLayoutCompat
import androidx.appcompat.widget.LinearLayoutCompat.HORIZONTAL
import androidx.appcompat.widget.LinearLayoutCompat.VERTICAL
import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.twotone.Science
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.ComposeView
import androidx.compose.ui.unit.dp
import androidx.compose.ui.viewinterop.AndroidView
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.core.view.WindowCompat
import androidx.fragment.app.FragmentContainerView
import androidx.fragment.app.FragmentManager
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.rememberNavController
import com.google.android.material.composethemeadapter.MdcTheme
import com.wyq0918dev.androntainer.app.initDynamicColors
import com.wyq0918dev.androntainer.app.initLibTaskbar
import com.wyq0918dev.androntainer.app.screen.Screen
import com.wyq0918dev.androntainer.service.InAppBillingService
import com.wyq0918dev.androntainer.ui.values.flutterId
import io.flutter.app.FlutterApplication
import io.flutter.embedding.android.FlutterActivity
import io.flutter.embedding.android.FlutterEngineConfigurator
import io.flutter.embedding.android.FlutterFragment
import io.flutter.embedding.engine.FlutterEngine
import io.flutter.plugin.common.MethodChannel
import io.flutter.plugins.GeneratedPluginRegistrant
import kotlin.math.hypot


/**
 * Application
 */

class Application : FlutterApplication() {

    override fun onCreate() {
        super.onCreate()
        initDynamicColors(this@Application)
        initLibTaskbar(this@Application)
    }
}

/**
 * Activity
 */

class MainActivity : AppCompatActivity(), FlutterEngineConfigurator, Runnable {

    private lateinit var thisContext: Context
    private lateinit var flutter: FragmentContainerView
    private lateinit var settings: FragmentContainerView
    private lateinit var compose: ComposeView
    private lateinit var main: LinearLayoutCompat
    private lateinit var logo: AppCompatImageView
    private var flutterFragment: FlutterFragment? = null
    private val tagFlutterFragment = "flutter_fragment"

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        init()
    }

    override fun run() {
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
                logo.visibility = GONE
            }
            .withStartAction {
                main.visibility = VISIBLE
                circularAnim.start()
            }
            .start()
    }

    override fun configureFlutterEngine(flutterEngine: FlutterEngine) {

        GeneratedPluginRegistrant.registerWith(flutterEngine)




        val android = "android"
        val origin = "origin"

        val messenger = flutterEngine.dartExecutor.binaryMessenger
        val channel = MethodChannel(messenger, android)

        channel.setMethodCallHandler { call, res ->
            when (call.method) {
                origin -> {
                    flutter.visibility = INVISIBLE
                    res.success("success")
                }
                else -> {
                    res.error("error", "error_message", null)
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
    @Suppress("DEPRECATION")
    override fun onBackPressed() {
        super.onBackPressed()
        flutterFragment?.onBackPressed()
    }

    @Suppress("DEPRECATION")
    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<out String>,
        grantResults: IntArray
    ) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        flutterFragment?.onRequestPermissionsResult(
            requestCode,
            permissions,
            grantResults
        )
    }

    override fun onUserLeaveHint() {
        super.onUserLeaveHint()
        flutterFragment?.onUserLeaveHint()
    }

    override fun onTrimMemory(level: Int) {
        super.onTrimMemory(level)
        flutterFragment?.onTrimMemory(level)
    }

    override fun onDestroy() {
        super.onDestroy()
        flutterFragment = null
        main.removeCallbacks(this)
    }


    private fun init() {
        initContext()
        initServices()
        initSystemBar()
        initUi()
        postAnim()
    }


    private fun initContext() {
        thisContext = this@MainActivity
    }

    private fun initServices() {
        val intent = Intent(this@MainActivity, InAppBillingService::class.java)
        startService(intent)
        bindService(intent, mConnection, Context.BIND_AUTO_CREATE)
    }

    private val mConnection: ServiceConnection = object : ServiceConnection {
        override fun onServiceConnected(
            className: ComponentName,
            service: IBinder
        ) {

        }

        override fun onServiceDisconnected(arg0: ComponentName) {

        }
    }

    @Suppress("DEPRECATION")
    private fun initSystemBar() {
        WindowCompat.setDecorFitsSystemWindows(window, false)
        val option = SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN or SYSTEM_UI_FLAG_LAYOUT_STABLE
        val decorView = window.decorView
        val visibility: Int = decorView.systemUiVisibility
        decorView.systemUiVisibility = visibility or option
        window.navigationBarColor = Color.TRANSPARENT
    }

    private fun initUi() {
        setView()
        setLayout()
    }

    private fun setView() {

        flutter = FragmentContainerView(
            thisContext
        ).apply {
            id = flutterId
            val fragmentManager: FragmentManager = supportFragmentManager
            flutterFragment =
                fragmentManager.findFragmentByTag(tagFlutterFragment) as FlutterFragment?
            if (flutterFragment == null) {
                flutterFragment = FlutterFragment
                    .withNewEngine()
                    .shouldAttachEngineToActivity(true)
                    .build()
                fragmentManager
                    .beginTransaction()
                    .add(
                        id,
                        flutterFragment!!,
                        tagFlutterFragment
                    )
                    .commit()
            }
        }

        settings = FragmentContainerView(
            thisContext
        ).apply {

        }

        compose = ComposeView(
            thisContext
        ).apply {
            fitsSystemWindows = true
            setContent {
                MdcTheme(
                    context = thisContext
                ) {
                      ActivityMainLayout()

                }
            }
        }

        main = LinearLayoutCompat(
            thisContext
        ).apply {
            visibility = VISIBLE
            orientation = HORIZONTAL
            addView(
                compose,
                LayoutParams(
                    170,
                    LayoutParams.MATCH_PARENT
                )
            )
            addView(
                flutter,
                LayoutParams(
                    LayoutParams.MATCH_PARENT,
                    LayoutParams.MATCH_PARENT
                )
            )
        }

        logo = AppCompatImageView(
            thisContext
        ).apply {
            visibility = VISIBLE
            scaleType = ImageView.ScaleType.CENTER
            setImageDrawable(
                AppCompatResources.getDrawable(
                    thisContext,
                    R.drawable.ic_baseline_androntainer_plat_logo_24
                )
            )
        }
    }

    private fun setLayout() {
        setContentView(
            ConstraintLayout(
                thisContext
            ).apply {
                addView(
                    main,
                    LayoutParams(
                        LayoutParams.MATCH_PARENT,
                        LayoutParams.MATCH_PARENT
                    )
                )
                addView(
                    logo,
                    LayoutParams(
                        LayoutParams.MATCH_PARENT,
                        LayoutParams.MATCH_PARENT
                    )
                )
            }
        )
    }

    private fun postAnim() {
        main.visibility = View.INVISIBLE
        main.postDelayed(this, 200)
    }















    @Composable
    private fun ActivityMainLayout() {
        val scaffoldState = rememberScaffoldState()
        val expandedMenu = remember { mutableStateOf(false) }
        val navController = rememberNavController()
        val navBackStackEntry by navController.currentBackStackEntryAsState()
        val currentDestination = navBackStackEntry?.destination
        val items = listOf(
            Screen.Navigation,
            Screen.Launcher,
            Screen.Flutter,
            Screen.Dashboard,
            Screen.Settings
        )
        Scaffold(
            modifier = Modifier.fillMaxWidth(),
            scaffoldState = scaffoldState,
            topBar = {
                TopAppBar(
                    title = {
                        Text(text = "sb")
                    },
                    modifier = Modifier.fillMaxWidth(),
                    navigationIcon = {
                        IconButton(
                            onClick = {

                            }
                        ) {
                            Icon(
                                imageVector = Icons.Filled.Home,
                                contentDescription = null
                            )
                        }
                    },

                    elevation = 10.dp
                )
            },
            floatingActionButton = {
                FloatingActionButton(onClick = { /* ... */ }) {
                    Image(imageVector = Icons.TwoTone.Science, contentDescription = null)
                }
            },
            floatingActionButtonPosition = FabPosition.Center
//            bottomBar = {
//                BottomNavigation(
//                    modifier = Modifier.fillMaxWidth(),
//                    elevation = 10.dp
//                ) {
//                    items.forEach { screen ->
//                        BottomNavigationItem(
//                            icon = {
//                                Icon(
//                                    screen.imageVector,
//                                    contentDescription = null
//                                )
//                            },
//                            label = {
//                                Text(
//                                    stringResource(screen.resourceId)
//                                )
//                            },
//                            selected = currentDestination?.hierarchy?.any {
//                                it.route == screen.route
//                            } == true,
//                            onClick = {
//                                navController.navigate(
//                                    screen.route
//                                ) {
//                                    popUpTo(
//                                        navController.graph.findStartDestination().id
//                                    ) {
//                                        saveState = true
//                                    }
//                                    launchSingleTop = true
//                                    restoreState = true
//                                }
//                            }
//                        )
//                    }
//                }
//            },
        ) { innerPadding ->
            NavHost(
                navController = navController,
                startDestination = Screen.Flutter.route,
                modifier = Modifier
                    .fillMaxSize()
                    .padding(innerPadding)
            ) {
                composable(Screen.Navigation.route) {
                    Surface(
                        modifier = Modifier.fillMaxSize(),
                        color = MaterialTheme.colors.background
                    ) {

                    }
                }
                composable(Screen.Launcher.route) {
                    Surface(
                        modifier = Modifier.fillMaxSize(),
                        color = MaterialTheme.colors.background
                    ) {

                    }
                }
                composable(Screen.Flutter.route) {
                    Surface(
                        modifier = Modifier.fillMaxSize(),
                        color = MaterialTheme.colors.background
                    ) {
                        AndroidView(
                            factory = { context ->
                                      TextView(context).apply {
                                          text = "sb"


                                      }
                            },
                            modifier = Modifier.fillMaxSize()
                        )
                    }
                }
                composable(Screen.Dashboard.route) {
                    Surface(
                        modifier = Modifier.fillMaxSize(),
                        color = MaterialTheme.colors.background
                    ) {

                    }
                }
                composable(Screen.Settings.route) {
//                Surface(
//                    modifier = Modifier.fillMaxSize(),
//                    color = MaterialTheme.colors.background
//                ) {
//
//
//                }

                }
            }


        }


    }

}

class FlutterActivity : FlutterActivity()