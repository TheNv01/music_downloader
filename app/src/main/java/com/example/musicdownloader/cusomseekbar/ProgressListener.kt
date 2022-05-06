package com.example.musicdownloader.cusomseekbar

interface ProgressListener {
    operator fun invoke(progress: Int)
}