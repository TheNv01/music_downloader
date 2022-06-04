package com.example.musicdownloader.view.fragment

import android.app.Dialog
import android.graphics.Color
import android.util.Log
import android.view.Gravity
import android.view.View
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.widget.SearchView
import androidx.appcompat.widget.SearchView.SearchAutoComplete
import androidx.navigation.findNavController
import androidx.navigation.fragment.navArgs
import com.example.musicdownloader.R
import com.example.musicdownloader.adapter.GenericAdapter
import com.example.musicdownloader.adapter.SearchBinding
import com.example.musicdownloader.adapter.SeeAllBinding
import com.example.musicdownloader.databinding.SearchFragmentBinding
import com.example.musicdownloader.interfaces.OnActionCallBack
import com.example.musicdownloader.interfaces.itemclickinterface.ItemClickListener
import com.example.musicdownloader.manager.MusicDonwnloadedManager
import com.example.musicdownloader.manager.MusicManager
import com.example.musicdownloader.model.Music
import com.example.musicdownloader.view.MainActivity
import com.example.musicdownloader.viewmodel.SearchViewModel


class SearchFragment : BaseFragment<SearchFragmentBinding, SearchViewModel>(), OnActionCallBack {


    lateinit var callBack: OnActionCallBack
    private val args: SearchFragmentArgs by navArgs()
    private var musics = ArrayList<Music>()

    private val musicItemClickListener = object : ItemClickListener<Music> {
        override fun onClickListener(model: Music) {
            if(args.fromdestination == 0){
                MusicDonwnloadedManager.currentMusicDownloaded = null
                MusicManager.setCurrentMusic(model)
                mViewModel.musics.value?.let { MusicManager.setListMusic(it) }
                callBack.callBack(null, null)
            }
            else{
                updateListMusicToAddToPlaylist(model)
                binding.tvQuantitySongAdded.text = "Add(${musics.size})"
            }

        }
    }

    private fun updateListMusicToAddToPlaylist(model: Music) {
        if(model.isAddToPlaylist){
            musics.add(model)
        }
        else{
            if(musics.isNotEmpty()){
                with(musics.iterator()) {
                    forEach {
                        if (it.id == model.id) {
                            remove()
                        }
                    }
                }
            }
        }
    }

    override fun initBinding(mRootView: View): SearchFragmentBinding {
        return SearchFragmentBinding.bind(mRootView)
    }

    override fun getViewModelClass(): Class<SearchViewModel> {
        return SearchViewModel::class.java
    }

    override fun getLayoutId(): Int {
        return R.layout.search_fragment
    }

    override fun initViews() {
        callBack = this
        setTextColorHint()
        if(args.fromdestination == 0){
            binding.tvRecommend.visibility = View.GONE
            binding.tvQuantitySongAdded.visibility = View.GONE
            SearchBinding.isIconMenu = true
            SeeAllBinding.itemClickListener = object : ItemClickListener<Music> {
                override fun onClickListener(model: Music) {
                    Log.d("hahaha", model.name!!)
                }
            }
        }
        else{
            SearchBinding.isIconMenu = false
            mViewModel.getMusics(ArrayList(), "")
        }

    }

    override fun setUpListener() {
        binding.tvQuantitySongAdded.setOnClickListener {
            if(musics.size > 0){
                showConfirmDialog()
            }
            else{
                val toast = Toast.makeText(context, "you haven't selected song to add to this playlist", Toast.LENGTH_SHORT)
                toast.setGravity(Gravity.CENTER, 0, 0)
                toast.show()
            }
        }
        binding.icClose.setOnClickListener {
            (activity as MainActivity).onBackPressed()
        }
    }

    override fun setUpObserver() {
        binding.viewmodel = mViewModel
        binding.lifecycleOwner = viewLifecycleOwner

        setupSearchView()
        setupRecyclerview()
    }

    private fun setTextColorHint(){
        val theTextArea = binding.searchSong.findViewById<View>(androidx.appcompat.R.id.search_src_text) as SearchAutoComplete
        theTextArea.setTextColor(Color.WHITE)
        theTextArea.alpha = 0.5f
        theTextArea.setHintTextColor(Color.WHITE)
    }

    private fun setupSearchView(){
        binding.searchSong.setOnQueryTextListener(object : SearchView.OnQueryTextListener {
            override fun onQueryTextSubmit(query: String?): Boolean {
                mViewModel.getMusics(musics, query)
                return true
            }
            override fun onQueryTextChange(newText: String?): Boolean {
                mViewModel.getMusics(musics, newText)
                return true
            }
        })
    }

    private fun setupRecyclerview() {

        binding.recyclerViewSongRecommend.adapter = GenericAdapter(
            R.layout.item_top_listened,
            SearchBinding,
            musicItemClickListener
        )
    }

    private fun showConfirmDialog() {
        val dialog = Dialog(requireActivity(), R.style.Theme_Dialog)
        dialog.window?.setBackgroundDrawableResource(android.R.color.transparent)
        dialog.setContentView(R.layout.comfirm_dialog)
        val tvDone = dialog.findViewById<TextView>(R.id.tv_done)
        val tvContent = dialog.findViewById<TextView>(R.id.tv_content)
        val tvCancel = dialog.findViewById<TextView>(R.id.tv_cancel)
        if(musics.size < 2) {
            tvContent.text = getString(R.string.text_in_confirm_dialog, musics.size.toString().plus(" song"))
        }
        else {
            tvContent.text = getString(R.string.text_in_confirm_dialog, musics.size.toString().plus(" songs"))
        }
        dialog.findViewById<TextView>(R.id.tv_title).text = args.playlist!!.name

        tvDone.setOnClickListener{
            dialog.dismiss()
            mViewModel.addMusicToPlaylist(args.playlist!!.name, args.playlist!!.id, musics)
            if(args.fromdestination == 1){
                val action = SearchFragmentDirections
                    .actionSearchFragmentToPlayListFragment()
                requireActivity().findNavController(R.id.activity_main_nav_host_fragment).navigate(action)
            }
            else if(args.fromdestination == 2){
                val action = SearchFragmentDirections
                    .actionSearchFragmentToPlaylistInsideFragment(args.playlist!!)
                requireActivity().findNavController(R.id.activity_main_nav_host_fragment).navigate(action)
            }
        }
        tvCancel.setOnClickListener {
            dialog.dismiss()
        }
        dialog.show()
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