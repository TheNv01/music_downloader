package com.example.musicdownloader.model


data class General<T: Any>(
    val status: String?,
    val currentOffset: Int?,
    val data: T
)
