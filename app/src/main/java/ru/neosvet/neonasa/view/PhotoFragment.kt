package ru.neosvet.neonasa.view

import android.animation.ObjectAnimator
import android.os.Bundle
import android.util.Size
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.Window
import android.webkit.WebView
import android.widget.ImageView
import android.widget.LinearLayout
import android.widget.TextView
import androidx.core.animation.addListener
import androidx.fragment.app.Fragment
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import androidx.transition.ChangeBounds
import androidx.transition.ChangeImageTransform
import androidx.transition.TransitionManager
import androidx.transition.TransitionSet
import com.google.android.material.bottomsheet.BottomSheetBehavior
import com.google.android.material.imageview.ShapeableImageView
import com.squareup.picasso.Callback
import ru.neosvet.neonasa.R
import ru.neosvet.neonasa.model.PhotoModel
import ru.neosvet.neonasa.repository.PhotoState
import ru.neosvet.neonasa.utils.DoubleClickListener


class PhotoFragment : Fragment(), Observer<PhotoState>, Callback {
    companion object {
        private val ARG_TYPE = "type"

        @JvmStatic
        fun newInstance(type: TypePhoto) =
            PhotoFragment().apply {
                arguments = Bundle().apply {
                    putInt(ARG_TYPE, type.index)
                }
            }
    }

    private val model: PhotoModel by lazy {
        ViewModelProvider(this).get(PhotoModel::class.java)
    }
    private val mainAct: MainActivity by lazy {
        activity as MainActivity
    }
    private lateinit var ivPhoto: ShapeableImageView
    private lateinit var wvVideo: WebView
    private lateinit var tvTitle: TextView
    private lateinit var tvInfo: TextView
    private lateinit var bottomSheetBehavior: BottomSheetBehavior<LinearLayout>
    private var isExpanded = false
    private val animPhotoRestore: ObjectAnimator by lazy {
        ObjectAnimator.ofFloat(ivPhoto, "translationX", 0f)
            .apply {
                duration = 500
            }
    }
    private val sizeScreen: Size by lazy {
        val screen: View = mainAct.window.findViewById(Window.ID_ANDROID_CONTENT)
        Size(screen.width, screen.height)
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.fragment_photo, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        view.run {
            ivPhoto = findViewById(R.id.ivPhoto)
            wvVideo = findViewById(R.id.wvVideo)
            wvVideo.settings.javaScriptEnabled = true
            tvTitle = findViewById(R.id.tvTitle)
            tvInfo = findViewById(R.id.tvInfo)
            bottomSheetBehavior =
                BottomSheetBehavior.from(findViewById(R.id.bottom_sheet_container))
            setBottomSheet()
            setPhotoListeners()
        }
        arguments?.getInt(ARG_TYPE)?.let {
            when (it) {
                TypePhoto.DAY.index ->
                    model.requestDayPhoto()
                TypePhoto.EARTH.index ->
                    model.requestEarthPhoto()
                TypePhoto.MARS.index ->
                    model.requestMarsPhoto()
            }
        }
    }

    private fun setPhotoListeners() {
        ivPhoto.setOnClickListener(object : DoubleClickListener() {
            override fun onSingleClick(v: View) {
                mainAct.hideMainBar()
                mainAct.showPhotoBar()
            }

            override fun onDoubleClick(v: View) {
                changePhotoState()
            }
        })
    }

    private fun changePhotoState() {
        isExpanded = !isExpanded
        if (isExpanded) {
            mainAct.hideBottomBarsWithFab()
            bottomSheetBehavior.peekHeight = 0
        } else {
            mainAct.showPhotoBar()
            bottomSheetBehavior.peekHeight =
                resources.getDimension(R.dimen.bottom_sheet_height).toInt()
        }

        val root = ivPhoto.parent as ViewGroup
        TransitionManager.beginDelayedTransition(
            root, TransitionSet()
                .addTransition(ChangeBounds())
                .addTransition(ChangeImageTransform())
        )

        ivPhoto.scaleType =
            if (isExpanded) ImageView.ScaleType.FIT_XY
            else ImageView.ScaleType.FIT_START


        val params = ivPhoto.getLayoutParams() as ViewGroup.MarginLayoutParams

        if (isExpanded) {
            val img_h = ivPhoto.drawable.intrinsicHeight
            val img_w = ivPhoto.drawable.intrinsicWidth

            params.height = sizeScreen.height
            val f: Float = img_w.toFloat() * (sizeScreen.height.toFloat() / img_h.toFloat())
            params.width = f.toInt()

            animPhotoScroll(f)
        } else {
            params.height = ViewGroup.LayoutParams.MATCH_PARENT
            params.width = ViewGroup.LayoutParams.MATCH_PARENT
            animPhotoRestore.start()
        }
        ivPhoto.setLayoutParams(params)
    }

    private fun animPhotoScroll(photoWidth: Float) {
        val time = (photoWidth / sizeScreen.width * 1100).toLong()
        val toEnd = -(photoWidth - sizeScreen.width)
        val anim1 = ObjectAnimator.ofFloat(ivPhoto, "translationX", toEnd)
            .apply {
                duration = time
            }
        anim1.addListener(onEnd = {
            val toCenter = toEnd * 0.543478f
            val anim2 = ObjectAnimator.ofFloat(ivPhoto, "translationX", toCenter)
                .apply {
                    duration = time / 2
                }
            anim2.start()
        })
        anim1.start()
    }

    private fun setBottomSheet() {
        bottomSheetBehavior.state = BottomSheetBehavior.STATE_COLLAPSED

        bottomSheetBehavior.addBottomSheetCallback(object :
            BottomSheetBehavior.BottomSheetCallback() {
            override fun onStateChanged(bottomSheet: View, newState: Int) {
                when (newState) {
                    BottomSheetBehavior.STATE_DRAGGING -> mainAct.hideBottomBars()
                    BottomSheetBehavior.STATE_COLLAPSED -> mainAct.showPhotoBar()
                }
            }

            override fun onSlide(bottomSheet: View, slideOffset: Float) {
            }
        })
    }

    override fun onResume() {
        super.onResume()
        model.getState().observe(this, this)
    }

    override fun onPause() {
        super.onPause()
        model.getState().removeObserver(this)
    }

    override fun onChanged(state: PhotoState?) {
        when (state) {
            is PhotoState.SuccessPhoto -> {
                if (state.response.hdurl == null) {
                    state.response.url?.let {
                        model.loadImage(ivPhoto, it, this)
                    }
                } else {
                    state.response.hdurl?.let {
                        model.loadImage(ivPhoto, it, this)
                    }
                }
                showInfo(state.response.title, state.response.explanation);
            }
            is PhotoState.SuccessVideo -> {
                mainAct.finishLoad(true)
                state.response.url?.let {
                    wvVideo.loadUrl(it)
                    ivPhoto.visibility = View.GONE
                    wvVideo.visibility = View.VISIBLE
                }
                showInfo(state.response.title, state.response.explanation);
            }
            is PhotoState.Loading -> {
                mainAct.startLoad()
            }
            is PhotoState.Error -> {
                mainAct.finishLoad(true)
                mainAct.showToast(state.error.message)
            }
        }
    }

    private fun showInfo(title: String?, info: String?) {
        if (title.isNullOrEmpty() || info.isNullOrEmpty())
            return
        tvTitle.text = title
        tvInfo.text = info
    }

    override fun onSuccess() { //com.squareup.picasso
        mainAct.finishLoad(true)
    }

    override fun onError(e: Exception?) { //com.squareup.picasso
        mainAct.finishLoad(true)
    }
}

enum class TypePhoto(val index: Int) {
    DAY(0), EARTH(1), MARS(2)
}