package com.bhtech.kirilovcontainerstask.ui.bluetoothdetection

import android.app.Activity
import android.bluetooth.le.ScanResult
import android.companion.AssociationRequest
import android.companion.BluetoothLeDeviceFilter
import android.companion.CompanionDeviceManager
import android.content.Context.COMPANION_DEVICE_SERVICE
import android.content.Intent
import android.content.IntentSender
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
    private lateinit var binding: FragmentBluetoothDetectionBinding

    private val companionCallback = object : CompanionDeviceManager.Callback() {
        override fun onDeviceFound(chooserLauncher: IntentSender) {
            startIntentSenderForResult(
                chooserLauncher,
                0, null, 0, 0, 0, null
            )
        }

        override fun onFailure(error: CharSequence?) {
            // Handle the failure.
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

    /** @deprecated use
     * {@link #registerForActivityResult(ActivityResultContract, ActivityResultCallback)}
     * with the appropriate {@link ActivityResultContract} and handling the result in the
     * {@link ActivityResultCallback#onActivityResult(Object) callback}.
     */
//    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
//        super.onActivityResult(requestCode, resultCode, data)
//    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        when (requestCode) {
            0 -> when (resultCode) {
                Activity.RESULT_OK -> {
                    val scanResult = data?.getParcelableExtra(CompanionDeviceManager.EXTRA_DEVICE) as ScanResult?
                    binding.tvBluetoothDetectedDevices.text = scanResult.toString()
                }
            }
            else -> super.onActivityResult(requestCode, resultCode, data)
        }
    }
}