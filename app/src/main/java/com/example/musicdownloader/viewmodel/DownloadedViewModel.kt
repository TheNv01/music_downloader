package com.example.musicdownloader.viewmodel

import com.example.musicdownloader.R
import com.example.musicdownloader.model.Option

class DownloadedViewModel: BaseViewModel() {
    val optionsDownloaded = ArrayList<Option>()

    init {
        initOption()
    }

    private fun initOption(){
        optionsDownloaded.add(Option("Add to playlist", R.drawable.ic_add_to_playlist))
        optionsDownloaded.add(Option("Remove Downloaded Song", R.drawable.ic_delete))
        optionsDownloaded.add(Option("Set as Ringtone", R.drawable.ic_bell))

    }
}