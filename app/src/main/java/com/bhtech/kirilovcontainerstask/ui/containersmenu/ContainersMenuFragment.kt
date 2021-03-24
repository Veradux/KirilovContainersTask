package com.bhtech.kirilovcontainerstask.ui.containersmenu

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.Observer
import androidx.recyclerview.widget.DividerItemDecoration
import androidx.recyclerview.widget.RecyclerView
import com.bhtech.kirilovcontainerstask.databinding.FragmentContainersMenuBinding
import com.bhtech.kirilovcontainerstask.service.containers.model.Container
import com.bhtech.kirilovcontainerstask.ui.containersmenu.ContainersMenuViewModel.ContainersState
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class ContainersMenuFragment : Fragment() {

    private val viewModel: ContainersMenuViewModel by viewModels()
    private lateinit var binding: FragmentContainersMenuBinding

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        binding = FragmentContainersMenuBinding.inflate(inflater, container, false)
        setUpContainersRecyclerView(binding.rvContainers)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        viewModel.containersState.observe(viewLifecycleOwner, Observer { onContainersStateChange(it) })
        viewModel.loadContainers()
    }

    private fun setUpContainersRecyclerView(recyclerView: RecyclerView) {
        recyclerView.addItemDecoration(DividerItemDecoration(context, DividerItemDecoration.VERTICAL))
    }

    private fun onContainersStateChange(containersState: ContainersState) {
        when (containersState) {
            is ContainersState.Loading -> showContainersLoading()
            is ContainersState.Empty -> showEmptyState()
            is ContainersState.Loaded -> showContainers(containersState.containers)
        }
    }

    private fun showContainers(containers: List<Container>) {
        binding.pbContainersLoadingSpinner.visibility = View.GONE
        val adapter = ContainersAdapter()
        binding.rvContainers.adapter = adapter
        adapter.submitList(containers)
    }

    private fun showContainersLoading() {
        binding.pbContainersLoadingSpinner.visibility = View.VISIBLE
    }

    private fun showEmptyState() {
        binding.pbContainersLoadingSpinner.visibility = View.GONE
        binding.tvNoContainersLoadedMessage.visibility = View.VISIBLE
    }
}
