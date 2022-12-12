package com.wyq0918dev.androntainer

import android.annotation.SuppressLint
import android.app.Application
import android.app.Service
import android.content.*
import android.content.pm.PackageManager
import android.content.pm.ResolveInfo
import android.graphics.drawable.Drawable
import android.os.*
import android.util.Log
import android.view.*
import android.widget.*
import androidx.annotation.StringRes
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.app.AppCompatDelegate
import androidx.appcompat.widget.*
import androidx.appcompat.widget.Toolbar
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.twotone.*
import androidx.compose.material3.*
import androidx.compose.material3.Typography
import androidx.compose.runtime.*
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.toArgb
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.ComposeView
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalView
import androidx.compose.ui.platform.ViewCompositionStrategy
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.sp
import androidx.coordinatorlayout.widget.CoordinatorLayout
import androidx.core.content.ContextCompat
import androidx.core.view.ViewCompat
import androidx.core.view.WindowCompat
import androidx.fragment.app.FragmentContainerView
import androidx.fragment.app.FragmentManager
import androidx.preference.PreferenceFragmentCompat
import androidx.preference.PreferenceManager
import com.android.vending.billing.IInAppBillingService
import com.blankj.utilcode.util.AppUtils
import com.farmerbb.taskbar.lib.Taskbar
import com.google.android.material.appbar.AppBarLayout
import com.google.android.material.appbar.MaterialToolbar
import com.google.android.material.color.DynamicColors
import com.kongzue.baseframework.BaseActivity
import com.kongzue.baseframework.BaseApp
import com.kongzue.baseframework.BaseFragment
import com.kongzue.baseframework.interfaces.GlobalLifeCircleListener
import com.kongzue.baseframework.interfaces.OnBugReportListener
import com.kongzue.baseframework.util.AppManager
import com.kongzue.baseframework.util.JumpParameter
import com.kongzue.dialogx.DialogX
import com.kongzue.dialogx.dialogs.PopTip
import com.kongzue.dialogxmaterialyou.style.MaterialYouStyle
import com.wyq0918dev.androntainer.databinding.ActivityApplistBinding
import com.wyq0918dev.androntainer.databinding.ActivityLauncherSettingsBinding
import io.flutter.app.FlutterApplication
import io.flutter.embedding.android.FlutterActivity
import io.flutter.embedding.android.FlutterEngineConfigurator
import io.flutter.embedding.android.FlutterFragment
import io.flutter.embedding.engine.FlutterEngine
import io.flutter.plugin.common.MethodChannel
import io.flutter.plugin.common.StandardMessageCodec
import io.flutter.plugin.platform.PlatformView
import io.flutter.plugin.platform.PlatformViewFactory
import io.flutter.plugins.GeneratedPluginRegistrant
import java.io.File
import kotlin.math.hypot

class MainApp : BaseApp<MainApp>() {

    private lateinit var application: BaseApp<MainApp>

    override fun init() {
        // 获取上下文
        application = me
        val msg = "SDK已加载完毕"

        setOnSDKInitializedCallBack {
            try {
                PopTip.show(msg)
            } catch (e: Exception) {
                toast(msg)
            }
        }

        setOnCrashListener(object : OnBugReportListener() {
            override fun onCrash(e: Exception, crashLogFile: File): Boolean {
                if (AppManager.getInstance().activeActivity == null || !AppManager.getInstance().activeActivity.isActive) {
                    return false
                }


                AlertDialog.Builder(AppManager.getInstance().activeActivity)
                    .setTitle("Ops！发生了一次崩溃！")
                    .setMessage("您是否愿意帮助我们改进程序以修复此Bug？")
                    .setPositiveButton("愿意") { _, _ ->
                        //val url = crashLogFile.absolutePath
                        toast("我真的会谢")
                    }
                    .setNegativeButton("不了") { _, _ ->
                        toast("抱歉打扰了")
                    }
                    .setCancelable(false)
                    .create()
                    .show()

                return false
            }
        })
    }

    // 加载SDK
    override fun initSDKs() {
        super.initSDKs()
        Androntainer.App.FlutterApp().init(application)
    }

    // SDK加载完成时调用
    override fun initSDKInitialized() {
        super.initSDKInitialized()
        Androntainer.App.FlutterApp().launch(application)
    }

    class Androntainer {

        class Abstract {

            abstract class AndrontainerSplash : BaseActivity() {

                // 获取上下文
                private val context: BaseActivity = me
                private lateinit var compose: ComposeView

                override fun resetContentView(): ComposeView {
                    compose = ComposeView(context).apply {
                        setViewCompositionStrategy(
                            ViewCompositionStrategy.DisposeOnViewTreeLifecycleDestroyed
                        )
                    }
                    return compose
                }

                override fun initViews() {
                    compose.apply {
                        setContent {
                            Contents()
                        }
                    }
                }

                override fun initDatas(parameter: JumpParameter?) {

                }

                override fun setEvents() {

                }

                // 供子类使用的上下文
                protected val viewContext: BaseActivity = context
                protected val thisContext: BaseActivity = context

                @Composable
                abstract fun Contents()

            }

            abstract class AndrontainerActivity : BaseActivity(), FlutterEngineConfigurator,
                Runnable {

                // 获取上下文
                private val context: BaseActivity = me

                // 布局
                override fun resetContentView(): View? {

                    initView()
                    setSupportActionBar(actionBar())
                    return contentView()
                }

//            override fun resetLayoutResId(): Int {
//                initView()
//                setSupportActionBar(actionBar())
//                return layoutResId()
//            }

                override fun initViews() {
                    initServices()
                    initSystemBar()
                    initUi()
                }

                override fun initDatas(parameter: JumpParameter?) {
                    postAnim()
                    initData(parameter)
                }

                override fun setEvents() {
                    setEvent()
                }

                override fun configureFlutterEngine(flutterEngine: FlutterEngine) {
                    configure(flutterEngine)
                }

                override fun cleanUpFlutterEngine(flutterEngine: FlutterEngine) {

                }

                override fun run() {
                    runnable()
                }


                // 供子类使用的上下文
                protected val viewContext: BaseActivity = context
                protected val thisContext: BaseActivity = context

                // 需要子类重写的方法
                abstract fun initView()

                //abstract fun layoutResId(): Int
                abstract fun contentView(): View?
                abstract fun actionBar(): Toolbar?
                abstract fun configure(flutterEngine: FlutterEngine)
                abstract fun runnable()
                abstract fun postAnim()
                abstract fun initServices()
                abstract fun initData(parameter: JumpParameter?)
                abstract fun initSystemBar()
                abstract fun initUi()
                abstract fun setEvent()
            }

            abstract class LibraryActivity : BaseActivity() {

                private val context: BaseActivity = me

                override fun resetContentView(): View {
                    return View(me)
                }

                override fun initViews() {
                    create()
                }

                override fun initDatas(parameter: JumpParameter?) {
                    data(parameter)
                }

                override fun setEvents() {
                }

                protected val thisContext: BaseActivity = context

                abstract fun create()
                abstract fun data(parameter: JumpParameter?)
            }

            abstract class FixedActivity : BaseActivity() {

                private val context: BaseActivity = me

                override fun resetContentView(): View? {
                    initView()
                    setSupportActionBar(actionBar())
                    return contentView()
                }

                override fun initViews() {

                }

                override fun initDatas(parameter: JumpParameter?) {

                }

                override fun setEvents() {

                }

                protected val thisContext: BaseActivity = context

                abstract fun initView()
                abstract fun contentView(): View?
                abstract fun actionBar(): Toolbar?

            }

            abstract class AndrontainerFlutterFragment<activity : AndrontainerActivity> :
                BaseFragment<activity>() {

                // 获取上下文
                private val context: activity = me

                override fun resetContentView(): View? {
                    super.resetContentView()
                    initView()
                    return contentView()
                }

                override fun initViews() {
                    initPage()
                }

                override fun initDatas() {

                }

                override fun setEvents() {

                }

                // 供子类使用的上下文
                protected val viewContext = context
                protected val thisContext = context


                abstract fun initView()
                abstract fun initPage()
                abstract fun contentView(): View?
            }

            class AndrontainerFragment<activity : AndrontainerActivity> : BaseFragment<activity>() {
                override fun initViews() {
                    TODO("Not yet implemented")
                }

                override fun initDatas() {
                    TODO("Not yet implemented")
                }

                override fun setEvents() {
                    TODO("Not yet implemented")
                }

            }

            abstract class AndrontainerSettings : PreferenceFragmentCompat() {

                override fun onCreatePreferences(savedInstanceState: Bundle?, rootKey: String?) {
                    setPreferencesFromResource(R.xml.root_preferences, rootKey)
                    events()
                }

                abstract fun events()
            }
        }

        class App {

            class FlutterApp : FlutterApplication() {

                fun init(application: Application) {
                    Utils.Flutter().initSDK(application)
                }

                fun launch(application: Application) {
                    Utils.Flutter().launcher(application)
                }
            }

            class FlutterAct : FlutterActivity()

            @SuppressLint("CustomSplashScreen")
            class SplashScreen : Abstract.AndrontainerSplash() {

                @Composable
                override fun Contents() {

                }
            }

            class MainActivity : Abstract.AndrontainerActivity() {

                private var flutterFragment = FlutterPage.flutterFragment

                private lateinit var toolbar: MaterialToolbar
                private lateinit var logo: AppCompatImageView
                private lateinit var greeting: ComposeView

                private var androidVersion: String = "unknown"

                private var targetAppName: String by mutableStateOf("unknown")
                private var targetPackageName: String by mutableStateOf("unknown")
                private var targetDescription: String by mutableStateOf("unknown")

                override fun initView() {
                    toolbar = MaterialToolbar(
                        viewContext
                    ).apply {
                        navigationIcon = ContextCompat.getDrawable(
                            viewContext,
                            R.drawable.ic_baseline_menu_24
                        )
                        subtitle = targetAppName
                    }

                    logo = AppCompatImageView(
                        viewContext
                    ).apply {
                        scaleType = ImageView.ScaleType.CENTER
                        setImageDrawable(
                            ContextCompat.getDrawable(
                                viewContext,
                                R.drawable.ic_baseline_androntainer_plat_logo_24
                            )
                        )
                    }

                    greeting = ComposeView(
                        viewContext
                    ).apply {
                        setViewCompositionStrategy(
                            ViewCompositionStrategy.DisposeOnViewTreeLifecycleDestroyed
                        )
                    }
                }

                override fun contentView(): View {
                    return Resources.Layout.ViewSystem()
                        .activityMainLayout(
                            viewContext = viewContext,
                            toolbar = toolbar,
                            logo = logo,
                            greeting = greeting
                        )
                }

                override fun actionBar(): Toolbar {
                    return toolbar
                }

                override fun configure(flutterEngine: FlutterEngine) {
                    GeneratedPluginRegistrant.registerWith(flutterEngine)
                    val registry = flutterEngine.platformViewsController.registry
                    registry.registerViewFactory("android_view", Classes.LinkNativeViewFactory())

                    val messenger = flutterEngine.dartExecutor.binaryMessenger
                    val channel = MethodChannel(messenger, "android")
                    channel.setMethodCallHandler { call, res ->
                        when (call.method) {
                            "origin" -> {
                                //flutter.visibility = INVISIBLE
                                res.success("success")
                            }
                            else -> {
                                res.error("error", "error_message", null)
                            }
                        }
                    }
                }

                override fun runnable() {
                    val cx = logo.x + logo.width / 2f
                    val cy = logo.y + logo.height / 2f
                    val startRadius = hypot(logo.width.toFloat(), logo.height.toFloat())
                    val endRadius = hypot(greeting.width.toFloat(), greeting.height.toFloat())
                    val circularAnim = ViewAnimationUtils
                        .createCircularReveal(greeting, cx.toInt(), cy.toInt(), startRadius, endRadius)
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
                            greeting.visibility = View.VISIBLE
                            circularAnim.start()
                        }
                        .start()
                }

                override fun postAnim() {
                    greeting.visibility = View.INVISIBLE
                    greeting.postDelayed(this, 200)
                }

                override fun initServices() {
                    Core().service(thisContext)
                }

                override fun initData(parameter: JumpParameter?) {

                }

                override fun initSystemBar() {
                    AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_FOLLOW_SYSTEM)
                    WindowCompat.setDecorFitsSystemWindows(window, true)
                    val option =
                        View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN or View.SYSTEM_UI_FLAG_LAYOUT_STABLE
                    val decorView = window.decorView
                    val visibility: Int = decorView.systemUiVisibility
                    decorView.systemUiVisibility = visibility or option
                    setDarkStatusBarTheme(true)
                    setDarkNavigationBarTheme(true)
                    setNavigationBarBackgroundColor(android.graphics.Color.TRANSPARENT)
                }

                override fun initUi() {
                    greeting.apply {
                        setContent {
                            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) {
                                val sharedPreferences =
                                    PreferenceManager.getDefaultSharedPreferences(application)
                                Resources.Values().AndrontainerTheme(
                                    dynamicColor = sharedPreferences.getBoolean(
                                        "dynamic_colors",
                                        true
                                    )
                                ) {
                                    //ComposableLayout()
                                }

                            } else {
                                Resources.Values().AndrontainerTheme(
                                    dynamicColor = false
                                ) {
                                    //ComposableLayout()
                                }
                            }
                        }
                    }
                }

                override fun setEvent() {
                    setGlobalLifeCircleListener(
                        object : GlobalLifeCircleListener() {
                            override fun onCreate(me: BaseActivity, className: String) {
                                super.onCreate(me, className)
                                when (me) {
                                    FixedPlay() -> finish()
                                }
                            }

                            override fun onResume(me: BaseActivity, className: String) {
                                super.onResume(me, className)

                            }

                            override fun onDestroy(me: BaseActivity, className: String) {
                                super.onDestroy(me, className)
                                when (me) {
                                    MainActivity() -> {
                                        //greeting.removeCallbacks(this@MainActivity)
                                    }
                                }
                            }

                            override fun onStart(activity: BaseActivity?, className: String?) {
                                super.onStart(activity, className)
                            }

                        }
                    )
                    AppManager.setOnActivityStatusChangeListener(
                        object : AppManager.OnActivityStatusChangeListener() {
                            override fun onActivityCreate(activity: BaseActivity) {
                                super.onActivityCreate(activity)

                            }

                            override fun onActivityDestroy(activity: BaseActivity) {
                                super.onActivityDestroy(activity)

                            }

                            override fun onAllActivityClose() {
                                Log.e(">>>", "所有Activity已经关闭")
                            }
                        }
                    )
                }

                /**
                 * Create OptionsMenu
                 */

                override fun onCreateOptionsMenu(menu: Menu): Boolean {
                    menuInflater.inflate(R.menu.main, menu)
                    return true
                }

                /**
                 * Options menu click
                 */

                override fun onOptionsItemSelected(item: MenuItem): Boolean {
                    return when (item.itemId) {
                        R.id.action_settings -> {
                            //navController.navigate(R.id.SettingsFragment)
                            true
                        }
                        else -> super.onOptionsItemSelected(item)
                    }
                }

                override fun onPostResume() {
                    super.onPostResume()
                    flutterFragment?.onPostResume()
                }

                override fun onNewIntent(intent: Intent?) {
                    super.onNewIntent(intent)
                    if (intent != null) {
                        FlutterPage.flutterFragment?.onNewIntent(intent)
                    }
                }

                override fun onBack(): Boolean {
                    super.onBack()
                    FlutterPage.flutterFragment?.onBackPressed()
                    return false
                }

                override fun onRequestPermissionsResult(
                    requestCode: Int,
                    permissions: Array<out String>,
                    grantResults: IntArray
                ) {
                    super.onRequestPermissionsResult(requestCode, permissions, grantResults)
                    @Suppress("DEPRECATION")
                    FlutterPage.flutterFragment?.onRequestPermissionsResult(
                        requestCode,
                        permissions,
                        grantResults
                    )
                }

                override fun onUserLeaveHint() {
                    super.onUserLeaveHint()
                    FlutterPage.flutterFragment?.onUserLeaveHint()
                }

                override fun onTrimMemory(level: Int) {
                    super.onTrimMemory(level)
                    FlutterPage.flutterFragment?.onTrimMemory(level)
                }

                private fun optionsMenu() {
                    toolbar.showOverflowMenu()
                }
            }

            class Google : Abstract.LibraryActivity() {

                override fun create() {
                    main(3)
                }

                override fun data(parameter: JumpParameter?) {

                }

                override fun onRequestPermissionsResult(
                    requestCode: Int,
                    permissions: Array<String>,
                    grantResults: IntArray
                ) {
                    super.onRequestPermissionsResult(requestCode, permissions, grantResults)
                    if (requestCode == 3 && grantResults.size == 3) {
                        setResult(if (grantResults[0] == PackageManager.PERMISSION_GRANTED) RESULT_OK else RESULT_CANCELED)
                        finish()
                    }
                }


                private fun main(code: Int) {
                    if (check(me)) {
                        setResult(RESULT_OK)
                    } else {
                        requires(me, code)
                    }
                    finish()
                }

                companion object {

                    fun check(activity: AppCompatActivity): Boolean {
                        return if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                            activity.checkSelfPermission("android.permission.FAKE_PACKAGE_SIGNATURE") == PackageManager.PERMISSION_GRANTED
                        } else {
                            true
                        }
                    }

                    fun requires(activity: AppCompatActivity, code: Int) {
                        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                            activity.requestPermissions(
                                arrayOf("android.permission.FAKE_PACKAGE_SIGNATURE"),
                                code
                            )
                        }
                    }
                }
            }

            class FixedPlay : BaseActivity() {

                override fun initViews() {
                    mPackageManager = packageManager
                }

                override fun initDatas(parameter: JumpParameter?) {
                    go()
                }

                override fun setEvents() {
                    TODO("Not yet implemented")
                }

                private lateinit var mPackageManager: PackageManager
                private val thisPackage = AppUtils.getAppPackageName()
                private var mode: String? = "r2"

                private fun go() {
                    val read = PreferenceManager.getDefaultSharedPreferences(me)
                    val app = read.getString("app", "")
                    val className = read.getString("class", "")
                    val perMode: String? = read.getString("mode", "r2")
                    if (perMode != "r2") {
                        mode = perMode
                    }
                    if (app!!.isNotEmpty() && app != thisPackage) {
                        when (mode) {
                            "r2" -> {
                                val intent = packageManager!!.getLaunchIntentForPackage(app)
                                intent?.let {
                                    startActivity(it)
                                }
                            }
                            "r1" -> if (className!!.length > 5) {
                                val intent = Intent()
                                intent.setClassName(app, className)
                                startActivity(intent)
                            } else {
                                val intent: Intent? =
                                    packageManager!!.getLaunchIntentForPackage(app)
                                intent!!.flags =
                                    Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_RESET_TASK_IF_NEEDED or Intent.FLAG_ACTIVITY_CLEAR_TOP
                                startActivity(intent)
                            }
                        }
                    } else {
                        jump(FixedSettings().javaClass)
                        //val intent = Intent(thisContext, FixedSettings().javaClass)
                        //startActivity(intent)
                    }
                }


            }

            class FixedSettings : AppCompatActivity() {

                private lateinit var mPackageManager: PackageManager
                private var _binding: ActivityLauncherSettingsBinding? = null
                private val binding get() = _binding!!
                private var _mode = ""
                private var packageName: AppCompatTextView? = null

                override fun onCreate(savedInstanceState: Bundle?) {
                    super.onCreate(savedInstanceState)
                    // packageManager
                    mPackageManager = packageManager
                    // Intent
                    val intent = intent
                    // Layout
                    _binding = ActivityLauncherSettingsBinding.inflate(layoutInflater)
                    setContentView(binding.root)
                    // Views
                    val button1: AppCompatButton = binding.button1
                    val button2: AppCompatButton = binding.button2
                    packageName = binding.textPakageName
                    val toolbar: Toolbar = binding.toolbar
                    // Click
                    button1.setOnClickListener {
                        //Core.selectLauncher(this@FixedSettings)
                    }
                    button2.setOnClickListener {
                        button2()
                    }
                    toolbar.setNavigationOnClickListener {
                        finish()
                    }
                    // Update Layout
                    go()
                    // Select App
                    val bundle = intent.extras
                    val setting = bundle?.getString("settings")
                    if (setting != null) {
                        if (setting == "select_app") {
                            button2()
                        }
                    }
                }

                override fun onDestroy() {
                    super.onDestroy()
                    _binding = null
                }

                @SuppressLint("SdCardPath")
                private fun button2() {
                    val btf = getString(R.string.btf)
                    val act = getString(R.string.act)
                    val mode = getString(R.string.mode)
                    val items3 = arrayOf(btf, act)
                    val alertDialog3: AlertDialog = AlertDialog.Builder(this@FixedSettings)
                        .setTitle(mode)
                        .setIcon(R.drawable.ic_baseline_androntainer_plat_logo_24)
                        .setItems(items3) { _: DialogInterface?, i: Int ->
                            _mode = ""
                            val select = Intent(this@FixedSettings, SelectOne::class.java)
                            when (i) {
                                0 -> {
                                    _mode = "r2"
                                    select.putExtra("_mode", _mode)
                                    startActivity(select)
                                }
                                1 -> {
                                    _mode = "r1"
                                    select.putExtra("_mode", _mode)
                                    startActivity(select)
                                }
                            }
                        }
                        .create()
                    alertDialog3.show()
                }

                public override fun onResume() {
                    super.onResume()
                    go()
                }

                @SuppressLint("SetTextI18n")
                fun go() {
                    val read = PreferenceManager.getDefaultSharedPreferences(this@FixedSettings)
                    val app = read.getString("app", "")
                    if (app!!.isEmpty()) return
                    val label = read.getString("label", "")
                    val uri = read.getString("uri", "")
                    val className = read.getString("class", "")
                    val icon: Drawable
                    try {
                        val appInfo = packageManager!!.getApplicationInfo(app, 0)
                        icon = appInfo.loadIcon(packageManager)
                        runOnUiThread {
                            packageName!!.text = label
                            binding.applistItem.itemImg.setImageDrawable(icon)
                            binding.applistItem.itemText.text = app
                            binding.applistItem.itemPackageName.text = """
                                    $uri
                                    $className
                                    """.trimIndent()
                        }
                    } catch (e: Exception) {
                        e.printStackTrace()
                    }
                }
            }

            class SelectOne : AppCompatActivity() {

                private var _binding: ActivityApplistBinding? = null
                private val binding get() = _binding!!
                private val list: MutableList<Classes.Item?> = ArrayList()
                private lateinit var listView: ListView
                private lateinit var progressBar: ProgressBar
                private var _mode: String? = null
                private var _uri: String? = null

                override fun onCreate(savedInstanceState: Bundle?) {
                    super.onCreate(savedInstanceState)
                    _binding = ActivityApplistBinding.inflate(layoutInflater)

//                val layout = LinearLayoutCompat(this@SelectOne).apply {
//                    orientation = LinearLayoutCompat.VERTICAL
//                    addView(
//                        AppBarLayout(this@SelectOne).apply {
//                            setTheme(com.google.android.material.R.style.ThemeOverlay_Material3_ActionBar)
//                            addView(
//                                MaterialToolbar(this@SelectOne).apply {
//
//                                },
//                                ViewGroup.LayoutParams(
//                                    ViewGroup.LayoutParams.MATCH_PARENT,
//                                    ViewGroup.LayoutParams.WRAP_CONTENT
//                                )
//                            )
//                        },
//                        ViewGroup.LayoutParams(
//                            ViewGroup.LayoutParams.MATCH_PARENT,
//                            ViewGroup.LayoutParams.WRAP_CONTENT
//                        )
//                    )
//                    addView(
//                        ProgressBar(this@SelectOne).apply {
//
//                        },
//                        ViewGroup.LayoutParams(
//                            ViewGroup.LayoutParams.MATCH_PARENT,
//                            ViewGroup.LayoutParams.WRAP_CONTENT
//                        )
//                    )
//                    addView(
//                        ListView(this@SelectOne).apply {
//
//                        },
//                        ViewGroup.LayoutParams(
//                            ViewGroup.LayoutParams.MATCH_PARENT,
//                            ViewGroup.LayoutParams.MATCH_PARENT
//                        )
//                    )
//                }

                    setContentView(binding.root)
                    progressBar = binding.progressBar
                    val intent = intent
                    val bundle = intent.extras

                    _mode = bundle?.getString("_mode")
                    _uri = bundle?.getString("_uri")
                    Thread {
                        loadAllApps(makeIntent())
                        val itemAdapter =
                            Classes.ItemAdapter(this@SelectOne, R.layout.item_applist, list)
                        itemAdapter.setMode(_mode!!)
                        itemAdapter.setUri(_uri!!)
                        runOnUiThread {
                            listView = binding.listview
                            listView.adapter = itemAdapter
                            progressBar.visibility = View.GONE
                        }
                    }.start()
                    val toolbar: Toolbar = binding.toolbar
                    toolbar.setNavigationOnClickListener {
                        finish()
                    }
                }

                override fun onPause() {
                    super.onPause()
                    finish()
                }

                private fun loadAllApps(intent: Intent?) {
                    val mApps: MutableList<ResolveInfo>
                    mApps = ArrayList()
                    try {
                        mApps.addAll(this.packageManager.queryIntentActivities(intent!!, 0))
                        val pm = packageManager
                        for (r in mApps) {
                            val item = Classes.Item(
                                r.loadLabel(pm).toString(),
                                r.activityInfo.packageName,
                                r.activityInfo.name,
                                r.loadIcon(pm)
                            )
                            list.add(item)
                        }
                    } catch (e: Exception) {
                        e.printStackTrace()
                    }
                }

                @SuppressLint("WrongConstant, UseSwitchCompatOrMaterialCode")
                private fun makeIntent(): Intent? {
                    when (_mode) {
                        "r2", "r1" -> {
                            val mainIntent = Intent(Intent.ACTION_MAIN, null)
                            mainIntent.addCategory(Intent.CATEGORY_HOME)
                            return mainIntent
                        }
                    }
                    return null
                }

                override fun onDestroy() {
                    super.onDestroy()
                    _binding = null
                }
            }

            class InAppBillingService : Service() {

                override fun onBind(intent: Intent?): IBinder {
                    Utils.Billing().stub()
                    return Utils.Billing().mInAppBillingService
                }
            }

            class FlutterPage : Abstract.AndrontainerFlutterFragment<MainActivity>() {

                private lateinit var flutter: FragmentContainerView
                private val tagFlutterFragment = "flutter_fragment"

                override fun initView() {
                    flutter = FragmentContainerView(
                        viewContext
                    ).apply {
                        id = Resources.Values.flutterId

                    }
                }

                override fun initPage() {
                    val fragmentManager: FragmentManager = childFragmentManager
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
                                flutter.id,
                                flutterFragment!!,
                                tagFlutterFragment
                            )
                            .commit()
                    }
                }

                override fun contentView(): View {
                    return flutter
                }

                override fun onDestroy() {
                    super.onDestroy()
                    flutterFragment = null
                }

                companion object {
                    var flutterFragment: FlutterFragment? = null
                }
            }
        }

        class Utils {

            class Flutter {

                // 加载SDK
                fun initSDK(application: Application) {
                    Core().initFlutter(application)
                    Core().initDynamicColors(application)
                    Core().initDialogX(application)
                    Core().initTaskbar(application)
                }

                // 启动主页面
                fun launcher(application: Application) {
                    Core().launch(application)
                }
            }

            class Splash {

                // 启动主页面
                fun launcher(application: Application) {
                    Core().launch(application)
                }
            }

            class Main {

                // 获取系统版本
                fun getAndroidVersion(): String {
                    return when (Build.VERSION.SDK_INT) {
                        Build.VERSION_CODES.LOLLIPOP -> "Android Lollipop 5.0"
                        Build.VERSION_CODES.LOLLIPOP_MR1 -> "Android Lollipop MR1 5.1"
                        Build.VERSION_CODES.M -> "Android Marshmallow 6.0"
                        Build.VERSION_CODES.N -> "Android Nougat 7.0"
                        Build.VERSION_CODES.N_MR1 -> "Android Nougat MR1 7.1"
                        Build.VERSION_CODES.O -> "Android Oreo 8.0"
                        Build.VERSION_CODES.O_MR1 -> "Android Oreo MR1 8.1"
                        Build.VERSION_CODES.P -> "Android Pie 9.0"
                        Build.VERSION_CODES.Q -> "Android Q 10.0"
                        Build.VERSION_CODES.R -> "Android RedVelvetCake 11.0"
                        Build.VERSION_CODES.S -> "Android SnowCone 12.0"
                        Build.VERSION_CODES.S_V2 -> "Android SnowCone V2 12.1"
                        Build.VERSION_CODES.TIRAMISU -> "Android Tiramisu 13.0"
                        else -> "Unknown"
                    }
                }
            }

            class Billing {

                private val tag: String = "FakeInAppStore"
                lateinit var mInAppBillingService: IInAppBillingService.Stub

                fun stub() {
                    mInAppBillingService =
                        object : IInAppBillingService.Stub() {
                            override fun isBillingSupported(
                                apiVersion: Int,
                                packageName: String,
                                type: String
                            ): Int {
                                return isBillingSupportedV7(
                                    apiVersion,
                                    packageName,
                                    type,
                                    Bundle()
                                )
                            }

                            override fun getSkuDetails(
                                apiVersion: Int,
                                packageName: String,
                                type: String,
                                skusBundle: Bundle
                            ): Bundle {
                                return getSkuDetailsV10(
                                    apiVersion,
                                    packageName,
                                    type,
                                    skusBundle,
                                    Bundle()
                                )
                            }

                            override fun getBuyIntent(
                                apiVersion: Int,
                                packageName: String,
                                sku: String,
                                type: String,
                                developerPayload: String
                            ): Bundle {
                                return getBuyIntentV6(
                                    apiVersion,
                                    packageName,
                                    sku,
                                    type,
                                    developerPayload,
                                    Bundle()
                                )
                            }

                            override fun getPurchases(
                                apiVersion: Int,
                                packageName: String,
                                type: String,
                                continuationToken: String
                            ): Bundle {
                                return getPurchasesV6(
                                    apiVersion,
                                    packageName,
                                    type,
                                    continuationToken,
                                    Bundle()
                                )
                            }

                            override fun consumePurchase(
                                apiVersion: Int,
                                packageName: String,
                                purchaseToken: String
                            ): Int {
                                return consumePurchaseV9(
                                    apiVersion,
                                    packageName,
                                    purchaseToken,
                                    Bundle()
                                ).getInt("RESPONSE_CODE", 8)
                            }

                            override fun getBuyIntentToReplaceSkus(
                                apiVersion: Int,
                                packageName: String,
                                oldSkus: List<String>,
                                newSku: String,
                                type: String,
                                developerPayload: String
                            ): Bundle {
                                Log.d(
                                    tag,
                                    "getBuyIntentToReplaceSkus($apiVersion, $packageName, $newSku, $type, $developerPayload)"
                                )
                                val data = Bundle()
                                data.putInt("RESPONSE_CODE", 4)
                                return data
                            }

                            override fun getBuyIntentV6(
                                apiVersion: Int,
                                packageName: String,
                                sku: String,
                                type: String,
                                developerPayload: String,
                                extras: Bundle
                            ): Bundle {
                                Log.d(
                                    tag,
                                    "getBuyIntent($apiVersion, $packageName, $sku, $type, $developerPayload)"
                                )
                                val data = Bundle()
                                data.putInt("RESPONSE_CODE", 4)
                                return data
                            }

                            override fun getPurchasesV6(
                                apiVersion: Int,
                                packageName: String,
                                type: String,
                                continuationToken: String,
                                extras: Bundle
                            ): Bundle {
                                return getPurchasesV9(
                                    apiVersion,
                                    packageName,
                                    type,
                                    continuationToken,
                                    extras
                                )
                            }

                            override fun isBillingSupportedV7(
                                apiVersion: Int,
                                packageName: String,
                                type: String,
                                extras: Bundle
                            ): Int {
                                Log.d(
                                    tag,
                                    "isBillingSupported($apiVersion, $packageName, $type)"
                                )
                                return 0
                            }

                            override fun getPurchasesV9(
                                apiVersion: Int,
                                packageName: String,
                                type: String,
                                continuationToken: String,
                                extras: Bundle
                            ): Bundle {
                                Log.d(
                                    tag,
                                    "getPurchases($apiVersion, $packageName, $type, $continuationToken)"
                                )
                                val data = Bundle()
                                data.putInt("RESPONSE_CODE", 0)
                                data.putStringArrayList("INAPP_PURCHASE_ITEM_LIST", ArrayList())
                                data.putStringArrayList("INAPP_PURCHASE_DATA_LIST", ArrayList())
                                data.putStringArrayList(
                                    "INAPP_DATA_SIGNATURE_LIST",
                                    ArrayList()
                                )
                                return data
                            }

                            override fun consumePurchaseV9(
                                apiVersion: Int,
                                packageName: String,
                                purchaseToken: String,
                                extras: Bundle
                            ): Bundle {
                                Log.d(
                                    tag,
                                    "consumePurchase($apiVersion, $packageName, $purchaseToken)"
                                )
                                val data = Bundle()
                                data.putInt("RESPONSE_CODE", 8)
                                return data
                            }

                            override fun getPriceChangeConfirmationIntent(
                                apiVersion: Int,
                                packageName: String,
                                sku: String,
                                type: String,
                                extras: Bundle
                            ): Bundle {
                                Log.d(
                                    tag,
                                    "getPriceChangeConfirmationIntent($apiVersion, $packageName, $sku, $type)"
                                )
                                val data = Bundle()
                                data.putInt("RESPONSE_CODE", 4)
                                return data
                            }

                            override fun getSkuDetailsV10(
                                apiVersion: Int,
                                packageName: String,
                                type: String,
                                skuBundle: Bundle,
                                extras: Bundle
                            ): Bundle {
                                Log.d(tag, "getSkuDetails($apiVersion, $packageName, $type)")
                                val data = Bundle()
                                data.putInt("RESPONSE_CODE", 0)
                                data.putStringArrayList("DETAILS_LIST", ArrayList())
                                return data
                            }

                            override fun acknowledgePurchase(
                                apiVersion: Int,
                                packageName: String,
                                purchaseToken: String,
                                extras: Bundle
                            ): Bundle {
                                Log.d(
                                    tag,
                                    "acknowledgePurchase($apiVersion, $packageName, $purchaseToken)"
                                )
                                val data = Bundle()
                                data.putInt("RESPONSE_CODE", 8)
                                return data
                            }

                            @Throws(RemoteException::class)
                            override fun onTransact(
                                code: Int,
                                data: Parcel,
                                reply: Parcel?,
                                flags: Int
                            ): Boolean {
                                if (super.onTransact(code, data, reply, flags)) return true
                                Log.d(
                                    tag,
                                    "onTransact [unknown]: $code, $data, $flags"
                                )
                                return false
                            }
                        }
                }
            }
        }

        class Core {

            fun initFlutter(application: Application) {
                App.FlutterApp().apply {
                    application.onCreate()
                }
            }

            /**
             * 加载动态颜色
             */

            fun initDynamicColors(application: Application) {
                val sharedPreferences =
                    PreferenceManager.getDefaultSharedPreferences(application)
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) {
                    if (sharedPreferences.getBoolean("dynamic_colors", true)) {
                        DynamicColors.applyToActivitiesIfAvailable(application)
                    }
                }
            }

            /**
             * 初始化DialogX
             */

            fun initDialogX(application: Application) {
                DialogX.init(application)
                DialogX.globalStyle = MaterialYouStyle()
                DialogX.globalTheme = DialogX.THEME.AUTO
                DialogX.autoShowInputKeyboard = true
                DialogX.onlyOnePopTip = false
                DialogX.cancelable = true
                DialogX.cancelableTipDialog = false
                DialogX.bottomDialogNavbarColor = android.graphics.Color.TRANSPARENT
                DialogX.autoRunOnUIThread = false
                DialogX.useHaptic = true
            }

            /**
             * 初始化任务栏
             */

            fun initTaskbar(application: Application) {
                val sharedPreferences =
                    PreferenceManager.getDefaultSharedPreferences(application)
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
                    if (sharedPreferences.getBoolean("desktop", true)) {
                        Taskbar.setEnabled(application, true)
                    } else {
                        Taskbar.setEnabled(application, false)
                    }
                } else {
                    Taskbar.setEnabled(application, false)
                }
            }

            fun launch(context: Context) {
                val intent = Intent(context, App.MainActivity().javaClass)
                intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
                context.startActivity(intent)
            }

            fun service(context: Context) {
                val intent = Intent(context, App.InAppBillingService().javaClass)
                context.startService(intent)
                context.bindService(intent, mConnection, Context.BIND_AUTO_CREATE)
            }

            private val mConnection: ServiceConnection = object : ServiceConnection {

                val context = AppManager.getInstance().activeActivity

                override fun onServiceConnected(
                    className: ComponentName,
                    service: IBinder
                ) {
                    val intentUi = Intent(context, App.Google().javaClass)
                    intentUi.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
                    context.startActivity(intentUi)
                }

                override fun onServiceDisconnected(arg0: ComponentName) {
                    val msg = "error"
                    try {
                        PopTip.show(msg)
                    } catch (e: Exception) {
                        Toast.makeText(context, msg, Toast.LENGTH_SHORT).show()
                    }
                }
            }
        }

        class Classes {

            /**
             * Flutter链接原生View
             */

            class LinkNativeViewFactory : PlatformViewFactory(StandardMessageCodec.INSTANCE) {
                override fun create(context: Context, viewId: Int, args: Any?): PlatformView {
                    return LinkNativeView(context)
                }
            }

            /**
             * 创建用于Flutter的原生View
             */

            class LinkNativeView(context: Context) : PlatformView {

                private var button: AppCompatButton = AppCompatButton(context)

                init {
                    button.text = "fuck"
                    button.setOnClickListener {
                        Toast.makeText(context, "oh ye", Toast.LENGTH_SHORT).show()
                    }
                }

                override fun getView(): AppCompatButton {
                    return button
                }

                override fun dispose() {

                }
            }

            /**
             * 导航栏配置
             */

            sealed class Screen(
                val route: String,
                val imageVector: ImageVector,
                @StringRes val resourceId: Int
            ) {

                object Navigation :
                    Screen(
                        Resources.Values.routeNavigation,
                        Icons.TwoTone.Navigation,
                        R.string.menu_navigation
                    )

                object Launcher :
                    Screen(
                        Resources.Values.routeLauncher,
                        Icons.TwoTone.RocketLaunch,
                        R.string.menu_launcher
                    )

                object Home :
                    Screen(
                        Resources.Values.routeHome,
                        Icons.TwoTone.Home,
                        R.string.menu_home
                    )

                object Dashboard :
                    Screen(
                        Resources.Values.routeDashboard,
                        Icons.TwoTone.Dashboard,
                        R.string.menu_dashboard
                    )

                object Settings :
                    Screen(
                        Resources.Values.routeSettings,
                        Icons.TwoTone.Settings,
                        R.string.menu_settings
                    )
            }

            /**
             * 选项配置
             */

            class Item(
                val name: String,
                val packageName: String,
                val className: String,
                val appIcon: Drawable
            )

            /**
             * 选项适配器
             */

            class ItemAdapter(
                context: Context?,
                private val layoutId:
                Int, list: List<Item?>?
            ) : ArrayAdapter<Item?>(
                context!!,
                layoutId,
                list!!
            ) {

                private var mode = "r2"
                private var uri = ""

                fun setMode(mode: String) {
                    this.mode = mode
                }

                fun setUri(uri: String) {
                    this.uri = uri
                }

                @SuppressLint("ViewHolder")
                override fun getView(position: Int, convertView: View?, parent: ViewGroup): View {
                    val item = getItem(position)
                    val view = LayoutInflater.from(context).inflate(layoutId, parent, false)
                    val packageName = item!!.packageName
                    (view.findViewById<AppCompatImageView>(R.id.item_img)).setImageDrawable(item.appIcon)
                    (view.findViewById<AppCompatTextView>(R.id.item_text)).text = item.name
                    (view.findViewById<AppCompatTextView>(R.id.item_packageName)).text = packageName
                    view.setOnClickListener {
                        val pm = context.packageManager
                        var intent = pm.getLaunchIntentForPackage(packageName)
                        if (intent != null) {
                            val editor =
                                PreferenceManager.getDefaultSharedPreferences(context).edit()
                            editor.putString("app", packageName)
                            editor.putString("label", item.name)
                            editor.putString("class", item.className)
                            editor.putString("uri", uri)
                            editor.putString("mode", mode)
                            editor.apply()
                            intent = Intent(context, App.FixedSettings::class.java)
                            context.startActivity(intent)
                        } else {
                            try {
                                PopTip.show(R.string.error_could_not_start)
                            } catch (e: Exception) {
                                Toast.makeText(
                                    context,
                                    R.string.error_could_not_start,
                                    Toast.LENGTH_SHORT
                                ).show()
                            }
                        }
                    }
                    return view
                }
            }


        }

        class Resources {

            class Values {

                // 应用主题配置
                @Composable
                fun AndrontainerTheme(
                    darkTheme: Boolean = isSystemInDarkTheme(),
                    dynamicColor: Boolean = true,
                    content: @Composable () -> Unit
                ) {
                    val colorScheme = when {
                        dynamicColor && Build.VERSION.SDK_INT >= Build.VERSION_CODES.S -> {
                            val context = LocalContext.current
                            if (darkTheme) dynamicDarkColorScheme(context) else dynamicLightColorScheme(
                                context
                            )
                        }
                        darkTheme -> DarkColorScheme
                        else -> LightColorScheme
                    }
                    val view = LocalView.current
                    if (!view.isInEditMode) {
                        SideEffect {
//                            (view.context as BaseActivity).window.statusBarColor =
//                                colorScheme.primary.toArgb()
//                            (view.context as BaseActivity).window.navigationBarColor =
//                                Color.Transparent.toArgb()
//                            @Suppress("DEPRECATION")
//                            ViewCompat.getWindowInsetsController(view)?.isAppearanceLightStatusBars =
//                                !darkTheme


                            val window = (view.context as Abstract.AndrontainerActivity).window
                            window.statusBarColor = colorScheme.primary.toArgb()
                            WindowCompat.getInsetsController(
                                window,
                                view
                            ).isAppearanceLightStatusBars = darkTheme
                        }
                    }

                    MaterialTheme(
                        colorScheme = colorScheme,
                        typography = Typography,
                        content = content
                    )
                }

                companion object {

                    //获取Flutter片段的ID
                    const val flutterId: Int = R.id.flutter_view

                    // 导航路由配置
                    const val routeNavigation: String = "navigation"
                    const val routeLauncher: String = "launcher"
                    const val routeHome: String = "home"
                    const val routeDashboard: String = "dashboard"
                    const val routeSettings: String = "settings"

                    // 颜色
                    private val Purple80 = Color(0xFFD0BCFF)
                    private val PurpleGrey80 = Color(0xFFCCC2DC)
                    private val Pink80 = Color(0xFFEFB8C8)
                    private val Purple40 = Color(0xFF6650a4)
                    private val PurpleGrey40 = Color(0xFF625b71)
                    private val Pink40 = Color(0xFF7D5260)

                    // 样式
                    private val Typography = Typography(
                        bodyLarge = TextStyle(
                            fontFamily = FontFamily.Default,
                            fontWeight = FontWeight.Normal,
                            fontSize = 16.sp,
                            lineHeight = 24.sp,
                            letterSpacing = 0.5.sp
                        )
                    )

                    // 深色主题
                    private val DarkColorScheme = darkColorScheme(
                        primary = Purple80,
                        secondary = PurpleGrey80,
                        tertiary = Pink80
                    )

                    // 浅色主题
                    private val LightColorScheme = lightColorScheme(
                        primary = Purple40,
                        secondary = PurpleGrey40,
                        tertiary = Pink40
                    )
                }
            }

            class Layout {

                class ViewSystem {

                    // MainActivity的根页面布局
                    @SuppressLint("InflateParams")
                    fun activityMainLayout(
                        viewContext: Context,
                        toolbar: MaterialToolbar,
                        logo: AppCompatImageView,
                        greeting: ComposeView
                    ): View {
                        return LinearLayoutCompat(
                            viewContext
                        ).apply {
                            orientation = LinearLayoutCompat.VERTICAL
                            fitsSystemWindows = true
                            addView(
                                AppBarLayout(
                                    viewContext
                                ).apply {
                                    fitsSystemWindows = true
                                    //viewContext.setTheme(com.google.android.material.R.style.ThemeOverlay_Material3_ActionBar)
                                    addView(
                                        toolbar,
                                        ViewGroup.LayoutParams(
                                            ViewGroup.LayoutParams.MATCH_PARENT,
                                            ViewGroup.LayoutParams.WRAP_CONTENT
                                        )
                                    )
                                },
                                ViewGroup.LayoutParams(
                                    ViewGroup.LayoutParams.MATCH_PARENT,
                                    ViewGroup.LayoutParams.WRAP_CONTENT
                                )
                            )
                            addView(
                                CoordinatorLayout(
                                    viewContext
                                ).apply {
                                    addView(
                                        greeting,
                                        ViewGroup.LayoutParams(
                                            ViewGroup.LayoutParams.MATCH_PARENT,
                                            ViewGroup.LayoutParams.MATCH_PARENT
                                        )
                                    )
                                    addView(
                                        logo,
                                        ViewGroup.LayoutParams(
                                            ViewGroup.LayoutParams.MATCH_PARENT,
                                            ViewGroup.LayoutParams.MATCH_PARENT
                                        )
                                    )
                                },
                                ViewGroup.LayoutParams(
                                    ViewGroup.LayoutParams.MATCH_PARENT,
                                    ViewGroup.LayoutParams.MATCH_PARENT
                                )
                            )
                        }
                    }

                    fun activitySelectOneLayout(context: Context): View {
                        return LinearLayoutCompat(context).apply {
                            orientation = LinearLayoutCompat.VERTICAL
                            addView(
                                AppBarLayout(context).apply {
                                    context.setTheme(com.google.android.material.R.style.ThemeOverlay_Material3_ActionBar)
                                    addView(
                                        MaterialToolbar(context).apply {
                                            title = ""
                                            navigationIcon = null
                                            setNavigationOnClickListener {

                                            }
                                        },
                                        ViewGroup.LayoutParams(
                                            ViewGroup.LayoutParams.MATCH_PARENT,
                                            ViewGroup.LayoutParams.WRAP_CONTENT
                                        )
                                    )
                                },
                                ViewGroup.LayoutParams(
                                    ViewGroup.LayoutParams.MATCH_PARENT,
                                    ViewGroup.LayoutParams.WRAP_CONTENT
                                )
                            )
                            addView(
                                ProgressBar(context).apply {

                                },
                                ViewGroup.LayoutParams(
                                    ViewGroup.LayoutParams.MATCH_PARENT,
                                    ViewGroup.LayoutParams.WRAP_CONTENT
                                )
                            )
                            addView(
                                ListView(context).apply {

                                },
                                ViewGroup.LayoutParams(
                                    ViewGroup.LayoutParams.MATCH_PARENT,
                                    ViewGroup.LayoutParams.MATCH_PARENT
                                )
                            )
                        }
                    }
                }

                class ComposeSystem {

                }
            }
        }
    }
}