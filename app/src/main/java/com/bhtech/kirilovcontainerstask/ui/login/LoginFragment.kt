package com.bhtech.kirilovcontainerstask.ui.login

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.inputmethod.EditorInfo
import androidx.fragment.app.Fragment
import com.bhtech.kirilovcontainerstask.databinding.FragmentLoginBinding
import com.bhtech.kirilovcontainerstask.screennavigator.ScreenNavigator
import dagger.hilt.android.AndroidEntryPoint
import javax.inject.Inject

@AndroidEntryPoint
class LoginFragment : Fragment() {

    @Inject lateinit var navigator: ScreenNavigator

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View {
        val binding = FragmentLoginBinding.inflate(inflater, container, false)
        setUsernameListener(binding)
        return binding.root
    }

    private fun setUsernameListener(binding: FragmentLoginBinding) {
        binding.etUsername.setOnEditorActionListener { textView, action, _ ->
            if (action == EditorInfo.IME_ACTION_DONE && textView.text.isNotBlank()) {
                // TODO save the username to shared prefs, so that it can be used in the BLE screen.
                navigator.navigateTo(ScreenNavigator.Screen.MAIN)
            }
            false
        }
    }
}