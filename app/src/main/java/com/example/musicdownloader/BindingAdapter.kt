package com.example.musicdownloader

import androidx.databinding.BindingAdapter
import androidx.databinding.ViewDataBinding
import androidx.recyclerview.widget.RecyclerView
import com.example.musicdownloader.adapter.GenericAdapter
import com.example.musicdownloader.model.Item

@BindingAdapter("listData")
fun <T: Any, V: ViewDataBinding> bindRecyclerView(recyclerView: RecyclerView, data: List<T>?) {
    val adapter = recyclerView.adapter as GenericAdapter<T, V>
    adapter.submitList(data)
}