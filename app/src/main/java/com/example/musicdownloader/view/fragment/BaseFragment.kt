package com.example.musicdownloader.view.fragment

import android.content.Context
import android.content.Intent
import android.net.ConnectivityManager
import android.os.*
import android.util.Log
import android.view.*
import android.widget.FrameLayout
import android.widget.Toast
import androidx.annotation.Nullable
import androidx.core.content.ContextCompat
import androidx.databinding.ViewDataBinding
import androidx.fragment.app.Fragment
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.NavDirections
import androidx.navigation.findNavController
import androidx.navigation.fragment.findNavController
import com.example.musicdownloader.MusicService
import com.example.musicdownloader.R
import com.example.musicdownloader.Utils
import com.example.musicdownloader.interfaces.itemclickinterface.ItemClickListener
import com.example.musicdownloader.manager.MediaManager
import com.example.musicdownloader.model.General
import com.example.musicdownloader.model.Music
import com.example.musicdownloader.networking.Services
import com.example.musicdownloader.view.MainActivity
import com.example.musicdownloader.view.dialog.AddToPlaylistBottomDialog
import com.example.musicdownloader.view.dialog.BottomDialog
import com.example.musicdownloader.viewmodel.BaseViewModel
import com.proxglobal.proxads.ProxUtils
import com.proxglobal.proxads.ads.callback.NativeAdCallback
import com.proxglobal.proxads.adsv2.ads.ProxAds
import com.proxglobal.proxads.adsv2.callback.AdsCallback
import com.proxglobal.rate.ProxRateDialog
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import java.io.File

abstract class BaseFragment<K: ViewDataBinding, V: ViewModel>: Fragment() {

    protected lateinit var mRootView: View
    protected lateinit var mViewModel: V
    lateinit var binding: K
    protected lateinit var music: Music
    protected lateinit var file: File

    protected var isUserClickPause = false

    @Nullable
    override fun onCreateView(
        inflater: LayoutInflater,
        @Nullable container: ViewGroup?,
        @Nullable savedInstanceState: Bundle?
    ): View {
        inflater.inflate(getLayoutId(), container, false)?.let {
            binding = initBinding(it)
            mRootView = it
        }
        mViewModel = ViewModelProvider(requireActivity(),
            ViewModelProvider.AndroidViewModelFactory(activity?.application!!))[getViewModelClass()]
        initViews()
        return mRootView
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        setUpListener()
        setUpObserver()
    }

    override fun onDestroyView() {
        super.onDestroyView()
        binding.unbind()
    }

    protected fun optionBottomDialog(musicClicked: Music){
        music = musicClicked
        val bottomSheetDialog = BottomDialog((mViewModel as BaseViewModel).options)
        bottomSheetDialog.show((activity as MainActivity).supportFragmentManager, null)
        bottomSheetDialog.itemClickListener = object : ItemClickListener<Int>{
            override fun onClickListener(model: Int) {

                when(model){
                    R.drawable.ic_add_to_playlist ->{
                        addToPlayList(musicClicked)
                    }
                    R.drawable.ic_white_dowload ->{
                        download(musicClicked)
                    }
                    R.drawable.ic_favorite ->{
                        addToFavorite(musicClicked)
                    }
                    R.drawable.ic_share ->{
                        shareMusic()
                    }
                }
            }
        }
    }

    private fun addToPlayList(music: Music){
        val bottomSheetDialog = AddToPlaylistBottomDialog(music)
        bottomSheetDialog.show((activity as MainActivity).supportFragmentManager, null)
    }

    protected fun download(music: Music){

        file = File(Utils.PATH)
        if (!file.exists()){
            file.mkdirs()
        }
        if(music.audioDownload != null){
            adsWhenDownload()
        }
        else{
            val toast = Toast.makeText(context, "Can't download", Toast.LENGTH_SHORT)
            toast.setGravity(Gravity.CENTER, 0, 0)
            toast.show()
        }
    }

    private fun addToFavorite(music: Music){
        if((mViewModel as BaseViewModel).existInFavorite(music.id) == null){
            (mViewModel as BaseViewModel).insertMusicToFavorite(music)
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

    private fun shareMusic(){

        val intent = Intent(Intent.ACTION_SEND)
        intent.type = "text/plain"
        intent.putExtra(Intent.EXTRA_SUBJECT, "Subject Here")

        if(music.source.equals("SC")){
            Services.retrofitService.getLinkSourceSc(music.id).enqueue(object :
                Callback<General<String>> {
                override fun onResponse(call: Call<General<String>>, response: Response<General<String>>) {
                    if (response.isSuccessful) {
                        intent.putExtra(Intent.EXTRA_TEXT, response.body()?.data)
                        startActivity(Intent.createChooser(intent, "Share link"))
                    }
                }
                override fun onFailure(call: Call<General<String>>, t: Throwable) {}
            })
        } else{
            intent.putExtra(Intent.EXTRA_TEXT, music.audio!!)
            startActivity(Intent.createChooser(intent, "Share link"))
        }

    }

    protected fun setStatusBarColor(res: Int){
        val window: Window = (activity as MainActivity).window
        window.addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS)
        window.statusBarColor = ContextCompat.getColor(activity as MainActivity, res)
    }

    protected fun handleClickSeeAll(action: NavDirections, count: MutableLiveData<Int>){
        if(count.value == 0){
            count.postValue(1)
            requireActivity().findNavController(R.id.activity_main_nav_host_fragment).navigate(action)
        }
        else{
            showAds(action)
            count.postValue(0)
        }
    }

    protected fun handleClickItemMusic(){
        when((mViewModel as BaseViewModel).countItemMusic.value){
            0 -> {
                (mViewModel as BaseViewModel).countItemMusic.postValue(1)
                ProxRateDialog.showIfNeed(context, (activity as MainActivity).supportFragmentManager)
            }
            1 -> {
                (mViewModel as BaseViewModel).countItemMusic.postValue(2)
                ProxRateDialog.showIfNeed(context, (activity as MainActivity).supportFragmentManager)
            }
        }
    }

    private fun adsWhenDownload(){

        val path = if(music.name!!.contains("/")){
            file.toString() +"/" + music.name!!.split("/")[0].plus(".mp3")
        } else{
            file.toString() +"/"+ music.name.plus(".mp3")
        }

        if(File(path).exists()){
            Toast.makeText(context, "Can't download because it was downloaded or downloading", Toast.LENGTH_SHORT).show()
        }
        else{
            ProxAds.getInstance().showInterstitialMax(requireActivity(), "inter", object: AdsCallback() {
                override fun onShow() {
                    if(!MediaManager.isPause){
                        (activity as MainActivity).playMusicFragment?.gotoService(MusicService.ACTION_PAUSE)
                    }
                    else{
                        isUserClickPause = true
                    }
                    (mViewModel as BaseViewModel).startDownload(music, path)
                }

                override fun onClosed() {
                    if(!MediaManager.mediaPlayer!!.isPlaying){
                        if(!isUserClickPause){
                            (activity as MainActivity).playMusicFragment?.gotoService(MusicService.ACTION_RESUME)
                        }
                        else{
                            isUserClickPause = false
                        }
                    }
                    (activity as MainActivity).binding.bottomView.selectedItemId  = R.id.download
                }

                override fun onError() {
                    (mViewModel as BaseViewModel).startDownload(music, path)
                    (activity as MainActivity).binding.bottomView.selectedItemId  = R.id.download

                }
            })
        }
    }


    protected open fun showAds(action:NavDirections?){

    }

    protected fun showSmallNative(fameLayout: FrameLayout){
        ProxAds.getInstance().showSmallNativeMaxWithShimmer(requireActivity(), getString(R.string.native_id), fameLayout, object : AdsCallback() {

            override fun onShow() {
                super.onShow()
            }

            override fun onClosed() {
                super.onClosed()
            }

            override fun onError() {
                super.onError()
                fameLayout.visibility = View.GONE
            }
        })

    }

    protected abstract fun initBinding(mRootView: View): K

    protected abstract fun getViewModelClass(): Class<V>

    protected abstract fun getLayoutId(): Int

    protected abstract fun initViews()

    protected abstract fun setUpListener()

    protected abstract fun setUpObserver()

}