package com.bhtech.kirilovcontainerstask.ui.bluetoothdetection

import android.app.Activity
import android.bluetooth.le.ScanResult
import android.companion.AssociationRequest
import android.companion.BluetoothLeDeviceFilter
import android.companion.CompanionDeviceManager
import android.content.Context.COMPANION_DEVICE_SERVICE
import android.content.IntentSender
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.activity.result.IntentSenderRequest
import androidx.activity.result.contract.ActivityResultContracts.StartIntentSenderForResult
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
    private lateinit var binding: FragmentBluetoothDetectionBinding

    private val resultLauncher = registerForActivityResult(StartIntentSenderForResult()) { result ->
        if (result.resultCode == Activity.RESULT_OK) {
            val scanResult = result.data?.getParcelableExtra(CompanionDeviceManager.EXTRA_DEVICE) as ScanResult?
            binding.tvBluetoothDetectedDevices.text = scanResult.toString()
        }
    }

    private val companionCallback = object : CompanionDeviceManager.Callback() {
        override fun onDeviceFound(chooserLauncher: IntentSender) {
            val intentSenderRequest = IntentSenderRequest.Builder(chooserLauncher).build()
            resultLauncher.launch(intentSenderRequest)
        }

        override fun onFailure(error: CharSequence?) {
            // TODO Handle the failure.
        }
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View {
        binding = FragmentBluetoothDetectionBinding.inflate(inflater, container, false)
        configureViews(binding)
        return binding.root
    }

    private fun configureViews(binding: FragmentBluetoothDetectionBinding) {
        binding.btnBluetoothDisplayList.setOnClickListener { detectDevices() }
    }

    private fun detectDevices() {
        val leDeviceFilter = BluetoothLeDeviceFilter.Builder().build()

        val pairingRequest = AssociationRequest.Builder()
            .addDeviceFilter(leDeviceFilter)
            .setSingleDevice(false)
            .build()

        val deviceManager = requireContext().getSystemService(COMPANION_DEVICE_SERVICE) as CompanionDeviceManager
        deviceManager.associate(pairingRequest, companionCallback, null)
    }
}