package com.example.musicdownloader.adapter

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.annotation.LayoutRes
import androidx.databinding.DataBindingUtil
import androidx.databinding.ViewDataBinding
import androidx.recyclerview.widget.RecyclerView
import com.example.musicdownloader.interfaces.itemclickinterface.ItemClickListener
import com.example.musicdownloader.manager.MusicManager
import com.example.musicdownloader.model.Music

abstract class BaseAdapter<T: Any, V: ViewDataBinding>(
    @LayoutRes protected val layoutID: Int,
    private val list: List<T>,
    private val itemClickListener: ItemClickListener<T>
):
    RecyclerView.Adapter<BaseAdapter.BaseViewHolder<T, V>>() {

    abstract fun setViewHolder(binding: ViewDataBinding): BaseViewHolder<T, V>

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): BaseViewHolder<T, V> {
        val layoutInflater = LayoutInflater.from(parent.context)

        val binding = DataBindingUtil
            .inflate<ViewDataBinding>(layoutInflater, layoutID, parent, false)

        return setViewHolder(binding)
    }

    override fun onBindViewHolder(holder: BaseViewHolder<T, V>, position: Int) {
        holder.bindData(list[position])
        holder.clickListener( list[position], itemClickListener)
    }

    override fun getItemCount(): Int {
        return list.size
    }

    abstract class BaseViewHolder<T: Any, V: ViewDataBinding>(binding: V) : RecyclerView.ViewHolder(binding.root) {

        abstract fun bindData(data: T)

        abstract fun clickListener( data: T, itemClickListener: ItemClickListener<T>)

    }

}