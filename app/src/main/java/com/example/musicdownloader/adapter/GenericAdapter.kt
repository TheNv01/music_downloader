package com.example.musicdownloader.adapter

import android.annotation.SuppressLint
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.annotation.LayoutRes
import androidx.databinding.DataBindingUtil
import androidx.databinding.ViewDataBinding
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.RecyclerView
import com.example.musicdownloader.interfaces.itemclickinterface.ItemClickListener
import com.example.musicdownloader.interfaces.recyclerbindinginterface.RecyclerBindingInterface
import com.example.musicdownloader.manager.MusicManager
import com.example.musicdownloader.model.Music

class GenericAdapter<T: Any, V: ViewDataBinding>(
    @LayoutRes val layoutID: Int,
    private val bindingInterface: RecyclerBindingInterface<V, T>,
    private val itemListener: ItemClickListener<T>
):
    androidx.recyclerview.widget.ListAdapter<T, GenericAdapter.GenericViewHolder>(GenericDiffCallback()) {

    class GenericDiffCallback<T : Any> : DiffUtil.ItemCallback<T>() {
        override fun areItemsTheSame(oldItem: T, newItem: T): Boolean {
            return oldItem.toString() == newItem.toString()
        }

        @SuppressLint("DiffUtilEquals")
        override fun areContentsTheSame(oldItem: T, newItem: T): Boolean {
            return oldItem == newItem
        }
    }

    override fun onCreateViewHolder(
        parent: ViewGroup,
        viewType: Int
    ): GenericViewHolder {

        val layoutInflater = LayoutInflater.from(parent.context)

        val binding =
            DataBindingUtil.inflate<ViewDataBinding>(layoutInflater, layoutID, parent, false)

        return GenericViewHolder(binding)
    }

    override fun onBindViewHolder(holder: GenericViewHolder, position: Int) {
        holder.bind(getItem(position), bindingInterface, itemListener)
        if(currentList[0] is Music){
            MusicManager.setListMusic(currentList as List<Music>)
        }
        else{
            MusicManager.setListMusic(ArrayList())
        }

    }

    class GenericViewHolder(private var binding: ViewDataBinding) :
        RecyclerView.ViewHolder(binding.root) {
        fun <T : Any, V : ViewDataBinding> bind(
            item: T,
            bindingInterface: RecyclerBindingInterface<V, T>,
            itemListener: ItemClickListener<T>
        ) = bindingInterface.binData(binding as V, item, adapterPosition, itemListener)
    }
}


