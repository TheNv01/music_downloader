package com.example.musicdownloader.adapter

import com.example.musicdownloader.R
import com.example.musicdownloader.databinding.ItemTopListenedBinding
import com.example.musicdownloader.interfaces.itemclickinterface.ItemClickListener
import com.example.musicdownloader.interfaces.recyclerbindinginterface.RecyclerBindingInterface
import com.example.musicdownloader.model.Music

object SearchBinding: RecyclerBindingInterface<ItemTopListenedBinding, Music> {

    var menuClickListener: ItemClickListener<Music>?= null
    var isIconMenu = false

    override fun binData(
        binder: ItemTopListenedBinding,
        model: Music,
        position: Int,
        itemListener: ItemClickListener<Music>
    ) {
        binder.music = model

        binder.layoutTopListened.setOnClickListener{
            if(!isIconMenu){
                if(!model.isAddToPlaylist){
                    model.isAddToPlaylist = true
                    binder.popup.setImageResource(R.drawable.ic_added)
                }
                else{
                    model.isAddToPlaylist = false
                    binder.popup.setImageResource(R.drawable.ic_add)
                }
            }
            itemListener.onClickListener(model)
        }
        if(!isIconMenu){
            if(model.isAddToPlaylist){
                binder.popup.setImageResource(R.drawable.ic_added)
            }
            else{
                binder.popup.setImageResource(R.drawable.ic_add)
            }
        }
        if(menuClickListener != null){
            binder.popup.setOnClickListener {
                menuClickListener?.onClickListener(model)
            }
        }


    }


}