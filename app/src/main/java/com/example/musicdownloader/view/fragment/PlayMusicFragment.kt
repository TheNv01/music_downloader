package com.example.musicdownloader.view.fragment

import android.content.Context
import android.os.Handler
import android.os.Looper
import android.util.Log
import android.view.View
import android.view.animation.LinearInterpolator
import androidx.constraintlayout.motion.widget.MotionLayout
import com.example.musicdownloader.MusicService
import com.example.musicdownloader.R
import com.example.musicdownloader.cusomseekbar.ProgressListener
import com.example.musicdownloader.databinding.PlayMusicFragmentBinding
import com.example.musicdownloader.interfaces.OnActionCallBack
import com.example.musicdownloader.manager.MediaManager
import com.example.musicdownloader.manager.MusicManager
import com.example.musicdownloader.model.MessageEvent
import com.example.musicdownloader.model.Music
import com.example.musicdownloader.view.MainActivity
import com.example.musicdownloader.viewmodel.PlayMusicViewModel
import org.greenrobot.eventbus.EventBus
import org.greenrobot.eventbus.Subscribe
import org.greenrobot.eventbus.ThreadMode
import kotlin.math.abs


class PlayMusicFragment(private val callBack: OnActionCallBack): BaseFragment<PlayMusicFragmentBinding, PlayMusicViewModel>(callBack) {

    lateinit var runnable: Runnable
    lateinit var music: Music
    companion object{
        const val KEY_SHOW_ADD_FAVORITE = "KEY_SHOW_ADD_FAVORITE"
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
            }
            MusicService.ACTION_RESUME -> {
                binding.icPlayOrPause.setImageResource(R.drawable.ic_pause_not_background)
            }
            MusicService.ACTION_CLOSE ->{
                (activity as MainActivity).supportFragmentManager.popBackStack()
                Handler(Looper.getMainLooper()).removeCallbacks(runnable)
            }
        }
    }

    override fun initViews() {
        MusicManager.setCurrentMusic(music)
        playSong(music)
        MediaManager.createMediaPlayer()
    }

    override fun setUpListener() {

        setupMotionLayout()
        binding.icFavorite.setOnClickListener {
            callBack.callBack(KEY_SHOW_ADD_FAVORITE, null)
        }
        binding.icClose.setOnClickListener{
            (activity as MainActivity).onBackPressed()
            gotoService(MusicService.ACTION_CLOSE)
        }
        binding.icBack.setOnClickListener {
            (activity as MainActivity).onBackPressed()
        }
        binding.icPlayOrPause.setOnClickListener {
            handlePauseResumeMusic()
        }
    }

    override fun setUpObserver() {

    }

    private fun playSong(music: Music) {
        binding.tvMusic.text = music.artistName
        binding.tvSingle.text = music.name
        binding.tvProgressMax.text = formattedTime(music.duration!!)
        gotoService(MusicService.ACTION_START)
        initSeekBar(music)
    }

    private fun gotoService(action: Int){
        callBack.callBack(KEY_SHOW_SERVICE, action)
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
                    .setDuration(4000)
                    .setInterpolator(LinearInterpolator()).start()
            }
        }

        binding.imgCircle.animate().rotationBy(360f).withEndAction(runnable).setDuration(4000)
            .setInterpolator(LinearInterpolator()).start()
    }

    private fun setupMotionLayout(){
        binding.layoutPlayMusic.setTransitionListener(object : MotionLayout.TransitionListener {
            override fun onTransitionChange(motionLayout: MotionLayout?, startId: Int, endId: Int, progress: Float) {

                (activity as MainActivity).also {
                    it.binding.mainMotionLayout.progress = abs(progress)
                }
            }
            override fun onTransitionCompleted(motionLayout: MotionLayout?, currentId: Int) {

                (activity as MainActivity).also {
                    if(binding.layoutPlayMusic.currentState == R.id.start){
                        it.binding.mainMotionLayout.transitionToStart()
                    }
                }
            }
            override fun onTransitionStarted(p0: MotionLayout?, p1: Int, p2: Int) {
            }
            override fun onTransitionTrigger(p0: MotionLayout?, p1: Int, p2: Boolean, p3: Float) {
            }
        })
        binding.layoutPlayMusic.transitionToEnd()
    }

    private fun initSeekBar(music: Music) {
        binding.seekBar.maxProgress = music.duration!!
        runnable = object : Runnable {
            override fun run() {
                MediaManager.getMediaPlayer()?.let {
                    val currentPosition: Int = it.currentPosition/1000
                    binding.seekBar.progress = currentPosition
                    binding.tvProgress.text = formattedTime(currentPosition)

                }
                Handler(Looper.getMainLooper()).postDelayed(this, 1000)
            }

        }
        activity?.runOnUiThread(runnable)

        binding.seekBar.onProgressChangedListener = object : ProgressListener {
            override fun invoke(progress: Int, fromUser: Boolean) {
                if(fromUser){
                    MediaManager.getMediaPlayer()?.seekTo(progress*1000)
                    binding.tvProgress.text = formattedTime(progress)
                }
            }
        }
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

}