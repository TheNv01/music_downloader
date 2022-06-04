package com.example.musicdownloader.view.dialog

import android.app.Dialog
import android.graphics.Color
import android.os.Bundle
import android.util.Log
import android.view.Gravity
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.EditText
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.widget.SearchView
import androidx.lifecycle.ViewModelProvider
import com.example.musicdownloader.R
import com.example.musicdownloader.adapter.GenericAdapter
import com.example.musicdownloader.adapter.PlaylistExistBinding
import com.example.musicdownloader.databinding.AddToPlaylistBottomsheetBinding
import com.example.musicdownloader.interfaces.itemclickinterface.ItemClickListener
import com.example.musicdownloader.manager.MusicManager
import com.example.musicdownloader.model.Playlist
import com.example.musicdownloader.viewmodel.AddToPlaylistViewModel
import com.google.android.material.bottomsheet.BottomSheetDialogFragment

class AddToPlaylistBottomDialog  : BottomSheetDialogFragment() {

    private var mRootView: View ?= null
    private var binding: AddToPlaylistBottomsheetBinding?= null
    private lateinit var viewModel: AddToPlaylistViewModel
    private val adapter by lazy { GenericAdapter(
        R.layout.item_existing_playlist,
        PlaylistExistBinding,
        object : ItemClickListener<Playlist> {
            override fun onClickListener(model: Playlist) {
                viewModel.addMusicToPlaylist(model.name, model.id, MusicManager.getCurrentMusic()!!)
                dismiss()
                val toast = Toast.makeText(context, "Added the song to the playlist", Toast.LENGTH_SHORT)
                toast.setGravity(Gravity.CENTER, 0, 0)
                toast.show()
            }
        }) }


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setStyle(STYLE_NORMAL, R.style.BottomSheetDialogTheme)
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        mRootView = inflater.inflate(getLayoutId(), container, false)

        viewModel = ViewModelProvider(requireActivity(),
            ViewModelProvider.AndroidViewModelFactory(requireActivity().application))[AddToPlaylistViewModel::class.java]
        binding = initBinding(mRootView)
        return mRootView
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        setTextColorHint()
        setUpObserver()
        setUpListener()
    }

    private fun initBinding(mRootView: View?): AddToPlaylistBottomsheetBinding? {
        return mRootView?.let { AddToPlaylistBottomsheetBinding.bind(it) }
    }

    private fun getLayoutId(): Int{
        return R.layout.add_to_playlist_bottomsheet
    }

    private fun setUpListener(){
        binding!!.imgBackgroundCreate.setOnClickListener {
            showCreatePlaylistDialog()
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        binding!!.unbind()
    }

    private fun setUpObserver(){
        binding!!.viewmodel = viewModel
        binding!!.lifecycleOwner = viewLifecycleOwner
        PlaylistExistBinding.haveIconPopup = false
        setupSearchView()
        binding!!.recyclerViewPlaylist.adapter = adapter

    }

    private fun setupSearchView(){
        binding!!.searchPlaylist.setOnQueryTextListener(object : SearchView.OnQueryTextListener {
            override fun onQueryTextSubmit(query: String?): Boolean {
                if(query != null){
                    viewModel.getPlaylistFlowName("%$query%").observe(viewLifecycleOwner){
                        adapter.submitList(it)
                    }
                }
                return true
            }
            override fun onQueryTextChange(newText: String?): Boolean {
                if(newText != null){
                    viewModel.getPlaylistFlowName("%$newText%").observe(viewLifecycleOwner) {
                        adapter.submitList(it)
                    }
                }
                return true
            }
        })
    }

    private fun setTextColorHint(){
        val theTextArea = binding!!.searchPlaylist.findViewById<View>(androidx.appcompat.R.id.search_src_text) as SearchView.SearchAutoComplete
        theTextArea.setTextColor(Color.WHITE)
        theTextArea.alpha = 0.5f
        theTextArea.setHintTextColor(Color.WHITE)
    }


    private fun showCreatePlaylistDialog() {
        val dialog = Dialog(requireActivity(), R.style.Theme_Dialog)
        dialog.window?.setBackgroundDrawableResource(android.R.color.transparent)
        dialog.setContentView(R.layout.create_playlist_fragment)
        val tvCreate = dialog.findViewById<TextView>(R.id.tv_create)
        val edt = dialog.findViewById<EditText>(R.id.edt_playlist)
        val tvCancel = dialog.findViewById<TextView>(R.id.tv_cancel)
        tvCreate.text = "CREATE"
        dialog.findViewById<TextView>(R.id.tv_title).text = "CREATE Playlist"
        tvCreate.setOnClickListener{
            viewModel.createPlaylist(Playlist(edt.text.toString(), ArrayList()))
            dialog.dismiss()
        }
        tvCancel.setOnClickListener {
            dialog.dismiss()
        }
        dialog.show()
    }


}