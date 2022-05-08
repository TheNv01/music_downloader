package com.example.musicdownloader.viewmodel

import com.example.musicdownloader.R
import com.example.musicdownloader.model.Option

class AddFavoriteViewModel: BaseViewModel() {
    private var _optionFavorites = ArrayList<Option>()
    val optionFavorites: ArrayList<Option> = _optionFavorites

    init {
        setOptionSettings()
    }

    private fun setOptionSettings() {
        _optionFavorites.add(Option("Add to playlist", R.drawable.ic_add_to_playlist))
        _optionFavorites.add(Option("Download Song", R.drawable.ic_white_dowload))
    }
}