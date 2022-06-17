package com.example.musicdownloader.view.fragment

import android.annotation.SuppressLint
import android.content.Intent
import android.net.Uri
import android.view.Gravity
import android.view.LayoutInflater
import android.view.View
import android.widget.ImageView
import android.widget.TextView
import android.widget.Toast
import com.example.musicdownloader.R
import com.example.musicdownloader.databinding.SettingFragmentBinding
import com.example.musicdownloader.model.Option
import com.example.musicdownloader.viewmodel.SettingViewModel

class SettingFragment: BaseFragment<SettingFragmentBinding, SettingViewModel>() {
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
        setStatusBarColor(R.color.status_bar_setting)
    }

    override fun setUpListener() {

    }

    override fun setUpObserver() {

    }

    private fun initTopicView(option: Option): View {
        val view: View = LayoutInflater.from(context)
            .inflate(R.layout.item_option_setting, null, false)
        val icon: ImageView = view.findViewById(R.id.ic_option)
        val tvName = view.findViewById<TextView>(R.id.tv_name)
        icon.setImageResource(option.icon)
        tvName.text = option.name
        return view
    }

    @SuppressLint("QueryPermissionsNeeded")
    private fun setUpLayoutTopic() {
        for (option in mViewModel.optionSettings) {
            val v: View = initTopicView(option)
            v.setOnClickListener {
                when(option.icon){
                    R.drawable.ic_feedback ->{
                        val intent = Intent(Intent.ACTION_SENDTO)
                        intent.data = Uri.parse("mailto:")
                        intent.putExtra(Intent.EXTRA_EMAIL, arrayOf("lionelvanthe@gmail.com"))
                        intent.putExtra(Intent.EXTRA_SUBJECT, "Feedback: MUSIC DOWNLOADER")
                        startActivity(intent)
                    }
                    else ->{
                        val toast = Toast.makeText(context, "Comming soon", Toast.LENGTH_SHORT)
                        toast.setGravity(Gravity.CENTER, 0, 0)
                        toast.show()
                    }
                }
            }
            binding.layoutBottom.addView(v)
        }
    }
}