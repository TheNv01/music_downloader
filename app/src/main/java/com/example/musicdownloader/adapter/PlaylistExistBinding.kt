package com.example.musicdownloader.adapter

import android.view.View
import com.example.musicdownloader.databinding.ItemExistingPlaylistBinding
import com.example.musicdownloader.interfaces.itemclickinterface.ItemClickListener
import com.example.musicdownloader.interfaces.recyclerbindinginterface.RecyclerBindingInterface
import com.example.musicdownloader.model.Playlist

object PlaylistExistBinding : RecyclerBindingInterface<ItemExistingPlaylistBinding, Playlist> {

    var haveIconPopup: Boolean = true
    var menuClickListener: ItemClickListener<Playlist> ?= null

    override fun binData(
        binder: ItemExistingPlaylistBinding,
        model: Playlist,
        position: Int,
        itemListener: ItemClickListener<Playlist>
    ) {
        if(!haveIconPopup){
            binder.icMenu.visibility = View.GONE
        }
        binder.playlist = model
        binder.layoutItemExistingPlaylist.setOnClickListener{
            itemListener.onClickListener(model)
        }
        binder.icMenu.setOnClickListener {
            menuClickListener?.onClickListener(model)
        }
    }

}