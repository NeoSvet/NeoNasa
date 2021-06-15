package ru.neosvet.neonasa.view

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.appcompat.widget.Toolbar
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import com.google.android.material.floatingactionbutton.FloatingActionButton
import com.google.android.material.imageview.ShapeableImageView
import com.squareup.picasso.Callback
import ru.neosvet.neonasa.R
import ru.neosvet.neonasa.model.PhotoModel
import ru.neosvet.neonasa.repository.PhotoState

class DayPhotoFragment : Fragment(), Observer<PhotoState>, Callback {

    private val model: PhotoModel by lazy {
        ViewModelProvider(this).get(PhotoModel::class.java)
    }
    private val mainAct: MainActivity by lazy {
        activity as MainActivity
    }
    private lateinit var fabFullscreen: FloatingActionButton
    private lateinit var ivPhoto: ShapeableImageView
    private lateinit var toolbar: Toolbar
    private lateinit var tvInfo: TextView
    private lateinit var day_appbar: View


    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.fragment_day_photo, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        view.run {
            fabFullscreen = findViewById(R.id.fabFullscreen)
            ivPhoto = findViewById(R.id.ivPhoto)
            toolbar = findViewById(R.id.day_toolbar)
            tvInfo = findViewById(R.id.tvInfo)
            day_appbar = findViewById(R.id.day_appbar)

            fabFullscreen.setOnClickListener {
                mainAct.openPhoto(TypePhoto.DAY)
            }

            ivPhoto.setOnClickListener {
                mainAct.hideMainBar()
            }

            tvInfo.setOnClickListener {
                mainAct.hideMainBar()
            }
        }
        model.requestDayPhoto()
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
                    model.loadImage(ivPhoto, it, this)
                }
                showInfo(state.response.title, state.response.explanation);
            }
            is PhotoState.SuccessVideo -> {
                mainAct.finishLoad(true)
                ivPhoto.visibility = View.GONE
                lessHeightAppBar()

                fabFullscreen.setImageDrawable(
                    ContextCompat.getDrawable(
                        requireContext(),
                        R.drawable.ic_play
                    )
                )
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

    private fun lessHeightAppBar() {
        val params = day_appbar.layoutParams as ViewGroup.LayoutParams
        params.height = resources.getDimension(R.dimen.appbar_video).toInt()
        day_appbar.layoutParams = params
    }

    private fun showInfo(title: String?, info: String?) {
        if (title.isNullOrEmpty() || info.isNullOrEmpty())
            return
        toolbar.title = title
        tvInfo.text = info
    }

    override fun onSuccess() { //com.squareup.picasso
        mainAct.finishLoad(true)
    }

    override fun onError(e: Exception?) { //com.squareup.picasso
        mainAct.finishLoad(true)
    }

}