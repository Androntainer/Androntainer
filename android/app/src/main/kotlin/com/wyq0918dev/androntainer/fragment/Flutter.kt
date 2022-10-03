package com.wyq0918dev.androntainer.fragment

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.ViewGroup.*
import androidx.appcompat.widget.AppCompatButton
import androidx.appcompat.widget.AppCompatTextView
import androidx.appcompat.widget.LinearLayoutCompat
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentContainerView
import androidx.fragment.app.FragmentManager
import com.wyq0918dev.androntainer.ui.values.flutterId
import io.flutter.embedding.android.FlutterFragment


class Flutter : Fragment() {

    private val tagFlutterFragment = "flutter_fragment"

    companion object {
        lateinit var fragmentContainerView: FragmentContainerView
        var flutterFragment: FlutterFragment? = null
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): ConstraintLayout {
        super.onCreateView(inflater, container, savedInstanceState)
        fragmentContainerView = FragmentContainerView(
            requireContext()
        ).apply {
            id = flutterId
        }
        return ConstraintLayout(
            requireContext()
        ).apply {
            addView(
                LinearLayoutCompat(
                    requireContext()
                ).apply {
                    addView(
                        AppCompatButton(
                            requireContext()
                        ).apply {
                            orientation = LinearLayoutCompat.VERTICAL
                            text = "显示Flutter"
                            setOnClickListener {
                                if (fragmentContainerView.visibility == INVISIBLE) {
                                    fragmentContainerView.visibility = VISIBLE
                                } else {
                                    fragmentContainerView.visibility = INVISIBLE
                                }
                            }
                        },
                        LayoutParams(
                            LayoutParams.MATCH_PARENT,
                            LayoutParams.WRAP_CONTENT
                        )
                    )
                    addView(
                        AppCompatTextView(
                            requireContext()
                        ).apply {
                            text =
                                "由于FlutterFragment特性，Flutter界面会显示在最顶层而不是当前Fragment。因此会遮挡内容。为了操作其他界面已将Flutter界面隐藏，点击按钮显示。"
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
                fragmentContainerView,
                LayoutParams(
                    LayoutParams.MATCH_PARENT,
                    LayoutParams.MATCH_PARENT
                )
            )
        }
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