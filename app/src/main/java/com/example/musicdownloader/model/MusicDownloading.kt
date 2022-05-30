package com.example.musicdownloader.model

import com.tonyodev.fetch2.Request

data class MusicDownloading(
    val music: String,
    val artist: String,
    val request: Request
)
