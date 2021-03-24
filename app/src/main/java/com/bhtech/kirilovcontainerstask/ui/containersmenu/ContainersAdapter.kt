package com.bhtech.kirilovcontainerstask.ui.containersmenu

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.bhtech.kirilovcontainerstask.databinding.ItemContainerBinding
import com.bhtech.kirilovcontainerstask.service.containers.model.Container

class ContainersAdapter : ListAdapter<Container, ContainersAdapter.ContainerViewHolder>(Companion) {

    class ContainerViewHolder(val binding: ItemContainerBinding) : RecyclerView.ViewHolder(binding.root)

    companion object: DiffUtil.ItemCallback<Container>() {
        override fun areItemsTheSame(old: Container, new: Container) = old === new
        override fun areContentsTheSame(old: Container, new: Container) = old == new
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ContainerViewHolder {
        val layoutInflater = LayoutInflater.from(parent.context)
        val binding = ItemContainerBinding.inflate(layoutInflater)

        return ContainerViewHolder(binding)
    }

    override fun onBindViewHolder(holder: ContainerViewHolder, position: Int) {
        val currentContainer = getItem(position)
        holder.binding.container = currentContainer
        holder.binding.executePendingBindings()
    }
}
