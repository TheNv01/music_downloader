package com.example.musicdownloader.view.fragment

import android.content.Context
import android.content.pm.PackageManager
import android.graphics.BitmapFactory
import android.os.Build
import android.os.Environment
import android.os.Handler
import android.os.Looper
import android.util.Log
import android.view.Gravity
import android.view.LayoutInflater
import android.view.View
import android.widget.ImageView
import android.widget.TextView
import android.widget.Toast
import androidx.activity.OnBackPressedCallback
import androidx.activity.result.contract.ActivityResultContracts
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.core.net.toUri
import androidx.fragment.app.FragmentManager
import androidx.fragment.app.setFragmentResultListener
import androidx.lifecycle.viewModelScope
import coil.load
import com.example.musicdownloader.R
import com.example.musicdownloader.databinding.AddFavoriteFragmentBinding
import com.example.musicdownloader.interfaces.OnActionCallBack
import com.example.musicdownloader.manager.DownloadingManager
import com.example.musicdownloader.manager.MusicManager
import com.example.musicdownloader.model.Music
import com.example.musicdownloader.model.MusicDownloading
import com.example.musicdownloader.model.Option
import com.example.musicdownloader.view.MainActivity
import com.example.musicdownloader.viewmodel.AddFavoriteViewModel
import com.tonyodev.fetch2.*
import com.tonyodev.fetch2core.DownloadBlock
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch
import java.io.File
import java.io.InputStream

class AddFavoriteFragment: BaseFragment<AddFavoriteFragmentBinding, AddFavoriteViewModel>(), OnActionCallBack {

    private lateinit var callback: OnActionCallBack

    lateinit var file: File

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
                    bindImage(binding.imgBackground, it.image)
                    bindImage(binding.imgCornersRadius, it.image)
                }
            }
        }

        (activity as MainActivity).onBackPressedDispatcher.addCallback(viewLifecycleOwner, object : OnBackPressedCallback(true) {
            override fun handleOnBackPressed() {
                activity?.supportFragmentManager?.popBackStack("addFavorite", FragmentManager.POP_BACK_STACK_INCLUSIVE)
            }
        })
    }

    private fun bindImage(imgView: ImageView, imgUrl: String?) {
        if(imgUrl == null){
            imgView.setImageResource(R.drawable.ic_broken_image)
        }
        else{
            val reallyImgUrl: String = if(imgUrl.length < 15){
                "http://marstechstudio.com/img-msd/$imgUrl"
            }
            else{
                imgUrl
            }
            reallyImgUrl.let {
                val imgUri = reallyImgUrl.toUri().buildUpon().scheme("http").build()
                imgView.load(imgUri){
                    placeholder(R.drawable.loading_animation)
                    error(R.drawable.ic_broken_image)
                }
            }
        }

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
                    file = File(Environment.getExternalStorageDirectory().toString().plus("/music downloader"))
                    if (!file.exists()){
                        file.mkdirs()
                    }
                    if(MusicManager.getCurrentMusic()?.audioDownloadAllowed == true){
                        checkPermissions()
                    }
                    else{
                        val toast = Toast.makeText(context, "Can't download", Toast.LENGTH_SHORT)
                        toast.setGravity(Gravity.CENTER, 0, 0)
                        toast.show()
                    }

                }
            }
            binding.layoutBottom.addView(v)
        }
    }

    private fun startDownload(){
        val path = file.toString() +"/"+ MusicManager.getCurrentMusic()!!.name.plus(".mp3")
        val request = Request(MusicManager.getCurrentMusic()!!.audioDownload!!, path)
        request.priority = Priority.HIGH
        request.networkType = NetworkType.ALL

        val musicDownloading = MusicDownloading(MusicManager.getCurrentMusic()!!.name!!,
            MusicManager.getCurrentMusic()!!.artistName!!,
            request)

        DownloadingManager.listDownloading().add(musicDownloading)

        DownloadingManager.getFetch(requireContext()).enqueue(request,
            { Toast.makeText(context, "Downloading...", Toast.LENGTH_SHORT).show()},
            { Toast.makeText(context, "Something wrong!...", Toast.LENGTH_SHORT).show() })
        DownloadingManager.fetch!!.addListener(
            object : FetchListener {
                override fun onQueued(download: Download, waitingOnNetwork: Boolean) {}
                override fun onRemoved(download: Download) {}
                override fun onCompleted(download: Download) {
                    DownloadingManager.listDownloading().remove(musicDownloading)
                }
                override fun onDeleted(download: Download) {}
                override fun onDownloadBlockUpdated(download: Download, downloadBlock: DownloadBlock, totalBlocks: Int) {}
                override fun onError(download: Download, error: Error, throwable: Throwable?) {
                    DownloadingManager.fetch!!.retry(request.id)
                }
                override fun onPaused(download: Download) {}
                override fun onResumed(download: Download) {}
                override fun onStarted(download: Download, downloadBlocks: List<DownloadBlock>, totalBlocks: Int) {}
                override fun onWaitingNetwork(download: Download) {}
                override fun onAdded(download: Download) {}
                override fun onCancelled(download: Download) {}
                override fun onProgress(
                    download: Download,
                    etaInMilliSeconds: Long,
                    downloadedBytesPerSecond: Long
                ) {}

            })
    }
    private val permReqLauncher =
        registerForActivityResult(ActivityResultContracts.RequestMultiplePermissions()) { permissions ->
            val granted = permissions.entries.all {
                it.value == true
            }
            if (granted) {
                GlobalScope.launch(Dispatchers.IO) {
                    startDownload()
                }

            }
        }

    private fun checkPermissions() {
        if (Build.VERSION.SDK_INT < Build.VERSION_CODES.M) {
            GlobalScope.launch(Dispatchers.IO) {
                startDownload()
            }
        }
        val permission = arrayOf(android.Manifest.permission.WRITE_EXTERNAL_STORAGE)
        activity?.let {
            if (hasPermissions(activity as Context, permission)) {
                GlobalScope.launch(Dispatchers.IO) {
                    startDownload()
                }
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

    override fun callBack(key: String?, data: Any?) {
        val addToPlaylistFragment = AddToPlaylistFragment()
        val tran = (activity as MainActivity).supportFragmentManager.beginTransaction()
        tran.add(R.id.container_layout_playing, addToPlaylistFragment)
        tran.addToBackStack("addToPlaylist")
        tran.commit()
    }
}