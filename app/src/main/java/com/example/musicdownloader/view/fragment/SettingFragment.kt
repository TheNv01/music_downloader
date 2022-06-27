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
import com.example.musicdownloader.view.MainActivity
import com.example.musicdownloader.viewmodel.SettingViewModel
import com.google.firebase.analytics.FirebaseAnalytics
import com.google.firebase.analytics.ktx.logEvent
import com.proxglobal.rate.ProxRateDialog
import com.proxglobal.rate.RatingDialogListener

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
                    R.drawable.ic_rating ->{
//                        initRateDialog()
                        ProxRateDialog.showAlways(context, (activity as MainActivity).supportFragmentManager)
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
//    private fun initRateDialog(){
//
//        val ratingListener = object : RatingDialogListener(){
//            override fun onChangeStar(rate: Int) {
//
//            }
//
//            override fun onSubmitButtonClicked(rate: Int, comment: String?) {
//                (activity as MainActivity).firebaseAnalytics.logEvent("prox_rating_layout") {
//                    param(FirebaseAnalytics.Param.CONTENT_TYPE, "image")
//                }
//                (activity as MainActivity).firebaseAnalytics.logEvent("prox_rating_layout") {
//                    param("event_type", "rated")
//                    param("star", "$rate star")
//                    param("comment", comment.toString())
//                }
//            }
//
//            override fun onLaterButtonClicked() {
//
//            }
//
//            override fun onDone() {
//                //this method will call after dismiss tks dialog
//            }
//        }
//
//        ProxRateDialog.init(R.layout.dialog_rating, ratingListener)
//
//    }
}