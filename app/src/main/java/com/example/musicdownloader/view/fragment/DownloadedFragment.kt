package com.example.musicdownloader.view.fragment

import android.app.Dialog
import android.content.ContentValues
import android.content.Context
import android.content.Intent
import android.media.RingtoneManager
import android.net.Uri
import android.os.Build
import android.provider.MediaStore
import android.provider.Settings
import android.util.Log
import android.view.Gravity
import android.view.View
import android.widget.TextView
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
import com.example.musicdownloader.viewmodel.DownloadViewModel
import java.io.BufferedInputStream
import java.io.File
import java.io.FileInputStream
import java.io.IOException


class DownloadedFragment: BaseFragment<DownloadedFragmentBinding, DownloadViewModel>(), OnActionCallBack {

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

    override fun getViewModelClass(): Class<DownloadViewModel> {
        return DownloadViewModel::class.java
    }

    override fun getLayoutId(): Int {
        return R.layout.downloaded_fragment
    }

    override fun onResume() {
        super.onResume()
        adapter.musicsDownloaded = MusicDonwnloadedManager.musicsDownloaded
        adapter.notifyItemChanged(adapter.musicsDownloaded.size + 1)
    }

    override fun initViews() {
        callback = this
    }

    override fun setUpListener() {}

    override fun setUpObserver() {
        MusicDonwnloadedManager.getMusicFromExternal()
        adapter= DownloadedAdapter(
            R.layout.item_downloaded,
            MusicDonwnloadedManager.musicsDownloaded,
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
                        showConfirmRemoveSongDialog(musicDownloaded)
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

    private fun showConfirmRemoveSongDialog(musicDownloaded: MusicDownloaded) {
        val dialog = Dialog(requireActivity(), R.style.Theme_Dialog)
        dialog.window?.setBackgroundDrawableResource(android.R.color.transparent)
        dialog.setContentView(R.layout.comfirm_dialog)
        val tvDone = dialog.findViewById<TextView>(R.id.tv_done)
        tvDone.text = "DELETE"
        val tvContent = dialog.findViewById<TextView>(R.id.tv_content)
        val tvCancel = dialog.findViewById<TextView>(R.id.tv_cancel)

        tvContent.text = "Do You Want To Remove this song from downloaded?"
        dialog.findViewById<TextView>(R.id.tv_title).text = "Confirm"
        tvDone.setOnClickListener{
            musicDownloaded.uri?.let { File(it).delete() }
            MusicDonwnloadedManager.getMusicFromExternal()
            adapter.musicsDownloaded = MusicDonwnloadedManager.musicsDownloaded
            adapter.notifyDataSetChanged()
            dialog.dismiss()
        }

        tvCancel.setOnClickListener {
            dialog.dismiss()
        }
        dialog.show()
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

    private fun setAsRingtone(musicDownloaded: MusicDownloaded): Boolean {
        val values = ContentValues()
        val file = File(musicDownloaded.uri!!)
        values.put(MediaStore.MediaColumns.TITLE, musicDownloaded.music)
        values.put(MediaStore.MediaColumns.MIME_TYPE, "audio/mpeg")
        values.put(MediaStore.Audio.Media.IS_RINGTONE, true)

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
            val newUri: Uri? = requireContext().contentResolver
                .insert(MediaStore.Audio.Media.EXTERNAL_CONTENT_URI, values)
            try {
                requireContext().contentResolver.openOutputStream(newUri!!).use { os ->
                    val size = file.length().toInt()
                    val bytes = ByteArray(size)
                    try {
                        val buf = BufferedInputStream(FileInputStream(file))
                        buf.read(bytes, 0, bytes.size)
                        buf.close()
                        os?.write(bytes)
                        os?.close()
                        os?.flush()
                    } catch (e: IOException) {
                        return false
                    }
                }
            } catch (ignored: Exception) {
                return false
            }
            RingtoneManager.setActualDefaultRingtoneUri(context, RingtoneManager.TYPE_RINGTONE, newUri)
        } else {
            values.put(MediaStore.MediaColumns.DATA, file.absolutePath)
            val uri = MediaStore.Audio.Media.getContentUriForPath(file.absolutePath)
            requireContext().contentResolver
                .delete(uri!!, MediaStore.MediaColumns.DATA + "=\"" + file.absolutePath + "\"", null)
            val newUri: Uri? = requireContext().contentResolver.insert(uri, values)
            RingtoneManager.setActualDefaultRingtoneUri(context, RingtoneManager.TYPE_RINGTONE, newUri)
            MediaStore.Audio.Media.getContentUriForPath(file.absolutePath)?.let {
                requireContext().contentResolver
                    .insert(it, values)
            }
        }
        return true
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