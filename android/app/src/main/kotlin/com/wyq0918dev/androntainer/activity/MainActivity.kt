package com.wyq0918dev.androntainer.activity

import android.annotation.SuppressLint
import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.view.View
import android.view.ViewAnimationUtils
import android.view.ViewGroup.*
import android.widget.ImageView
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.AppCompatImageView
import androidx.compose.runtime.Composable
import androidx.compose.ui.platform.ComposeView
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.core.view.WindowCompat
import androidx.fragment.app.FragmentContainerView
import androidx.fragment.app.FragmentManager
import com.google.android.material.composethemeadapter.MdcTheme
import com.wyq0918dev.androntainer.R
import com.wyq0918dev.androntainer.ui.layout.Greeting
import io.flutter.embedding.android.FlutterEngineConfigurator
import io.flutter.embedding.android.FlutterFragment
import io.flutter.embedding.engine.FlutterEngine
import io.flutter.plugin.common.MethodChannel
import kotlin.math.hypot

/**
 * Androntainer Project
 * Copyright (c) 2022 wyq0918dev.
 */

class MainActivity : AppCompatActivity(), Runnable, FlutterEngineConfigurator {

    // 上下文
    private lateinit var thisContext: Context

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

    private fun initPostAnim() {
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
        initPostAnim()
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
        val option = View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN or View.SYSTEM_UI_FLAG_LAYOUT_STABLE
        val decorView = window.decorView
        val visibility: Int = decorView.systemUiVisibility
        decorView.systemUiVisibility = visibility or option
    }

    /**
     * --应用加载--
     * 加载UI界面
     */

    @SuppressLint("UseCompatLoadingForDrawables")
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
                        addView(
                            FragmentContainerView(
                                thisContext
                            ).apply {
                                id = flutterId
                                visibility = VISIBLE
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
                    },
                    LayoutParams(
                        LayoutParams.MATCH_PARENT,
                        LayoutParams.MATCH_PARENT
                    )
                )
                addView(
                    AppCompatImageView(
                        thisContext
                    ).apply {
                        id = logoId
                        visibility = VISIBLE
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
        MdcTheme(
            context = thisContext,
            readColors = true,
            readTypography = true,
            readShapes = true,
            setTextColors = true,
            setDefaultFontFamily = true,
        ) {
            Greeting(name = "sb")
        }
    }

    override fun configureFlutterEngine(flutterEngine: FlutterEngine) {
        val messenger = flutterEngine.dartExecutor.binaryMessenger
        val channel = MethodChannel(messenger, "origin")
        val flutter = findViewById<FragmentContainerView>(flutterId)
        channel.setMethodCallHandler { call, res ->
            when (call.method) {
                "origin" -> {
                    if (flutter.visibility == GONE) {
                        flutter.visibility = VISIBLE
                    } else {
                        flutter.visibility = GONE
                    }
                    res.success("success")
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