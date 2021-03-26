package com.bhtech.kirilovcontainerstask.ui.containersmenu

import android.content.Context
import android.graphics.Color
import android.graphics.PorterDuff
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.bhtech.kirilovcontainerstask.R
import com.bhtech.kirilovcontainerstask.databinding.ItemContainerBinding
import com.bhtech.kirilovcontainerstask.service.containers.model.Container
import com.bhtech.kirilovcontainerstask.ui.cartography.FILLING_LEVEL_GREEN_UPPER_BOUND
import com.bhtech.kirilovcontainerstask.ui.cartography.FILLING_LEVEL_YELLOW_UPPER_BOUND

class ContainersAdapter(private val listener: (Container) -> Unit) :
    ListAdapter<Container, ContainersAdapter.ContainerViewHolder>(Companion) {

    class ContainerViewHolder(val binding: ItemContainerBinding) : RecyclerView.ViewHolder(binding.root)

    companion object : DiffUtil.ItemCallback<Container>() {
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
        holder.itemView.setOnClickListener { listener(currentContainer) }
        with(holder.binding.itemContainerFillingLevel) {
            val color = Color.parseColor(getColorBy(context, currentContainer.fillingLevel))
            background.setColorFilter(color, PorterDuff.Mode.SRC_ATOP)
        }
    }

    private fun getColorBy(context: Context, fillingLevel: Int): String = when (fillingLevel) {
        in 0..FILLING_LEVEL_GREEN_UPPER_BOUND -> getHexValueFrom(context, R.color.fillingLevelGreen)
        in FILLING_LEVEL_GREEN_UPPER_BOUND..FILLING_LEVEL_YELLOW_UPPER_BOUND -> getHexValueFrom(
            context,
            R.color.fillingLevelYellow
        )
        else -> getHexValueFrom(context, R.color.fillingLevelRed)
    }

    private fun getHexValueFrom(context: Context, colorId: Int): String =
        String.format("#%06x", ContextCompat.getColor(context, colorId) and 0xffffff)
}
