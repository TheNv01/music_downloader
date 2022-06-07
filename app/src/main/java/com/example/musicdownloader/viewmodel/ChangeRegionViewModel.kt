package com.example.musicdownloader.viewmodel


import androidx.lifecycle.ViewModel
import com.example.musicdownloader.R
import com.example.musicdownloader.model.Region

class ChangeRegionViewModel: ViewModel(){

    private var _regions = ArrayList<Region>()
    val regions: List<Region> = _regions

    init {
        initRegions()
    }

    private fun initRegions(){
        _regions.add(Region("us", "US", R.drawable.united_states))
        _regions.add(Region("gb", "UK", R.drawable.united_kingdom))
        _regions.add(Region("au", "Australia", R.drawable.australia))
        _regions.add(Region("kr", "Korea", R.drawable.south_korea))
        _regions.add(Region("de", "Germany", R.drawable.germany))
        _regions.add(Region("jp", "Japan", R.drawable.japan))
        _regions.add(Region("vn", "Việt Nam", R.drawable.vietnam))

    }
}