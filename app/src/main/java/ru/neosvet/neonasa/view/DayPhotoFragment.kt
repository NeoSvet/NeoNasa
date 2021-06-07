package ru.neosvet.neonasa.view

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.webkit.WebView
import android.widget.LinearLayout
import android.widget.TextView
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import com.google.android.material.bottomsheet.BottomSheetBehavior
import com.google.android.material.imageview.ShapeableImageView
import ru.neosvet.neonasa.R
import ru.neosvet.neonasa.model.DayPhotoModel
import ru.neosvet.neonasa.repository.DayPhotoState

class DayPhotoFragment : Fragment(), Observer<DayPhotoState> {
    private val model: DayPhotoModel by lazy {
        ViewModelProvider(this).get(DayPhotoModel::class.java)
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
        return inflater.inflate(R.layout.fragment_day_photo, container, false)
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
            bottomSheetBehavior.state = BottomSheetBehavior.STATE_COLLAPSED
            setBottomSheetEvent()
        }
    }

    private fun setBottomSheetEvent() {
        bottomSheetBehavior.addBottomSheetCallback(object :
            BottomSheetBehavior.BottomSheetCallback() {
            override fun onStateChanged(bottomSheet: View, newState: Int) {
                val act = activity as MainActivity
                when (newState) {
                    BottomSheetBehavior.STATE_DRAGGING -> act.hideBar()
                    BottomSheetBehavior.STATE_COLLAPSED -> act.showBar()
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

    override fun onChanged(state: DayPhotoState?) {
        when (state) {
            is DayPhotoState.SuccessPhoto -> {
                state.response.url?.let {
                    model.loadImage(ivPhoto, it)
                }
                showInfo(state.response.title, state.response.explanation);
            }
            is DayPhotoState.SuccessVideo -> {
                state.response.url?.let {
                    wvVideo.loadUrl(it)
                    ivPhoto.visibility = View.GONE
                    wvVideo.visibility = View.VISIBLE
                }
                showInfo(state.response.title, state.response.explanation);
            }
            is DayPhotoState.Loading -> {
                showToast(getString(R.string.loading))
            }
            is DayPhotoState.Error -> {
                showToast(state.error.message)
            }
        }
    }

    private fun showInfo(title: String?, info: String?) {
        if (title.isNullOrEmpty() || info.isNullOrEmpty())
            return
        tvTitle.text = title
        tvInfo.text = info
    }

    private fun showToast(string: String?) {
        Toast.makeText(context, string, Toast.LENGTH_SHORT).apply {
            //setGravity(Gravity.BOTTOM, 0, 250)
            show()
        }
    }
}