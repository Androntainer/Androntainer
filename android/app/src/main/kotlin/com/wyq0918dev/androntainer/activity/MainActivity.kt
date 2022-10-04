package com.wyq0918dev.androntainer.activity

import android.annotation.SuppressLint
import android.content.ComponentName
import android.content.Context
import android.content.Intent
import android.content.ServiceConnection
import android.os.Bundle
import android.os.IBinder
import android.view.Menu
import android.view.View
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.AppCompatImageButton
import androidx.compose.runtime.Composable
import androidx.compose.ui.tooling.preview.Preview
import androidx.core.view.WindowCompat
import androidx.drawerlayout.widget.DrawerLayout
import androidx.navigation.NavController
import androidx.navigation.fragment.NavHostFragment
import androidx.navigation.ui.AppBarConfiguration
import androidx.navigation.ui.navigateUp
import androidx.navigation.ui.setupActionBarWithNavController
import androidx.navigation.ui.setupWithNavController
import com.google.android.material.appbar.MaterialToolbar
import com.google.android.material.navigation.NavigationView
import com.kongzue.dialogx.dialogs.BottomMenu
import com.kongzue.dialogx.dialogs.TipDialog
import com.kongzue.dialogx.dialogs.WaitDialog
import com.wyq0918dev.androntainer.R
import com.wyq0918dev.androntainer.core.Compose
import com.wyq0918dev.androntainer.core.utils.OnDoubleClickListener
import com.wyq0918dev.androntainer.databinding.ActivityMainBinding
import com.wyq0918dev.androntainer.fragment.Flutter.Companion.flutterFragment
import com.wyq0918dev.androntainer.fragment.Flutter.Companion.fragmentContainerView
import com.wyq0918dev.androntainer.service.InAppBillingService
import com.wyq0918dev.androntainer.ui.theme.AndrontainerTheme
import com.wyq0918dev.androntainer.ui.values.navCompose
import com.wyq0918dev.androntainer.ui.values.navFlutter
import com.wyq0918dev.androntainer.ui.values.navSettings
import io.flutter.embedding.android.FlutterEngineConfigurator
import io.flutter.embedding.engine.FlutterEngine
import io.flutter.plugin.common.MethodChannel

class MainActivity : AppCompatActivity(), FlutterEngineConfigurator, Runnable {

    private lateinit var thisContext: Context
    private lateinit var appBarConfiguration: AppBarConfiguration
    private lateinit var navController: NavController
    private lateinit var binding: ActivityMainBinding
    private lateinit var toolbar: MaterialToolbar

    companion object {
        lateinit var drawerLayout: DrawerLayout

        @Composable
        fun ComposeLayout() {
            Compose()
        }
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
        val origin = "origin"

        channel.setMethodCallHandler { call, res ->
            when (call.method) {
                origin -> {
                    fragmentContainerView.visibility = View.INVISIBLE
                    res.success(origin + "Success!")
                    TipDialog.show(origin + "Success!", WaitDialog.TYPE.SUCCESS)
                }
                else -> {
                    res.error(
                        "error_code",
                        origin + "error_message",
                        null
                    )
                    TipDialog.show(origin + "Error!", WaitDialog.TYPE.ERROR)
                }
            }
        }
    }

    override fun cleanUpFlutterEngine(flutterEngine: FlutterEngine) {

    }

    override fun onCreateOptionsMenu(menu: Menu): Boolean {
        // Inflate the menu; this adds items to the action bar if it is present.
        menuInflater.inflate(R.menu.main, menu)
        return true
    }

    override fun onSupportNavigateUp(): Boolean {
        return navController.navigateUp(appBarConfiguration) || super.onSupportNavigateUp()
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
    }

    @Preview
    @Composable
    private fun DefaultPreview() {
        AndrontainerTheme {
            ComposeLayout()
        }
    }

    private fun init() {
        initContext()
        initServices()
        initSystemBar()
        initUi()
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
        WindowCompat.setDecorFitsSystemWindows(window, true)
        val option = View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN or View.SYSTEM_UI_FLAG_LAYOUT_STABLE
        val decorView = window.decorView
        val visibility: Int = decorView.systemUiVisibility
        decorView.systemUiVisibility = visibility or option
    }

    private fun initUi() {
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)
        toolbar = binding.toolbar
        setSupportActionBar(toolbar)
        initView()
    }

    @SuppressLint("ClickableViewAccessibility")
    private fun initView() {
        val navView: NavigationView = binding.navView
        val fragmentId = binding.navHostFragmentContentMain.id
        val navHostFragment =
            (supportFragmentManager.findFragmentById(fragmentId) as NavHostFragment?)!!
        val home: AppCompatImageButton = binding.navigationHome

        drawerLayout = binding.drawerLayout
        navController = navHostFragment.navController
        // Passing each menu ID as a set of Ids because each
        // menu should be considered as top level destinations.
        appBarConfiguration = AppBarConfiguration(
            setOf(
                navFlutter, navCompose, navSettings
            ), drawerLayout
        )
        setupActionBarWithNavController(navController, appBarConfiguration)
        navView.setupWithNavController(navController)


        home.setOnTouchListener(
            OnDoubleClickListener(
                object : OnDoubleClickListener.MyClickCallBack {
                    override fun onClick() {
                        if (!drawerLayout.isOpen) {
                            navController.navigate(navFlutter)
                        } else {
                            drawerLayout.close()
                        }
                    }

                    override fun OnDoubleClick() {
                        if (!drawerLayout.isOpen) {
                            BottomMenu.show(arrayOf("新标签页中打开", "稍后阅读", "复制链接网址"))
                                .setMessage("操作菜单")
                                .setOnMenuItemClickListener { dialog, text, index ->
                                    when(index){
                                        0 ->{

                                        }
                                        1 ->{

                                        }

                                    }
                                    false
                                }
                        } else {
                            drawerLayout.close()
                        }
                    }
                }
            )
        )
    }


    private fun optionsMenu() {
        toolbar.showOverflowMenu()
    }

    private fun toggleFlutter() {
        if (fragmentContainerView.visibility == View.INVISIBLE) {
            showFlutter()
        } else {
            hideFlutter()
        }
    }

    private fun hideFlutter() {
        fragmentContainerView.visibility = View.INVISIBLE
    }

    private fun showFlutter() {
        fragmentContainerView.visibility = View.VISIBLE
    }

}