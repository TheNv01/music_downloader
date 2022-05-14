package com.example.musicdownloader.model

import java.io.Serializable

data class Region(
    val regionCode: String,
    val regionName: String,
    val regionIcon: Int
): Serializable