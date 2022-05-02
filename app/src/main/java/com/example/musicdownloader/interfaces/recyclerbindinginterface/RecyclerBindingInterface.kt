package com.example.musicdownloader.recyclerbindinginterface

import androidx.databinding.ViewDataBinding
import com.example.musicdownloader.interfaces.itemclickinterface.ItemClickListener

interface RecyclerBindingInterface<V: ViewDataBinding, T: Any>{
    fun binData(binder: V, model: T, itemListener: ItemClickListener<T>)
}