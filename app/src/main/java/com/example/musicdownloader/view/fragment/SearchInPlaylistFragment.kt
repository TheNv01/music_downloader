package com.example.musicdownloader.view.fragment

import android.app.Dialog
import android.graphics.Color
import android.view.Gravity
import android.view.View
import android.widget.TextView
import android.widget.Toast
import androidx.activity.OnBackPressedCallback
import androidx.appcompat.widget.SearchView
import androidx.appcompat.widget.SearchView.SearchAutoComplete
import androidx.navigation.findNavController
import androidx.navigation.fragment.navArgs
import com.example.musicdownloader.R
import com.example.musicdownloader.adapter.GenericAdapter
import com.example.musicdownloader.adapter.SearchBinding
import com.example.musicdownloader.databinding.SearchInPlaylistFragmentBinding
import com.example.musicdownloader.interfaces.itemclickinterface.ItemClickListener
import com.example.musicdownloader.model.Music
import com.example.musicdownloader.view.MainActivity
import com.example.musicdownloader.viewmodel.SearchViewModel


class SearchInPlaylistFragment : BaseFragment<SearchInPlaylistFragmentBinding, SearchViewModel>() {

    private val args: SearchInPlaylistFragmentArgs by navArgs()
    private var musics = ArrayList<Music>()

    private val musicItemClickListener = object : ItemClickListener<Music> {
        override fun onClickListener(model: Music) {
            updateListMusicToAddToPlaylist(model)
            binding.tvQuantitySongAdded.text = "Add(${musics.size})"
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

    override fun initBinding(mRootView: View): SearchInPlaylistFragmentBinding {
        return SearchInPlaylistFragmentBinding.bind(mRootView)
    }

    override fun getViewModelClass(): Class<SearchViewModel> {
        return SearchViewModel::class.java
    }

    override fun getLayoutId(): Int {
        return R.layout.search_in_playlist_fragment
    }

    override fun initViews() {
        setTextColorHint()
        SearchBinding.menuClickListener = null
        SearchBinding.isIconMenu = false
        if(mViewModel.musics.value == null || mViewModel.musics.value!!.isEmpty()){
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
        (activity as MainActivity).onBackPressedDispatcher.addCallback(viewLifecycleOwner, object : OnBackPressedCallback(true) {
            override fun handleOnBackPressed() {
                (activity as MainActivity).findNavController(R.id.activity_main_nav_host_fragment).popBackStack()
                mViewModel.setListToEmpty()
            }
        })
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
                val action = SearchInPlaylistFragmentDirections
                    .actionSearchFragmentToPlayListFragment()
                requireActivity().findNavController(R.id.activity_main_nav_host_fragment).navigate(action)
            }
            else if(args.fromdestination == 2){
                val action = SearchInPlaylistFragmentDirections
                    .actionSearchFragmentToPlaylistInsideFragment(args.playlist!!)
                requireActivity().findNavController(R.id.activity_main_nav_host_fragment).navigate(action)
            }
            mViewModel.setListToEmpty()
        }
        tvCancel.setOnClickListener {
            dialog.dismiss()
        }
        dialog.show()
    }

}