package com.example.musicdownloader.view.fragment

import android.content.ContentValues
import android.content.Intent
import android.media.RingtoneManager
import android.net.Uri
import android.os.Build
import android.provider.MediaStore
import android.provider.Settings
import android.view.Gravity
import android.view.View
import android.widget.Toast
import androidx.core.content.FileProvider
import com.example.musicdownloader.BuildConfig
import com.example.musicdownloader.R
import com.example.musicdownloader.adapter.DownloadedAdapter
import com.example.musicdownloader.databinding.DownloadedFragmentBinding
import com.example.musicdownloader.interfaces.OnActionCallBack
import com.example.musicdownloader.interfaces.itemclickinterface.ItemClickListener
import com.example.musicdownloader.manager.MusicDonwnloadedManager
import com.example.musicdownloader.manager.MusicManager
import com.example.musicdownloader.model.MusicDownloaded
import com.example.musicdownloader.view.MainActivity
import com.example.musicdownloader.view.dialog.BottomDialog
import com.example.musicdownloader.viewmodel.DownloadedViewModel
import java.io.File

class DownloadedFragment: BaseFragment<DownloadedFragmentBinding, DownloadedViewModel>(), OnActionCallBack {

    private lateinit var adapter: DownloadedAdapter
    private lateinit var callback: OnActionCallBack

    private val itemClickListener = object : ItemClickListener<MusicDownloaded> {
        override fun onClickListener(model: MusicDownloaded) {
            MusicManager.setCurrentMusic(null)
            MusicDonwnloadedManager.currentMusicDownloaded = model
            callback.callBack(HomeFragment.KEY_SHOW_PLAY_MUSIC, null)
        }

    }

    private val menuClickListener = object : ItemClickListener<MusicDownloaded> {
        override fun onClickListener(model: MusicDownloaded) {
            openDownloadingBottomSheet(model)
        }

    }

    override fun initBinding(mRootView: View): DownloadedFragmentBinding {
        return DownloadedFragmentBinding.bind(mRootView)
    }

    override fun getViewModelClass(): Class<DownloadedViewModel> {
        return DownloadedViewModel::class.java
    }

    override fun getLayoutId(): Int {
        return R.layout.downloaded_fragment
    }

    override fun initViews() {
        callback = this
    }

    override fun setUpListener() {

    }

    override fun setUpObserver() {
        mViewModel.getListMusicDownloaded()
        adapter= DownloadedAdapter(
            R.layout.item_downloaded,
            mViewModel.downloadeds,
            itemClickListener,
            menuClickListener
        )
        binding.recyclerViewDownloaded.adapter = adapter
    }

    private fun openDownloadingBottomSheet(musicDownloaded: MusicDownloaded) {
        val bottomSheetDialog = BottomDialog(mViewModel.optionsDownloaded)
        bottomSheetDialog.show((activity as MainActivity).supportFragmentManager, null)
        bottomSheetDialog.itemClickListener = object : ItemClickListener<Int>{
            override fun onClickListener(model: Int) {
                when(model){
                    R.drawable.ic_delete ->{
                        musicDownloaded.uri?.let { File(it).delete() }
                        mViewModel.getListMusicDownloaded()
                        adapter.notifyDataSetChanged()
                    }
                    R.drawable.ic_bell ->{
                        checkSystemWritePermission(musicDownloaded)
                    }
                    R.drawable.ic_share ->{
                        shareMusic(musicDownloaded)
                    }

                }
            }
        }
    }

    private fun shareMusic(musicDownloaded: MusicDownloaded){
        val intent = Intent(Intent.ACTION_SEND)
        val fileUri: Uri? = FileProvider.getUriForFile(
                requireContext(),
                BuildConfig.APPLICATION_ID + ".provider",
                File(musicDownloaded.uri!!))
        intent.type = "audio/*"
        intent.setDataAndType(fileUri, requireContext().contentResolver.getType(fileUri!!))
        intent.putExtra(Intent.EXTRA_STREAM, fileUri)
        intent.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION)
        startActivity(Intent.createChooser(intent, "Share Sound File"))
    }

    private fun checkSystemWritePermission(musicDownloaded: MusicDownloaded) {
        val retVal: Boolean
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            retVal = Settings.System.canWrite(context)
            if (retVal) {
                setAsRingtone(musicDownloaded)
                val toast = Toast.makeText(context, "Set as ringtone successfully", Toast.LENGTH_SHORT)
                toast.setGravity(Gravity.TOP or Gravity.CENTER_HORIZONTAL, 0, 50)
                toast.show()
            } else {
                val toast = Toast.makeText(context, "Please allow Modify System Setting permission to change ringtone.",
                    Toast.LENGTH_SHORT)
                toast.setGravity(Gravity.TOP or Gravity.CENTER_HORIZONTAL, 0, 50)
                toast.show()
                val intent = Intent(Settings.ACTION_MANAGE_WRITE_SETTINGS)
                intent.data = Uri.parse("package:" + requireContext().packageName)
                startActivity(intent)
            }
        }
    }

    private fun setAsRingtone(musicDownloaded: MusicDownloaded){
        val k = File(musicDownloaded.uri!!)
        val values = ContentValues()
        values.put(MediaStore.MediaColumns.DATA, k.absolutePath)
        values.put(MediaStore.MediaColumns.TITLE, musicDownloaded.music)

        values.put(MediaStore.MediaColumns.SIZE, k.length())
        values.put(MediaStore.MediaColumns.MIME_TYPE, "audio/mp3")
        values.put(MediaStore.Audio.Media.ARTIST, musicDownloaded.artist)

        values.put(MediaStore.Audio.Media.DURATION, musicDownloaded.duration!!.toInt())
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


    override fun callBack(key: String?, data: Any?) {
        val tran = (activity as MainActivity).supportFragmentManager.beginTransaction()
        when(key){
            HomeFragment.KEY_SHOW_PLAY_MUSIC ->{
                (activity as MainActivity).let {
                    if(it.playMusicFragment == null){
                        it.playMusicFragment = PlayMusicFragment()
                        tran.replace(R.id.container_layout_playing, it.playMusicFragment!!)
                        tran.addToBackStack("playFragment")
                    }
                    else{
                        it.playMusicFragment!!.updateSong()
                        tran.show(it.playMusicFragment!!)
                    }
                    tran.commit()
                }
            }
        }
    }

}