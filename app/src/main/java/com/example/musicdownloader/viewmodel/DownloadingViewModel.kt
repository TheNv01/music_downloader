package com.example.musicdownloader.viewmodel

import com.example.musicdownloader.R
import com.example.musicdownloader.model.Option

class DownloadingViewModel: BaseViewModel() {
    val optionsDownloading = ArrayList<Option>()

    init {
        initOption()
    }

    private fun initOption(){

        optionsDownloading.add(Option("Remove Download", R.drawable.ic_remove))
        optionsDownloading.add(Option("Pause Download", R.drawable.ic_pause_download))
        optionsDownloading.add(Option("Share", R.drawable.ic_share))

    }
}