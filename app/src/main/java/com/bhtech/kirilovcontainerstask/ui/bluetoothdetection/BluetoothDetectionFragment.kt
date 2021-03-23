package com.bhtech.kirilovcontainerstask.ui.bluetoothdetection

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import com.bhtech.kirilovcontainerstask.databinding.FragmentBluetoothDetectionBinding
import com.bhtech.kirilovcontainerstask.screennavigator.ScreenNavigator
import dagger.hilt.android.AndroidEntryPoint
import javax.inject.Inject

@AndroidEntryPoint
class BluetoothDetectionFragment : Fragment() {

    private val viewModel: BluetoothDetectionViewModel by viewModels()
    @Inject lateinit var navigator: ScreenNavigator

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View {
        val binding = FragmentBluetoothDetectionBinding.inflate(inflater, container, false)
        return binding.root
    }

}