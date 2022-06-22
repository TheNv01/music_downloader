package com.example.musicdownloader.view.fragment

import android.Manifest
import android.app.Service
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.net.Uri
import android.os.Build
import android.os.Handler
import android.os.Looper
import android.telephony.PhoneStateListener
import android.telephony.TelephonyCallback
import android.telephony.TelephonyManager
import android.util.Log
import android.view.Gravity
import android.view.View
import android.view.animation.LinearInterpolator
import android.widget.ImageView
import android.widget.Toast
import androidx.activity.OnBackPressedCallback
import androidx.constraintlayout.motion.widget.MotionLayout
import androidx.core.content.ContextCompat
import androidx.core.content.FileProvider
import androidx.core.net.toUri
import androidx.fragment.app.FragmentManager
import androidx.navigation.findNavController
import coil.load
import com.example.musicdownloader.BuildConfig
import com.example.musicdownloader.MusicService
import com.example.musicdownloader.R
import com.example.musicdownloader.cusomseekbar.ProgressListener
import com.example.musicdownloader.databinding.PlayMusicFragmentBinding
import com.example.musicdownloader.interfaces.OnActionCallBack
import com.example.musicdownloader.interfaces.itemclickinterface.ItemClickListener
import com.example.musicdownloader.manager.MediaManager
import com.example.musicdownloader.manager.MusicDonwnloadedManager
import com.example.musicdownloader.manager.MusicManager
import com.example.musicdownloader.manager.RepeatStatus
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
    private val handler by lazy { Handler(Looper.getMainLooper()) }

    private val runnableForSeekBar: Runnable by lazy{
        object : Runnable {
            override fun run() {
                val currentPosition: Int = MediaManager.getProgress()/1000
                binding.seekBar.progress = currentPosition
                binding.tvProgress.text = formattedTime(currentPosition)
                handler.postDelayed(this, 1000)
            }
        }
    }

    private val runnableForRotateImage by lazy {
        object : Runnable {
            override fun run() {
                binding.imgCircle
                    .animate()
                    .rotationBy(360f)
                    .withEndAction(this)
                    .setDuration(7000)
                    .setInterpolator(LinearInterpolator()).start()
            }
        }
    }

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
            MusicService.ACTION_START ->{
                //binding.icPlayOrPause.setImageResource(R.drawable.ic_pause_not_background)
                MediaManager.isPause = false
            }
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
                gotoService(MusicService.ACTION_START)
                if(MusicDonwnloadedManager.currentMusicDownloaded == null){
                    handler.removeCallbacks(runnableForSeekBar)
                    binding.imgCircle.animate().cancel()
                    mViewModel.isPrepared.postValue( false)
                    if(MediaManager.mediaPlayer!!.isPlaying){
                        MediaManager.mediaPlayer?.stop()
                    }
                    else{
                        MediaManager.mediaPlayer?.setOnPreparedListener(null)
                    }
                    MusicManager.getCurrentMusic()?.let { playSong(it) }
                }
                else{
                    playSong()
                }

            }
            MusicService.ACTION_CLOSE ->{
                (activity as MainActivity).playMusicFragment = null
                (activity as MainActivity).binding.bottomView.visibility = View.VISIBLE
                (activity as MainActivity).binding.viewPlaceHolder.visibility = View.GONE
                activity?.supportFragmentManager!!.popBackStack("playFragment", FragmentManager.POP_BACK_STACK_INCLUSIVE)
            }
        }
    }

    override fun initViews() {
        mViewModel.isPrepared.postValue( false)
        (activity as MainActivity).binding.viewPlaceHolder.visibility = View.VISIBLE
        MusicManager.getCurrentMusic()?.let {
            music = it
        }
        callBack = this
        if(MusicDonwnloadedManager.currentMusicDownloaded == null){
            mViewModel.initOption(true)
            playSong(MusicManager.getCurrentMusic()!!)
        }
        else{
            mViewModel.initOption(false)
            playSong()
        }
    }

    override fun setUpListener() {

        setupMotionLayout()
        binding.icMenu.setOnClickListener {
            openBottomSheet()
        }
        binding.icClose.setOnClickListener{
            gotoService(MusicService.ACTION_CLOSE)
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
            else{
                val toast = Toast.makeText(context, "Comming soon", Toast.LENGTH_SHORT)
                toast.setGravity(Gravity.CENTER, 0, 0)
                toast.show()
            }
        }
        binding.imgExpand.setOnClickListener {
            Toast.makeText(context, "Comming soon", Toast.LENGTH_SHORT).show()
        }
        binding.tvLyrics.setOnClickListener {
            Toast.makeText(context, "Comming soon", Toast.LENGTH_SHORT).show()
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
            val fileUri: Uri? = MusicDonwnloadedManager.currentMusicDownloaded?.let {
                FileProvider.getUriForFile(
                    requireContext(),
                    BuildConfig.APPLICATION_ID + ".provider",
                    File(it.uri!!)
                )
            }
            intent.type = "audio/*"
            intent.setDataAndType(fileUri, requireContext().contentResolver.getType(fileUri!!))
            intent.putExtra(Intent.EXTRA_STREAM, fileUri)
            intent.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION)
            startActivity(Intent.createChooser(intent, "Share Sound File"))
        }

    }


    private fun openBottomSheet() {
        val bottomSheetDialog = BottomDialog(mViewModel.optionsDownloaded)
        bottomSheetDialog.show((activity as MainActivity).supportFragmentManager, null)
        bottomSheetDialog.itemClickListener = object : ItemClickListener<Int> {
            override fun onClickListener(model: Int) {
                when(model){
                    R.drawable.ic_add_to_playlist ->{
                        if(MusicDonwnloadedManager.currentMusicDownloaded == null){
                            val bottomSheetDialogAdd = AddToPlaylistBottomDialog(MusicManager.getCurrentMusic()!!)
                            bottomSheetDialogAdd.show((activity as MainActivity).supportFragmentManager, null)
                        }

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
                        download(MusicManager.getCurrentMusic()!!)
                        if(music.audioDownload != null){
                            binding.layoutPlayMusic.transitionToStart()
                        }
                    }
                }
            }
        }
    }

    override fun setUpObserver() {
        binding.viewmodel = mViewModel
        binding.lifecycleOwner = viewLifecycleOwner

        //binding.icFavorite.setImageResource(R.drawable.ic_add_to_playlist)

        mViewModel.isPrepared.observe(viewLifecycleOwner){
            if(it){
                binding.progressBar.alpha = 0f
                binding.icPlayOrPause.setImageResource(R.drawable.ic_pause_not_background)
                binding.icPlayOrPause.isClickable = true
                binding.seekBar.isEnabled = true
                binding.imgNext.isClickable = true
                binding.imgPrevious.isClickable = true
            }
            else{
                binding.progressBar.alpha = 1f
                binding.icPlayOrPause.setImageResource(R.drawable.ic_play_not_background)
                binding.icPlayOrPause.isClickable = false
                binding.seekBar.isEnabled = false
                binding.imgNext.isClickable = false
                binding.imgPrevious.isClickable = false
            }
        }

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
                        gotoService(MusicService.ACTION_CLOSE)
                    }
                    else{
                        activity?.findNavController(R.id.activity_main_nav_host_fragment)?.popBackStack()
                    }
                }
            }
        })
    }

    private fun playSong(music: Music? = null) {

        binding.seekBar.progress = 0
        binding.tvProgress.text = formattedTime(0)
        if(music != null){
            mViewModel.existInFavorite()
            if(music.artistName == null || music.artistName == ""){
                binding.tvMusic.text = "UNKNOW"
            }
            else{
                binding.tvMusic.text = music.artistName
            }

            binding.tvSingle.text = music.name
            binding.tvProgressMax.text = formattedTime(music.duration!!)
            bindImage(binding.imgBackgroundRectangle, music.image)
            bindImage(binding.imgCircle, music.image)
        }
        else{
            val musicDownloaded = MusicDonwnloadedManager.currentMusicDownloaded
            binding.tvMusic.text = musicDownloaded?.artist
            binding.tvSingle.text = musicDownloaded?.music
            binding.tvProgressMax.text = formattedTime(musicDownloaded?.duration!!.toInt())
            if(musicDownloaded.bitmap != null){
                binding.imgBackgroundRectangle.setImageBitmap(musicDownloaded.bitmap)
                binding.imgCircle.setImageBitmap(musicDownloaded.bitmap)
            }
            else{
                binding.imgBackgroundRectangle.setImageResource(R.drawable.bg_playlist)
                binding.imgCircle.setImageResource(R.drawable.bg_playlist)
            }
            gotoService(MusicService.ACTION_START)
        }
        MediaManager.mediaPlayer?.setOnPreparedListener {

            rotateImageView()
            initSeekBar()
            mViewModel.isPrepared.postValue( true)
            if(context != null){
                MediaManager.mediaPlayer?.start()
                val telephonyManager = context?.getSystemService(Service.TELEPHONY_SERVICE) as TelephonyManager
                registerReceiveCall(telephonyManager)
            }

        }
        //gotoServicegotoService(MusicService.ACTION_START)

    }

    private fun registerReceiveCall(telephonyManager: TelephonyManager){
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) {
            context?.mainExecutor?.let {
                if (ContextCompat.checkSelfPermission(requireContext(), Manifest.permission.READ_PHONE_STATE) == PackageManager.PERMISSION_GRANTED) {
                    telephonyManager.registerTelephonyCallback(
                        it,
                        object : TelephonyCallback(), TelephonyCallback.CallStateListener {
                            override fun onCallStateChanged(state: Int) {
                            try {
                                when (state) {
                                    TelephonyManager.CALL_STATE_RINGING -> gotoService(MusicService.ACTION_PAUSE)
                                    TelephonyManager.CALL_STATE_OFFHOOK -> {}
                                    TelephonyManager.CALL_STATE_IDLE -> gotoService(MusicService.ACTION_RESUME)
                                    else -> {}
                                }
                            } catch (e: Exception) {
                                Log.e("TAG", e.message!!)
                            }
                            }
                        })
                }
                }

        } else {
            telephonyManager.listen(object : PhoneStateListener() {
                override fun onCallStateChanged(state: Int, phoneNumber: String?) {
                    try {
                        when (state) {
                            TelephonyManager.CALL_STATE_RINGING -> gotoService(MusicService.ACTION_PAUSE)
                            TelephonyManager.CALL_STATE_OFFHOOK -> {}
                            TelephonyManager.CALL_STATE_IDLE -> gotoService(MusicService.ACTION_RESUME)
                            else -> {}
                        }
                    } catch (e: Exception) {
                        Log.e("TAG", e.message!!)
                    }
                }
            }, PhoneStateListener.LISTEN_CALL_STATE)
        }
    }

    private fun bindImage(imgView: ImageView, imgUrl: String?) {
        if(imgUrl == null){
            imgView.setImageResource(R.drawable.bg_playlist)
        }
        else{
            imgUrl.let {
                val imgUri = imgUrl.toUri().buildUpon().scheme("http").build()
                imgView.load(imgUri)
            }
        }
    }

    fun gotoService(action: Int){
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
        if (MediaManager.isPause) {
            handler.post(runnableForSeekBar)
            gotoService(MusicService.ACTION_RESUME)
        } else {
            handler.removeCallbacks(runnableForSeekBar)
            gotoService(MusicService.ACTION_PAUSE)
        }
    }

    private fun rotateImageView(){

        binding.imgCircle.animate().rotationBy(360f).withEndAction(runnableForRotateImage).setDuration(7000)
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
        //handler.removeCallbacks(runnable)
        handler.post(runnableForSeekBar)
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
            handler.removeCallbacks(runnableForSeekBar)
            binding.imgCircle.animate().cancel()
            playSong(MusicManager.getCurrentMusic()!!)
            mViewModel.existInFavorite()
            mViewModel.initOption(true)
            MusicManager.getCurrentMusic()?.let {
                music = it
            }
            mViewModel.isPrepared.postValue( false)
            if(MediaManager.mediaPlayer!!.isPlaying){
                MediaManager.mediaPlayer?.stop()
            }
        }
        else{
            mViewModel.initOption(false)
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