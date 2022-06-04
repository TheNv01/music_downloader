package com.example.musicdownloader.adapter

import android.view.View
import androidx.databinding.ViewDataBinding
import com.example.musicdownloader.R
import com.example.musicdownloader.databinding.ItemExistingPlaylistBinding
import com.example.musicdownloader.interfaces.itemclickinterface.ItemClickListener
import com.example.musicdownloader.model.Playlist

class ExistingPlaylistAdapter(
    layoutID: Int,
    playlists: ArrayList<Playlist>,
    private val isAdd: Boolean,
    itemClickListener: ItemClickListener<Playlist>
): BaseAdapter<Playlist, ItemExistingPlaylistBinding>(layoutID, playlists, itemClickListener) {

    override fun setViewHolder(binding: ViewDataBinding): BaseViewHolder<Playlist, ItemExistingPlaylistBinding> {
        return AddToPlaylistViewHolder(binding as ItemExistingPlaylistBinding)
    }

    inner class AddToPlaylistViewHolder(private val binding: ItemExistingPlaylistBinding) : BaseViewHolder<Playlist, ItemExistingPlaylistBinding>(binding) {
        override fun bindData(data: Playlist) {
            binding.playlist = data
            if(isAdd){
                binding.icMenu.visibility = View.VISIBLE
            }
            else{
                binding.icMenu.visibility = View.GONE
            }
        }

        override fun clickListener(data: Playlist, itemClickListener: ItemClickListener<Playlist>) {
            binding.layoutItemExistingPlaylist.setOnClickListener {
                if(isAdd){
                    itemClickListener.onClickListener(data)
                    binding.icMenu.setImageResource(R.drawable.ic_choosed)

                }
                else{
                    itemClickListener.onClickListener(data)
                }
            }
        }
    }
}