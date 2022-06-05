package com.example.musicdownloader.view.fragment

import android.app.Dialog
import android.content.ContentValues
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.media.RingtoneManager
import android.net.Uri
import android.os.Build
import android.provider.MediaStore
import android.provider.Settings
import android.view.Gravity
import android.view.View
import android.widget.EditText
import android.widget.TextView
import android.widget.Toast
import androidx.activity.result.contract.ActivityResultContracts
import androidx.core.app.ActivityCompat
import androidx.navigation.findNavController
import androidx.navigation.fragment.navArgs
import com.example.musicdownloader.R
import com.example.musicdownloader.adapter.GenericAdapter
import com.example.musicdownloader.adapter.PlaylistInsideBinding
import com.example.musicdownloader.databinding.PlaylistInsideFragmentBinding
import com.example.musicdownloader.interfaces.OnActionCallBack
import com.example.musicdownloader.interfaces.itemclickinterface.ItemClickListener
import com.example.musicdownloader.manager.MusicDonwnloadedManager
import com.example.musicdownloader.manager.MusicManager
import com.example.musicdownloader.model.Music
import com.example.musicdownloader.view.MainActivity
import com.example.musicdownloader.view.dialog.BottomDialog
import com.example.musicdownloader.viewmodel.PlaylistInsideViewModel
import java.io.File

class PlaylistInsideFragment : BaseFragment<PlaylistInsideFragmentBinding, PlaylistInsideViewModel>(), OnActionCallBack {

    lateinit var callBack: OnActionCallBack
    private val args: PlaylistInsideFragmentArgs by navArgs()
    private lateinit var dialog: Dialog
    private lateinit var musicInSidePlaylist: Music

    private val musicItemClickListener = object : ItemClickListener<Music> {
        override fun onClickListener(model: Music) {
            MusicDonwnloadedManager.currentMusicDownloaded = null
            MusicManager.setCurrentMusic(model)
            mViewModel.musics.value?.let { MusicManager.setListMusic(it) }
            callBack.callBack(null, null)
        }
    }

    private val menuClickListener = object : ItemClickListener<Music> {
        override fun onClickListener(model: Music) {
            openSongBottomSheet(model)
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
        binding.tvNamePlaylist.text = args.playList.name
        binding.icPopup.setOnClickListener {
            openPlaylistBottomSheet()
        }
        binding.tvAddSong.setOnClickListener {
            val action = PlaylistInsideFragmentDirections
                .actionPlaylistInsideFragmentToSearchFragment( 2, args.playList)
            requireActivity().findNavController(R.id.activity_main_nav_host_fragment).navigate(action)
        }
    }

    private fun openPlaylistBottomSheet() {
        val bottomSheetDialog = BottomDialog(mViewModel.optionsPlaylist)
        bottomSheetDialog.show((activity as MainActivity).supportFragmentManager, null)
        bottomSheetDialog.itemClickListener = object : ItemClickListener<Int>{
            override fun onClickListener(model: Int) {
                if(model == R.drawable.ic_delete){
                    mViewModel.deletePlaylist(args.playList.id)
                    (activity as MainActivity).findNavController(R.id.activity_main_nav_host_fragment).popBackStack()
                }
                else{
                    showRenamePlaylistDialog()
                }
            }
        }
    }

    private fun openSongBottomSheet(musicSelected: Music) {
        val bottomSheetDialog = BottomDialog(mViewModel.optionsSong)
        bottomSheetDialog.show((activity as MainActivity).supportFragmentManager, null)
        bottomSheetDialog.itemClickListener = object : ItemClickListener<Int> {
            override fun onClickListener(model: Int) {
                when(model){
                    R.drawable.ic_delete -> {
                        mViewModel.removeMusic(args.playList.name, args.playList.id, musicSelected)
                    }
                    R.drawable.ic_share -> {
                        val intent = Intent(Intent.ACTION_SEND)
                        intent.type = "text/plain"
                        intent.putExtra(Intent.EXTRA_SUBJECT, "Subject Here")
                        intent.putExtra(Intent.EXTRA_TEXT, musicSelected.audio)
                        startActivity(Intent.createChooser(intent, "Share via"))
                    }
                    R.drawable.ic_bell -> {
                        if(musicSelected.audioDownload != null){
                            musicInSidePlaylist = musicSelected
                            checkPermissions()
                            loadingDialog()
                        }
                        else{
                            val toast = Toast.makeText(context, "Can't set as ringtone", Toast.LENGTH_SHORT)
                            toast.setGravity(Gravity.CENTER, 0, 0)
                            toast.show()
                        }

                        mViewModel.isDownloadComplete.observe(viewLifecycleOwner){
                            if(it){
                                dialog.dismiss()
                                checkSystemWritePermission(musicSelected)
                            }
                            else{
                                checkSystemWritePermission(musicSelected)
                                dialog.dismiss()
                            }
                        }

                    }
                    R.drawable.ic_favorite -> {
                       if(mViewModel.existInFavorite(musicSelected.id) == null){
                           mViewModel.insertMusicToFavorite(musicSelected)
                           val toast = Toast.makeText(context, "Added the song to the favorite", Toast.LENGTH_SHORT)
                           toast.setGravity(Gravity.CENTER, 0, 0)
                           toast.show()
                       }
                        else{
                           val toast = Toast.makeText(context, "the song already exists in favorite", Toast.LENGTH_SHORT)
                           toast.setGravity(Gravity.CENTER, 0, 0)
                           toast.show()
                       }
                    }
                }
            }
        }
    }

    private fun loadingDialog(){
        dialog = Dialog(requireActivity())
        dialog.window?.setBackgroundDrawableResource(android.R.color.transparent)
        dialog.setContentView(R.layout.progress_dialog)
        dialog.setCancelable(false)
        dialog.show()
    }

    private fun checkSystemWritePermission(music: Music) {
        val retVal: Boolean
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            retVal = Settings.System.canWrite(context)
            if (retVal) {
                setAsRingtone(music)
                val toast = Toast.makeText(context, "Set as ringtone successfully", Toast.LENGTH_SHORT)
                toast.setGravity(Gravity.TOP or Gravity.CENTER_HORIZONTAL, 0, 50)
                toast.show()
            } else {
                val toast = Toast.makeText(context, "Please allow Modify System Setting permission to change ringtone.",
                    Toast.LENGTH_LONG)
                toast.setGravity(Gravity.TOP or Gravity.CENTER_HORIZONTAL, 0, 50)
                toast.show()
                val intent = Intent(Settings.ACTION_MANAGE_WRITE_SETTINGS)
                intent.data = Uri.parse("package:" + requireContext().packageName)
                startActivity(intent)
            }
        }
    }

    private val permReqLauncher =
        registerForActivityResult(ActivityResultContracts.RequestMultiplePermissions()) { permissions ->
            val granted = permissions.entries.all {
                it.value == true
            }
            if (granted) {
                mViewModel.startDownload(musicInSidePlaylist)
            }
        }

    private fun checkPermissions() {
        if (Build.VERSION.SDK_INT < Build.VERSION_CODES.M) {
            mViewModel.startDownload(musicInSidePlaylist)
        }
        val permission = arrayOf(android.Manifest.permission.WRITE_EXTERNAL_STORAGE)
        activity?.let {
            if (hasPermissions(activity as Context, permission)) {
                mViewModel.startDownload(musicInSidePlaylist)
            } else {
                permReqLauncher.launch(
                    permission
                )
            }
        }
    }

    private fun hasPermissions(context: Context, permissions: Array<String>): Boolean = permissions.all {
        ActivityCompat.checkSelfPermission(context, it) == PackageManager.PERMISSION_GRANTED
    }


    private fun setAsRingtone(music: Music){
        val k = File(mViewModel.request.file)
        val values = ContentValues()
        values.put(MediaStore.MediaColumns.DATA, k.absolutePath)
        values.put(MediaStore.MediaColumns.TITLE, music.name)

        values.put(MediaStore.MediaColumns.SIZE, k.length())
        values.put(MediaStore.MediaColumns.MIME_TYPE, "audio/mp3")
        values.put(MediaStore.Audio.Media.ARTIST, music.artistName)

        values.put(MediaStore.Audio.Media.DURATION, music.duration!!)
        values.put(MediaStore.Audio.Media.IS_RINGTONE, true)
        values.put(MediaStore.Audio.Media.IS_NOTIFICATION, false)
        values.put(MediaStore.Audio.Media.IS_ALARM, false)
        values.put(MediaStore.Audio.Media.IS_MUSIC, false)

        val uri = MediaStore.Audio.Media.getContentUriForPath(k.absolutePath)
        if (uri != null) {
            requireContext().contentResolver.delete(
                uri,
                MediaStore.MediaColumns.DATA + "=\""
                        + k.absolutePath + "\"", null)
        }
        val newUri = context?.contentResolver!!.insert(uri!!, values)

        RingtoneManager.setActualDefaultRingtoneUri(
            context,
            RingtoneManager.TYPE_RINGTONE,
            newUri
        )
    }

    override fun setUpObserver() {
        PlaylistInsideBinding.itemClickListener = menuClickListener
        binding.viewmodel = mViewModel
        binding.lifecycleOwner = viewLifecycleOwner
        setupRecyclerview()
        mViewModel.existingPlaylist.observe(viewLifecycleOwner){
            loadData()
        }

    }

    private fun setupRecyclerview(){
        binding.recyclerViewPlaylistInside.adapter = GenericAdapter(
            R.layout.item_top_listened,
            PlaylistInsideBinding,
            musicItemClickListener)
    }

    fun showRenamePlaylistDialog() {
        val dialog = Dialog(requireActivity(), R.style.Theme_Dialog)
        dialog.window?.setBackgroundDrawableResource(android.R.color.transparent)
        dialog.setContentView(R.layout.create_playlist_fragment)
        val tvCreate = dialog.findViewById<TextView>(R.id.tv_create)
        val edt = dialog.findViewById<EditText>(R.id.edt_playlist)
        val tvCancel = dialog.findViewById<TextView>(R.id.tv_cancel)
        tvCreate.text = "DONE"
        dialog.findViewById<TextView>(R.id.tv_title).text = "Name Playlist"
        tvCreate.setOnClickListener{
            mViewModel.renamePlaylist(edt.text.toString(), args.playList.id)
            dialog.dismiss()
            binding.tvNamePlaylist.text = edt.text.toString()
        }
        tvCancel.setOnClickListener {
            dialog.dismiss()
        }
        dialog.show()
    }

    private fun loadData(){
        mViewModel.getMusics(args.playList.id)
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