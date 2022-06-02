package com.example.musicdownloader.interfaces.recyclerbindinginterface

import androidx.databinding.ViewDataBinding
import com.example.musicdownloader.interfaces.itemclickinterface.ItemClickListener

interface RecyclerBindingInterface<V: ViewDataBinding, T: Any>{
    fun binData(binder: V, model: T, position: Int, itemListener: ItemClickListener<T>)
}