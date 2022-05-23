package com.example.musicdownloader.view.fragment

import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.widget.ImageView
import android.widget.TextView
import androidx.activity.OnBackPressedCallback
import androidx.fragment.app.FragmentManager
import androidx.fragment.app.setFragmentResultListener
import com.example.musicdownloader.R
import com.example.musicdownloader.databinding.AddFavoriteFragmentBinding
import com.example.musicdownloader.interfaces.OnActionCallBack
import com.example.musicdownloader.model.Music
import com.example.musicdownloader.model.Option
import com.example.musicdownloader.view.MainActivity
import com.example.musicdownloader.viewmodel.AddFavoriteViewModel

class AddFavoriteFragment: BaseFragment<AddFavoriteFragmentBinding, AddFavoriteViewModel>(), OnActionCallBack {

    private lateinit var callback: OnActionCallBack

    companion object{
        const val KEY_SHOW_ADD_TO_PLAYLIST = "KEY_SHOW_ADD_TO_PLAYLIST"
    }

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
        callback = this
        setUpLayoutTopic()
    }

    override fun setUpListener() {
        binding.icBack.setOnClickListener {
            (activity as MainActivity).onBackPressed()
        }
    }

    override fun setUpObserver() {

        setFragmentResultListener("favoriteKey") { _, bundle ->
            bundle.get("favoriteMusic").also {
                if(it is Music){
                   binding.tvMusic.text = it.name
                    binding.tvSingle.text = it.artistName
                }
            }
        }

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
                if(option.icon == R.drawable.ic_add_to_playlist){
                    callback.callBack(KEY_SHOW_ADD_TO_PLAYLIST, null)
                }
                else{
                    Log.d("asdfasdfasdf", "hahaaaaaaaaaa")
                }
            }
            binding.layoutBottom.addView(v)
        }
    }

    override fun callBack(key: String?, data: Any?) {
        val addToPlaylistFragment = AddToPlaylistFragment()
        val tran = (activity as MainActivity).supportFragmentManager.beginTransaction()
        tran.add(R.id.container_layout_playing, addToPlaylistFragment)
        tran.addToBackStack("addToPlaylist")
        tran.commit()
    }
}