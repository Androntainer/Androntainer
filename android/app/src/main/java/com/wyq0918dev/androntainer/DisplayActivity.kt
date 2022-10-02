package com.wyq0918dev.androntainer

import android.annotation.SuppressLint
import android.content.Context
import android.content.Intent
import android.os.Build
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.view.View
import android.view.WindowInsets
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.AppCompatImageButton
import androidx.compose.runtime.Composable
import androidx.compose.ui.tooling.preview.Preview
import androidx.drawerlayout.widget.DrawerLayout
import androidx.fragment.app.FragmentManager
import androidx.navigation.NavController
import androidx.navigation.fragment.NavHostFragment
import androidx.navigation.ui.setupWithNavController
import com.google.android.material.navigation.NavigationView
import com.wyq0918dev.androntainer.core.Compose
import com.wyq0918dev.androntainer.core.utils.OnDoubleClickListener
import com.wyq0918dev.androntainer.databinding.ActivityDisplayBinding
import com.wyq0918dev.androntainer.fragment.Flutter.Companion.fragmentContainerView
import com.wyq0918dev.androntainer.ui.theme.AndrontainerTheme
import io.flutter.embedding.android.FlutterEngineConfigurator
import io.flutter.embedding.android.FlutterFragment
import io.flutter.embedding.engine.FlutterEngine
import io.flutter.plugin.common.MethodChannel


class DisplayActivity : AppCompatActivity(), Runnable, FlutterEngineConfigurator {

    private lateinit var thisContext: Context
    private lateinit var binding: ActivityDisplayBinding
    private lateinit var navigationMenu: AppCompatImageButton
    //private lateinit var navController: NavController
    private var flutterFragment: FlutterFragment? = null
    private val tagFlutterFragment = "flutter_fragment"
    private var isFullscreen: Boolean = false
    private val hideHandler = Handler(Looper.myLooper()!!)

    companion object {
        private const val UI_ANIMATION_DELAY = 300
        private const val UI_DELAY_MILLIS = 100
        lateinit var drawer: DrawerLayout
        lateinit var navController: NavController

        @Composable
        fun ComposeLayout() {
            Compose()
        }
    }

    @Suppress("DEPRECATION")
    @SuppressLint("InlinedApi")
    private val hidePart2Runnable = Runnable {
        if (Build.VERSION.SDK_INT >= 30) {
            navigationMenu.windowInsetsController?.hide(WindowInsets.Type.statusBars() or WindowInsets.Type.navigationBars())
        } else {
            navigationMenu.systemUiVisibility =
                View.SYSTEM_UI_FLAG_LOW_PROFILE or
                        View.SYSTEM_UI_FLAG_FULLSCREEN or
                        View.SYSTEM_UI_FLAG_LAYOUT_STABLE or
                        View.SYSTEM_UI_FLAG_IMMERSIVE_STICKY or
                        View.SYSTEM_UI_FLAG_LAYOUT_HIDE_NAVIGATION or
                        View.SYSTEM_UI_FLAG_HIDE_NAVIGATION
        }
    }

    private val hideRunnable = Runnable {
        hideSystemUi()
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        init()
    }

    override fun run() {

    }

    override fun configureFlutterEngine(flutterEngine: FlutterEngine) {
        val messenger = flutterEngine.dartExecutor.binaryMessenger
        val channel = MethodChannel(messenger, "android")

        channel.setMethodCallHandler { call, res ->
            when (call.method) {
                "origin" -> {
                    Toast.makeText(this, "???", Toast.LENGTH_SHORT).show()
                    res.success("success")
                }
                else -> {
                    res.error(
                        "error_code",
                        "error_message",
                        null
                    )
                }
            }
        }
    }

    override fun cleanUpFlutterEngine(flutterEngine: FlutterEngine) {

    }

    override fun onPostCreate(savedInstanceState: Bundle?) {
        super.onPostCreate(savedInstanceState)
        hideHandler.removeCallbacks(hideRunnable)
        hideHandler.postDelayed(hideRunnable, UI_DELAY_MILLIS.toLong())
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
        //  composeView = null
    }


    private fun init() {
        initFast()
        initContext()
        initServices()
        initUi()
    }

    private fun initContext() {
        thisContext = this@DisplayActivity
    }

    private fun initServices() {

    }

    private fun initFast() {
        isFullscreen = true
    }

    private fun initUi() {
        binding = ActivityDisplayBinding.inflate(layoutInflater)
        setContentView(binding.root)
        initView()
    }

    @SuppressLint("ClickableViewAccessibility")
    private fun initView() {
        val navigationBack = binding.navigationBack
        val navigationHome = binding.navigationHome
        val navView: NavigationView = binding.navView
        val fragmentContainerId = binding.navHostFragmentContentMain.id
        val flutterContainerId = fragmentContainerView.id
        navigationMenu = binding.navigationMenu
        drawer = binding.drawerLayout

        val navHostFragment =
            (supportFragmentManager.findFragmentById(fragmentContainerId) as NavHostFragment?)!!
        val fragmentManager: FragmentManager = supportFragmentManager

        navController = navHostFragment.navController
        flutterFragment =
            fragmentManager.findFragmentByTag(tagFlutterFragment) as FlutterFragment?

        navView.setupWithNavController(navController)
        navigationBack.setOnTouchListener(
            OnDoubleClickListener(
                object : OnDoubleClickListener.MyClickCallBack {
                    override fun onClick() {
                        if (!drawer.isOpen) {
                            navController.navigateUp()
                        } else {
                            drawer.close()
                        }
                    }

                    override fun OnDoubleClick() {
                        if (!drawer.isOpen) {
                            flutterFragment?.onBackPressed()
                        } else {
                            drawer.close()
                        }
                    }
                }
            )
        )
        navigationHome.setOnTouchListener(
            OnDoubleClickListener(
                object : OnDoubleClickListener.MyClickCallBack {
                    override fun onClick() {
                        if (!drawer.isOpen) {
                            navController.navigate(R.id.nav_flutter)
                        } else {
                            drawer.close()
                        }
                    }

                    override fun OnDoubleClick() {
                        if (!drawer.isOpen) {
                            toggleSystemUi()
                        } else {
                            drawer.close()
                        }
                    }
                }
            )
        )
        navigationMenu.setOnTouchListener(
            OnDoubleClickListener(
                object : OnDoubleClickListener.MyClickCallBack {
                    override fun onClick() {
                        if (drawer.isOpen) {
                            drawer.close()
                        } else {
                            drawer.open()
                        }
                    }

                    override fun OnDoubleClick() {
                        if (!drawer.isOpen) {
                            toggleFlutter()
                        } else {
                            drawer.close()
                        }
                    }
                }
            )
        )
        if (flutterFragment == null) {
            flutterFragment = FlutterFragment
                .withNewEngine()
                .shouldAttachEngineToActivity(true)
                .build()
            fragmentManager
                .beginTransaction()
                .add(
                    flutterContainerId,
                    flutterFragment!!,
                    tagFlutterFragment
                )
                .commit()
        }
    }

    @Preview
    @Composable
    private fun DefaultPreview() {
        AndrontainerTheme {
            ComposeLayout()
        }
    }

    private fun toggleSystemUi() {
        if (isFullscreen) {
            hideSystemUi()
        } else {
            showSystemUi()
        }
    }

    private fun toggleFlutter() {
        if (fragmentContainerView.visibility == View.INVISIBLE) {
            showFlutter()
        } else {
            hideFlutter()
        }
    }

    private fun hideSystemUi() {
        isFullscreen = false
        hideHandler.postDelayed(hidePart2Runnable, UI_ANIMATION_DELAY.toLong())
    }

    @Suppress("DEPRECATION")
    private fun showSystemUi() {
        if (Build.VERSION.SDK_INT >= 30) {
            navigationMenu.windowInsetsController?.show(
                WindowInsets.Type.statusBars() or
                        WindowInsets.Type.navigationBars()
            )
        } else {
            navigationMenu.systemUiVisibility =
                View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN or
                        View.SYSTEM_UI_FLAG_LAYOUT_HIDE_NAVIGATION
        }
        isFullscreen = true
        hideHandler.removeCallbacks(hidePart2Runnable)
    }

    private fun hideFlutter() {
        fragmentContainerView.visibility = View.INVISIBLE
    }

    private fun showFlutter() {
        fragmentContainerView.visibility = View.VISIBLE
    }

}