package com.wyq0918dev.androntainer.fragment

import android.os.Bundle
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentContainerView
import com.wyq0918dev.androntainer.ui.values.flutterId

class Flutter : Fragment() {

    companion object{
        lateinit var fragmentContainerView: FragmentContainerView
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        fragmentContainerView = FragmentContainerView(
            requireContext()
        ).apply {
            id = flutterId
        }
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): FragmentContainerView {
        super.onCreateView(inflater, container, savedInstanceState)
        return fragmentContainerView
    }
}