package com.example.musicdownloader.view.fragment

import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.widget.ImageView
import android.widget.TextView
import androidx.activity.OnBackPressedCallback
import androidx.fragment.app.FragmentManager
import com.example.musicdownloader.MusicService
import com.example.musicdownloader.R
import com.example.musicdownloader.databinding.AddFavoriteFragmentBinding
import com.example.musicdownloader.interfaces.OnActionCallBack
import com.example.musicdownloader.model.Option
import com.example.musicdownloader.view.MainActivity
import com.example.musicdownloader.viewmodel.AddFavoriteViewModel

class AddFavoriteFragment (): BaseFragment<AddFavoriteFragmentBinding, AddFavoriteViewModel>() {
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
        setUpLayoutTopic()
    }

    override fun setUpListener() {
        binding.icBack.setOnClickListener {
            (activity as MainActivity).onBackPressed()
        }
    }

    override fun setUpObserver() {

        (activity as MainActivity).onBackPressedDispatcher.addCallback(viewLifecycleOwner, object : OnBackPressedCallback(true) {
            override fun handleOnBackPressed() {
                activity?.supportFragmentManager?.popBackStack("addFavorite", FragmentManager.POP_BACK_STACK_INCLUSIVE)
            }
        })
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