package com.bhtech.kirilovcontainerstask.ui.editcontainer

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import com.bhtech.kirilovcontainerstask.BR
import com.bhtech.kirilovcontainerstask.databinding.FragmentEditContainerBinding
import com.bhtech.kirilovcontainerstask.service.containers.model.Container
import com.bhtech.kirilovcontainerstask.service.containers.model.Gps
import com.bhtech.kirilovcontainerstask.ui.base.ContainersMenuViewModel
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class EditContainerFragment : Fragment() {

    private val viewModel: ContainersMenuViewModel by activityViewModels()
    private lateinit var binding: FragmentEditContainerBinding

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View {
        binding = FragmentEditContainerBinding.inflate(inflater, container, false)
        binding.setVariable(BR.selectedContainer, viewModel.selectedContainer)
        return binding.root
    }

    override fun onPause() {
        super.onPause()
        updateContainersWithChanges()
    }

    private fun updateContainersWithChanges() {
        viewModel.selectedContainer?.let { editedContainer ->
            val newContainer = getNewContainerFrom(editedContainer)
            viewModel.setStateWithReplacedContainer(newContainer, editedContainer)
            viewModel.selectedContainer = newContainer
        }
    }

    private fun getNewContainerFrom(editedContainer: Container) = Container(
        name = binding.edEditName.text.toString(),
        street = editedContainer.street,
        city = editedContainer.city,
        zipcode = editedContainer.zipcode,
        gps = Gps(
            binding.edEditLatitude.text.toString().toDouble(),
            binding.edEditLongitude.text.toString().toDouble()
        ),
        wasteType = binding.edEditWasteType.text.toString(),
        fillingLevel = binding.edEditFillingLevel.text.toString().toInt()
    )
}