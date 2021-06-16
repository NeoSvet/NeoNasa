package ru.neosvet.neonasa.view

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.webkit.WebView
import android.widget.ImageView
import android.widget.LinearLayout
import android.widget.TextView
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
            if (isExpanded) ImageView.ScaleType.CENTER_CROP
            else ImageView.ScaleType.FIT_START
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