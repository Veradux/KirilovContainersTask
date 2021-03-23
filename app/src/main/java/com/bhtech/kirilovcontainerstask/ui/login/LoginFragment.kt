package com.bhtech.kirilovcontainerstask.ui.login

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import com.bhtech.kirilovcontainerstask.databinding.FragmentLoginBinding
import com.bhtech.kirilovcontainerstask.screennavigator.ScreenNavigator
import dagger.hilt.android.AndroidEntryPoint
import javax.inject.Inject

@AndroidEntryPoint
class LoginFragment : Fragment() {

    //private val viewModel: LoginViewModel by viewModels()
    private lateinit var binding: FragmentLoginBinding

    @Inject lateinit var navigator: ScreenNavigator

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View {
        binding = FragmentLoginBinding.inflate(inflater, container, false)
        return binding.root
    }
}