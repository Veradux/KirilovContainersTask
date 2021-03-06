package com.bhtech.kirilovcontainerstask.ui.login

import android.content.Context
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.inputmethod.EditorInfo
import android.widget.TextView
import androidx.fragment.app.Fragment
import com.bhtech.kirilovcontainerstask.R
import com.bhtech.kirilovcontainerstask.databinding.FragmentLoginBinding
import com.bhtech.kirilovcontainerstask.screennavigator.ScreenNavigator.Screen
import com.bhtech.kirilovcontainerstask.ui.navigateTo
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class LoginFragment : Fragment() {

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View {
        val binding = FragmentLoginBinding.inflate(inflater, container, false)
        configureViews(binding)
        return binding.root
    }

    private fun configureViews(binding: FragmentLoginBinding) {
        setUsernameListener(binding)
        binding.btnLogin.setOnClickListener { onLoginListener(binding.etUsername) }
        binding.etUsername.setText(getSavedUsernameFromPrefs())
    }

    private fun setUsernameListener(binding: FragmentLoginBinding) {
        binding.etUsername.setOnEditorActionListener { textView, action, _ ->
            if (action == EditorInfo.IME_ACTION_DONE) {
                onLoginListener(textView)
            }
            false
        }
    }

    private fun onLoginListener(textView: TextView) {
        if (textView.text.isNotBlank()) {
            activity?.getPreferences(Context.MODE_PRIVATE)
                ?.edit()
                ?.putString(getString(R.string.username_pref_key), textView.text.toString())
                ?.apply()

            navigateTo(Screen.MAIN)
        }
    }

    private fun getSavedUsernameFromPrefs(): String = activity?.getPreferences(Context.MODE_PRIVATE)
        ?.getString(getString(R.string.username_pref_key), "") ?: ""
}