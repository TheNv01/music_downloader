package com.example.musicdownloader.adapter

import android.graphics.Color
import com.example.musicdownloader.interfaces.itemclickinterface.ItemClickListener
import com.example.musicdownloader.databinding.ItemTopListenedBinding
import com.example.musicdownloader.interfaces.recyclerbindinginterface.RecyclerBindingInterface
import com.example.musicdownloader.model.Music

object TopListenedBinding : RecyclerBindingInterface<ItemTopListenedBinding, Music> {

    var itemClickListener: ItemClickListener<Music> ?= null

    override fun binData(
        binder: ItemTopListenedBinding,
        model: Music,
        position: Int,
        itemListener: ItemClickListener<Music>
    ) {
        binder.music = model
        binder.tvId.text = (position + 1).toString() + "."
        when(position){
            0 ->{
                binder.tvId.setTextColor(Color.parseColor("#F89500"))
            }
            1 ->{
                binder.tvId.setTextColor(Color.parseColor("#00FF57"))
            }
            2 ->{
                binder.tvId.setTextColor(Color.parseColor("#FF1C1C"))
            }
            else ->  binder.tvId.setTextColor(Color.parseColor("#FFFFFF"))
        }
        binder.layoutTopListened.setOnClickListener{
            itemListener.onClickListener(model)
        }
        binder.popup.setOnClickListener {
            itemClickListener?.onClickListener(model)
        }
    }


}