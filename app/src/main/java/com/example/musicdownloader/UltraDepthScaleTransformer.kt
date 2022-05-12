package com.example.musicdownloader

import android.view.View
import androidx.viewpager2.widget.ViewPager2
import kotlin.math.abs

class UltraDepthScaleTransformer : ViewPager2.PageTransformer {
    override fun transformPage(view: View, position: Float) {
        val scale = MIN_SCALE + (1 - MIN_SCALE) * (1 - abs(position))
        val rotation = MAX_ROTATION * abs(position)
        if (position <= 0f) {
            view.translationX = view.width * -position * 0.10f
            view.pivotY = 0.5f * view.height
            view.pivotX = 0.2f * view.width
            view.scaleX = scale
            view.scaleY = scale
            view.rotationY = rotation
        } else if (position <= 2f) {
            view.translationX = view.width * -position * 0.10f
            view.pivotY = 0.5f * view.height
            view.pivotX = 0.8f * view.width
            view.scaleX = scale
            view.scaleY = scale
            view.rotationY = -rotation
        }
    }

    companion object {
        private const val MIN_SCALE = 0.9f
        private const val MAX_ROTATION = 10f
    }
}