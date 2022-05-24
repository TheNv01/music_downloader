package com.example.musicdownloader.view.fragment


import android.view.View
import com.example.musicdownloader.R
import com.example.musicdownloader.SharedPreferencesManager
import com.example.musicdownloader.adapter.TopListenedAdapter
import com.example.musicdownloader.databinding.PlaylistInsideFragmentBinding
import com.example.musicdownloader.interfaces.OnActionCallBack
import com.example.musicdownloader.interfaces.itemclickinterface.ItemClickListener
import com.example.musicdownloader.manager.MusicManager
import com.example.musicdownloader.model.Music
import com.example.musicdownloader.model.Region
import com.example.musicdownloader.view.MainActivity
import com.example.musicdownloader.viewmodel.PlaylistInsideViewModel

class PlaylistInsideFragment : BaseFragment<PlaylistInsideFragmentBinding, PlaylistInsideViewModel>(), OnActionCallBack {

    lateinit var callBack: OnActionCallBack

    private val musicItemClickListener = object : ItemClickListener<Music> {
        override fun onClickListener(model: Music) {
            MusicManager.setCurrentMusic(model)
            mViewModel.musics.value?.let { MusicManager.setListMusic(it) }
            callBack.callBack(null, null)
        }
    }

    override fun initBinding(mRootView: View): PlaylistInsideFragmentBinding {
        return PlaylistInsideFragmentBinding.bind(mRootView)
    }

    override fun getViewModelClass(): Class<PlaylistInsideViewModel> {
        return PlaylistInsideViewModel::class.java
    }

    override fun getLayoutId(): Int {
        return R.layout.playlist_inside_fragment
    }

    override fun initViews() {
        callBack = this
    }

    override fun setUpListener() {
        loadData()
    }

    override fun setUpObserver() {
        mViewModel.musics.observe(this){
            binding.recyclerViewPlaylistInside.adapter = TopListenedAdapter(
                R.layout.item_top_listened,
                it,
                musicItemClickListener)
        }
    }

    private fun loadData(){
        SharedPreferencesManager.get<Region>("country").let { region ->
            if (region == null) {
                mViewModel.getMusics("listened", null)
            } else {
                mViewModel.getMusics("listened", region.regionCode)
            }
        }
    }

    override fun callBack(key: String?, data: Any?) {
        val tran = activity?.supportFragmentManager?.beginTransaction()
        (activity as MainActivity).let {
            if(it.playMusicFragment == null){
                it.playMusicFragment = PlayMusicFragment()
                tran?.replace(R.id.container_layout_playing, it.playMusicFragment!!)
                tran?.addToBackStack("playFragment")
            }
            else{
                it.playMusicFragment!!.updateSong()
                tran?.show(it.playMusicFragment!!)
            }
            tran?.commit()
        }
    }


}