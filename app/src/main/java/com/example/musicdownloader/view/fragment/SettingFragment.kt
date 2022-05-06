package com.example.musicdownloader.view.fragment

import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.widget.ImageView
import android.widget.TextView
import com.example.musicdownloader.R
import com.example.musicdownloader.databinding.SettingFragmentBinding
import com.example.musicdownloader.interfaces.OnActionCallBack
import com.example.musicdownloader.model.OptionSetting
import com.example.musicdownloader.viewmodel.SettingViewModel


class SettingFragment(private val callBack: OnActionCallBack): BaseFragment<SettingFragmentBinding, SettingViewModel>(callBack) {
    override fun initBinding(mRootView: View): SettingFragmentBinding {
        return SettingFragmentBinding.bind(mRootView)
    }

    override fun getViewModelClass(): Class<SettingViewModel> {
        return SettingViewModel::class.java
    }

    override fun getLayoutId(): Int {
        return R.layout.setting_fragment
    }

    override fun initViews() {
        setUpLayoutTopic()
    }

    override fun setUpListener() {

    }

    override fun setUpObserver() {

    }

    private fun initTopicView(option: OptionSetting): View {
        val view: View = LayoutInflater.from(context)
            .inflate(R.layout.item_option_setting, null, false)
        val icon: ImageView = view.findViewById(R.id.ic_option)
        val tvName = view.findViewById<TextView>(R.id.tv_name)
        icon.setImageResource(option.icon)
        tvName.text = option.name
        return view
    }

    private fun setUpLayoutTopic() {
        for (option in mViewModel.optionSettings) {
            val v: View = initTopicView(option)
            v.setOnClickListener {
                Log.d("tag", "hello")
            }
            binding.layoutBottom.addView(v)
        }
    }
}