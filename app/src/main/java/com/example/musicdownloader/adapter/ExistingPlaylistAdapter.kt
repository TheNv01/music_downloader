package com.example.musicdownloader.adapter

import android.content.Context
import android.view.View
import android.view.animation.AnimationUtils
import androidx.databinding.ViewDataBinding
import com.example.musicdownloader.R
import com.example.musicdownloader.databinding.ItemExistingPlaylistBinding
import com.example.musicdownloader.interfaces.itemclickinterface.AddMusicToPlaylist
import com.example.musicdownloader.interfaces.itemclickinterface.ItemClickListener
import com.example.musicdownloader.manager.MusicManager
import com.example.musicdownloader.model.Option
import com.example.musicdownloader.model.Playlist

class ExistingPlaylistAdapter(
    layoutID: Int,
    private val playlists: ArrayList<Playlist>,
    private val isAdd: Boolean,
    context: Context,
    itemClickListener: ItemClickListener<Playlist>
): BaseAdapter<Playlist, ItemExistingPlaylistBinding>(layoutID, playlists, itemClickListener) {

    private var isChoose: Boolean = false
    var isDelete: Boolean = false
    private val animShow = AnimationUtils.loadAnimation(context , R.anim.view_show)
    private val animHide = AnimationUtils.loadAnimation( context, R.anim.view_hide)
    lateinit var addMusicToPlaylist: AddMusicToPlaylist


    override fun setViewHolder(binding: ViewDataBinding): BaseViewHolder<Playlist, ItemExistingPlaylistBinding> {
        return AddToPlaylistViewHolder(binding as ItemExistingPlaylistBinding)
    }

    inner class AddToPlaylistViewHolder(private val binding: ItemExistingPlaylistBinding) : BaseViewHolder<Playlist, ItemExistingPlaylistBinding>(binding) {
        override fun bindData(data: Playlist) {
            binding.playlist = data
            if(isAdd){
                binding.icAdd.setImageResource(R.drawable.ic_plus_21)
            }
            else{
                binding.icAdd.setImageResource(R.drawable.kebab_menu_listened)
                binding.icAdd.alpha = 0.5f
            }
            if(isDelete){
                binding.tvDelete.visibility = View.VISIBLE
                binding.tvDelete.startAnimation(animShow)
            }
            else{
                binding.tvDelete.visibility = View.GONE
                binding.tvDelete.startAnimation(animHide)
            }
        }

        override fun clickListener(data: Playlist, itemClickListener: ItemClickListener<Playlist>) {

            binding.layoutItemExistingPlaylist.setOnClickListener {
                itemClickListener.onClickListener(data)
            }

            binding.icAdd.setOnClickListener {
                if(isAdd){
                    isChoose = if(isChoose){
                        binding.icAdd.setImageResource(R.drawable.ic_plus_21)
                        false
                    } else{
                        addMusicToPlaylist.onClickAddMusicListener(
                            binding.tvCreatePlaylist.text.toString(),
                            MusicManager.getCurrentMusic()!!)
                        binding.icAdd.setImageResource(R.drawable.ic_choosed)
                        true
                    }
                }
            }
            binding.tvDelete.setOnClickListener {
                playlists.removeAt(adapterPosition)
                notifyItemRemoved(adapterPosition)
            }
        }
    }
}