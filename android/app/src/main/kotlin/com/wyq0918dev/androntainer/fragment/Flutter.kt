package com.wyq0918dev.androntainer.fragment

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentContainerView
import androidx.fragment.app.FragmentManager
import com.wyq0918dev.androntainer.ui.values.flutterId
import io.flutter.embedding.android.FlutterFragment


class Flutter : Fragment() {

    private val tagFlutterFragment = "flutter_fragment"

    companion object{
        lateinit var fragmentContainerView: FragmentContainerView
        var flutterFragment: FlutterFragment? = null
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): FragmentContainerView {
        super.onCreateView(inflater, container, savedInstanceState)
        fragmentContainerView = FragmentContainerView(
            requireContext()
        ).apply {
            id = flutterId
        }
        return fragmentContainerView
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
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
                    fragmentContainerView.id,
                    flutterFragment!!,
                    tagFlutterFragment
                )
                .commit()
        }
    }

    override fun onDestroy() {
        super.onDestroy()
        flutterFragment = null
    }
}