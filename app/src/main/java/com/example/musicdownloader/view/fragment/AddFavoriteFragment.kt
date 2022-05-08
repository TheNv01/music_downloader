package com.example.musicdownloader.view.fragment

import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.widget.ImageView
import android.widget.TextView
import com.example.musicdownloader.R
import com.example.musicdownloader.adapter.GenericAdapter
import com.example.musicdownloader.databinding.AddFavoriteFragmentBinding
import com.example.musicdownloader.interfaces.OnActionCallBack
import com.example.musicdownloader.interfaces.itemclickinterface.ItemClickListener
import com.example.musicdownloader.model.Item
import com.example.musicdownloader.model.Option
import com.example.musicdownloader.viewmodel.AddFavoriteViewModel

class AddFavoriteFragment (private val callBack: OnActionCallBack): BaseFragment<AddFavoriteFragmentBinding, AddFavoriteViewModel>(callBack) {
    override fun initBinding(mRootView: View): AddFavoriteFragmentBinding {
        return AddFavoriteFragmentBinding.bind(mRootView)
    }

    override fun getViewModelClass(): Class<AddFavoriteViewModel> {
        return AddFavoriteViewModel::class.java
    }

    override fun getLayoutId(): Int {
        return R.layout.add_favorite_fragment
    }

    override fun initViews() {
    }

    override fun setUpListener() {

    }

    override fun setUpObserver() {
        setUpLayoutTopic()
    }

    private fun initTopicView(option: Option): View {
        val view: View = LayoutInflater.from(context)
            .inflate(R.layout.item_option_add_favorite, null, false)
        val icon: ImageView = view.findViewById(R.id.ic_option)
        val tvName = view.findViewById<TextView>(R.id.tv_name)
        icon.setImageResource(option.icon)
        tvName.text = option.name
        return view
    }

    private fun setUpLayoutTopic() {
        for (option in mViewModel.optionFavorites) {
            val v: View = initTopicView(option)
            v.setOnClickListener {
                Log.d("tag", "hello")
            }
            binding.layoutBottom.addView(v)
        }
    }
}