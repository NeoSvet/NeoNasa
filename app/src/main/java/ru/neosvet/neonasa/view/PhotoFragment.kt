package ru.neosvet.neonasa.view

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.webkit.WebView
import android.widget.LinearLayout
import android.widget.TextView
import androidx.fragment.app.Fragment
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import com.google.android.material.bottomsheet.BottomSheetBehavior
import com.google.android.material.imageview.ShapeableImageView
import ru.neosvet.neonasa.R
import ru.neosvet.neonasa.model.PhotoModel
import ru.neosvet.neonasa.repository.PhotoState

class PhotoFragment : Fragment(), Observer<PhotoState> {
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
            ivPhoto.setOnClickListener {
                mainAct.hideMainBar()
            }
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
                state.response.url?.let {
                    model.loadImage(ivPhoto, it)
                }
                showInfo(state.response.title, state.response.explanation);
            }
            is PhotoState.SuccessVideo -> {
                state.response.url?.let {
                    wvVideo.loadUrl(it)
                    ivPhoto.visibility = View.GONE
                    wvVideo.visibility = View.VISIBLE
                }
                showInfo(state.response.title, state.response.explanation);
            }
            is PhotoState.Loading -> {
                mainAct.showToast(getString(R.string.loading))
            }
            is PhotoState.Error -> {
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
}

enum class TypePhoto(val index: Int) {
    DAY(0), EARTH(1), MARS(2)
}