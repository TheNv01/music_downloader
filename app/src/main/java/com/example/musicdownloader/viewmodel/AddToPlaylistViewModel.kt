package com.example.musicdownloader.viewmodel

import com.example.musicdownloader.R
import com.example.musicdownloader.model.Option

class AddToPlaylistViewModel: BaseViewModel() {

    private var _existingPlaylist = ArrayList<Option>()
    val existingPlaylist: ArrayList<Option> = _existingPlaylist

    init {
        setExistingPlaylist()
    }

    private fun setExistingPlaylist(){
        _existingPlaylist.add(Option("Rating App", R.drawable.ic_music))
        _existingPlaylist.add(Option("Feedback", R.drawable.ic_music))
        _existingPlaylist.add(Option("Privacy & Terms", R.drawable.ic_music))
        _existingPlaylist.add(Option("Share with Friend", R.drawable.ic_music))
    }
}