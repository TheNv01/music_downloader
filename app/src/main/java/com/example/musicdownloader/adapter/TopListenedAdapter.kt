package com.example.musicdownloader.adapter

import android.graphics.Color
import androidx.databinding.ViewDataBinding
import com.example.musicdownloader.databinding.ItemTopListenedBinding
import com.example.musicdownloader.interfaces.itemclickinterface.ItemClickListener
import com.example.musicdownloader.manager.MusicManager
import com.example.musicdownloader.model.Music

class TopListenedAdapter(
    layoutID: Int,
    private val musics: List<Music>,
    itemClickListener: ItemClickListener<Music>
): BaseAdapter<Music, ItemTopListenedBinding>(layoutID, musics, itemClickListener) {


    override fun setViewHolder(binding: ViewDataBinding): BaseViewHolder<Music, ItemTopListenedBinding> {
        return TopListenedViewHolder(binding as ItemTopListenedBinding)
    }

    inner class TopListenedViewHolder(private val binding: ItemTopListenedBinding) : BaseViewHolder<Music, ItemTopListenedBinding>(binding) {
        override fun bindData(data: Music) {
            binding.music = data
            binding.tvId.text =  "${(adapterPosition + 1)}."
            when(adapterPosition){
                0 ->{
                    binding.tvId.setTextColor(Color.parseColor("#F89500"))
                }
                1 ->{
                    binding.tvId.setTextColor(Color.parseColor("#00FF57"))
                }
                2 ->{
                    binding.tvId.setTextColor(Color.parseColor("#FF1C1C"))
                }
            }
        }

        override fun clickListener(data: Music, itemClickListener: ItemClickListener<Music>) {
                binding.layoutTopListened.setOnClickListener {
                    itemClickListener.onClickListener(data)
                    MusicManager.setListMusic(musics)
                }
        }
    }
}