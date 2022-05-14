package com.example.musicdownloader.adapter

import androidx.databinding.ViewDataBinding
import com.example.musicdownloader.databinding.ItemRegionBinding
import com.example.musicdownloader.interfaces.itemclickinterface.ItemClickListener
import com.example.musicdownloader.model.Region

class ChangeRegionAdapter(layoutID: Int,
                          regions: List<Region>,
                          itemClickListener: ItemClickListener<Region>
): BaseAdapter<Region, ItemRegionBinding>(layoutID, regions, itemClickListener) {

    override fun setViewHolder(binding: ViewDataBinding): BaseViewHolder<Region, ItemRegionBinding> {
        return ChangeRegionViewHolder(binding as ItemRegionBinding)
    }

    inner class ChangeRegionViewHolder(private val binding: ItemRegionBinding) : BaseViewHolder<Region, ItemRegionBinding>(binding) {
        override fun bindData(data: Region) {
            binding.region = data
        }

        override fun clickListener(data: Region, itemClickListener: ItemClickListener<Region>) {
            binding.layoutChangeRegion.setOnClickListener {
                itemClickListener.onClickListener(data)
            }
        }
    }
}