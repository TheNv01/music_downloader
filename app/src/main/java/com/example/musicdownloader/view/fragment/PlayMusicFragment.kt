package com.example.musicdownloader.view.fragment

import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.view.View
import android.view.animation.LinearInterpolator
import androidx.activity.OnBackPressedCallback
import androidx.constraintlayout.motion.widget.MotionLayout
import androidx.fragment.app.FragmentManager
import androidx.fragment.app.setFragmentResult
import androidx.navigation.findNavController
import com.example.musicdownloader.MusicService
import com.example.musicdownloader.R
import com.example.musicdownloader.cusomseekbar.ProgressListener
import com.example.musicdownloader.databinding.PlayMusicFragmentBinding
import com.example.musicdownloader.interfaces.OnActionCallBack
import com.example.musicdownloader.manager.MediaManager
import com.example.musicdownloader.manager.MusicManager
import com.example.musicdownloader.manager.RepeatStatus
import com.example.musicdownloader.model.MessageEvent
import com.example.musicdownloader.model.Music
import com.example.musicdownloader.view.MainActivity
import com.example.musicdownloader.viewmodel.PlayMusicViewModel
import org.greenrobot.eventbus.EventBus
import org.greenrobot.eventbus.Subscribe
import org.greenrobot.eventbus.ThreadMode
import kotlin.math.abs

class PlayMusicFragment: BaseFragment<PlayMusicFragmentBinding, PlayMusicViewModel>(), OnActionCallBack {

    lateinit var callBack: OnActionCallBack

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
                binding.imgCircle.animate().cancel()
            }
            MusicService.ACTION_RESUME -> {
                binding.icPlayOrPause.setImageResource(R.drawable.ic_pause_not_background)
                rotateImageView()
            }
            MusicService.ACTION_NEXT,
            MusicService.ACTION_PREVIOUS ->{
                MusicManager.getCurrentMusic()?.let { playSong(it) }
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
        playSong(MusicManager.getCurrentMusic()!!)
    }

    override fun setUpListener() {

        setupMotionLayout()
        binding.icFavorite.setOnClickListener {
            callBack.callBack(KEY_SHOW_ADD_FAVORITE, null)
            val bundle = Bundle()
            bundle.putSerializable("favoriteMusic", MusicManager.getCurrentMusic())
            setFragmentResult("favoriteKey", bundle)
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
    }

    override fun setUpObserver() {
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

    private fun playSong(music: Music) {
        rotateImageView()
        binding.tvMusic.text = music.artistName
        binding.tvSingle.text = music.name
        binding.tvProgressMax.text = formattedTime(music.duration!!)
        gotoService(MusicService.ACTION_START)
        initSeekBar(MusicManager.getCurrentMusic()!!)
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
        playSong(MusicManager.getCurrentMusic()!!)
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
            KEY_SHOW_ADD_FAVORITE ->{
                val addFavoriteFragment = AddFavoriteFragment()
                val tran = (activity as MainActivity).supportFragmentManager.beginTransaction()
                tran.add(R.id.container_layout_playing, addFavoriteFragment)
                tran.addToBackStack("addFavorite")
                tran.commit()
            }
        }
    }
}