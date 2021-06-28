package ru.neosvet.neonasa.view

import android.graphics.*
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.appcompat.widget.Toolbar
import androidx.core.content.ContextCompat
import androidx.core.content.res.ResourcesCompat
import androidx.core.graphics.drawable.toBitmap
import androidx.fragment.app.Fragment
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import com.google.android.material.appbar.AppBarLayout
import com.google.android.material.appbar.AppBarLayout.OnOffsetChangedListener
import com.google.android.material.appbar.CollapsingToolbarLayout
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
    private lateinit var day_collapsing: CollapsingToolbarLayout
    private lateinit var tvInfo: TextView
    private lateinit var day_appbar: AppBarLayout


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
            day_collapsing = findViewById(R.id.day_collapsing)
            tvInfo = findViewById(R.id.tvInfo)
            day_appbar = findViewById(R.id.day_appbar)

            fabFullscreen.setOnClickListener {
                mainAct.openPhoto(TypePhoto.DAY)
            }

            val clicker = object : View.OnClickListener {
                override fun onClick(v: View?) {
                    mainAct.hideMainBar()
                    mainAct.showPhotoBar()
                }
            }

            ivPhoto.setOnClickListener(clicker)
            tvInfo.setOnClickListener(clicker)
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
                showInfo(state.response.title, state.response.explanation)
                state.response.url?.let {
                    model.loadImage(ivPhoto, it, this)
                }
            }
            is PhotoState.SuccessVideo -> {
                mainAct.finishLoad(true)
                ivPhoto.visibility = View.GONE
                lessHeightAppBar()

                showInfo(state.response.title, state.response.explanation)
                fabFullscreen.setImageDrawable(
                    ContextCompat.getDrawable(
                        requireContext(),
                        R.drawable.ic_play
                    )
                )
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
        tvInfo.text = model.selectWords(info, mainAct)
    }

    override fun onSuccess() { //com.squareup.picasso
        mainAct.finishLoad(true)
        setTitleOnToolbar()

        val img = getPhoto()
        val text = toolbar.title.toString()
        val p = getPaint()

        //count text size
        var w = p.measureText(text).toInt()
        val max = (img.width * 0.6f).toInt()
        while (w < max) {
            p.textSize++
            w = p.measureText(text).toInt()
        }
        p.textSize *= 1.2f

        //count path for text
        val h = (img.height * 0.3f).toInt()
        val left = max / 3f
        val top = (img.height - h).toFloat()
        val rect = RectF(left, top, left + max, top + h + h)
        val path = Path()
        path.addArc(rect, 180f, 180f)

        //draw text
        val canvas = Canvas(img)
        canvas.drawTextOnPath(text, path, 50f, 0f, p)

        ivPhoto.setImageBitmap(img)
    }

    private fun getPaint(): Paint {
        val p = Paint()
        p.color = Color.WHITE
        p.strokeWidth = 5f
        p.typeface = ResourcesCompat.getFont(requireContext(), R.font.spacequest)
        p.textSize = 20f
        p.isAntiAlias = true
        p.setShadowLayer(5.0f, 10.0f, 10.0f, Color.BLACK)
        return p
    }

    private fun getPhoto(): Bitmap {
        val h = ivPhoto.height
        val w = ivPhoto.width
        val x = (ivPhoto.drawable.intrinsicWidth - w) / 2
        val y = (ivPhoto.drawable.intrinsicHeight - h) / 2
        return if (x > 0 && y > 0) { //crop
            Bitmap.createBitmap(
                ivPhoto.drawable.toBitmap(),
                x, y, w, h
            )
        } else { //scale
            ivPhoto.drawable.toBitmap(w, h)
                .copy(Bitmap.Config.ARGB_8888, true)
        }
    }

    private fun setTitleOnToolbar() {
        day_collapsing.isTitleEnabled = false
        toolbar.visibility = View.GONE
        day_appbar.addOnOffsetChangedListener(OnOffsetChangedListener { appBarLayout, verticalOffset ->
            if (verticalOffset < -250) {
                day_collapsing.isTitleEnabled = true
                toolbar.visibility = View.VISIBLE
            } else {
                day_collapsing.isTitleEnabled = false
                toolbar.visibility = View.GONE
            }
        })
    }

    override fun onError(e: Exception?) { //com.squareup.picasso
        mainAct.finishLoad(true)
    }

}