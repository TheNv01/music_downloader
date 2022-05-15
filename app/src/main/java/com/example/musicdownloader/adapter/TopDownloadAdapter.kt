package com.example.musicdownloader.adapter

import androidx.databinding.ViewDataBinding
import com.example.musicdownloader.databinding.ItemTopDownloadBinding
import com.example.musicdownloader.interfaces.itemclickinterface.ItemClickListener
import com.example.musicdownloader.model.Music

class TopDownloadAdapter(
    layoutID: Int,
    musics: List<Music>,
    itemClickListener: ItemClickListener<Music>
): BaseAdapter<Music, ItemTopDownloadBinding>(layoutID, musics, itemClickListener) {

    override fun getItemCount(): Int {
        return 5
    }

    override fun setViewHolder(binding: ViewDataBinding): BaseViewHolder<Music, ItemTopDownloadBinding> {
        return TopDownloadViewHolder(binding as ItemTopDownloadBinding)
    }

    inner class TopDownloadViewHolder(private val binding: ItemTopDownloadBinding) : BaseViewHolder<Music, ItemTopDownloadBinding>(binding) {
        override fun bindData(data: Music) {
            binding.music = data
        }

        override fun clickListener(data: Music, itemClickListener: ItemClickListener<Music>) {
            binding.layoutTopDownload.setOnClickListener {
                itemClickListener.onClickListener(data)
            }
        }
    }
}