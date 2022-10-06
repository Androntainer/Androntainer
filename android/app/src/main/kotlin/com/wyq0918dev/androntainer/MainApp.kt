/**
 * Androntainer Project.
 * Copyright (c) Androntainer Project
 */
package com.wyq0918dev.androntainer

import android.app.Application
import android.app.Service
import android.content.ComponentName
import android.content.Context
import android.content.Intent
import android.content.ServiceConnection
import android.os.*
import android.util.Log
import android.view.View.*
import android.view.ViewAnimationUtils
import android.view.ViewGroup.LayoutParams
import android.widget.ImageView
import android.widget.Toast
import androidx.annotation.StringRes
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.content.res.AppCompatResources
import androidx.appcompat.widget.AppCompatButton
import androidx.appcompat.widget.AppCompatImageView
import androidx.appcompat.widget.LinearLayoutCompat
import androidx.appcompat.widget.LinearLayoutCompat.HORIZONTAL
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.twotone.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.ComposeView
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.core.view.WindowCompat
import androidx.fragment.app.FragmentContainerView
import androidx.fragment.app.FragmentManager
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.rememberNavController
import androidx.preference.PreferenceFragmentCompat
import androidx.preference.PreferenceManager
import com.android.vending.billing.IInAppBillingService
import com.farmerbb.taskbar.lib.Taskbar
import com.google.android.material.color.DynamicColors
import com.google.android.material.composethemeadapter.MdcTheme
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
 * MainActivity
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
        val registry = flutterEngine.platformViewsController.registry
        registry.registerViewFactory("android_view",LinkNativeViewFactory())

        val messenger = flutterEngine.dartExecutor.binaryMessenger
        val channel = MethodChannel(messenger, "android")
        channel.setMethodCallHandler { call, res ->
            when (call.method) {
                "origin" -> {
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
        window.navigationBarColor = android.graphics.Color.TRANSPARENT
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
                AndrontainerTheme {
                    Layout()
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
        main.visibility = INVISIBLE
        main.postDelayed(this, 200)
    }


    @Composable
    private fun Layout() {
        MdcTheme(
            context = thisContext
        ) {
            ActivityMainLayout()
        }
    }

    @Preview
    @Composable
    private fun DefaultPreview() {
        AndrontainerTheme {
            Layout()
        }
    }


}

/**
 * FlutterActivity
 */

class FlutterActivity : FlutterActivity()

class Settings : PreferenceFragmentCompat() {

    companion object {
        fun newInstance() = Settings()
    }

    override fun onCreatePreferences(savedInstanceState: Bundle?, rootKey: String?) {
        setPreferencesFromResource(R.xml.root_preferences, rootKey)
    }
}

/**
 * 后台服务
 */

class InAppBillingService : Service() {

    private val tag = "FakeInAppStore"

    private val mInAppBillingService: IInAppBillingService.Stub =
        object : IInAppBillingService.Stub() {
            override fun isBillingSupported(
                apiVersion: Int,
                packageName: String,
                type: String
            ): Int {
                return isBillingSupportedV7(apiVersion, packageName, type, Bundle())
            }

            override fun getSkuDetails(
                apiVersion: Int,
                packageName: String,
                type: String,
                skusBundle: Bundle
            ): Bundle {
                return getSkuDetailsV10(apiVersion, packageName, type, skusBundle, Bundle())
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
                return getPurchasesV6(apiVersion, packageName, type, continuationToken, Bundle())
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
                return getPurchasesV9(apiVersion, packageName, type, continuationToken, extras)
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
                data.putStringArrayList("INAPP_DATA_SIGNATURE_LIST", ArrayList())
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
            override fun onTransact(code: Int, data: Parcel, reply: Parcel?, flags: Int): Boolean {
                if (super.onTransact(code, data, reply, flags)) return true
                Log.d(
                    tag,
                    "onTransact [unknown]: $code, $data, $flags"
                )
                return false
            }
        }

    override fun onBind(intent: Intent?): IBinder {
        return mInAppBillingService
    }
}

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
 * 页面配置
 */

sealed class Screen(val route: String, val imageVector: ImageVector, @StringRes val resourceId: Int) {
    object Navigation : Screen(navigation, Icons.TwoTone.Navigation, R.string.menu_home)
    object Launcher : Screen(launcher, Icons.TwoTone.Launch, R.string.menu_apps)
    object Flutter : Screen(flutter, Icons.TwoTone.FlutterDash, R.string.menu_home)
    object Dashboard : Screen(dashboard, Icons.TwoTone.Dashboard, R.string.menu_apps)
    object Settings : Screen(settings, Icons.TwoTone.Settings, R.string.menu_apps)
}

/**
 * 加载动态颜色
 */

fun initDynamicColors(application: Application){
    val sharedPreferences = PreferenceManager.getDefaultSharedPreferences(application)
    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) {
        if (sharedPreferences.getBoolean("dynamic_colors", true)){
            DynamicColors.applyToActivitiesIfAvailable(application)
        }
    }
}

/**
 * 初始化任务栏
 */

fun initLibTaskbar(application: Application){
    val sharedPreferences = PreferenceManager.getDefaultSharedPreferences(application)
    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q){
        if (sharedPreferences.getBoolean("desktop", true)){
            Taskbar.setEnabled(application, true)
        } else {
            Taskbar.setEnabled(application, false)
        }
    } else {
        Taskbar.setEnabled(application, false)
    }
}

/**
 * 获取Flutter片段的ID
 */

const val flutterId: Int = R.id.flutter_view

/**
 * 页面ID
 */

const val navigation: String = "navigation"
const val launcher: String = "launcher"
const val flutter: String = "flutter"
const val dashboard: String = "apps"
const val settings: String = "settings"

/**
 * 颜色
 */

val Purple200 = Color(0xFFBB86FC)
val Purple500 = Color(0xFF6200EE)
val Purple700 = Color(0xFF3700B3)
val Teal200 = Color(0xFF03DAC5)

/**
 * 形状
 */

val Shapes = Shapes(
    small = RoundedCornerShape(4.dp),
    medium = RoundedCornerShape(4.dp),
    large = RoundedCornerShape(0.dp)
)

/**
 * 排版
 */

val Typography = Typography(
    body1 = TextStyle(
        fontFamily = FontFamily.Default,
        fontWeight = FontWeight.Normal,
        fontSize = 16.sp
    )
)

/**
 * 深色主题
 */

private val DarkColorPalette = darkColors(
    primary = Purple200,
    primaryVariant = Purple700,
    secondary = Teal200
)

/**
 * 浅色主题
 */

private val LightColorPalette = lightColors(
    primary = Purple500,
    primaryVariant = Purple700,
    secondary = Teal200
)

/**
 * 应用主题配置
 */

@Composable
fun AndrontainerTheme(
    darkTheme: Boolean = isSystemInDarkTheme(),
    content: @Composable () -> Unit
) {
    val colors = if (darkTheme) {
        DarkColorPalette
    } else {
        LightColorPalette
    }

    MaterialTheme(
        colors = colors,
        typography = Typography,
        shapes = Shapes,
        content = content
    )
}

/**
 * MainActivity布局
 */

@Composable
fun ActivityMainLayout() {
    val scaffoldState = rememberScaffoldState()
    Scaffold(
        modifier = Modifier.fillMaxWidth(),
        scaffoldState = scaffoldState,
        topBar = {
            TopAppBar(
                title = {
                    Spacer(
                        Modifier.size(
                            AppBarDefaults.TopAppBarElevation
                        )
                    )
                },
                modifier = Modifier.fillMaxWidth(),
                navigationIcon = {
                    IconButton(
                        onClick = {

                        }
                    ) {
                        Icon(
                            imageVector = Icons.TwoTone.Home,
                            contentDescription = null
                        )
                    }
                },
                elevation = AppBarDefaults.TopAppBarElevation
            )
        },
        floatingActionButton = {
            FloatingActionButton(
                onClick = {

                }
            ) {
                Icon(
                    imageVector = Icons.TwoTone.Science,
                    contentDescription = null
                )
            }
        },
        floatingActionButtonPosition = FabPosition.Center
    ) { innerPadding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
        ) {

        }

    }


}

//@Composable
//private fun WidgetSettings(modifier: Modifier) {
//    AndroidView(
//        factory = { context ->
//            FragmentContainerView(
//                context
//            ).apply {
//                id = settingsId
//            }
//        },
//        modifier = modifier
//    )
//}

/**
 * MainActivity布局预览
 */
@Preview
@Composable
fun ActivityMainLayoutPreview() {
    AndrontainerTheme {
        ActivityMainLayout()
    }
}