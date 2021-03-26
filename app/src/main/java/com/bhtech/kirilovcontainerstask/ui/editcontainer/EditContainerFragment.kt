package com.bhtech.kirilovcontainerstask.ui.editcontainer

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import com.bhtech.kirilovcontainerstask.databinding.FragmentEditContainerBinding
import com.bhtech.kirilovcontainerstask.ui.containersmenu.ContainersMenuViewModel
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class EditContainerFragment : Fragment() {

    private val viewModel: ContainersMenuViewModel by activityViewModels()

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View {
        val binding = FragmentEditContainerBinding.inflate(inflater, container, false)
        return binding.root
    }
}