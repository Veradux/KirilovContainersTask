package com.bhtech.kirilovcontainerstask.ui.login

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import com.bhtech.kirilovcontainerstask.databinding.LoginFragmentBinding

class LoginFragment : Fragment() {

    private val viewModel = LoginViewModel()
    private lateinit var binding: LoginFragmentBinding

    companion object {
        fun newInstance() = LoginFragment()
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View {
        binding = LoginFragmentBinding.inflate(inflater, container, false)

        return binding.root
    }
}