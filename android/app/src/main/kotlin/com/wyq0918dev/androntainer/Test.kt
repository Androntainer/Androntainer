package com.wyq0918dev.androntainer

import android.annotation.SuppressLint
import android.content.Context
import android.graphics.Color
import android.view.LayoutInflater
import android.view.View
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.content.res.AppCompatResources
import androidx.appcompat.widget.Toolbar
import com.google.android.material.floatingactionbutton.FloatingActionButton
import io.flutter.embedding.engine.FlutterEngine
import io.flutter.plugin.common.BinaryMessenger
import io.flutter.plugin.common.StandardMessageCodec
import io.flutter.plugin.platform.PlatformView
import io.flutter.plugin.platform.PlatformViewFactory
import io.flutter.plugins.GeneratedPluginRegistrant

class LinkNativeView(context: Context) : PlatformView {

    private var showTextView: FloatingActionButton = FloatingActionButton(context)

    init {
        showTextView.setImageDrawable(AppCompatResources.getDrawable(context, R.drawable.ic_baseline_add_24))
        showTextView.setOnClickListener {
            Toast.makeText(context, "oh ye", Toast.LENGTH_SHORT).show()
        }
    }

    override fun getView(): View {
        return showTextView
    }

    override fun dispose() {

    }
}

class LinkNativeViewFactory : PlatformViewFactory(StandardMessageCodec.INSTANCE) {
    override fun create(context: Context, viewId: Int, args: Any?): PlatformView {
        return LinkNativeView(context)
    }
}

class TestActivity: io.flutter.embedding.android.FlutterActivity() {

    override fun configureFlutterEngine(flutterEngine: FlutterEngine) {
        super.configureFlutterEngine(flutterEngine)
        GeneratedPluginRegistrant.registerWith(flutterEngine)
        val registry = flutterEngine.platformViewsController.registry
        registry.registerViewFactory("android_show_view",LinkNativeViewFactory())
    }
}