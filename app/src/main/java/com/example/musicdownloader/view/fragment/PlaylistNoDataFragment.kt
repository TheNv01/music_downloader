package com.example.musicdownloader.view.fragment

import android.app.Dialog
import android.util.Log
import android.view.Gravity
import android.view.View
import android.widget.EditText
import android.widget.TextView
import android.widget.Toast
import androidx.navigation.findNavController
import androidx.navigation.fragment.navArgs
import com.example.musicdownloader.R
import com.example.musicdownloader.databinding.PlaylistNoDataFragmentBinding
import com.example.musicdownloader.interfaces.itemclickinterface.ItemClickListener
import com.example.musicdownloader.model.Playlist
import com.example.musicdownloader.view.MainActivity
import com.example.musicdownloader.view.dialog.BottomDialog
import com.example.musicdownloader.viewmodel.PlaylistNoDataViewModel

class PlaylistNoDataFragment: BaseFragment<PlaylistNoDataFragmentBinding, PlaylistNoDataViewModel>() {

    private val args: PlaylistNoDataFragmentArgs by navArgs()

    override fun initBinding(mRootView: View): PlaylistNoDataFragmentBinding {
        return PlaylistNoDataFragmentBinding.bind(mRootView)
    }

    override fun getViewModelClass(): Class<PlaylistNoDataViewModel> {
        return PlaylistNoDataViewModel::class.java
    }

    override fun getLayoutId(): Int {
        return R.layout.playlist_no_data_fragment
    }

    override fun initViews() {
        binding.tvNamePlaylist.text = args.nameplaylist
    }

    override fun setUpListener() {
        binding.icPopup.setOnClickListener {
            val bottomSheetDialog = BottomDialog(mViewModel.optionsDownloaded)
            bottomSheetDialog.show((activity as MainActivity).supportFragmentManager, null)
            bottomSheetDialog.itemClickListener = object : ItemClickListener<Int> {
                override fun onClickListener(model: Int) {
                    when(model){
                        R.drawable.ic_rename ->{
                            showRenamePlaylistDialog()
                        }
                        R.drawable.ic_delete ->{
                           showConfirmDialog()
                        }

                    }
                }
            }
        }
        binding.tvAddSong.setOnClickListener {
            val action = PlaylistNoDataFragmentDirections
                .actionPlaylistNoDataFragmentToSearchFragment( 1, Playlist(args.nameplaylist, ArrayList(), args.idplaylist))
            requireActivity().findNavController(R.id.activity_main_nav_host_fragment).navigate(action)
        }
    }

    private fun showConfirmDialog() {
        val dialog = Dialog(requireActivity(), R.style.Theme_Dialog)
        dialog.window?.setBackgroundDrawableResource(android.R.color.transparent)
        dialog.setContentView(R.layout.comfirm_dialog)
        val tvDone = dialog.findViewById<TextView>(R.id.tv_done)
        tvDone.text = "DELETE"
        val tvContent = dialog.findViewById<TextView>(R.id.tv_content)
        val tvCancel = dialog.findViewById<TextView>(R.id.tv_cancel)

        tvContent.text = "Do You Want To Remove Playlist?"
        dialog.findViewById<TextView>(R.id.tv_title).text = binding.tvNamePlaylist.text
        tvDone.setOnClickListener{
            dialog.dismiss()
            mViewModel.deletePlaylist(args.idplaylist)
            (activity as MainActivity).findNavController(R.id.activity_main_nav_host_fragment).popBackStack()

        }
        tvCancel.setOnClickListener {
            dialog.dismiss()
        }
        dialog.show()
    }

    fun showRenamePlaylistDialog() {
        val dialog = Dialog(requireActivity(), R.style.Theme_Dialog)
        dialog.window?.setBackgroundDrawableResource(android.R.color.transparent)
        dialog.setContentView(R.layout.create_playlist_fragment)
        val tvCreate = dialog.findViewById<TextView>(R.id.tv_create)
        val edt = dialog.findViewById<EditText>(R.id.edt_playlist)
        val tvCancel = dialog.findViewById<TextView>(R.id.tv_cancel)
        tvCreate.text = "DONE"
        edt.setText(binding.tvNamePlaylist.text)
        dialog.findViewById<TextView>(R.id.tv_title).text = "Name Playlist"
        tvCreate.setOnClickListener{

            if(edt.text.toString() != ""){
                mViewModel.renamePlaylist(edt.text.toString(), args.idplaylist)
                dialog.dismiss()
                binding.tvNamePlaylist.text = edt.text.toString()
            }
            else{
                val toast = Toast.makeText(context, "Enter name playlist, Please", Toast.LENGTH_SHORT)
                toast.setGravity(Gravity.CENTER, 0, 0)
                toast.show()
            }


        }
        tvCancel.setOnClickListener {
            dialog.dismiss()
        }
        dialog.show()
    }

    override fun setUpObserver() {
    }


}