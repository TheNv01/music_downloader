package com.example.musicdownloader.view.fragment

import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.net.Uri
import android.os.*
import android.util.Log
import android.view.Gravity
import android.view.View
import android.view.animation.LinearInterpolator
import android.widget.ImageView
import android.widget.Toast
import androidx.activity.OnBackPressedCallback
import androidx.activity.result.contract.ActivityResultContracts
import androidx.constraintlayout.motion.widget.MotionLayout
import androidx.core.app.ActivityCompat
import androidx.core.net.toUri
import androidx.fragment.app.FragmentManager
import androidx.navigation.findNavController
import coil.load
import com.example.musicdownloader.MusicService
import com.example.musicdownloader.R
import com.example.musicdownloader.cusomseekbar.ProgressListener
import com.example.musicdownloader.databinding.PlayMusicFragmentBinding
import com.example.musicdownloader.interfaces.OnActionCallBack
import com.example.musicdownloader.interfaces.itemclickinterface.ItemClickListener
import com.example.musicdownloader.manager.*
import com.example.musicdownloader.model.MessageEvent
import com.example.musicdownloader.model.Music
import com.example.musicdownloader.view.MainActivity
import com.example.musicdownloader.view.dialog.AddToPlaylistBottomDialog
import com.example.musicdownloader.view.dialog.BottomDialog
import com.example.musicdownloader.viewmodel.PlayMusicViewModel

import org.greenrobot.eventbus.EventBus
import org.greenrobot.eventbus.Subscribe
import org.greenrobot.eventbus.ThreadMode
import java.io.File

class PlayMusicFragment: BaseFragment<PlayMusicFragmentBinding, PlayMusicViewModel>(), OnActionCallBack {

    lateinit var callBack: OnActionCallBack
    lateinit var file: File
    private var bottomSheetDialog: BottomDialog ?= null

    private var isExited: Boolean = false

    companion object{
        const val KEY_SHOW_SERVICE = "KEY_SHOW_SERVICE"
    }

    override fun initBinding(mRootView: View): PlayMusicFragmentBinding {
        return PlayMusicFragmentBinding.bind(mRootView)
    }

    override fun getViewModelClass(): Class<PlayMusicViewModel> {
        return PlayMusicViewModel::class.java
    }

    override fun getLayoutId(): Int {
        return R.layout.play_music_fragment
    }

    override fun onAttach(context: Context) {
        super.onAttach(context)
        EventBus.getDefault().register(this)
    }

    override fun onDetach() {
        super.onDetach()
        EventBus.getDefault().unregister(this)
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    fun onEvent(messageEvent: MessageEvent) {
        when (messageEvent.message) {
            MusicService.ACTION_PAUSE -> {
                binding.icPlayOrPause.setImageResource(R.drawable.ic_play_not_background)
                binding.imgCircle.animate().cancel()
            }
            MusicService.ACTION_RESUME -> {
                binding.icPlayOrPause.setImageResource(R.drawable.ic_pause_not_background)
                rotateImageView()
            }
            MusicService.ACTION_NEXT,
            MusicService.ACTION_PREVIOUS ->{
                if(MusicDonwnloadedManager.currentMusicDownloaded == null){
                    MusicManager.getCurrentMusic()?.let { playSong(it) }
                }
                else{
                    playSong()
                }
                rotateImageView()
                binding.icPlayOrPause.setImageResource(R.drawable.ic_pause_not_background)
            }
            MusicService.ACTION_CLOSE ->{

            }
        }
    }

    override fun initViews() {
        callBack = this
        MediaManager.setProgress(0)
        if(MusicDonwnloadedManager.currentMusicDownloaded == null){
            mViewModel.initOption(false)
            playSong(MusicManager.getCurrentMusic()!!)
            mViewModel.existInFavorite()

        }
        else{
            binding.icFavorite.setImageResource(R.drawable.ic_add_to_playlist)
            mViewModel.initOption()
            playSong()
        }
    }

    override fun setUpListener() {

        setupMotionLayout()
        binding.icMenu.setOnClickListener {
            openBottomSheet()
        }
        binding.icClose.setOnClickListener{
            (activity as MainActivity).playMusicFragment = null
            gotoService(MusicService.ACTION_CLOSE)
            activity?.supportFragmentManager!!.popBackStack("playFragment", FragmentManager.POP_BACK_STACK_INCLUSIVE)
        }
        binding.icBack.setOnClickListener {
            binding.layoutPlayMusic.transitionToStart()
        }
        binding.icPlayOrPause.setOnClickListener {
            handlePauseResumeMusic()
        }
        binding.imgNext.setOnClickListener {
            gotoService(MusicService.ACTION_NEXT)
        }
        binding.imgPrevious.setOnClickListener {
            gotoService(MusicService.ACTION_PREVIOUS)
        }
        binding.imgRepeat.setOnClickListener {
            handleRepeatMusic()
        }
        binding.imgRandom.setOnClickListener {
            handleRandom()
        }
        binding.icShare.setOnClickListener {
            shareMusic()
        }
        binding.icFavorite.setOnClickListener {
            if(MusicDonwnloadedManager.currentMusicDownloaded == null){
                if(!isExited){
                    insertMusicToFavorite()
                }
                else{
                    deleteMusicFromFavorite()
                }
                mViewModel.existInFavorite()
            }
        }
    }

    private fun insertMusicToFavorite(){
        val music = MusicManager.getCurrentMusic()!!
        music.isFavorite = true
        mViewModel.insertMusicToFavorite(music)
        binding.icFavorite.setImageResource(R.drawable.ic_favorite_selected)
        val toast = Toast.makeText(context, "Added the song to the favorite", Toast.LENGTH_SHORT)
        toast.setGravity(Gravity.CENTER, 0, 0)
        toast.show()
    }
    private fun deleteMusicFromFavorite(){
        val music = MusicManager.getCurrentMusic()!!
        mViewModel.deleteMusicFromFavorite(music.id)
        binding.icFavorite.setImageResource(R.drawable.ic_favorite)
        val toast = Toast.makeText(context, "Deleted the song to the favorite", Toast.LENGTH_SHORT)
        toast.setGravity(Gravity.CENTER, 0, 0)
        toast.show()
    }

    private fun shareMusic(){
        val intent = Intent(Intent.ACTION_SEND)
        if(MusicDonwnloadedManager.currentMusicDownloaded == null){
            mViewModel.linkAudio.observe(viewLifecycleOwner){
                intent.type = "text/plain"
                intent.putExtra(Intent.EXTRA_SUBJECT, "Subject Here")
                intent.putExtra(Intent.EXTRA_TEXT,it)
                startActivity(Intent.createChooser(intent, "Share link"))
            }
        }
        else{
            intent.type = "audio/mp3"
            intent.putExtra(Intent.EXTRA_STREAM, Uri.parse(MusicDonwnloadedManager.currentMusicDownloaded!!.uri.toString()))
            Log.d("urrrrrll;", MusicDonwnloadedManager.currentMusicDownloaded!!.uri.toString())
            startActivity(Intent.createChooser(intent, "Share file"))
        }

    }


    private fun openBottomSheet() {
        if(bottomSheetDialog == null){
            bottomSheetDialog = BottomDialog(mViewModel.optionsDownloaded)
        }
        bottomSheetDialog!!.show((activity as MainActivity).supportFragmentManager, null)
        bottomSheetDialog!!.itemClickListener = object : ItemClickListener<Int> {
            override fun onClickListener(model: Int) {
                when(model){
                    R.drawable.ic_add_to_playlist ->{
                        val bottomSheetDialog = AddToPlaylistBottomDialog()
                        bottomSheetDialog.show((activity as MainActivity).supportFragmentManager, null)
                    }
                    R.drawable.ic_favorite ->{
                        if(MusicManager.getCurrentMusic() != null){
                            if(isExited){
                                val toast = Toast.makeText(context, "the song already exists in favorite", Toast.LENGTH_SHORT)
                                toast.setGravity(Gravity.CENTER, 0, 0)
                                toast.show()
                            }
                            else{
                                insertMusicToFavorite()
                            }

                        }
                    }
                    R.drawable.ic_white_dowload ->{
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
            }
        }
    }

    private val permReqLauncher =
        registerForActivityResult(ActivityResultContracts.RequestMultiplePermissions()) { permissions ->
            val granted = permissions.entries.all {
                it.value == true
            }
            if (granted) {
                mViewModel.startDownload(file)
            }
        }

    private fun checkPermissions() {
        if (Build.VERSION.SDK_INT < Build.VERSION_CODES.M) {
            mViewModel.startDownload(file)
        }
        val permission = arrayOf(android.Manifest.permission.WRITE_EXTERNAL_STORAGE)
        activity?.let {
            if (hasPermissions(activity as Context, permission)) {
                mViewModel.startDownload(file)
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

    override fun setUpObserver() {
        binding.viewmodel = mViewModel
        binding.lifecycleOwner = viewLifecycleOwner

        mViewModel.isExisted.observe(viewLifecycleOwner){
            isExited = it
        }

        (activity as MainActivity).onBackPressedDispatcher.addCallback(viewLifecycleOwner, object : OnBackPressedCallback(true) {
            override fun handleOnBackPressed() {
                if(binding.layoutPlayMusic.currentState != R.id.start){
                    binding.layoutPlayMusic.transitionToStart()
                }
                else{
                    if((activity as MainActivity).supportFragmentManager.findFragmentById(R.id.activity_main_nav_host_fragment)?.childFragmentManager?.backStackEntryCount == 0){
                        (activity as MainActivity).playMusicFragment = null
                        gotoService(MusicService.ACTION_CLOSE)
                        activity?.supportFragmentManager!!.popBackStack("playFragment", FragmentManager.POP_BACK_STACK_INCLUSIVE)
                    }
                    else{
                        activity?.findNavController(R.id.activity_main_nav_host_fragment)?.popBackStack()
                    }
                }
            }
        })
    }

    private fun playSong(music: Music? = null) {
        rotateImageView()
        if(music != null){
            binding.tvMusic.text = music.artistName
            binding.tvSingle.text = music.name
            binding.tvProgressMax.text = formattedTime(music.duration!!)
            bindImage(binding.imgBackgroundRectangle, music.image)
            bindImage(binding.imgCircle, music.image)
        }
        else{
            val musicDownloaded = MusicDonwnloadedManager.currentMusicDownloaded
            binding.tvMusic.text = musicDownloaded?.music
            binding.tvSingle.text = musicDownloaded?.artist
            binding.tvProgressMax.text = formattedTime(musicDownloaded?.duration!!.toInt())
            binding.imgBackgroundRectangle.setImageBitmap(musicDownloaded.bitmap)
            binding.imgCircle.setImageBitmap(musicDownloaded.bitmap)
        }
        initSeekBar()
        gotoService(MusicService.ACTION_START)

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

    private fun gotoService(action: Int){
        callBack.callBack(KEY_SHOW_SERVICE, action)
    }

    private fun handleRepeatMusic() {
        when(MusicManager.getRepeatStatus()){
            RepeatStatus.NoRepeat ->{
                binding.imgRepeat.setImageResource(R.drawable.ic_repeat_list)
                MusicManager.setRepeatStatus(RepeatStatus.RepeatListMusic)
            }
            RepeatStatus.RepeatListMusic ->{
                binding.imgRepeat.setImageResource(R.drawable.ic_repeat_one_music)
                MusicManager.setRepeatStatus(RepeatStatus.RepeatOneMusic)
            }
            RepeatStatus.RepeatOneMusic ->{
                binding.imgRepeat.setImageResource(R.drawable.ic_no_repeat)
                MusicManager.setRepeatStatus(RepeatStatus.NoRepeat)
            }
        }
    }

    private fun handleRandom(){
        if(!MusicManager.isRandom()){
            binding.imgRandom.setImageResource(R.drawable.ic_random_selected)
            MusicManager.setRandom(true)
        }
        else{
            binding.imgRandom.setImageResource(R.drawable.ic_random)
            MusicManager.setRandom(false)
        }
    }

    private fun handlePauseResumeMusic() {
        if (MediaManager.isPause()) {
            binding.icPlayOrPause.setImageResource(R.drawable.ic_play_not_background)
            gotoService(MusicService.ACTION_RESUME)
            rotateImageView()
        } else {
            binding.icPlayOrPause.setImageResource(R.drawable.ic_pause_not_background)
            gotoService(MusicService.ACTION_PAUSE)
            binding.imgCircle.animate().cancel()
        }
    }

    private fun rotateImageView(){
        val runnable: Runnable = object : Runnable {
            override fun run() {
                binding.imgCircle
                    .animate()
                    .rotationBy(360f)
                    .withEndAction(this)
                    .setDuration(5000)
                    .setInterpolator(LinearInterpolator()).start()
            }
        }

        binding.imgCircle.animate().rotationBy(360f).withEndAction(runnable).setDuration(4000)
            .setInterpolator(LinearInterpolator()).start()
    }

    private fun setupMotionLayout(){
        binding.layoutPlayMusic.setTransitionListener(object : MotionLayout.TransitionListener {
            override fun onTransitionChange(motionLayout: MotionLayout?, startId: Int, endId: Int, progress: Float) {

            }
            override fun onTransitionCompleted(motionLayout: MotionLayout?, currentId: Int) {
                if (currentId == R.id.end){
                    (activity as MainActivity).binding.bottomView.visibility = View.GONE
                }
                else if (currentId == R.id.start){
                    (activity as MainActivity).binding.bottomView.visibility = View.VISIBLE
                }

            }
            override fun onTransitionStarted(p0: MotionLayout?, p1: Int, p2: Int) {

            }
            override fun onTransitionTrigger(p0: MotionLayout?, p1: Int, p2: Boolean, p3: Float) {
            }
        })
        binding.layoutPlayMusic.transitionToEnd()
    }

    private fun initSeekBar() {
        if(MusicDonwnloadedManager.currentMusicDownloaded == null){
            binding.seekBar.maxProgress = MusicManager.getCurrentMusic()?.duration!!
        }
        else{
            binding.seekBar.maxProgress = MusicDonwnloadedManager.currentMusicDownloaded?.duration!!.toInt()
        }

        (activity as MainActivity).runOnUiThread(object : Runnable {
            override fun run() {
                val currentPosition: Int = MediaManager.getProgress()/1000
                binding.seekBar.progress = currentPosition
                binding.tvProgress.text = formattedTime(currentPosition)
                Handler(Looper.getMainLooper()).postDelayed(this, 1000)
            }
        })

        binding.seekBar.onProgressChangedListener = object : ProgressListener {
            override fun invoke(progress: Int, fromUser: Boolean) {
                if(fromUser){
                    MediaManager.setProgress(progress*1000)
                    binding.tvProgress.text = formattedTime(progress)
                }
            }
        }
    }

    fun updateSong() {
        if(MusicDonwnloadedManager.currentMusicDownloaded == null){
            playSong(MusicManager.getCurrentMusic()!!)
            mViewModel.existInFavorite()
        }
        else{
            playSong()
        }

        binding.layoutPlayMusic.transitionToEnd()
    }

    private fun formattedTime(currentPosition: Int): String{
        val minutes = currentPosition/60
        val seconds = currentPosition%60
        return if(seconds < 10){
            "$minutes:0$seconds"
        } else{
            "$minutes:$seconds"
        }
    }

    override fun callBack(key: String?, data: Any?) {
        when(key){
            KEY_SHOW_SERVICE->{
                if (data != null ) {
                    val intent = Intent(context, MusicService::class.java)
                    intent.putExtra("action", data as Int)
                    (activity as MainActivity).startService(intent)
                }
            }
        }
    }
}