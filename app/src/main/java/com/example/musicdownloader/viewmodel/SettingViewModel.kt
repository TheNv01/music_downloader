package com.example.musicdownloader.viewmodel

import com.example.musicdownloader.R
import com.example.musicdownloader.model.OptionSetting

class SettingViewModel: BaseViewModel() {
    private var _optionSettings = ArrayList<OptionSetting>()
    val optionSettings: ArrayList<OptionSetting> = _optionSettings

    init {
        setOptionSettings()
    }

    private fun setOptionSettings(){
        _optionSettings.add(OptionSetting("Rating App", R.drawable.ic_rating))
        _optionSettings.add(OptionSetting("Feedback", R.drawable.ic_feedback))
        _optionSettings.add(OptionSetting("Privacy & Terms", R.drawable.ic_privacy))
        _optionSettings.add(OptionSetting("Share with Friend", R.drawable.ic_share))
    }
}