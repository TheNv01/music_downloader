package com.example.musicdownloader.adapter

import android.widget.Filter
import android.widget.Filterable
import androidx.databinding.ViewDataBinding
import com.example.musicdownloader.databinding.ItemRegionBinding
import com.example.musicdownloader.interfaces.itemclickinterface.ItemClickListener
import com.example.musicdownloader.model.Region
import kotlin.collections.ArrayList

class ChangeRegionAdapter(layoutID: Int,
                          private val regions: List<Region>,
                          itemClickListener: ItemClickListener<Region>,
): BaseAdapter<Region, ItemRegionBinding>(layoutID, regions, itemClickListener), Filterable {

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

    override fun getFilter(): Filter {
        return object : Filter() {
            override fun performFiltering(charSequence: CharSequence): FilterResults {
                val charString = charSequence.toString()
                list = if (charString.isEmpty()) {
                    regions
                } else {
                    val filteredList = ArrayList<Region>()
                    regions.filter {
                        it.regionName.lowercase().contains(charString.lowercase())
                    }.forEach {
                        filteredList.add(it)
                    }
                    filteredList
                }

                return FilterResults().apply { values = list }
            }

            override fun publishResults(charSequence: CharSequence, filterResults: FilterResults) {
                list = filterResults.values as ArrayList<Region>
                notifyDataSetChanged()
            }
        }
    }
}