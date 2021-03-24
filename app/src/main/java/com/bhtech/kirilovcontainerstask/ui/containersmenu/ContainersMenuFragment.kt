package com.bhtech.kirilovcontainerstask.ui.containersmenu

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.recyclerview.widget.DividerItemDecoration
import androidx.recyclerview.widget.RecyclerView
import com.bhtech.kirilovcontainerstask.R
import com.bhtech.kirilovcontainerstask.databinding.FragmentContainersMenuBinding
import com.bhtech.kirilovcontainerstask.ui.util.MarginItemDecoration
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class ContainersMenuFragment : Fragment() {

    private val viewModel: ContainersMenuViewModel by viewModels()

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        val binding = FragmentContainersMenuBinding.inflate(inflater, container, false)
        setUpContainersRecyclerView(binding.rvContainers)
        return binding.root
    }

    private fun setUpContainersRecyclerView(recyclerView: RecyclerView) {
        val margin = resources.getDimension(R.dimen.screen_edge_margin).toInt()
        recyclerView.addItemDecoration(DividerItemDecoration(context, DividerItemDecoration.VERTICAL))

        recyclerView.adapter = ContainersAdapter().also { it.submitList(viewModel.getContainers()) }
    }
}
