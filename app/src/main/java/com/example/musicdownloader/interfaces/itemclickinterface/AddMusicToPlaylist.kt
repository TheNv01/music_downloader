package com.example.musicdownloader.interfaces.itemclickinterface

import com.example.musicdownloader.model.Music

interface AddMusicToPlaylist {
    fun onClickAddMusicListener(namePlaylist: String, music: Music)
}