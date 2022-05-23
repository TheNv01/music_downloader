package com.example.musicdownloader.adapter

import androidx.databinding.ViewDataBinding
import com.example.musicdownloader.R
import com.example.musicdownloader.databinding.ItemExistingPlaylistBinding
import com.example.musicdownloader.interfaces.itemclickinterface.ItemClickListener
import com.example.musicdownloader.model.Option

class AddToPlaylistAdapter(
    layoutID: Int,
    options: List<Option>,
    itemClickListener: ItemClickListener<Option>
): BaseAdapter<Option, ItemExistingPlaylistBinding>(layoutID, options, itemClickListener) {

    private var isChoose: Boolean = false

    override fun setViewHolder(binding: ViewDataBinding): BaseViewHolder<Option, ItemExistingPlaylistBinding> {
        return AddToPlaylistViewHolder(binding as ItemExistingPlaylistBinding)
    }

    inner class AddToPlaylistViewHolder(private val binding: ItemExistingPlaylistBinding) : BaseViewHolder<Option, ItemExistingPlaylistBinding>(binding) {
        override fun bindData(data: Option) {
            binding.opton = data
        }

        override fun clickListener(data: Option, itemClickListener: ItemClickListener<Option>) {
            binding.icAdd.setOnClickListener {
                if(isChoose){
                    binding.icAdd.setImageResource(R.drawable.ic_plus_21)
                    isChoose = false
                }
                else{
                    binding.icAdd.setImageResource(R.drawable.ic_choosed)
                    isChoose = true
                }
            }
        }
    }
}