package com.example.musicdownloader.viewmodel

import android.app.Application
import com.example.musicdownloader.R
import com.example.musicdownloader.model.Option

class SettingViewModel(application: Application) : BaseViewModel(application) {
    private var _optionSettings = ArrayList<Option>()
    val optionSettings: ArrayList<Option> = _optionSettings

    init {
        setOptionSettings()
    }

    private fun setOptionSettings(){
        _optionSettings.add(Option("Rating App", R.drawable.ic_rating))
        _optionSettings.add(Option("Feedback", R.drawable.ic_feedback))
        _optionSettings.add(Option("Privacy & Terms", R.drawable.ic_privacy))
        _optionSettings.add(Option("Share with Friend", R.drawable.ic_share))
    }
}