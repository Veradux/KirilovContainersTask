package com.bhtech.kirilovcontainerstask.ui.mainmenu

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import com.bhtech.kirilovcontainerstask.databinding.FragmentMainMenuBinding
import com.bhtech.kirilovcontainerstask.screennavigator.ScreenNavigator
import com.bhtech.kirilovcontainerstask.screennavigator.ScreenNavigator.Screen
import dagger.hilt.android.AndroidEntryPoint
import javax.inject.Inject

@AndroidEntryPoint
class MainMenuFragment : Fragment() {

    @Inject lateinit var navigator: ScreenNavigator

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View {
        val binding = FragmentMainMenuBinding.inflate(inflater, container, false)
        setButtonListeners(binding)
        return binding.root
    }

    private fun setButtonListeners(binding: FragmentMainMenuBinding) {
        binding.btnMainMenuBluetooth.setOnClickListener { _ -> navigator.navigateTo(Screen.BLUETOOTH) }
        binding.btnMainMenuMap.setOnClickListener { _ -> navigator.navigateTo(Screen.MAP) }
        binding.btnMainMenuDisconnect.setOnClickListener { _ -> navigator.navigateTo(Screen.LOGIN) }
    }
}